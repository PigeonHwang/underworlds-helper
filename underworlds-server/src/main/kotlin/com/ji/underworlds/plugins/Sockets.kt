package com.ji.underworlds.plugins

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ji.underworlds.models.Game
import com.ji.underworlds.models.Message
import com.ji.underworlds.models.Player
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

private var players = ConcurrentHashMap<WebSocketServerSession, Player>()
private var games = ConcurrentHashMap<String, Game>()
private var uids = AtomicLong(0)

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
            var game = Game()
            try {
                for(frame in incoming) {
                    if(frame is Frame.Text) {
                        if(players[this] != null) {
                            player = players[this]!!
                            if(games[player.gameCode] != null) {
                                game = games[player.gameCode]!!
                            }
                        }

                        val json = ObjectMapper().readTree(frame.readText())

                        when(json.get("type").asText()) {
                            /* join player */
                            "join" -> {
                                /* set player info */
                                player.id = uids.getAndIncrement()
                                player.playerName = json.get("userName").asText()
                                player.gameCode = json.get("gameCode").asText()
                                player.session = this
                                players[this] = player

                                if(games[player.gameCode] != null) {  // join exist game
                                    games[player.gameCode]!!.playerList.add(player)
                                } else { // create new game
                                    games[player.gameCode] = Game()
                                    games[player.gameCode]!!.playerList.add(player)
                                    player.isAdmin = true
                                }

                                /* get player list */
                                emit(this, Message("players", games[player.gameCode]!!.playerList))

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
                                game.isActivationStep = true
                            }
                            "get_glory_point" -> {

                            }
                            "use_glory_point" -> {

                            }
                            "end_activation_step" -> {
                                player.activationPhase += 1
                                updatePlayerState(player.gameCode)

                                /*if(player.activationPhase == 4 && player.gameCode == game.playerSequence.last().gameCode) {
                                    game.round += 1
                                    game.playerList.forEach {
                                        it.activationPhase = 0
                                    }
                                    broadcast(player.gameCode, Message("end_phase", ""))
                                }*/
                            }
                            "end_power_step" -> {
                                GlobalScope.launch {
                                    players.forEach {
                                        launch {
                                            if(it.key != this && it.value.gameCode == player.gameCode) emit(it.key, Message("join", player.playerName))
                                        }
                                    }
                                }
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
    session.send(jacksonObjectMapper().writeValueAsString(message))
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
fun updatePlayerState(gameCode: String) {
    GlobalScope.launch {
        games[gameCode]!!.playerList.forEach {
            launch {
                emit(it.session, Message("update_player_state", games[gameCode]!!.playerList))
            }
        }
    }
}