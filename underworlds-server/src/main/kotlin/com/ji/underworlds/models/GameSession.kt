package com.ji.underworlds.models

import com.fasterxml.jackson.annotation.JsonManagedReference

class GameSession {
    var id: Long = 0
    @JsonManagedReference
    var players = mutableListOf<Player>()
    var round = 1
    var phase = 1
    var isActivationStep = 0
    var isPowerStep = 0
}