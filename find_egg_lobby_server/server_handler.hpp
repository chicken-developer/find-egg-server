//
// Created by dev on 5/30/21.
//

#ifndef GAME_MASTER_SERVER_SERVER_HANDLER_HPP
#define GAME_MASTER_SERVER_SERVER_HANDLER_HPP

#include "server_ws.hpp"
using WsServer = GameMasterServer::SocketServer<GameMasterServer::WS>;

namespace GameMasterServer {
    class Handle {
    private:
        ~Handle();
    public:
        Handle();
        std::basic_string<char> handle_out_message(std::shared_ptr<WsServer::InMessage> in_message);
    };


}
#endif //GAME_MASTER_SERVER_SERVER_HANDLER_HPP
