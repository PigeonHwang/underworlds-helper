package com.ji.underworlds.models

class Game {
    var playerList = mutableListOf<Player>()
    var round = 1
    var phase = 1
    var isActivationStep = false
    var isPowerStep = false
}