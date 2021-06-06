#include "server_ws.hpp"
#include <future>
#include "server_handler.hpp"
using namespace std;

using WsServer = GameMasterServer::SocketServer<GameMasterServer::WS>;

int main() {
    WsServer server;
    server.config.port = 8086;
    server.config.address = "192.168.220.129";

    auto &publicLobby = server.endpoint["^/publicLobby/?$"];
    publicLobby.on_message = [](shared_ptr<WsServer::Connection> connection, shared_ptr<WsServer::InMessage> in_message) {
        auto out_message = make_shared<WsServer::OutMessage>();
        auto* handler = new GameMasterServer::Handle();
        *out_message << handler->handle_out_message(in_message);

        cout << "Server: Message received: \"" << out_message << "\" from " << connection.get() << endl;
        cout << "Server: Sending message \"" << out_message << "\" to " << connection.get() << endl;

        // connection->send is an asynchronous function
        connection->send(out_message, [](const GameMasterServer::error_code &ec) {
            if(ec) {
                cout << "Server: Error sending message. " <<
                     "Error: " << ec << ", error message: " << ec.message() << endl;
            }
        });

        // Alternatively use streams:
        // auto out_message = make_shared<WsServer::OutMessage>();
        // *out_message << in_message->string();
        // connection->send(out_message);
    };

    publicLobby.on_open = [](shared_ptr<WsServer::Connection> connection) {
        cout << "Server: Opened connection " << connection.get() << endl;
    };

    // See RFC 6455 7.4.1. for status codes
    publicLobby.on_close = [](shared_ptr<WsServer::Connection> connection, int status, const string & /*reason*/) {
        cout << "Server: Closed connection " << connection.get() << " with status code " << status << endl;
    };

    // Can modify handshake response headers here if needed
    publicLobby.on_handshake = [](shared_ptr<WsServer::Connection> /*connection*/, GameMasterServer::CaseInsensitiveMultimap & /*response_header*/) {
        return GameMasterServer::StatusCode::information_switching_protocols; // Upgrade to websocket
    };

    // See http://www.boost.org/doc/libs/1_55_0/doc/html/boost_asio/reference.html, Error Codes for error code meanings
    publicLobby.on_error = [](shared_ptr<WsServer::Connection> connection, const GameMasterServer::error_code &ec) {
        cout << "Server: Error in connection " << connection.get() << ". "
             << "Error: " << ec << ", error message: " << ec.message() << endl;
    };


    auto &privateLobby = server.endpoint["^/privateLobby/?$"];
    privateLobby.on_message = [](shared_ptr<WsServer::Connection> connection, shared_ptr<WsServer::InMessage> in_message) {
        auto out_message = make_shared<WsServer::OutMessage>();
        auto* handler = new GameMasterServer::Handle();
        *out_message << handler->handle_out_message(in_message);

        connection->send(out_message, [connection, out_message](const GameMasterServer::error_code &ec) {
            if(!ec)
                connection->send(out_message); // Sent after the first send operation is finished
        });
        connection->send(out_message); // Most likely queued. Sent after the first send operation is finished.
    };


    auto &allLobby = server.endpoint["^/allLobby/?$"];
    allLobby.on_message = [&server](shared_ptr<WsServer::Connection> /*connection*/, shared_ptr<WsServer::InMessage> in_message) {
        auto out_message = in_message->string();

        // echo_all.get_connections() can also be used to solely receive connections on this endpoint
        for(auto &a_connection : server.get_connections())
            a_connection->send(out_message);
    };

    // Start server and receive assigned port when server is listening for requests
    promise<unsigned short> server_port;
    thread server_thread([&server, &server_port]() {
        // Start server
        server.start([&server_port](unsigned short port) {
            server_port.set_value(port);
        });
    });
    cout << "Server listening on port " << server_port.get_future().get() << endl
         << endl;

    server_thread.join();
}
