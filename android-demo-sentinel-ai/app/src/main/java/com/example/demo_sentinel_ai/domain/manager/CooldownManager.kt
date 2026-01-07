package com.example.demo_sentinel_ai.domain.manager

object CooldownManager {
    private var cooldownEndTime: Long = 0

    fun startCooldown(durationMinutes: Int = 10) {
        cooldownEndTime = System.currentTimeMillis() + (durationMinutes * 60 * 1000)
    }

    fun isCooldownActive(): Boolean {
        return System.currentTimeMillis() < cooldownEndTime
    }

    fun getRemainingTimeSeconds(): Long {
        if (!isCooldownActive()) return 0
        return (cooldownEndTime - System.currentTimeMillis()) / 1000
    }
}

