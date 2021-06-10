package EasterEggExtremeServer.Core
import EasterEggExtremeServer.Core.Game.{Player, PlayerData, PlayerInLobby}
import akka.actor.ActorRef
import spray.json._
object PlayerDataJsonProtocol extends DefaultJsonProtocol {
    import EasterEggExtremeServer.Core.Game._
    implicit val positionFormat = jsonFormat2(Position)
    implicit val playerDataFormat = jsonFormat3(PlayerData)
    implicit val PlayerFormat = jsonFormat3(Player)
    implicit val PlayerInLobbyFormat = jsonFormat4(PlayerInLobby)

}


object Game {
    trait GameData
        case class PlayerWithActor(player: Player, actor: ActorRef) extends GameData
        case class Player(playerIndex: Int, playerName: String, playerData: PlayerData) extends GameData
        case class PlayerData(currentPoint: Int, position: Position, eggPosition: Position)

        case class PlayerInLobbyWithActor(playerInLobby: PlayerInLobby, actorRef: ActorRef)
        case class PlayerInLobby(playerIndex: Int, playerName: String, position: Position, serverWillJoin: String) extends GameData

        case class Position(x:Double, y:Double) extends GameData {
            def + (other: Position) : Position = {
                Position(x+other.x, y+other.y)
            }
        }

    trait GameEvent
        case class JoinLobby(player: PlayerInLobby, actor: ActorRef) extends GameEvent
        case class LeftLobby(player: PlayerInLobby) extends GameEvent
        case class LobbyDataUpdate(playerInLobby: PlayerInLobby, newData: String) extends GameEvent
        case class LobbyPositionUpdate(playerInLobby: PlayerInLobby, direction: String) extends GameEvent
        case class LobbyDataChanged(playersInLobby: Iterable[PlayerInLobby]) extends GameEvent

        case class JoinMatch(player: Player, actor: ActorRef) extends GameEvent
        case class LeftMatch(player: Player) extends GameEvent

        case class SpecialRequestUpdate(player: Player, request: String) extends GameEvent
        case class GameDataUpdate(player: Player, newData: String) extends GameEvent
        case class PositionUpdate(player: Player, direction: String) extends GameEvent

        case class SpecialDataChanged(specialData: String) extends GameEvent
        case class GameDataChanged(players: Iterable[Player]) extends GameEvent
        case class PositionChanged(playersData: Iterable[PlayerData]) extends GameEvent
        case class NoHaveUpdate()

}

object Behavior {
    case object Generation {
        import Game.Position
        def GenerationRandomPosition(mapPosition: String): Position ={
            val position = mapPosition.split("_").map(_.trim).toList
            val x_Pos: Int = position.head.toInt
            val y_Pos: Int = position(1).toInt
            Position(x_Pos, y_Pos)
        }
        def GenerationPlayerInLobbyData(playerName: String): PlayerInLobby = {
            PlayerInLobby(0, playerName, Position(0,0),s"ws://192.168.1.9:8088/?playerName=$playerName&mapPosition=20_00")
        }
        def GenerationStartGamePosition(mapPosition: String): Position = {
            val startGamePosition = GenerationRandomPosition(mapPosition)
            startGamePosition
        }

        def GenerationEggPosition(mapPosition: String): Position ={
            val eggPosition = GenerationRandomPosition(mapPosition)
            eggPosition
        }

        def GenerationPlayerData(playerName: String, mapPosition: String): Player = {
            var defaultPlayerData = PlayerData(0, GenerationStartGamePosition(mapPosition),GenerationEggPosition(mapPosition))
            var player = Player(0, playerName, defaultPlayerData)
            player
        }
    }

    case object Handler {
        //TODO: Handler data update
        def HandleAPlayerGetEgg(oldData: PlayerData): PlayerData = {
            oldData
        }

        def HandleGameWaiting(oldData: PlayerData): PlayerData = {
            oldData
        }

        def HandleEndGame(oldData: PlayerData): PlayerData = {
            oldData
        }
    }
}

