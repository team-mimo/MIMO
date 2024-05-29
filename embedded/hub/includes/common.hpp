#pragma once

#include <mutex>
#include <condition_variable>
#include <unordered_map>
#include <vector>
#include <string>
#include <queue>

#include "json.hpp"

#define BUF_SIZE 2048	// send/recv buffer max size

using json = nlohmann::json;

typedef std::unordered_map<int, std::vector<std::string>> response_map;

// send request enum from device
enum RequestType {
	REQUEST_SERVER,
	REQUEST_ID,
	REQUEST_LIGHT,
	REQUEST_LAMP,
	REQUEST_WINDOW,
	REQUEST_CURTAIN
};

struct Request {
	RequestType request_type;
	int id;
	json request_data;
	std::string request_id;
};

extern std::mutex mtx_interrupt;
extern std::condition_variable cv_interrupt;
extern bool is_end;

extern std::mutex mtx_read;
extern std::condition_variable cv_read;

// response json string to device
extern std::unordered_map<std::string, int> machine_id_response;
extern std::unordered_map<int, std::vector<std::string>> light_response;
extern std::unordered_map<int, std::vector<std::string>> lamp_response;
extern std::unordered_map<int, std::vector<std::string>> window_response;
extern std::unordered_map<int, std::vector<std::string>> curtain_response;

extern std::mutex mtx_write;
extern std::condition_variable cv_write;

// request data from device
extern std::queue<Request> request_list;
