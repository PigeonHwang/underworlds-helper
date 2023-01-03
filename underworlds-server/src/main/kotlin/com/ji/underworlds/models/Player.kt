package com.ji.underworlds.models

import com.fasterxml.jackson.annotation.JsonBackReference
import io.ktor.server.websocket.*

class Player(
    val id: Int, var name: String = "anonymous",
    @JsonBackReference
    val webSocketServerSession: WebSocketServerSession) {
    var priority = 0
    var gloryPoint = 0
    var gloryPointUsed = 0
    var activated = false
    var isAdmin = false

    fun setPlayer(player: Player): Player {
        this.priority = player.priority
        this. gloryPoint = player.gloryPoint
        this.gloryPointUsed = player.gloryPointUsed
        this. activated = player.activated
        this.isAdmin = player.isAdmin
        return this
    }
}