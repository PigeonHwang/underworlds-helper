package com.ji.underworlds.models

import io.ktor.server.websocket.*

class Player {
    var id: Long = 0
    lateinit var session: WebSocketServerSession
    var playerName = "anonymous"
    var gameCode = "0000"
    var order = 0
    var gloryPoint = 0
    var gloryPointUsed = 0
    var activationPhase = 0
    var isAdmin = false
}