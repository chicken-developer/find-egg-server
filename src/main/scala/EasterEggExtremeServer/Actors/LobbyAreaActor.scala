package EasterEggExtremeServer.Actors

import akka.actor.{Actor, ActorLogging}
import EasterEggExtremeServer.Core.Game._

class LobbyAreaActor extends Actor with ActorLogging {
  val playersInLobby = collection.mutable.LinkedHashMap[String, PlayerInLobbyWithActor]()
  def takenPositions = playersInLobby.values.map(_.playerInLobby.position).toList

  override def receive: Receive = {

    case JoinLobby(player, actor) =>
      val newPlayerInLobby = PlayerInLobby(playersInLobby.size + 1, player.playerName, player.position, player.serverWillJoin);
      playersInLobby += (newPlayerInLobby.playerName -> PlayerInLobbyWithActor(newPlayerInLobby, actor))
      println(s"Player $newPlayerInLobby enter game succeed")
      NotifyLobbyDataUpdate()

    case LeftLobby(player) =>
      playersInLobby -= player.playerName
      NotifyLobbyDataUpdate()

    case LobbyPositionUpdate(player, direction) =>
      val offset = direction match {
        case "up" => Position(0,0.1)
        case "down" => Position(0,-0.1)
        case "right" => Position(0.1,0)
        case "left" => Position(-0.1,0)
      }
      val oldPlayerWithActor = playersInLobby(player.playerName)
      val oldPlayer = oldPlayerWithActor.playerInLobby
      val newPosition = oldPlayer.position + offset
      if (!takenPositions.contains(newPosition)) {
        val actor = oldPlayerWithActor.actorRef
        playersInLobby(player.playerName) =
          PlayerInLobbyWithActor(
            PlayerInLobby(player.playerIndex,
              player.playerName,
              newPosition,
              player.serverWillJoin),
            actor)
        NotifyLobbyDataUpdate()
      }


    case LobbyDataUpdate(player, newRequest) =>
      log.info(s"Receive new update request is : $newRequest")
      val oldPlayerWithActor = playersInLobby(player.playerName)
      val oldPlayer = oldPlayerWithActor.playerInLobby
      //TODO: No need handle now
      NotifyLobbyDataUpdate()

    case _ =>
      log.info("Enter lobby master actor")
      NotifyLobbyDataUpdate()
  }

  def NotifyLobbyDataUpdate(): Unit = {
    playersInLobby.values.foreach(_.actorRef ! LobbyDataChanged(playersInLobby.values.map(_.playerInLobby)))
  }


}