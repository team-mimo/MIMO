#include "ble.hpp"

int connect_device_cnt = 0;
int remain_device_cnt = MACHINE_NUM;
bool is_running = true;

struct {
	char *adapter_name;
	char *mac_address[MACHINE_NUM];
	int machine_id[MACHINE_NUM];
	RequestType machine_request_type[MACHINE_NUM];

	uuid_t read_uuid;
	uuid_t write_uuid;
	char value_data[100];
} m_argument;

GMainLoop *m_main_loop;
GSList *m_adapter_list = NULL;
GMutex mtx_loop;
GCond cond_loop;

GMutex mtx_connect;

pthread_mutex_t m_connection_terminated_lock = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t m_connection_terminated = PTHREAD_COND_INITIALIZER;

const uuid_t m_battery_level_uuid = { SDP_UUID16, 0x2A19 };
const uuid_t m_ccc_uuid = { SDP_UUID16, 0x2902 };

int ble_id[MACHINE_NUM];

void stop_ble(){
	if(connect_device_cnt <= 0){
		mtx_interrupt.lock();
		is_end = true;
		cv_interrupt.notify_all();
		mtx_interrupt.unlock();
	}
	else{
		is_running = false;
		cv_read.notify_all();
	}
}

int set_ble(int argc, char *argv[]){
	if(argc < MACHINE_NUM + 1){
		std::cout << "ble_test mac_address * " << MACHINE_NUM << '\n';
		exit(1);
	}

	m_argument.adapter_name = NULL;
	for(int i = 0; i < MACHINE_NUM; i++){
		m_argument.mac_address[i] = argv[i + 1];
		m_argument.machine_id[i] = -1;
	}

	string_to_uuid(READ_UUID, strlen(READ_UUID) + 1, &m_argument.read_uuid);
	string_to_uuid(WRITE_UUID, strlen(WRITE_UUID) + 1, &m_argument.write_uuid);

	struct _execute_task_arg execute_task_arg = {
		.task = ble_task,
		.arg = NULL
	};
	GError *error;

	m_main_loop = g_main_loop_new(NULL, FALSE);
	
	GThread *task_thread = g_thread_try_new("main_task", _execute_task, &execute_task_arg, &error);
	if(task_thread == NULL){
		std::cout << "Could not create task for main loop: " << error->message << '\n';
		g_error_free(error);
		exit(1);
	}

	g_main_loop_run(m_main_loop);
	g_main_loop_unref(m_main_loop);

	g_thread_join(task_thread);
	g_thread_unref(task_thread);

	m_main_loop = NULL;

	return 0;
}

int string_to_uuid(const char *str, size_t n, uuid_t *uuid){
	bt_uuid_t bt_uuid;
	
	int ret = bt_string_to_uuid(&bt_uuid, str);
	if(ret == 0){
		memcpy(&uuid->value, &bt_uuid.value, sizeof(uuid->value));
		if(bt_uuid.type == 16){
			uuid->type = SDP_UUID16;
		}
		else if(bt_uuid.type == 32){
			uuid->type = SDP_UUID32;
		}
		else if(bt_uuid.type == 128){
			uuid->type = SDP_UUID128;
		}
		else{
			uuid->type = SDP_UUID_UNSPEC;
		}
	}

	return ret;
}

void *_execute_task(void *arg){
	struct _execute_task_arg *execute_task_arg = (struct _execute_task_arg*)arg;
	execute_task_arg->task(execute_task_arg->arg);
	g_main_loop_quit(m_main_loop);
	return NULL;
}

void *ble_task(void *arg){
	char *addr = (char*)arg;
	ble_adapter *adapter;
	int ret;

	ret = adapter_open(m_argument.adapter_name, &adapter);
	if(ret){
		std::cout << "Failed to open adapter\n";
		exit(1);
	}

	ret = adapter_scan_enable(
			adapter,
			NULL, 0, 0,
			ble_discovered_device, 10, addr);
	if(ret){
		std::cout << "Failed to scan\n";
		return NULL;
	}

	pthread_mutex_lock(&m_connection_terminated_lock);
	pthread_cond_wait(&m_connection_terminated, &m_connection_terminated_lock);
	pthread_mutex_unlock(&m_connection_terminated_lock);

	return NULL;
}

int adapter_open(const char *adapter_name, ble_adapter **adapter){
	char object_path[20];
	ble_adapter *new_adapter;
	OrgBluezAdapter1 *adapter_proxy;
	GError *error = NULL;
	
	if(adapter == NULL){
		return BLE_ERROR;
	}

	if(adapter_name == NULL){
		adapter_name = DEFAULT_ADAPTER;
	}

	snprintf(object_path, sizeof(object_path), "/org/bluez/%s", adapter_name);

	adapter_proxy = org_bluez_adapter1_proxy_new_for_bus_sync(
			G_BUS_TYPE_SYSTEM, G_DBUS_PROXY_FLAGS_NONE,
			"org.bluez",
			object_path,
			NULL, &error);
	if(adapter_proxy == NULL){
		int ret = BLE_ERROR;
		if(error){
			std::cout << "Failed to get adapter " << object_path << ": " << error->message << '\n';
			g_error_free(error);
		}
		else{
			std::cout << "Failed to get adapter " << object_path << '\n';
		}
		return ret;
	}

	org_bluez_adapter1_set_powered(adapter_proxy, TRUE);

	new_adapter = (ble_adapter*)calloc(1, sizeof(ble_adapter));
	if(new_adapter == NULL){
		return BLE_ERROR;
	}

	new_adapter->id = strdup(object_path);
	new_adapter->name = strdup(adapter_name);
	new_adapter->reference_counter = 1;
	new_adapter->adapter_proxy = adapter_proxy;

	m_adapter_list = g_slist_append(m_adapter_list, new_adapter);
	*adapter = new_adapter;

	return BLE_SUCCESS;
}

int stricmp(char const *a, char const *b){
	for(;; a++, b++){
		int d = tolower((unsigned char)*a) - tolower((unsigned char)*b);
		if(d != 0 || !*a){
			return d;
		}
	}
	return 0;
}

