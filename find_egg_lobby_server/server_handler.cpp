//
// Created by dev on 6/6/21.
//

#include "server_handler.hpp"



std::basic_string<char> GameMasterServer::Handle::handle_out_message(std::shared_ptr<WsServer::InMessage> in_message) {
    return in_message->string();
}

GameMasterServer::Handle::~Handle() = default;

GameMasterServer::Handle::Handle() = default;
