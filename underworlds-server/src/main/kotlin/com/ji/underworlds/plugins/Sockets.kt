package com.ji.underworlds.plugins

import com.fasterxml.jackson.databind.ObjectMapper
import com.ji.underworlds.models.GameSession
import com.ji.underworlds.models.Message
import com.ji.underworlds.models.Player
import com.ji.underworlds.models.PlayerInput
import com.ji.underworlds.services.GameSessionService
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.concurrent.atomic.AtomicInteger

val gameSessionService = GameSessionService()
val gameSessionList = mutableListOf<GameSession>()
val playerList = mutableListOf<Player>()
private var uids = AtomicInteger(0)

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        webSocket("/uw") { // websocketSession
            try {
                val player = Player(id = Int.MAX_VALUE - uids.getAndIncrement(), webSocketServerSession = this)
                val gameSession: GameSession? = null

                for(frame in incoming) {
                    val text = frame as Frame.Text
                    val message = ObjectMapper().readValue(text.readText(), Message::class.java)

                    when(message.type) {
                        "create_player" -> {
                            player.name = message.data.toString()
                            emit(this, Message(type = "create_player", data = player))
                        }
                        "create_session" -> {
                            val gameSession = gameSessionService.createGameSession(gameSessionList, Int.MAX_VALUE - uids.getAndIncrement()).setAdmin(player)
                            gameSession.addPlayer(player)
                            emit(this, Message("create_session", gameSession))
                        }
                        /* join player */
                        "join" -> {
                            /* set player info */
                            val gameSession = gameSessionList.firstOrNull { it.id == message.data.toString().toInt() }
                            gameSession?.addPlayer(player)
                            /* send new player info */
                            GlobalScope.launch {
                                gameSession?.playerList?.forEach {
                                    launch {
                                        emit(it.webSocketServerSession, Message("join", gameSession))
                                        /*if(it.webSocketServerSession != this@webSocket) {
                                            emit(it.webSocketServerSession, Message("join", gameSession))
                                        }*/
                                    }
                                }
                            }
                            gameSession?.playerList?.forEach {
                                println(it.name)
                            }
                        }
                        "start" -> {

                        }
                        "get_glory_point" -> {

                        }
                        "use_glory_point" -> {

                        }
                        "end_activation_step" -> {

                        }
                        "end_power_step" -> {
                            GlobalScope.launch {

                            }
                        }
                    }
                }
            } catch(e: Exception) {
                println(e.localizedMessage)
            } finally {
                playerList.removeIf { it.webSocketServerSession == this }
                gameSessionList.forEach { it.removePlayer(PlayerInput(websocketServerSession = this)) }

                GlobalScope.launch {
                    gameSessionList.forEach {
                        launch {
                            //if(it.value.gameCode == player.gameCode) emit(it.key, Message("left", player))
                        }
                    }
                }
            }
        }
        static {
            defaultResource("index.html", "web")
            resources("web")
        }
    }
}

suspend fun emit(session: WebSocketServerSession, message: Message) {
    session.send(ObjectMapper().writeValueAsString(message))
}
/*fun broadcast(gameCode: String, message: Message) {
    GlobalScope.launch {
        players.forEach {
            launch {
                if(it.value.gameCode == gameCode) emit(it.key, message)
            }
        }
    }
}
fun broadcastToOthers(session: WebSocketServerSession, gameCode: String, message: Message) {

}
fun updateState(gameCode: String) {
    GlobalScope.launch {
        games[gameCode]!!.players.forEach {
            launch {
                emit(it.session!!, Message("update_player_state", games[gameCode]!!))
            }
        }
    }
}*/