void ble_discovered_device(ble_adapter *adapter, const char *addr, const char *name, void *user_data){
	int ret;

	int matched_num = -1;
	for(int i = 0; i < MACHINE_NUM; i++){
		if(stricmp(addr, m_argument.mac_address[i]) == 0){
			matched_num = i;
			ble_id[matched_num] = i;
		}
	}
	if(matched_num < 0){
		return;
	}

	g_mutex_lock(&mtx_loop);

	remain_device_cnt--;
	if(remain_device_cnt <= 0){
		adapter->ble_scan.is_scanning = false;
		g_cond_broadcast(&cond_loop);
	}
	adapter->ble_scan.ble_scan_timeout_id = 0;

	g_mutex_unlock(&mtx_loop);

	std::cout << "Found bluetooth device " << addr << '\n';

	g_mutex_lock(&mtx_connect);
	ret = ble_connect(adapter, addr, 0, on_device_connect, &ble_id[matched_num]);
	if(ret != BLE_SUCCESS){
		std::cout << "Failed to connect to the bluetooth device '" << addr << "'\n";
	}
}

std::unordered_map<int, std::vector<std::string>> &get_response_reference(RequestType type){
	switch(type){
		case REQUEST_LIGHT:
			return light_response;
		case REQUEST_LAMP:
			return lamp_response;
		case REQUEST_WINDOW:
			return window_response;
		case REQUEST_CURTAIN:
			return curtain_response;
	}
	return light_response;
}

void on_device_connect(ble_adapter *adapter, const char *dst, ble_connection *connection, int, void *user_data){
	std::cout << "device connected\n";
	g_mutex_unlock(&mtx_connect);

	connect_device_cnt++;

	// read data
	read_notify(connection, &m_argument.read_uuid, (void*)on_handle_characteristic_property_change);

	int ret;
	json root = json::object();
	root["requestName"] = "getId";
	std::string ble_setting = root.dump();
	ret = write_char_by_uuid(connection, &m_argument.write_uuid, ble_setting.c_str(), ble_setting.length());
	
	// write data
	int cur_machine_num = *(int*)user_data;
	std::unique_lock<std::mutex> lk(mtx_read);
	while(is_running && machine_id_response.find(dst) == machine_id_response.end()){
		cv_read.wait(lk);
	}
	if(!is_running){
		ble_disconnect(connection, false);
		return;
	}

	std::unordered_map<std::string, int>::iterator it_id = machine_id_response.find(dst);
	m_argument.machine_id[cur_machine_num] = it_id->second;
	machine_id_response.erase(it_id);
	cv_read.notify_all();

	int cur_id = m_argument.machine_id[cur_machine_num];
	RequestType cur_type = m_argument.machine_request_type[cur_machine_num];
	std::unordered_map<int, std::vector<std::string>> &cur_response = get_response_reference(cur_type);
	std::vector<std::string> &cur_data_list = cur_response.insert({cur_id, std::vector<std::string>()}).first->second;
	while(is_running){
		while(is_running && cur_data_list.empty()){
			cv_read.wait(lk);
		}
		if(!is_running){
			break;
		}

		std::string cur_data = cur_data_list.back();
		cur_data_list.pop_back();

		ret = write_char_by_uuid(connection, &m_argument.write_uuid, cur_data.c_str(), cur_data.length());
	}
	lk.unlock();

	ble_disconnect(connection, false);
	std::cout << "device disconnected\n";

	/*
	pthread_mutex_lock(&m_connection_terminated_lock);
	pthread_cond_signal(&m_connection_terminated);
	pthread_mutex_unlock(&m_connection_terminated_lock);
	*/
}

void wait_for_scan(ble_adapter *adapter){
	g_mutex_lock(&mtx_loop);
	if(adapter->ble_scan.is_scanning){
		g_cond_wait(&cond_loop, &mtx_loop);
	}
	g_mutex_unlock(&mtx_loop);
}

gint _compare_device_with_device_id(gconstpointer a, gconstpointer b){
	const ble_device *device = (ble_device*)a;
	const char *device_id = (char*)b;

	return g_ascii_strcasecmp(device->device_id, device_id);
}

int device_set_state(ble_adapter *adapter, const char *device_id, enum device_state new_state){
	enum device_state old_state;

	GSList *adapter_entry = g_slist_find(m_adapter_list, adapter);
	if(adapter_entry == NULL){
		std::cout << "device_set_state: Adapter not valid\n";
		return BLE_ERROR;
	}

	GSList *item = g_slist_find_custom(adapter->devices, device_id, _compare_device_with_device_id);
	if(item == NULL || item->data == NULL){
		old_state = NOT_FOUND;
	}
	else{
		ble_device *device = (ble_device*)item->data;
		if(device == NULL){
			old_state = NOT_FOUND;
		}
		else{
			old_state = device->state;
		}
	}
	
	if(old_state == NOT_FOUND){
		if(new_state != NOT_FOUND){
			ble_device *device = (ble_device*)calloc(sizeof(ble_device), 1);
			if(device == NULL){
				std::cout << "device_set_state: Cannot allocate device\n";
				return BLE_ERROR;
			}

			device->reference_counter = 1;
			device->adapter = adapter;
			device->device_id = g_strdup(device_id);
			device->state = new_state;
			device->connection.device = device;

			adapter->devices = g_slist_append(adapter->devices, device);
		}
	}
	else if(new_state == NOT_FOUND){
		item = g_slist_find_custom(adapter->devices, device_id, _compare_device_with_device_id);
		if(item == NULL){
			std::cout << "The device is not present. It is not expected\n";
			return BLE_ERROR;
		}

		ble_device *device = (ble_device*)item->data;

		if(device->state == DISCONNECTED){
			adapter->devices = g_slist_remove(adapter->devices, device);
			device->reference_counter--;
			if(device->reference_counter <= 0){
				free(device);
			}
		}
	}
	else{
		item = g_slist_find_custom(adapter->devices, device_id, _compare_device_with_device_id);
		ble_device *device;
		if(item == NULL){
			device = NULL;
		}
		else{
			device = (ble_device*)item->data;
		}
		device->state = new_state;
	}

	return BLE_SUCCESS;
}

discovered_device_args *_discovered_device_args_allocator(va_list args){
	ble_adapter *adapter = va_arg(args, ble_adapter*);
	OrgBluezDevice1 *device1 = va_arg(args, OrgBluezDevice1*);

	discovered_device_args *thread_args = (discovered_device_args*)calloc(sizeof(discovered_device_args), 1);
	thread_args->adapter = adapter;
	thread_args->mac_address = strdup(org_bluez_device1_get_address(device1));
	const char *device_name = org_bluez_device1_get_name(device1);
	if(device_name != NULL){
		thread_args->name = strdup(device_name);
	}
	else{
		thread_args->name = NULL;
	}

	return thread_args;
}

