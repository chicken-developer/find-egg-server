//
// Created by dev on 6/6/21.
//

#include "server_handler.hpp"



std::basic_string<char> GameMasterServer::Handle::handle_out_message(std::shared_ptr<WsServer::InMessage> in_message) {
    std::string akka_01_server_address = "ws://192.168.220.129:8089/?";
    std::string response = in_message->string();
    std::string delimiter = "-";
    std::string userName = response.substr(0, response.find(delimiter));
    std::string position = response.substr(response.find(delimiter), response.length() - response.find(delimiter));

    return akka_01_server_address + "playerName=" + userName + "&mapPosition=" + position;
}

GameMasterServer::Handle::~Handle() = default;

GameMasterServer::Handle::Handle() = default;
