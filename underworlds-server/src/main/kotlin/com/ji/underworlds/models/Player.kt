package com.ji.underworlds.models

import io.ktor.server.websocket.*

class Player {
    var id: Long = 0
    var name = "anonymous"
    var priority = 0
    var gloryPoint = 0
    var gloryPointUsed = 0
    var activated = false
    var isAdmin = false
}