void *_discovered_device_thread(gpointer data){
	discovered_device_args *args = (discovered_device_args*)data;

	args->adapter->reference_counter++;

	args->adapter->discovered_device_callback.callback.discovered_device(
			args->adapter,
			args->mac_address,
			args->name,
			args->adapter->discovered_device_callback.user_data);

	args->adapter->reference_counter--;

	free(args->mac_address);
	if(args->name != NULL){
		free(args->name);
		args->name = NULL;
	}
	free(args);

	return NULL;
}

void on_discovered_device(ble_adapter *adapter, discovered_device_args* (*args_allocator)(va_list), ...){
	GError *error = NULL;

	va_list args;
	va_start(args, args_allocator);
	discovered_device_args *thread_args = args_allocator(args);
	va_end(args);

	adapter->discovered_device_callback.thread = g_thread_try_new("discovered_device", _discovered_device_thread, thread_args, &error);
	if(adapter->discovered_device_callback.thread == NULL){
		std::cout << "Failed to create thread 'discovered_device': " << error->message << '\n';
		g_error_free(error);
		return;
	}
}

void device_manager_on_added_device1_signal(const char *device1_path, ble_adapter *adapter){
	GError *error = NULL;
	OrgBluezDevice1 *device1 = org_bluez_device1_proxy_new_for_bus_sync(
			G_BUS_TYPE_SYSTEM,
			G_DBUS_PROXY_FLAGS_NONE,
			"org.bluez",
			device1_path,
			NULL,
			&error);
	if (error) {
		std::cout << "Failed to connection to new DBus Bluez Device: " << error->message << '\n';
		g_error_free(error);
	}

	if (device1) {
		const gchar *address = org_bluez_device1_get_address(device1);
		int ret;

		if (address == NULL) {
			g_object_unref(device1);
			return;
		}

		GSList *adapter_entry = g_slist_find(m_adapter_list, adapter);
		if (adapter_entry == NULL) {
			std::cout << "device_manager_on_added_device1_signal: Adapter not valid\n";
			g_object_unref(device1);
			return;
		}

		ret = device_set_state(adapter, device1_path, DISCONNECTED);
		if (ret == BLE_SUCCESS) {
			on_discovered_device(adapter, _discovered_device_args_allocator, adapter, device1);
		}

		g_object_unref(device1);
	}
}

void on_dbus_object_added(GDBusObjectManager *device_manager, GDBusObject *object, ble_adapter *user_data){
	const char *object_path = g_dbus_object_get_object_path(G_DBUS_OBJECT(object));

	GDBusInterface *interface = g_dbus_object_manager_get_interface(device_manager, object_path, "org.bluez.Device1");
	if(!interface){
//		std::cout << "DBUS: on_object_added: " << object_path << " (has 'org.bluez.Device1')\n";
		return;
	}

//	std::cout << "DBUS: on_object_added: " << object_path << " (has 'org.bluez.Device1')\n";

	device_manager_on_added_device1_signal(object_path, user_data);

	g_object_unref(interface);
}

void on_dbus_object_removed(GDBusObjectManager *device_manager, GDBusObject *object, ble_adapter *adapter){
	const char *object_path = g_dbus_object_get_object_path(G_DBUS_OBJECT(object));

	device_set_state(adapter, object_path, NOT_FOUND);
}

void on_interface_proxy_properties_changed(GDBusObjectManagerClient *device_manager, GDBusObjectProxy *object_proxy, GDBusProxy *interface_proxy, GVariant *changed_properties, const gchar *const *invalidated_properties, ble_adapter *adapter){
	const char *proxy_object_path = g_dbus_proxy_get_object_path(interface_proxy);

	size_t invalidated_properties_count = 0;
	if(invalidated_properties != NULL){
		const gchar *const *invalidated_properties_ptr = invalidated_properties;
		while(*invalidated_properties_ptr != NULL){
			invalidated_properties_count++;
			invalidated_properties_ptr++;
		}
	}

	if(strcmp(g_dbus_proxy_get_interface_name(interface_proxy), "org.bluez.Device1") == 0){
		GError *error = NULL;

		OrgBluezDevice1 *device1 = org_bluez_device1_proxy_new_for_bus_sync(
				G_BUS_TYPE_SYSTEM,
				G_DBUS_PROXY_FLAGS_NONE,
				"org.bluez",
				proxy_object_path, NULL, &error);
		if(error){
			std::cout << "Failed to connection to new DBus Bluez Device: " << error->message << '\n';
			g_error_free(error);
			return;
		}
		else if(device1 == NULL){
			std::cout << "Unexpected NULL device\n";
			return;
		}

		GVariantDict dict;
		g_variant_dict_init(&dict, changed_properties);
		GVariant *has_rssi = g_variant_dict_lookup_value(&dict, "RSSI", NULL);
		GVariant *has_manufacturer_data = g_variant_dict_lookup_value(&dict, "ManufacturerData", NULL);

		enum device_state old_device_state;
		GSList *item = g_slist_find_custom(adapter->devices, proxy_object_path, _compare_device_with_device_id);
		if(item == NULL){
			old_device_state = NOT_FOUND;
		}
		else{
			ble_device *device = (ble_device*)item->data;
			if(device == NULL){
				old_device_state = NOT_FOUND;
			}
			else{
				old_device_state = device->state;
			}
		}

		if(old_device_state == NOT_FOUND){
			if(has_rssi || has_manufacturer_data){
				int ret = device_set_state(adapter, proxy_object_path, DISCONNECTED);
				if(ret == BLE_SUCCESS){
					on_discovered_device(adapter, _discovered_device_args_allocator, adapter, device1);
				}
			}
		}

		g_variant_dict_end(&dict);
		g_object_unref(device1);
	}
}

