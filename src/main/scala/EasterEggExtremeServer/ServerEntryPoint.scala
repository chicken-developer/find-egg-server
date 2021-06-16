package EasterEggExtremeServer

import EasterEggExtremeServer.Service.{AccountServer, GameServer, LobbyServer}
import akka.actor.ActorSystem
import akka.http.scaladsl.{ConnectionContext, Http}
import akka.stream.Materializer

import java.io.InputStream
import java.security.{KeyStore, SecureRandom}
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}
import scala.io.StdIn

object ServerEntryPoint {
  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem()
    implicit val materializer = Materializer
    implicit val executionContext = system.dispatcher

    val key: KeyStore = KeyStore.getInstance("PKCS12")
    val keyStoreFile: InputStream = getClass.getClassLoader.getResourceAsStream("myKeystore.p12")
    val password = "MY_PASSWORD".toCharArray
    key.load(keyStoreFile, password)

    val keyManagerFactory = KeyManagerFactory.getInstance("SunX509")
    keyManagerFactory.init(key, password)

    val trustManagerFactory = TrustManagerFactory.getInstance("SunX509")
    trustManagerFactory.init(key)

    val sslContext: SSLContext = SSLContext.getInstance("TLS")
    sslContext.init(keyManagerFactory.getKeyManagers, trustManagerFactory.getTrustManagers, new SecureRandom)

    val httpsConnectionContext = ConnectionContext.httpsServer(sslContext)

    val accountService = new AccountServer()
    val gameService = new GameServer()
    val lobbyService = new LobbyServer()


    val localHost = "127.0.0.1"
    val vpsHost= "103.153.65.194"

    val accountServerPort = 8086
    val lobbyPort = 8087
    val gameServerPort = 8088

    val accountServerBind = Http().newServerAt(localHost, accountServerPort).bindFlow(accountService.finalRoute)
    val lobbyServerBind = Http().newServerAt(localHost, lobbyPort).bindFlow(lobbyService.LobbyFinalRoute)
    val gameServerBind = Http().newServerAt(localHost, gameServerPort).bindFlow(gameService.GameFinalRoute)


    val listBindingFutureWithSecurity = List(accountServerBind, lobbyServerBind, gameServerBind)
    println(s"Server is progressing...\nPress RETURN to stop...")
    StdIn.readLine()
    listBindingFutureWithSecurity
      .foreach { server =>
             server.flatMap(_.unbind())
            .onComplete(_ => system.terminate())
      }

  }

}
