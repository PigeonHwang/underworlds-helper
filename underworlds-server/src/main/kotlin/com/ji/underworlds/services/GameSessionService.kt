package com.ji.underworlds.services

import com.ji.underworlds.models.GameSession
import com.ji.underworlds.models.Player
import java.util.concurrent.atomic.AtomicLong

class GameSessionService {
    val gameSessions = mutableListOf<GameSession>()
    private var uids = AtomicLong(0)

    fun createGameSession() {
        val gameSession = GameSession()
        gameSession.id = uids.getAndIncrement()
        gameSessions.add(gameSession)
    }

    fun joinPlayer(gameSession: GameSession, player: Player): GameSession {
        gameSession.players.add(player)
        return gameSession
    }

    fun leftPlayer(gameSession: GameSession, player: Player): GameSession {
        gameSession.players = gameSession.players.filter { it != player }.toMutableList()
        return gameSession
    }
}