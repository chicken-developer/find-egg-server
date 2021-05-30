package EasterEggExtremeServer.Core
import EasterEggExtremeServer.Core.Game.{Player, PlayerData}
import akka.actor.ActorRef

import spray.json._
object PlayerDataJsonProtocol extends DefaultJsonProtocol {
    import EasterEggExtremeServer.Core.Game._
    implicit val positionFormat = jsonFormat2(Position)
    implicit val playerDataFormat = jsonFormat3(PlayerData)
    implicit val PlayerFormat = jsonFormat2(Player)

}


object Game {
    trait GameData
        case class PlayerWithActor(player: Player, actor: ActorRef) extends GameData
        case class Player(playerName: String, playerData: PlayerData) extends GameData
        case class PlayerData(currentPoint: Int, position: Position, eggPosition: Position)

        case class Position(x:Int, y:Int) extends GameData {
            def + (other: Position) : Position = {
                Position(x+other.x, y+other.y)
            }
        }

    trait GameEvent
        case class JoinMatch(player: Player, actor: ActorRef) extends GameEvent
        case class LeftMatch(player: Player) extends GameEvent

        case class SpecialRequestUpdate(player: Player, request: String) extends GameEvent
        case class GameDataUpdate(player: Player, newData: String) extends GameEvent
        case class PositionUpdate(player: Player, direction: String) extends GameEvent

        case class SpecialDataChanged(specialData: String) extends GameEvent
        case class GameDataChanged(player: Iterable[Player]) extends GameEvent
        case class PositionChanged(playerData: Iterable[PlayerData]) extends GameEvent
        case class NoHaveUpdate()



}

object Behavior {
    case object Generation {
        import Game.Position
        def GenerationRandomPosition(mapPosition: String): Position ={
            Position(0, 0)
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
            val newPlayerData = PlayerData(0, GenerationStartGamePosition(mapPosition),GenerationEggPosition(mapPosition))
            val newPlayer = Player(playerName, playerData = newPlayerData)
            newPlayer
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

