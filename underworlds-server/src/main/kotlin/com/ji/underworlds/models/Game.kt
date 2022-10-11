package com.ji.underworlds.models

import com.fasterxml.jackson.annotation.JsonManagedReference
import io.ktor.server.websocket.*
import java.util.concurrent.ConcurrentHashMap

class Game {
    @JsonManagedReference
    var players = mutableListOf<Player>()
    var round = 1
    var phase = 1
    var isActivationStep = false
    var isPowerStep = false
}