int _adapter_scan_enable(ble_adapter *adapter, uuid_t **uuid_list, int16_t rssi_threshold, uint32_t enabled_filters, discovered_device_t discovered_device_cb, size_t timeout, void *user_data){
	GDBusObjectManager *device_manager;
	GError *error = NULL;
	GVariantBuilder arg_properties_builder;
	GVariant *rssi_variant = NULL;
	int ret;

	if((adapter == NULL) || (adapter->adapter_proxy == NULL)){
		std::cout << "Could not start BLE scan. No opened bluetooth adapter\n";
		return BLE_ERROR;
	}

	g_variant_builder_init(&arg_properties_builder, G_VARIANT_TYPE("a{sv}"));

	org_bluez_adapter1_call_set_discovery_filter_sync(adapter->adapter_proxy, g_variant_builder_end(&arg_properties_builder), NULL, &error);

	if(rssi_variant){
		g_variant_unref(rssi_variant);
	}

	if(error){
		std::cout << "Failed to set discovery filter: " << error->message << " (" << error->domain << "." << error->code << ")\n";
		g_error_free(error);
		return BLE_ERROR;
	}

	adapter->device_manager = g_dbus_object_manager_client_new_for_bus_sync(
			G_BUS_TYPE_SYSTEM,
			G_DBUS_OBJECT_MANAGER_CLIENT_FLAGS_NONE,
			"org.bluez",
			"/",
			NULL, NULL, NULL, NULL,
			&error);
	device_manager = adapter->device_manager;

	if(device_manager == NULL){
		if(error != NULL){
			g_error_free(error);
		}
		return BLE_ERROR;
	}

	memset(&adapter->ble_scan, 0, sizeof(adapter->ble_scan));
	adapter->ble_scan.enabled_filters = enabled_filters;
	adapter->ble_scan.ble_scan_timeout = timeout;
	adapter->discovered_device_callback.callback.discovered_device = discovered_device_cb;
	adapter->discovered_device_callback.user_data = user_data;

	adapter->ble_scan.added_signal_id = g_signal_connect(G_DBUS_OBJECT_MANAGER(device_manager),
	                    "object-added",
	                    G_CALLBACK(on_dbus_object_added),
	                    adapter);

	adapter->ble_scan.removed_signal_id = g_signal_connect(G_DBUS_OBJECT_MANAGER(device_manager),
	                    "object-removed",
	                    G_CALLBACK(on_dbus_object_removed),
	                    adapter);
	
	adapter->ble_scan.changed_signal_id = g_signal_connect(G_DBUS_OBJECT_MANAGER(device_manager),
					     "interface-proxy-properties-changed",
					     G_CALLBACK(on_interface_proxy_properties_changed),
					     adapter);

	org_bluez_adapter1_call_start_discovery_sync(adapter->adapter_proxy, NULL, &error);
	if(error){
		std::cout << "Failed to start discovery: " << error->message << '\n';
		g_error_free(error);
		return BLE_ERROR;
	}

	return BLE_SUCCESS;
}

gboolean _stop_scan_on_timeout(gpointer user_data){
	ble_adapter *adapter = (ble_adapter*)user_data;

	g_mutex_lock(&mtx_loop);

	if(adapter->ble_scan.is_scanning){
		adapter->ble_scan.is_scanning = false;
		g_cond_broadcast(&cond_loop);
	}
	adapter->ble_scan.ble_scan_timeout_id = 0;

	g_mutex_unlock(&mtx_loop);

	return FALSE;
}

int adapter_scan_disable(ble_adapter *adapter){
	GError *error = NULL;
	int ret = BLE_SUCCESS;

	g_mutex_lock(&mtx_loop);

	if(!org_bluez_adapter1_get_discovering(adapter->adapter_proxy)){
		std::cout << "No discovery is progress. We skip discovery stopping 1\n";
		ret = BLE_ERROR;
		goto EXIT;
	}
	else if(!adapter->ble_scan.is_scanning){
//		std::cout << "No discovery is progress. We skip discovery stopping 2\n";
		ret = BLE_ERROR;
		goto EXIT;
	}

	org_bluez_adapter1_call_stop_discovery_sync(adapter->adapter_proxy, NULL, &error);
	if(error != NULL){
		if(((error->domain == 238) || (error->domain == 239)) && (error->code == 36)){
			std::cout << "No bluetooth scan has been started\n";
			goto EXIT;
		}
		else{
			std::cout << "Error while stopping BLE discovery: " << error->message << " (" << error->domain << "," << error->code << ")\n";
		}
	}

	if(adapter->ble_scan.is_scanning){
		adapter->ble_scan.is_scanning = false;
		g_cond_broadcast(&cond_loop);
	}

	if(adapter->ble_scan.ble_scan_timeout_id){
		g_source_remove(adapter->ble_scan.ble_scan_timeout_id);
		adapter->ble_scan.ble_scan_timeout_id = 0;
	}

EXIT:
	g_mutex_unlock(&mtx_loop);
	return ret;
}

void *_ble_scan_loop_thread(gpointer user_data){
	ble_adapter *adapter = (ble_adapter*)user_data;

	if(adapter->ble_scan.ble_scan_timeout_id > 0){
		return NULL;
	}

	if(adapter->ble_scan.ble_scan_timeout > 0){
		adapter->ble_scan.ble_scan_timeout_id = g_timeout_add_seconds(adapter->ble_scan.ble_scan_timeout, _stop_scan_on_timeout, adapter);
	}

	wait_for_scan(adapter);

/*
	g_signal_handler_disconnect(G_DBUS_OBJECT_MANAGER(adapter->device_manager), adapter->ble_scan.added_signal_id);
	g_signal_handler_disconnect(G_DBUS_OBJECT_MANAGER(adapter->device_manager), adapter->ble_scan.removed_signal_id);
	g_signal_handler_disconnect(G_DBUS_OBJECT_MANAGER(adapter->device_manager), adapter->ble_scan.changed_signal_id);
*/

	adapter_scan_disable(adapter);

	return NULL;
}

