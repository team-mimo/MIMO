#include "common.hpp"

std::mutex mtx_interrupt;
std::condition_variable cv_interrupt;
bool is_end = false;

std::mutex mtx_read;
std::condition_variable cv_read;

std::unordered_map<std::string, int> machine_id_response;
std::unordered_map<int, std::vector<std::string>> light_response;
std::unordered_map<int, std::vector<std::string>> lamp_response;
std::unordered_map<int, std::vector<std::string>> window_response;
std::unordered_map<int, std::vector<std::string>> curtain_response;

std::mutex mtx_write;
std::condition_variable cv_write;

std::queue<Request> request_list;
