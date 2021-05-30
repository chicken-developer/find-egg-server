//#include "server_wss.hpp"
//#include <future>
//
//using namespace std;
//
//using WssServer = GameMasterServer::SocketServer<GameMasterServer::WSS>;
//
//int main() {
//    // WebSocket Secure (WSS)-server at port 8080 using 1 thread
//    WssServer server("/home/dev/Desktop/Projects/unity-game-find-egg/project/find_egg_lobby_server/host.crt",
//                     "/home/dev/Desktop/Projects/unity-game-find-egg/project/find_egg_lobby_server/host.key");
//    server.config.port = 8082;
//    server.config.address = "192.168.220.129";
//    auto &echo = server.endpoint["^/echo/?$"];
//
//    echo.on_message = [](shared_ptr<WssServer::Connection> connection, shared_ptr<WssServer::InMessage> in_message) {
//        auto out_message = in_message->string();
//
//        cout << "Server: Message received: \"" << out_message << "\" from " << connection.get() << endl;
//
//        cout << "Server: Sending message \"" << out_message << "\" to " << connection.get() << endl;
//
//        // connection->send is an asynchronous function
//        connection->send(out_message, [](const GameMasterServer::error_code &ec) {
//            if(ec) {
//                cout << "Server: Error sending message. " <<
//                     // See http://www.boost.org/doc/libs/1_55_0/doc/html/boost_asio/reference.html, Error Codes for error code meanings
//                     "Error: " << ec << ", error message: " << ec.message() << endl;
//            }
//        });
//
//        // Alternatively use streams:
//        // auto out_message = make_shared<WssServer::OutMessage>();
//        // *out_message << in_message->string();
//        // connection->send(out_message);
//    };
//
//    echo.on_open = [](shared_ptr<WssServer::Connection> connection) {
//        cout << "Server: Opened connection " << connection.get() << endl;
//    };
//
//    // See RFC 6455 7.4.1. for status codes
//    echo.on_close = [](shared_ptr<WssServer::Connection> connection, int status, const string & /*reason*/) {
//        cout << "Server: Closed connection " << connection.get() << " with status code " << status << endl;
//    };
//
//    // Can modify handshake response header here if needed
//    echo.on_handshake = [](shared_ptr<WssServer::Connection> /*connection*/, GameMasterServer::CaseInsensitiveMultimap & /*response_header*/) {
//        return GameMasterServer::StatusCode::information_switching_protocols; // Upgrade to websocket
//    };
//
//    // See http://www.boost.org/doc/libs/1_55_0/doc/html/boost_asio/reference.html, Error Codes for error code meanings
//    echo.on_error = [](shared_ptr<WssServer::Connection> connection, const GameMasterServer::error_code &ec) {
//        cout << "Server: Error in connection " << connection.get() << ". "
//             << "Error: " << ec << ", error message: " << ec.message() << endl;
//    };
//
//    // Example 2: Echo thrice
//    // Demonstrating queuing of messages by sending a received message three times back to the client.
//    // Concurrent send operations are automatically queued by the library.
//    // Test with the following JavaScript:
//    //   var wss=new WebSocket("wss://localhost:8080/echo_thrice");
//    //   wss.onmessage=function(evt){console.log(evt.data);};
//    //   wss.send("test");
//    auto &echo_thrice = server.endpoint["^/echo_thrice/?$"];
//    echo_thrice.on_message = [](shared_ptr<WssServer::Connection> connection, shared_ptr<WssServer::InMessage> in_message) {
//        auto out_message = make_shared<string>(in_message->string());
//
//        connection->send(*out_message, [connection, out_message](const GameMasterServer::error_code &ec) {
//            if(!ec)
//                connection->send(*out_message); // Sent after the first send operation is finished
//        });
//        connection->send(*out_message); // Most likely queued. Sent after the first send operation is finished.
//    };
//
//    // Example 3: Echo to all WebSocket Secure endpoints
//    // Sending received messages to all connected clients
//    // Test with the following JavaScript on more than one browser windows:
//    //   var wss=new WebSocket("wss://localhost:8080/echo_all");
//    //   wss.onmessage=function(evt){console.log(evt.data);};
//    //   wss.send("test");
//    auto &echo_all = server.endpoint["^/echo_all/?$"];
//    echo_all.on_message = [&server](shared_ptr<WssServer::Connection> /*connection*/, shared_ptr<WssServer::InMessage> in_message) {
//        auto out_message = in_message->string();
//
//        // echo_all.get_connections() can also be used to solely receive connections on this endpoint
//        for(auto &a_connection : server.get_connections())
//            a_connection->send(out_message);
//    };
//
//    // Start server and receive assigned port when server is listening for requests
//    promise<unsigned short> server_port;
//    thread server_thread([&server, &server_port]() {
//        // Start server
//        server.start([&server_port](unsigned short port) {
//            server_port.set_value(port);
//        });
//    });
//    cout << "Server listening on port " << server_port.get_future().get() << endl
//         << endl;
//
//    server_thread.join();
//}
