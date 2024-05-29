#include "socket.hpp"

int serv_sock;
std::thread recv_thread, send_thread;
bool socket_status = false;

// connect socket to server
void set_socket(){
	while(true){
		// socket create
		serv_sock = socket(PF_INET, SOCK_STREAM, 0);
		if(serv_sock == -1){
			error_handling("socket() error");
		}

		struct sockaddr_in serv_addr;
		memset(&serv_addr, 0, sizeof(serv_addr));
		serv_addr.sin_family = AF_INET;
		serv_addr.sin_port = htons(atoi("65432"));			// server port
//		serv_addr.sin_port = htons(atoi("65431"));			// develop server port
		serv_addr.sin_addr.s_addr = inet_addr("43.203.239.150");	// server ip
//		serv_addr.sin_addr.s_addr = inet_addr("127.0.0.1");			// localhost server ip

		// connect server
		if(connect(serv_sock, (struct sockaddr *)&serv_addr, sizeof(serv_addr)) == -1){
			error_handling("connect() error");
		}

		std::cout << "server connected\n";

		recv_thread = std::thread(recv_msg);	// recv data from server thread
		send_thread = std::thread(send_msg);	// send data to server thread

		recv_thread.join();
		send_thread.join();

		close(serv_sock);

		std::cout << "socket ended\n";
	}
}

// error message handling
void error_handling(const char *message){
	fputs(message, stderr);
	fputc('\n', stderr);
	exit(1);
}

// recieve data from server
void recv_msg(){
	int cnt = 0;
	std::string buf_recv;
	char buf_cur_recv[BUF_SIZE];	// current recv string buffer
	
	while(true){
		memset(buf_cur_recv, 0, sizeof(buf_cur_recv));
		int read_len = read(serv_sock, buf_cur_recv, BUF_SIZE - 1);	// recv cur data
		if(read_len == 0){		// socket disconnect
			std::cout << "server disconnected\n";
			mtx_write.lock();
			socket_status = false;
			cv_write.notify_all();
			mtx_write.unlock();
			break;
		}

		std::cout << "read : " << buf_cur_recv << '\n';

		int before = 0;
		for(int i = 0; i < read_len; i++){
			if(buf_cur_recv[i] == '{'){
				cnt++;
			}
			else if(buf_cur_recv[i] == '}'){
				cnt--;
				if(cnt == 0){
					char buf_cpy[BUF_SIZE] = {0,};
					int copy_len = i - before + 1;
					strncpy(buf_cpy, buf_cur_recv + before, copy_len);
					buf_recv += buf_cpy;

					std::lock_guard<std::mutex> lk(mtx_read);	// get read mutex
					parse_json(buf_recv);		// parse json data
					cv_read.notify_all();		// notify wait read mutex
					// unlock read mutex

					buf_recv.clear();
					before = i + 1;
				}
			}
		}
		if(before != read_len){
			char buf_cpy[BUF_SIZE] = {0,};
			strncpy(buf_cpy, buf_cur_recv + before, BUF_SIZE);
			buf_recv += buf_cpy;
		}
	}
}

void send_request_server(std::string request_id){
	struct Request new_request;
	new_request.request_type = REQUEST_SERVER;
	new_request.request_id = request_id;
		
	mtx_write.lock();
	request_list.push(new_request);
	cv_write.notify_all();
	mtx_write.unlock();
}

void send_unconnected_device(RequestType request_type, int id, json request_data){
	struct Request new_request;
	new_request.request_type = request_type;
	new_request.id = id;
	new_request.request_data = request_data;

	mtx_write.lock();
	request_list.push(new_request);
	cv_write.notify_all();
	mtx_write.unlock();
}

