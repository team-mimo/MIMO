#include <iostream>
#include <sys/socket.h>
#include <cstdlib>
#include <unistd.h>
#include <arpa/inet.h>
#include <cstring>
#include <thread>

#include "common.hpp"
#include "json.hpp"

#define HUB_SN "10000000e63286b1"

using json = nlohmann::json;

void set_socket();		// connect socket to server
void error_handling(const char *);	// error message handling
void recv_msg();		// recieve data from server
void send_request_server(std::string);
void send_unconnected_device(RequestType, int, json);
void parse_json(std::string &);		// parsing recv json string
void send_serial_number(std::string);
void send_msg();		// send data to server
void make_json(std::string &);		// making send json string
