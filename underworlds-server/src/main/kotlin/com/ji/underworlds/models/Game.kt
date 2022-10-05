package com.ji.underworlds.models

import io.ktor.server.websocket.*
import java.util.concurrent.ConcurrentHashMap

class Game {
    var players = ConcurrentHashMap<WebSocketServerSession, Player>()
    var round = 1
    var phase = 1
    var isActivationStep = false
    var isPowerStep = false
}