int adapter_scan_enable(ble_adapter *adapter, uuid_t **uuid_list, int16_t rssi_threshold, uint32_t enabled_filters, discovered_device_t discovered_device_cb, size_t timeout, void *user_data){
	GError *error = NULL;
	int ret;

	GSList *adapter_entry = g_slist_find(m_adapter_list, adapter);
	if(adapter_entry == NULL){
		std::cout << "Adapter not valid\n";
		return BLE_ERROR;
	}

	ret = _adapter_scan_enable(adapter, uuid_list, rssi_threshold, enabled_filters, discovered_device_cb, timeout, user_data);
	if(ret != BLE_SUCCESS){
		return BLE_ERROR;
	}

	adapter->ble_scan.is_scanning = true;
	
	adapter->ble_scan.scan_loop_thread = g_thread_try_new("ble_scan", _ble_scan_loop_thread, adapter, &error);
	if(adapter->ble_scan.scan_loop_thread == NULL){
		std::cout << "Failed to create BLE scan thread: " << error->message << '\n';
		g_error_free(error);
		return BLE_ERROR;
	}

	wait_for_scan(adapter);

	g_thread_unref(adapter->ble_scan.scan_loop_thread);
	adapter->ble_scan.scan_loop_thread = NULL;

	return BLE_SUCCESS;
}

void end_notification(void *notified_characteristic){

}

void on_disconnected_device(ble_connection *connection){
	if((&connection->on_disconnection != NULL) && (connection->on_disconnection.callback.callback != NULL)){
		connection->on_disconnection.callback.disconnection_handler(connection, connection->on_disconnection.user_data);
	}

	char *device_id = connection->device->device_id;

	if(connection->on_handle_device_property_change_id != 0){
		g_signal_handler_disconnect(connection->bluez_device, connection->on_handle_device_property_change_id);
		connection->on_handle_device_property_change_id = 0;
	}

	if(connection->connection_timeout_id){
		g_source_remove(connection->connection_timeout_id);
		connection->connection_timeout_id = 0;
	}

	if(connection->device_object_path != NULL){
		free(connection->device_object_path);
		connection->device_object_path = NULL;
	}

	g_list_free_full(connection->dbus_objects, g_object_unref);

	g_list_free_full((GList*)g_steal_pointer(&connection->notified_characteristics), end_notification);

	device_set_state(connection->device->adapter, device_id, DISCONNECTED);
}

ble_connection *_connected_device_args_allocator(va_list args){
	return (ble_connection*)va_arg(args, ble_connection*);
}

gpointer _connected_device_thread(gpointer data){
	ble_connection *connection = (ble_connection*)data;
	const gchar *device_mac_address = org_bluez_device1_get_address(connection->bluez_device);

	connection->on_connection.callback.connection_handler(
			connection->device->adapter,
			device_mac_address,
			connection, 0,
			connection->on_connection.user_data);

	return NULL;
}

void on_connected_device(ble_handler *handler, ble_connection* (*args_allocator)(va_list), ...){
	GError *error = NULL;

	va_list args;
	va_start(args, args_allocator);
	ble_connection *thread_args = args_allocator(args);
	va_end(args);

	handler->thread = g_thread_try_new("connected_device", _connected_device_thread, thread_args, &error);
	if(handler->thread == NULL){
		std::cout << "Failed to create thread 'connected_device': " << error->message << '\n';
		g_error_free(error);
		return;
	}
}

void _on_device_connect(ble_connection *connection){
	GDBusObjectManager *device_manager;
	GError *error = NULL;
	
	if(connection->connection_timeout_id){
		g_source_remove(connection->connection_timeout_id);
		connection->connection_timeout_id = 0;
	}

	connection->device->adapter->device_manager = g_dbus_object_manager_client_new_for_bus_sync(
			G_BUS_TYPE_SYSTEM,
			G_DBUS_OBJECT_MANAGER_CLIENT_FLAGS_NONE,
			"org.bluez",
			"/",
			NULL, NULL, NULL, NULL,
			&error);
	device_manager = connection->device->adapter->device_manager;

	if(device_manager == NULL){
		if(error != NULL){
			g_error_free(error);
		}
		return;
	}
	
	connection->dbus_objects = g_dbus_object_manager_get_objects(device_manager);

	device_set_state(connection->device->adapter, connection->device->device_id, CONNECTED);

	on_connected_device(&connection->on_connection, _connected_device_args_allocator, connection);
}

gboolean on_handle_device_property_change(GDBusProxy *proxy, GVariant *arg_changed_properties, const gchar *const *arg_invalidated_properties, ble_connection *connection){
	if (g_variant_n_children(arg_changed_properties) > 0) {
		const gchar *device_object_path = g_dbus_proxy_get_object_path(proxy);
		GVariantIter *iter;
		const gchar *key;
		GVariant *value;

		g_variant_get(arg_changed_properties, "a{sv}", &iter);
		while(g_variant_iter_loop(iter, "{&sv}", &key, &value)){
			if(strcmp(key, "Connected") == 0 && !g_variant_get_boolean(value)){
				std::cout << "DBUS: device_property_change(" << device_object_path << "): Disconnection\n";
			}
			else{
				std::cout << "DBUS property change : " << device_object_path << " : " << key << '\n';
			}
			if(strcmp(key, "Connected") == 0){
				if(!g_variant_get_boolean(value)){
//					std::cout << "DBUS: device_property_change(" << device_object_path << "): Disconnection\n";
					on_disconnected_device(connection);

					mtx_interrupt.lock();
					connect_device_cnt--;
					if(connect_device_cnt <= 0){
						cv_interrupt.notify_all();
					}
					mtx_interrupt.unlock();
				}
				else{
//					std::cout << "DBUS: device_property_change(" << device_object_path << "): Connection\n";
				}
			}
			else if(strcmp(key, "ServicesResolved") == 0){
				if(g_variant_get_boolean(value)){
//					std::cout << "DBUS: device_property_change(" << device_object_path << "): Service Resolved\n";
					_on_device_connect(connection);
				}
			}
		}
		g_variant_iter_free(iter);
	}

	return TRUE;
}

gboolean _stop_connect_on_timeout(gpointer data){
	ble_connection *connection = (ble_connection*)data;

	connection->connection_timeout_id = 0;

	return FALSE;
}

