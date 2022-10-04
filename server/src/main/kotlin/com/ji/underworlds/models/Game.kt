package com.ji.underworlds.models

class Game {
    var playerList = mutableListOf<Player>()
    var round = 0
    var isActivationStep = false
    var isPowerStep = false
    var playerSequence = mutableListOf<Player>()
}