// parsing recv json string
void parse_json(std::string &str_recv){
	json root = json::parse(str_recv);			// parse string to json
	std::string type = root["type"].get<std::string>();	// get device type

	if(type.compare("hub") == 0){
		std::string request_name = root["requestName"].get<std::string>();
		if(request_name.compare("getId") == 0){
			machine_id_response.insert({root["macAddress"].get<std::string>(), root["id"].get<int>()});
		}
	}
	else{
		std::string request_id = root["requestId"].get<std::string>();
		json data = root["data"].get<json>();
		std::string request_name = data["requestName"].get<std::string>();

		if(request_name.compare("getState") == 0){
			data["requestId"] = request_id;
		}
		else{
			send_request_server(request_id);
		}

		if(type.compare("light") == 0){				// light
			int light_id = root["lightId"].get<int>();
			response_map::iterator it = light_response.find(light_id);
			if(it == light_response.end()){
				data["state"] = nullptr;
				send_unconnected_device(REQUEST_LIGHT, light_id, data);
			}
			else{
				it->second.push_back(data.dump());
			}
		}
		else if(type.compare("lamp") == 0){			// lamp
			int lamp_id = root["lampId"].get<int>();
			response_map::iterator it = lamp_response.find(lamp_id);
			if(it == lamp_response.end()){
				data["state"] = nullptr;
				send_unconnected_device(REQUEST_LAMP, lamp_id, data);
			}
			else{
				it->second.push_back(data.dump());
			}
		}
		else if(type.compare("window") == 0){		// window
			int window_id = root["windowId"].get<int>();
			response_map::iterator it = window_response.find(window_id);
			if(it == window_response.end()){
				data["state"] = nullptr;
				send_unconnected_device(REQUEST_WINDOW, window_id, data);
			}
			else{
				it->second.push_back(data.dump());
			}
		}
		else if(type.compare("curtain") == 0){		// curtain
			int curtain_id = root["curtainId"].get<int>();
			response_map::iterator it = curtain_response.find(curtain_id);
			if(it == curtain_response.end()){
				data["state"] = nullptr;
				send_unconnected_device(REQUEST_CURTAIN, curtain_id, data);
			}
			else{
				it->second.push_back(data.dump());
			}
		}
	}
}

void send_serial_number(std::string serial_number){
	json serial = json::object();
	serial["type"] = "hub";
	serial["requestName"] = "setConnect";
	serial["hubSerialNumber"] = serial_number;
	std::string buf_send = serial.dump();
	std::cout << "write : " << buf_send << '\n';
	write(serv_sock, buf_send.c_str(), strlen(buf_send.c_str()));
}

// send data to server
void send_msg(){
	socket_status = true;
	std::unique_lock<std::mutex> lk(mtx_write);		// get write mutex
	std::string buf_send;

	send_serial_number(HUB_SN);

	while(true){
		if(request_list.empty() && socket_status){
			cv_write.wait(lk);		// unlock write mutex && wait write data
		}
		// get write mutex
		if(!socket_status){
			break;
		}

		buf_send.clear();
		make_json(buf_send);	// making json string

		std::cout << "write : " << buf_send << '\n';

		if(write(serv_sock, buf_send.c_str(), strlen(buf_send.c_str())) < 0){	// write data to server
			break;
		}
	}
}

// making send json string
void make_json(std::string &str_send){
	Request cur_request = request_list.front();
	request_list.pop();

	if(cur_request.request_type == REQUEST_SERVER){
		json root = cur_request.request_data;
		root["requestId"] = cur_request.request_id;
		str_send = root.dump();
	}	
	else if(cur_request.request_type == REQUEST_ID){
		json root = cur_request.request_data;
		root["type"] = "hub";
		str_send = root.dump();
	}
	else{
		json root = json::object();
		json data = cur_request.request_data;
		std::string request_name = data["requestName"].get<std::string>();

		if(request_name.compare("getState") == 0){
			root["requestId"] = data["requestId"].get<std::string>();
			data.erase("requestId");
		}

		switch(cur_request.request_type){
			case REQUEST_LIGHT:			// light
				root["type"] = "light";
				root["lightId"] = cur_request.id;
				root["data"] = data;
				break;
			case REQUEST_LAMP:			// lamp
				root["type"] = "lamp";
				root["lampId"] = cur_request.id;
				root["data"] = data;
				break;
			case REQUEST_WINDOW:		// window
				root["type"] = "window";
				root["windowId"] = cur_request.id;
				root["data"] = data;
				break;
			case REQUEST_CURTAIN:		// curtain
				root["type"] = "curtain";
				root["curtainId"] = cur_request.id;
				root["data"] = data;
				break;
		}
		str_send = root.dump();		// make json to string
	}
}
