#pragma once

#include <iostream>
#include <glib-2.0/glib.h>
#include <bluetooth/bluetooth.h>
#include <bluetooth/sdp.h>
#include <glib.h>
#include <pthread.h>
#include <cassert>
#include <string>

#include "uuid.h"
#include "org-bluez-adapter1.h"
#include "org-bluez-battery1.h"
#include "org-bluez-device1.h"
#include "org-bluez-gattcharacteristic1.h"
#include "org-bluez-gattservice1.h"

#include "common.hpp"
#include "json.hpp"

using json = nlohmann::json;

#define DEFAULT_ADAPTER "hci0"
#define BLE_SUCCESS 0
#define BLE_ERROR 1

#define READ_UUID "6e400003-b5a3-f393-e0a9-e50e24dcca9e"
#define WRITE_UUID "6e400002-b5a3-f393-e0a9-e50e24dcca9e"

#define MACHINE_NUM 4

typedef struct _ble_handler ble_handler;
typedef struct _ble_adapter ble_adapter;
typedef struct _ble_connection ble_connection;
typedef struct _ble_device ble_device;
typedef struct _discovered_device_args discovered_device_args;
typedef struct _dbus_characteristic dbus_characteristic;

typedef void (*discovered_device_t)(ble_adapter*, const char*, const char*, void*);
typedef void (*connect_cb_t)(ble_adapter*, const char*, ble_connection*, int, void*);
typedef void (*event_handler_t)(const uuid_t*, const uint8_t*, size_t, void*);
typedef void (*disconnection_handler_t)(ble_connection*, void*);

struct _execute_task_arg{
	void* (*task)(void* arg);
	void *arg;
};

struct _ble_handler {
	union {
		discovered_device_t discovered_device;
		connect_cb_t connection_handler;
		event_handler_t notification_handler;
		disconnection_handler_t disconnection_handler;
		void (*callback)(void);
	} callback;

	void *user_data;
	GThread *thread;
	GThreadPool *thread_pool;
};

enum device_state {
	NOT_FOUND = 0,
	CONNECTING,
	CONNECTED,
	DISCONNECTING,
	DISCONNECTED
};

struct _ble_adapter {
	char *id;
	char *name;
	uintptr_t reference_counter;
	GSList *devices;

	GDBusObjectManager *device_manager;
	OrgBluezAdapter1 *adapter_proxy;
	struct {
		int added_signal_id;
		int changed_signal_id;
		int removed_signal_id;

		size_t ble_scan_timeout;
		guint ble_scan_timeout_id;

		GThread *scan_loop_thread;
		bool is_scanning;
		uint32_t enabled_filters;
	} ble_scan;

	ble_handler discovered_device_callback;
};

struct _ble_connection {
	ble_device *device;

	char *device_object_path;
	OrgBluezDevice1 *bluez_device;
	guint connection_timeout_id;
	guint on_handle_device_property_change_id;
	GList *dbus_objects;
	GList *notified_characteristics;

	ble_handler on_connection;
	ble_handler notification;
	ble_handler on_disconnection;
};

struct _ble_device {
	ble_adapter *adapter;
	char *device_id;
	uintptr_t reference_counter;
	enum device_state state;
	ble_connection connection;
};

struct _discovered_device_args {
	ble_adapter *adapter;
	char *mac_address;
	char *name;
	OrgBluezDevice1 *device1;
};

enum _dbus_characteristic_type {
	TYPE_NONE = 0,
	TYPE_GATT,
	TYPE_BATTERY_LEVEL
};

struct _dbus_characteristic {
	union {
		OrgBluezGattCharacteristic1 *gatt;
		OrgBluezBattery1 *battery;
	};
	enum _dbus_characteristic_type type;
};

void stop_ble();
int set_ble(int, char**);

int string_to_uuid(const char*, size_t, uuid_t*);
void *_execute_task(void*);
void *ble_task(void*);

int adapter_open(const char*, ble_adapter**);

int stricmp(char const*, char const*);
void ble_discovered_device(ble_adapter*, const char*, const char*, void*);
std::unordered_map<int, std::vector<std::string>> &get_response_reference(RequestType);
void on_device_connect(ble_adapter*, const char*, ble_connection*, int, void*);

void wait_for_scan(ble_adapter*);
gint _compare_device_with_device_id(gconstpointer, gconstpointer);
int device_set_state(ble_adapter*, const char*, enum device_state);
discovered_device_args *_discovered_device_args_allocator(va_list);
void *_discovered_device_thread(gpointer);
void on_discovered_device(ble_adapter*, discovered_device_args*, ...);
void device_manager_on_added_device1_signal(const char*, ble_adapter*);
void on_dbus_object_added(GDBusObjectManager*, GDBusObject*, ble_adapter*);
void on_dbus_object_removed(GDBusObjectManager*, GDBusObject*, ble_adapter*);
int _adapter_scan_enable(ble_adapter*, uuid_t**, int16_t, uint32_t, discovered_device_t, size_t, void*);
int _stop_scan_on_timeout(gpointer);
gboolean adapter_scan_disable(ble_adapter*);
void *_ble_scan_loop_thread(gpointer);
int adapter_scan_enable(ble_adapter*, uuid_t**, int16_t, uint32_t, discovered_device_t, size_t, void*);

void end_notification(void*);
void on_disconnected_device(ble_connection *connection);
ble_connection *_connected_device_args_allocator(va_list);
gpointer _connected_device_thread(gpointer);
void on_connected_device(ble_handler*, ble_connection*, ...);
void _on_device_connect(ble_connection*);
gboolean on_handle_device_property_change(GDBusProxy*, GVariant*, const gchar *const*, ble_connection*);
gboolean _stop_connect_on_timeout(gpointer);
int ble_connect(ble_adapter*, const char*, unsigned long, connect_cb_t, void*);

int ble_disconnect(ble_connection*, bool);

int uuid_to_uuid128(const uuid_t*, uuid_t*);
int uuid_cmp(const uuid_t*, const uuid_t*);
bool handle_dbus_gattcharacteristic_from_path(ble_connection*, const uuid_t*, dbus_characteristic*, const char*, GError**);
bool handle_dbus_battery_from_uuid(ble_connection*, const uuid_t*, dbus_characteristic*, const char*, GError**);
dbus_characteristic get_characteristic_from_uuid(ble_connection*, const uuid_t*);
int read_gatt_characteristic(dbus_characteristic*, void**, size_t*);
int read_char_by_uuid(ble_connection*, uuid_t*, void**, size_t*);

int write_char(dbus_characteristic*, const void*, size_t, uint32_t);
int write_char_by_uuid(ble_connection*, uuid_t*, const void*, size_t);

gboolean on_handle_characteristic_property_change(OrgBluezGattCharacteristic1*, GVariant*, const gchar *const*, gpointer);
void read_notify(ble_connection*, const uuid_t*, void*);
