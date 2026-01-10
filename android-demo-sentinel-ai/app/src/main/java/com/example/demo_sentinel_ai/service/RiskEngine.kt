package com.example.demo_sentinel_ai.service

import com.example.demo_sentinel_ai.model.RiskLevel
import com.example.demo_sentinel_ai.model.ScamDetection
import com.example.demo_sentinel_ai.model.SignalStatus
import com.example.demo_sentinel_ai.domain.ai.GeminiRepository
import kotlinx.coroutines.flow.first

class RiskEngine {

    private val geminiRepository = GeminiRepository()

    suspend fun calculateRisk(
        chatRisk: String,
        locationMismatch: Boolean,
        trustScore: Int,
        chatText: String? = null
    ): ScamDetection {
        var score = 0
        
        // Chat Risk Factor
        when (chatRisk) {
            "HIGH" -> score += 50
            "MEDIUM" -> score += 30
            "LOW" -> score += 10
        }

        // Location Mismatch Factor
        if (locationMismatch) {
            score += 40
        }

        // Trust Score Factor (Inverse)
        if (trustScore < 20) {
            score += 20 // New contact / low trust
        }

        // AI Classification (if text provided)
        var aiTactics = emptyList<String>()
        if (chatText != null) {
            try {
                val classification = geminiRepository.classifyChat(chatText).first()
                if (classification.isScam && classification.confidence > 70) {
                    score += 30
                    aiTactics = classification.tactics
                }
            } catch (e: Exception) {
                // AI failed, ignore
            }
        }

        // Cap at 100
        score = score.coerceAtMost(100)

        // Determine Level
        val level = when {
            score >= 80 -> RiskLevel.CRITICAL
            score >= 60 -> RiskLevel.HIGH
            score >= 40 -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }

        // Determine Traffic Lights
        val trafficLights = mapOf(
            "Chat" to if (chatRisk == "HIGH" || aiTactics.isNotEmpty()) SignalStatus.RED else SignalStatus.GREEN,
            "Location" to if (locationMismatch) SignalStatus.RED else SignalStatus.GREEN,
            "Trust" to if (trustScore < 50) SignalStatus.YELLOW else SignalStatus.GREEN
        )

        return ScamDetection(
            sourceApp = "SentinelAI Engine",
            riskScore = score,
            riskLevel = level,
            matchedPatterns = if (aiTactics.isNotEmpty()) aiTactics else (if (score > 50) listOf("Mock Pattern 1") else emptyList()),
            suspiciousText = chatText ?: "Mock suspicious text",
            trafficLights = trafficLights
        )
    }
}

