package com.example.demo_sentinel_ai.analysis

import com.example.demo_sentinel_ai.model.RiskLevel

/**
 * Analyzes text for scam patterns using weighted keyword matching.
 */
object ScamPatternAnalyzer {

    /**
     * Result of scam analysis.
     */
    data class AnalysisResult(
        val score: Int,
        val matchedPatterns: List<String>,
        val riskLevel: RiskLevel
    )

    /**
     * Analyzes text for scam patterns.
     * @return Analysis result with score, matched patterns, and risk level
     */
    fun analyze(text: String): AnalysisResult {
        val lowerText = text.lowercase()
        val matched = mutableListOf<String>()
        var score = 0

        for ((pattern, weight) in SCAM_PATTERNS) {
            if (lowerText.contains(pattern.lowercase())) {
                matched.add(pattern)
                score += weight
            }
        }

        val riskLevel = when {
            score >= 10 -> RiskLevel.CRITICAL
            score >= 6 -> RiskLevel.HIGH
            score >= 3 -> RiskLevel.MEDIUM
            score > 0 -> RiskLevel.LOW
            else -> RiskLevel.LOW
        }

        return AnalysisResult(score, matched, riskLevel)
    }

    /**
     * Extracts relevant text snippets around matched patterns.
     */
    fun extractRelevantText(fullText: String, patterns: List<String>, contextChars: Int = 50): String {
        val lowerText = fullText.lowercase()
        val snippets = patterns.mapNotNull { pattern ->
            val index = lowerText.indexOf(pattern.lowercase())
            if (index != -1) {
                val start = maxOf(0, index - contextChars)
                val end = minOf(fullText.length, index + pattern.length + contextChars)
                fullText.substring(start, end).trim()
            } else null
        }.toSet()

        return snippets.joinToString(" ... ").take(500)
    }

    /** Risk threshold to trigger warning */
    const val RISK_THRESHOLD = 3

    /**
     * Scam patterns with weights.
     * - High risk (3): Strong scam indicators
     * - Medium risk (2): Common in scams but could be legitimate
     * - Low risk (1): Suspicious but need more context
     */
    private val SCAM_PATTERNS = mapOf(
        // High risk (weight 3)
        "guaranteed profit" to 3,
        "การันตีกำไร" to 3,
        "send me money" to 3,
        "โอนเงิน" to 3,
        "don't tell anyone" to 3,
        "อย่าบอกใคร" to 3,

        // Medium risk (weight 2)
        "investment opportunity" to 2,
        "ลงทุน" to 2,
        "USDT" to 2,
        "crypto" to 2,
        "urgent" to 2,
        "รีบด่วน" to 2,
        "limited time" to 2,
        "Western Union" to 2,

        // Lower risk (weight 1)
        "passive income" to 1,
        "รายได้เสริม" to 1,
        "100% safe" to 1,
        "no risk" to 1,
        "ไม่มีความเสี่ยง" to 1,
        "act now" to 1
    )
}