int ble_connect(ble_adapter *adapter, const char *dst, unsigned long options, connect_cb_t connect_cb, void *user_data){
	const char *adapter_name = NULL;
	GError *error = NULL;
	char object_path[200];
	int ret = BLE_SUCCESS;
	char device_address_str[20 + 1];

	if(adapter == NULL){
		int ret_val = adapter_open(NULL, &adapter);
		if(ret_val == BLE_ERROR || adapter == NULL){
			return BLE_ERROR;
		}
	}
	else{
		adapter_name = adapter->name;
	}

	if(connect_cb == NULL){
		std::cout << "ble_connect: Missing connection callback\n";
		return BLE_ERROR;
	}

	if(!adapter_name){
		adapter_name = DEFAULT_ADAPTER;
	}

	strncpy(device_address_str, dst, sizeof(device_address_str) - 1);
	for(size_t i = 0; i < strlen(device_address_str); i++){
		if(device_address_str[i] == ':'){
			device_address_str[i] = '_';
		}
	}

	device_address_str[20] = '\0';

	snprintf(object_path, sizeof(object_path), "/org/bluez/%s/dev_%s", adapter_name, device_address_str);

	ble_device *device;
	GSList *item = g_slist_find_custom(adapter->devices, object_path, _compare_device_with_device_id);
	if(item == NULL){
		return BLE_ERROR;
	}
	else{
		device = (ble_device*)item->data;
		if(device == NULL){
			return BLE_ERROR;
		}
		else if(device->state != DISCONNECTED){
			std::cout << "ble_connect: Cannot connect to " << dst << '\n';
			return BLE_ERROR;
		}
	}

	device->connection.on_connection.callback.connection_handler = connect_cb;
	device->connection.on_connection.user_data = user_data;

	device_set_state(device->adapter, device->device_id, CONNECTING);

	OrgBluezDevice1 *bluez_device = org_bluez_device1_proxy_new_for_bus_sync(
			G_BUS_TYPE_SYSTEM,
			G_DBUS_PROXY_FLAGS_NONE,
			"org.bluez",
			object_path,
			NULL,
			&error);
	if(bluez_device == NULL){
		std::cout << "Failed to connect to DBus Device";
		if(error){
			std::cout << ": " << error->message;
			g_error_free(error);
		}
		std::cout << '\n';
		return BLE_ERROR;
	}
	else{
		device->connection.bluez_device = bluez_device;
		device->connection.device_object_path = strdup(object_path);
	}

	device->connection.on_handle_device_property_change_id = g_signal_connect(
			bluez_device,
			"g-properties-changed",
			G_CALLBACK(on_handle_device_property_change),
			&device->connection);

	error = NULL;
	org_bluez_device1_call_connect_sync(bluez_device, NULL, &error);
	if(error){
		g_mutex_unlock(&mtx_connect);
		const char *m_dbus_error_unknown_object = "GDBus.Error:org.freedesktop.DBus.Error.UnknownObject";
		if(strncmp(error->message, m_dbus_error_unknown_object, strlen(m_dbus_error_unknown_object)) == 0){
			std::cout << "Device '" << dst << "' cannot be found (" << error->domain << ", " << error->code << ")\n";
		}
		else if((error->domain == 238) && (error->code == 60952)){
			std::cout << "Device '" << dst << "': " << error->message << '\n';
		}
		else{
			std::cout << "Device connected error (device:" << device->connection.device_object_path << "): " << error->message << '\n';
		}
		
		g_error_free(error);

		device_set_state(adapter, device->device_id, DISCONNECTED);

		ret = BLE_ERROR;
	}
	else{
		device->connection.connection_timeout_id = g_timeout_add_seconds(10, _stop_connect_on_timeout, &device->connection);
		return BLE_SUCCESS;
	}

	free(device->connection.device_object_path);
	device->connection.device_object_path = NULL;

//	adapter_close(adapter);

//	connect_cb(adapter, dst, NULL, BLE_ERROR, user_data);

	return ret;
}

int ble_disconnect(ble_connection *connection, bool wait_disconnection){
	GError *error = NULL;
	int ret = BLE_SUCCESS;

	if(connection == NULL){
		std::cout << "Cannot disconnect - connection parameter is not valid\n";
		return BLE_ERROR;
	}

	org_bluez_device1_call_disconnect_sync(connection->bluez_device, NULL, &error);
	if(error){
		std::cout << "Failed to disconnect DBus Bluez Device: " << error->message << '\n';
		g_error_free(error);
	}

	device_set_state(connection->device->adapter, connection->device->device_id, DISCONNECTING);

	return ret;
}

int uuid_to_uuid128(const uuid_t *uuid, uuid_t *long_uuid){
	if(uuid->type == SDP_UUID128){
		memcpy(long_uuid, uuid, sizeof(uuid_t));
		return 0;
	}

	long uuid_type = SDP_UUID128;
	long_uuid->value.uuid128.data[0] = 0xEF;
	long_uuid->value.uuid128.data[1] = 0x68;
	long_uuid->value.uuid128.data[2] = 0x00;
	long_uuid->value.uuid128.data[3] = 0x00;
	long_uuid->value.uuid128.data[4] = 0x9B;
	long_uuid->value.uuid128.data[5] = 0x35;
	long_uuid->value.uuid128.data[6] = 0x49;
	long_uuid->value.uuid128.data[7] = 0x33;
	long_uuid->value.uuid128.data[8] = 0x9B;
	long_uuid->value.uuid128.data[9] = 0x10;
	long_uuid->value.uuid128.data[10] = 0x52;
	long_uuid->value.uuid128.data[11] = 0xFF;
	long_uuid->value.uuid128.data[12] = 0xA9;
	long_uuid->value.uuid128.data[13] = 0x74;
	long_uuid->value.uuid128.data[14] = 0x00;
	long_uuid->value.uuid128.data[15] = 0x42;

	if(uuid->type == SDP_UUID32){
		long_uuid->value.uuid128.data[0] = (uuid->value.uuid32 >> 24) & 0xFF;
		long_uuid->value.uuid128.data[1] = (uuid->value.uuid32 >> 16) & 0xFF;
		long_uuid->value.uuid128.data[2] = (uuid->value.uuid32 >> 8) & 0xFF;
		long_uuid->value.uuid128.data[3] = uuid->value.uuid32 & 0xFF;
	}
	else if(uuid->type == SDP_UUID16){
		long_uuid->value.uuid128.data[2] = uuid->value.uuid16 >> 8;
		long_uuid->value.uuid128.data[3] = uuid->value.uuid16 & 0xFF;
	}

	return 0;
}

