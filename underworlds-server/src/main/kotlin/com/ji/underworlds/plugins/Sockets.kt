package com.ji.underworlds.plugins

import com.fasterxml.jackson.databind.ObjectMapper
import com.ji.underworlds.models.GameSession
import com.ji.underworlds.models.Message
import com.ji.underworlds.models.Player
import com.ji.underworlds.services.GameSessionService
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import io.ktor.server.application.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        webSocket("/uw") { // websocketSession
            var player = Player()
            var game = GameSession()

            try {
                for(frame in incoming) {
                    val text = frame as Frame.Text

                    val message = ObjectMapper().readValue(text.readText(), Message::class.java)

                    when(message.type) {
                        /* join player */
                        "join" -> {
                            /* set player info */
                            player = ObjectMapper().readValue(message.data.toString(), Player::class.java)
                            player.id = uids.getAndIncrement()

                            if(games[player.gameCode] != null) {  // join exist game
                                games[player.gameCode]!!.players.add(player)
                            } else { // create new game
                                games[player.gameCode!!] = GameSession()
                                games[player.gameCode]!!.players.add(player)
                                player.isAdmin = true
                            }

                            /* send new player info */
                            GlobalScope.launch {
                                players.forEach {
                                    launch {
                                        if(it.key != this@webSocket && it.value.gameCode == player.gameCode) {
                                            emit(it.key, Message("join", player))
                                        }
                                    }
                                }
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
                players -= this

                players.forEach {
                    println(it.value.playerName)
                }

                GlobalScope.launch {
                    players.forEach {
                        launch {
                            if(it.value.gameCode == player.gameCode) emit(it.key, Message("left", player))
                        }
                    }
                }
            }
        }
    }
}

suspend fun emit(session: WebSocketServerSession, message: Message) {
    session.send(ObjectMapper().writeValueAsString(message))
}
fun broadcast(gameCode: String, message: Message) {
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
}