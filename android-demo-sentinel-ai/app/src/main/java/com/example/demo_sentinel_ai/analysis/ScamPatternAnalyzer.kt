package com.example.demo_sentinel_ai.analysis

import com.example.demo_sentinel_ai.model.RiskLevel
import com.example.demo_sentinel_ai.model.SignalStatus
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Analyzes text for scam patterns using Gemini 3 Flash for intelligent reasoning.
 */
object ScamPatternAnalyzer {

    private val gson = Gson()
    
    // API Key from BuildConfig
    private val GEMINI_API_KEY = com.example.demo_sentinel_ai.BuildConfig.GEMINI_API_KEY

    /**
     * Result of scam analysis.
     */
    data class AnalysisResult(
        val score: Int,
        val matchedPatterns: List<String>,
        val riskLevel: RiskLevel,
        val aiReasoning: String? = null,
        val trafficLights: Map<String, SignalStatus> = emptyMap(),
        val socraticQuestions: List<String> = emptyList()
    )

    /**
     * Gemini JSON Response Format
     */
    private data class GeminiResponse(
        val risk_score: Int,
        val risk_level: String,
        val reasoning: String,
        val matched_patterns: List<String>,
        val traffic_lights: Map<String, String>,
        val socratic_questions: List<String>
    )

    /**
     * Analyzes text for scam patterns using Gemini 3 Flash.
     * @return Analysis result with score, matched patterns, and risk level
     */
    suspend fun analyzeWithGemini(text: String, context: String = ""): AnalysisResult = withContext(Dispatchers.IO) {
        if (GEMINI_API_KEY == "YOUR_API_KEY_HERE") {
            return@withContext analyzeLegacy(text)
        }

        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = GEMINI_API_KEY
        )

        val prompt = """
            You are SentinelAI, a fraud prevention engine for mobile messaging apps.
            Analyze the following chat text and context for scam patterns (romance scams, investment fraud, pig butchering, etc.).
            
            TEXT TO ANALYZE:
            $text
            
            CONTEXT:
            $context
            
            Provide your analysis in JSON format with exactly these fields:
            - risk_score: 0-10 integer
            - risk_level: LOW, MEDIUM, HIGH, or CRITICAL
            - reasoning: 1-sentence explanation for a system notification
            - matched_patterns: list of detected scam tactics (e.g. "urgency", "secrecy")
            - traffic_lights: map of "chat", "location", "relationship" to "GREEN", "YELLOW", or "RED"
            - socratic_questions: list of 2-3 short questions to ask the user to make them pause
            
            JSON ONLY.
        """.trimIndent()

        try {
            val response = generativeModel.generateContent(prompt)
            val jsonText = response.text?.replace("```json", "")?.replace("```", "")?.trim() ?: throw Exception("Empty response")
            val result = gson.fromJson(jsonText, GeminiResponse::class.java)

            AnalysisResult(
                score = result.risk_score,
                matchedPatterns = result.matched_patterns,
                riskLevel = try { RiskLevel.valueOf(result.risk_level) } catch(e: Exception) { RiskLevel.LOW },
                aiReasoning = result.reasoning,
                trafficLights = result.traffic_lights.mapValues { try { SignalStatus.valueOf(it.value) } catch(e: Exception) { SignalStatus.GREEN } },
                socraticQuestions = result.socratic_questions
            )
        } catch (e: Exception) {
            analyzeLegacy(text)
        }
    }

    /**
     * Original legacy analysis for fallback or local checks.
     */
    fun analyzeLegacy(text: String): AnalysisResult {
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

    /**
     * Keep the public analyze for compatibility, but mark as legacy
     */
    @Deprecated("Use analyzeWithGemini instead")
    fun analyze(text: String): AnalysisResult = analyzeLegacy(text)

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
}
