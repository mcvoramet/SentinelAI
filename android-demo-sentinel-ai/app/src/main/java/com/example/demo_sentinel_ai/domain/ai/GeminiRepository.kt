package com.example.demo_sentinel_ai.domain.ai

import com.example.demo_sentinel_ai.BuildConfig
import com.example.demo_sentinel_ai.model.ScamDetection
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GeminiRepository {

    private val flashModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    private val proModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-pro",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    private val gson = Gson()

    suspend fun classifyChat(text: String): Flow<ScamClassification> = flow {
        val prompt = """
            Analyze the following chat text for scam tactics. 
            Return a JSON object with the following structure:
            {
                "isScam": boolean,
                "confidence": int (0-100),
                "tactics": [string]
            }
            
            Text: "$text"
        """.trimIndent()

        try {
            val response = flashModel.generateContent(prompt)
            val json = response.text?.trim()?.removePrefix("```json")?.removeSuffix("```") ?: ""
            val classification = gson.fromJson(json, ScamClassification::class.java)
            emit(classification)
        } catch (e: Exception) {
            // Fallback for demo if API fails or key invalid
            emit(ScamClassification(true, 85, listOf("Urgency (Offline Fallback)", "Secrecy")))
        }
    }

    suspend fun generateExplanation(detection: ScamDetection): Flow<RiskExplanation> = flow {
        val prompt = """
            You are a protective AI guardian called SentinelAI.
            A user is about to make a risky payment.
            
            Context:
            - Risk Level: ${detection.riskLevel}
            - App: ${detection.sourceApp}
            - Chat Partner: ${detection.chatPartner}
            - Tactics Detected: ${detection.matchedPatterns.joinToString(", ")}
            - Suspicious Text: "${detection.suspiciousText}"
            
            Task:
            1. Write a calm, rational explanation of WHY this is risky (max 2 sentences).
            2. Provide 2 Socratic questions to help the user reflect.
            
            Return JSON:
            {
                "explanation": "string",
                "questions": ["string", "string"]
            }
        """.trimIndent()

        try {
            val response = proModel.generateContent(prompt)
            val json = response.text?.trim()?.removePrefix("```json")?.removeSuffix("```") ?: ""
            val explanation = gson.fromJson(json, RiskExplanation::class.java)
            emit(explanation)
        } catch (e: Exception) {
            emit(RiskExplanation(
                "We detected high-pressure tactics often used in scams. The recipient's location does not match their claims.",
                listOf("Have you met this person in real life?", "Why is the payment urgent?")
            ))
        }
    }
}

data class ScamClassification(
    val isScam: Boolean,
    val confidence: Int,
    val tactics: List<String>
)

data class RiskExplanation(
    val explanation: String,
    val questions: List<String>
)

