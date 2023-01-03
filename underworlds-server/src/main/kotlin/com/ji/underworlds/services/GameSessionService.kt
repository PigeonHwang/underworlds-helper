package com.ji.underworlds.services

import com.ji.underworlds.models.GameSession
import com.ji.underworlds.models.Player
import com.ji.underworlds.models.PlayerInput
import java.util.concurrent.atomic.AtomicInteger

class GameSessionService {
    fun createGameSession(gameSessionList: MutableList<GameSession>, gameSessionId: Int): GameSession {
        gameSessionList.add(GameSession(gameSessionId))
        return gameSessionList.first { it.id == gameSessionId }
    }

    fun joinPlayer(gameSession: GameSession, player: Player): GameSession {
        return gameSession.addPlayer(player)
    }

    fun leftPlayer(gameSession: GameSession, playerInput: PlayerInput): GameSession {
        return gameSession.removePlayer(playerInput)
    }

    fun updatePlayer(gameSession: GameSession, playerInput: PlayerInput): GameSession {
        return gameSession.setPlayer(playerInput)
    }
}