package com.ji.underworlds.models

import com.fasterxml.jackson.annotation.JsonManagedReference
import io.ktor.server.websocket.*

class GameSession(val id: Int) {
    @JsonManagedReference
    var playerList = mutableListOf<Player>()
    var round = 1
    var phase = 1
    var isActivationStep = 0
    var isPowerStep = 0
    var admin: Player? = null

    fun addPlayer(player: Player): GameSession {
        this.playerList.add(player)
        return this
    }

    fun setAdmin(player: Player): GameSession {
        this.admin = player
        return this
    }

    fun findPlayerOrNull(playerInput: PlayerInput): Player? {
        if(playerInput.id != null ) return playerList.firstOrNull { it.id == playerInput.id }
        if(playerInput.websocketServerSession != null) return playerList.firstOrNull { it.webSocketServerSession == playerInput.websocketServerSession }
        return null
    }

    fun setPlayer(playerInput: PlayerInput): GameSession {
        this.playerList.filter { it.id == playerInput.id}.forEach {
            it.setPlayer(it)
        }
        return this
    }

    fun removePlayer(playerInput: PlayerInput): GameSession {
        if(playerInput.id != null) this.playerList.removeIf { it.id == playerInput.id }
        if(playerInput.websocketServerSession != null) this.playerList.removeIf { it.webSocketServerSession == playerInput.websocketServerSession }
        return this
    }
}