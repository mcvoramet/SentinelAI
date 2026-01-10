package com.example.demo_sentinel_ai.service

import com.example.demo_sentinel_ai.model.RiskLevel
import org.junit.Assert.assertEquals
import org.junit.Test

import kotlinx.coroutines.runBlocking

class RiskEngineTest {

    @Test
    fun calculateRisk_highRiskSignals_returnsCriticalScore() = runBlocking {
        val engine = RiskEngine()
        val result = engine.calculateRisk(
            chatRisk = "HIGH",
            locationMismatch = true,
            trustScore = 10
        )

        assertEquals(RiskLevel.CRITICAL, result.riskLevel)
        assertEquals(true, result.riskScore >= 80)
    }

    @Test
    fun calculateRisk_safeSignals_returnsLowScore() = runBlocking {
        val engine = RiskEngine()
        val result = engine.calculateRisk(
            chatRisk = "LOW",
            locationMismatch = false,
            trustScore = 90
        )

        assertEquals(RiskLevel.LOW, result.riskLevel)
        assertEquals(true, result.riskScore < 30)
    }
}