int uuid_cmp(const uuid_t *uuid1, const uuid_t *uuid2){
	if(uuid1->type != uuid2->type){
		uuid_t uuid128_1, uuid128_2;
		uuid_to_uuid128(uuid1, &uuid128_1);
		uuid_to_uuid128(uuid2, &uuid128_2);

		if(memcmp(&uuid128_1.value.uuid128, &uuid128_2.value.uuid128, sizeof(uuid1->value.uuid128)) == 0){
			return 0;
		}
		else{
			return 2;
		}
	}
	else if(uuid1->type == SDP_UUID16){
		if(uuid1->value.uuid16 == uuid2->value.uuid16){
			return 0;
		}
		else{
			return 2;
		}
	}
	else if(uuid1->type == SDP_UUID32){
		if(uuid1->value.uuid32 == uuid2->value.uuid32){
			return 0;
		}
		else{
			return 2;
		}
	}
	else if(uuid1->type == SDP_UUID128){
		if(memcmp(&uuid1->value.uuid128, &uuid2->value.uuid128, sizeof(uuid1->value.uuid128)) == 0){
			return 0;
		}
		else{
			return 2;
		}
	}
	else{
		return 3;
	}
}

bool handle_dbus_gattcharacteristic_from_path(ble_connection *connection, const uuid_t *uuid, dbus_characteristic *d_characteristic, const char *object_path, GError **error){
	OrgBluezGattCharacteristic1 *characteristic = NULL;

	*error = NULL;
	characteristic = org_bluez_gatt_characteristic1_proxy_new_for_bus_sync(
			G_BUS_TYPE_SYSTEM,
			G_DBUS_PROXY_FLAGS_NONE,
			"org.bluez",
			object_path,
			NULL,
			error);
	if(characteristic){
		if(uuid != NULL){
			uuid_t characteristic_uuid;
			const gchar *characteristic_uuid_str = org_bluez_gatt_characteristic1_get_uuid(characteristic);
			
			if(characteristic_uuid_str == NULL){
				std::cout << "Error: " << object_path << " path unexpectly returns a NULL UUID\n";
				g_object_unref(characteristic);
				return false;
			}

			string_to_uuid(characteristic_uuid_str, strlen(characteristic_uuid_str) + 1, &characteristic_uuid);
			
			if(uuid_cmp(uuid, &characteristic_uuid) != 0){
				g_object_unref(characteristic);
				return false;
			}
		}

		*error = NULL;
		OrgBluezGattService1 *service = org_bluez_gatt_service1_proxy_new_for_bus_sync(
				G_BUS_TYPE_SYSTEM,
				G_DBUS_PROXY_FLAGS_NONE,
				"org.bluez",
				org_bluez_gatt_characteristic1_get_service(characteristic),
				NULL,
				error);

		if(service){
			const bool found = !strcmp(connection->device_object_path, org_bluez_gatt_service1_get_device(service));

			g_object_unref(service);

			if(found){
				d_characteristic->gatt = characteristic;
				d_characteristic->type = TYPE_GATT;
				return true;
			}
		}

		g_object_unref(characteristic);
	}

	return false;
}

bool handle_dbus_battery_from_uuid(ble_connection *connection, const uuid_t *uuid, dbus_characteristic *d_characteristic, const char *object_path, GError **error){
	OrgBluezBattery1 *battery = NULL;
	
	*error = NULL;
	battery = org_bluez_battery1_proxy_new_for_bus_sync(
			G_BUS_TYPE_SYSTEM,
			G_DBUS_PROXY_FLAGS_NONE,
			"org.bluez",
			object_path,
			NULL,
			error);
	if(battery){
		d_characteristic->battery = battery;
		d_characteristic->type = TYPE_BATTERY_LEVEL;
	}

	return false;
}

dbus_characteristic get_characteristic_from_uuid(ble_connection *connection, const uuid_t *uuid){
	GError *error = NULL;
	GDBusObjectManager *device_manager;
	bool is_battery_level_uuid = false;
	dbus_characteristic d_characteristic = {
		.type = TYPE_NONE
	};

	connection->device->adapter->device_manager = g_dbus_object_manager_client_new_for_bus_sync(
			G_BUS_TYPE_SYSTEM,
			G_DBUS_OBJECT_MANAGER_CLIENT_FLAGS_NONE,
			"org.bluez",
			"/",
			NULL, NULL, NULL, NULL,
			&error);
	device_manager = connection->device->adapter->device_manager;
	
	if(device_manager == NULL){
		if(error != NULL){
			g_error_free(error);
		}
		return d_characteristic;
	}

	if(uuid_cmp(uuid, &m_battery_level_uuid) == 0){
		is_battery_level_uuid = true;
	}
	else if(uuid_cmp(uuid, &m_ccc_uuid) == 0){
		std::cout << "Error: Bluez v5.42+ does not expose Client Characteristic Configuration Descriptor through DBUS interface\n";
		return d_characteristic;
	}

	GList *l;
	for(l = connection->dbus_objects; l != NULL; l = l->next){
		GDBusInterface *interface;
		bool found = false;
		GDBusObject *object = (GDBusObject*)l->data;
		const char *object_path = g_dbus_object_get_object_path(G_DBUS_OBJECT(object));

		interface = g_dbus_object_manager_get_interface(device_manager, object_path, "org.bluez.GattCharacteristic1");
		if(interface){
			g_object_unref(interface);

			found = handle_dbus_gattcharacteristic_from_path(connection, uuid, &d_characteristic, object_path, &error);
			if(found){
				break;
			}
		}

		if(!found && is_battery_level_uuid){
			interface = g_dbus_object_manager_get_interface(device_manager, object_path, "org.bluez.Battery1");
			if(interface){
				g_object_unref(interface);

				found = handle_dbus_battery_from_uuid(connection, uuid, &d_characteristic, object_path, &error);
				if(found){
					break;
				}
			}
		}
	}

	return d_characteristic;
}

int read_gatt_characteristic(dbus_characteristic *d_characteristic, void **buffer, size_t *buffer_len){
	GVariant *out_value;
	GError *error = NULL;
	int ret = BLE_SUCCESS;

	GVariantBuilder *options = g_variant_builder_new(G_VARIANT_TYPE("a{sv}"));
	org_bluez_gatt_characteristic1_call_read_value_sync(
			d_characteristic->gatt,
			g_variant_builder_end(options),
			&out_value, NULL, &error);
	g_variant_builder_unref(options);

	if(error != NULL){
		std::cout << "Failed to read DBus GATT characteristic: " << error->message << '\n';
		g_error_free(error);
		return BLE_ERROR;
	}

	gsize n_elements = 0;
	gconstpointer const_buffer = g_variant_get_fixed_array(out_value, &n_elements, sizeof(guchar));
	if(const_buffer){
		*buffer = malloc(n_elements);
		if(*buffer == NULL){
			ret = BLE_ERROR;
		}
		else{
			*buffer_len = n_elements;
			memcpy(*buffer, const_buffer, n_elements);
		}
	}
	else{
		*buffer_len = 0;
	}
	
	g_variant_unref(out_value);
	return ret;
}

