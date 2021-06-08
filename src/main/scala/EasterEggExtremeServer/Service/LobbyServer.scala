package EasterEggExtremeServer.Service

import EasterEggExtremeServer.Actors.{GameAreaActor, LobbyAreaActor}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Route
import akka.stream.{FlowShape, Materializer, OverflowStrategy}
import akka.stream.scaladsl.{Flow, GraphDSL, Merge, Sink, Source}
import akka.http.scaladsl.server.Directives._
import EasterEggExtremeServer.Core.Game._
class LobbyServer(implicit val system: ActorSystem, implicit val materializer: Materializer) {

  val lobbyHandleActor: ActorRef = system.actorOf(Props[LobbyAreaActor], "LobbyHandleActor")
  val lobbyProfileSource: Source[GameEvent, ActorRef] = Source.actorRef[GameEvent](5,OverflowStrategy.fail)

  def lobbyFlow(playerInLobby: PlayerInLobby): Flow[Message, Message, Any] =
    Flow.fromGraph(GraphDSL.create(lobbyProfileSource) { implicit builder => profileShape =>
      import GraphDSL.Implicits._
      val materialization = builder.materializedValue.map(profileActorRef => JoinLobby(playerInLobby, profileActorRef))
      val merge = builder.add(Merge[GameEvent](2))
      val lobbyProfileSink = Sink.actorRef[GameEvent](lobbyHandleActor, LeftLobby(playerInLobby))

      val MessageToLobbyEventConverter = builder.add(Flow[Message].map {

        case TextMessage.Strict(s"MOVE_REQUEST:$direction") =>
          println("Have move request from " + playerInLobby.toString)
          LobbyPositionUpdate(playerInLobby, direction)

        case TextMessage.Strict(newRequest) =>
          println("Have update data request from " + playerInLobby.toString)
          LobbyDataUpdate(playerInLobby, newRequest)

      })
      val LobbyEventBackToMessageConverter = builder.add(Flow[GameEvent].map{
        case LobbyDataChanged(playerInLobby) =>
          import spray.json._
          import EasterEggExtremeServer.Core.PlayerDataJsonProtocol._
          TextMessage(playerInLobby.toList.toJson.toString)

      })

      materialization ~> merge ~> lobbyProfileSink
      MessageToLobbyEventConverter ~> merge
      profileShape ~> LobbyEventBackToMessageConverter
      FlowShape(MessageToLobbyEventConverter.in, LobbyEventBackToMessageConverter.out)
    })

  import EasterEggExtremeServer.Core.Behavior._
  val LobbyFinalRoute: Route =
    (get & parameter("playerName")) { playerName =>
      val playerInLobby = Generation.GenerationPlayerInLobbyData(playerName)
      handleWebSocketMessages(lobbyFlow(playerInLobby))
    }


}
