package com.ji.underworlds.models

import io.ktor.websocket.*

class Player {
    var id: Long = 0
    lateinit var session: DefaultWebSocketSession
    var playerName = "anonymous"
    var gameCode = "0000"
    var gloryPoint = 0
    var gloryPointUsed = 0
    var activation = 0
}