int read_char_by_uuid(ble_connection *connection, uuid_t *uuid, void **buffer, size_t *buffer_len){
	dbus_characteristic d_characteristic = get_characteristic_from_uuid(connection, uuid);
	if(d_characteristic.type == TYPE_NONE){
		return BLE_ERROR;
	}
	else if(d_characteristic.type == TYPE_BATTERY_LEVEL){
		guchar percentage = org_bluez_battery1_get_percentage(d_characteristic.battery);

		*buffer = malloc(sizeof(uint8_t));
		if(buffer == NULL){
			buffer_len = 0;
			return BLE_ERROR;
		}

		memcpy(*buffer, &percentage, sizeof(uint8_t));
		*buffer_len = sizeof(uint8_t);

		g_object_unref(d_characteristic.battery);

		return BLE_SUCCESS;
	}
	else{
		int ret;

		assert(d_characteristic.type == TYPE_GATT);

		ret = read_gatt_characteristic(&d_characteristic, buffer, buffer_len);

		g_object_unref(d_characteristic.gatt);

		return ret;
	}
}

int write_char(dbus_characteristic *d_characteristic, const void *buffer, size_t buffer_len, uint32_t options){
	GVariant *value = g_variant_new_from_data(G_VARIANT_TYPE("ay"), buffer, buffer_len, TRUE, NULL, NULL);
	GError *error = NULL;
	int ret = BLE_SUCCESS;

	GVariantBuilder *variant_options = g_variant_builder_new(G_VARIANT_TYPE("a{sv}"));

	if((options & 0x7) == 0x2){
		g_variant_builder_add(variant_options, "{sv}", "type", g_variant_new("s", "command"));
	}

	org_bluez_gatt_characteristic1_call_write_value_sync(d_characteristic->gatt, value, g_variant_builder_end(variant_options), NULL, &error);
	g_variant_builder_unref(variant_options);

	if(error != NULL){
		if((error->domain != 238) || (error->code != 36)){
			std::cout << "Failed to write DBus GATT characteristic: " << error->message << " (" << error->domain << "," << error->code << ")\n";
		}
		g_error_free(error);
		return BLE_ERROR;
	}

	return ret;
}

int write_char_by_uuid(ble_connection *connection, uuid_t *uuid, const void *buffer, size_t buffer_len){
	int ret;

	dbus_characteristic d_characteristic = get_characteristic_from_uuid(connection, uuid);
	if(d_characteristic.type == TYPE_NONE){
		return BLE_ERROR;
	}
	if(d_characteristic.type == TYPE_BATTERY_LEVEL){
		return BLE_ERROR;
	}
	else{
		assert(d_characteristic.type == TYPE_GATT);
	}

	ret = write_char(&d_characteristic, buffer, buffer_len, 1);

	g_object_unref(d_characteristic.gatt);

	return ret;
}

gboolean on_handle_characteristic_property_change(OrgBluezGattCharacteristic1 *object, GVariant *arg_changed_properties, const gchar *const *arg_invalidated_properties, gpointer user_data){
	ble_connection *connection = (ble_connection*)user_data;
	
	GVariantDict dict;
	g_variant_dict_init(&dict, arg_changed_properties);

	GVariant *value = g_variant_dict_lookup_value(&dict, "Value", NULL);
	if(value != NULL){
		size_t len;
		const uint8_t *buffer = (const uint8_t*)g_variant_get_fixed_array(value, &len, sizeof(guchar));
		uint8_t json_buf[1024] = {0,};
		memcpy(json_buf, buffer, len);
		json root = json::parse(json_buf);

		int cur_machine_num = *(int*)connection->on_connection.user_data;
		std::string cur_request = root["requestName"].get<std::string>();
		struct Request new_request;
		if(cur_request.compare("getId") == 0){
			std::string type = root["machineType"].get<std::string>();
			if(type.compare("light") == 0){
				m_argument.machine_request_type[cur_machine_num] = REQUEST_LIGHT;
			}
			else if(type.compare("lamp") == 0){
				m_argument.machine_request_type[cur_machine_num] = REQUEST_LAMP;
			}
			else if(type.compare("window") == 0){
				m_argument.machine_request_type[cur_machine_num] = REQUEST_WINDOW;
			}
			else if(type.compare("curtain") == 0){
				m_argument.machine_request_type[cur_machine_num] = REQUEST_CURTAIN;
			}
			root["macAddress"] = m_argument.mac_address[cur_machine_num];

			new_request.request_type = REQUEST_ID;
		}
		else{
			new_request.request_type = m_argument.machine_request_type[cur_machine_num];
			new_request.id = m_argument.machine_id[cur_machine_num];
		}
		new_request.request_data = root;

		mtx_write.lock();
		request_list.push(new_request);
		cv_write.notify_all();
		mtx_write.unlock();

		g_variant_unref(value);
	}
	g_variant_dict_end(&dict);

	return TRUE;
}

void read_notify(ble_connection *connection, const uuid_t *uuid, void *callback){
	int ret = BLE_SUCCESS;

	assert(callback != NULL);

	dbus_characteristic d_characteristic = get_characteristic_from_uuid(connection, uuid);

	gulong signal_id = g_signal_connect(
			d_characteristic.gatt,
			"g-properties-changed",
			G_CALLBACK(callback),
			connection);
	if(signal_id == 0){
		std::cout << "Failed to connect signal to DBus GATT notification\n";
		ret = BLE_ERROR;
		return;
	}

	GError *error = NULL;
	org_bluez_gatt_characteristic1_call_start_notify_sync(d_characteristic.gatt, NULL, &error);
	if(error){
		std::cout << "Failed to start DBus GATT notification: " << error->message << '\n';
		g_error_free(error);
	}
}
