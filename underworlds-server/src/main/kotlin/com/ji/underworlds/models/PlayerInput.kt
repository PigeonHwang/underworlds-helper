package com.ji.underworlds.models

import io.ktor.server.websocket.*

class PlayerInput(
    var id: Int? = null,
    var name: String? = null,
    var websocketServerSession: WebSocketServerSession? = null,
    var priority: Int? = null,
    var gloryPoint: Int? = null,
    var gloryPointUsed: Int? = null,
    var activated: Boolean? = null,
    var isAdmin: Boolean? = null
)