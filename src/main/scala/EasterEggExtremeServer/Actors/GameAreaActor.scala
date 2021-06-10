package EasterEggExtremeServer.Actors

import akka.actor.{Actor, ActorLogging}
import EasterEggExtremeServer.Core.Game._

case object GameMasterBehavior {
    case class UpdateScore(player: Player, score: String)
    case class UpdatePower(player: Player, power: String)
    case class UpdateState(player: Player, state: String)
}
class GameAreaActor extends Actor with ActorLogging {
    val maxPlayerInGame = 12
    val playersInGame = collection.mutable.LinkedHashMap[String, PlayerWithActor]()
    def takenPositions = playersInGame.values.map(_.player.playerData.position).toList

    override def receive: Receive = {

       case JoinMatch(player, actor) =>
           val newPlayer = Player(playersInGame.size + 1, player.playerName, player.playerData);
           playersInGame += (newPlayer.playerName -> PlayerWithActor(newPlayer, actor))
           println(s"Player $newPlayer enter game succeed")
           NotifyGameDataUpdate()

       case LeftMatch(player) =>
         println(s"Player $player left game succeed")
         playersInGame -= player.playerName
           NotifyGameDataUpdate()

       case PositionUpdate(player, direction) =>
         val offset = direction match {
           case "up" => Position(0,0.5)
           case "down" => Position(0,-0.5)
           case "right" => Position(0.5,0)
           case "left" => Position(-0.5,0)
         }
         val oldPlayerWithActor = playersInGame(player.playerName)
         val oldPlayer = oldPlayerWithActor.player
         log.info(s"Enter Position update with old pos is ${oldPlayer.playerData.position}")

         val newPosition = oldPlayer.playerData.position + offset
         val actor = oldPlayerWithActor.actor
         playersInGame(player.playerName) =
             PlayerWithActor(
               Player(player.playerIndex,
                      player.playerName,
                      PlayerData(player.playerData.currentPoint, newPosition, player.playerData.eggPosition)),
               actor)
         log.info(s"Exit Position update with new pos for player ${playersInGame(player.playerName).player.playerName}")
         log.info(s"Exit Position update with new pos is ${playersInGame(player.playerName).player.playerData.position}")

         NotifyGameDataUpdate()


       case GameDataUpdate(player, newRequest) =>
            log.info(s"Receive new update request is : $newRequest")
            val oldPlayerWithActor = playersInGame(player.playerName)
            val oldPlayer = oldPlayerWithActor.player

            val actor = oldPlayerWithActor.actor
            val oldPlayerData = oldPlayer.playerData

            import EasterEggExtremeServer.Core.Behavior._
            val newPlayerData: PlayerData = newRequest match {
              case "GET_EGG" =>
                Handler.HandleAPlayerGetEgg(oldPlayerData)
              case "START_GAME" =>
                Handler.HandleGameWaiting(oldPlayerData)
              case "END_GAME" =>
                Handler.HandleEndGame(oldPlayerData)
            }
           playersInGame(player.playerName) = PlayerWithActor(Player(player.playerIndex, player.playerName, newPlayerData), actor)
           NotifyGameDataUpdate()

       case SpecialRequestUpdate(player, request) =>
            log.info(s"Receive $request from $player")
            val returnData = request match {
              case "FIRST_INIT" =>
                player.playerData.position.toString
              case "MAX_PLAYER" =>
                maxPlayerInGame.toString
              case "CURRENT_PLAYER" =>
                playersInGame.size.toString
            }
            NotifySpecialRequestUpdate(returnData)

       case _ =>
           log.info("Enter Game master actor")
           NotifyNoHaveUpdate()
    }

    def NotifyGameDataUpdate(): Unit = {
        playersInGame.values.foreach(_.actor ! GameDataChanged(playersInGame.values.map(_.player)))
    }

    def NotifyPositionUpdate(): Unit = {
        playersInGame.values.foreach(_.actor ! PositionChanged(playersInGame.values.map(_.player.playerData)))
    }
    def NotifySpecialRequestUpdate(newUpdate: String): Unit = {
      playersInGame.values.foreach(_.actor ! SpecialDataChanged(newUpdate))
    }

    def NotifyNoHaveUpdate(): Unit = {

    }

    def NotifyUpdateAll(): Unit = {

    }
}