package com.example.demo_sentinel_ai.model

import android.graphics.Bitmap
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Holds all information about a detected scam for display in the warning screen.
 */
data class ScamDetection(
    val id: Long = System.currentTimeMillis(),
    val timestamp: Long = System.currentTimeMillis(),
    val sourceApp: String,
    val chatPartner: String? = null,
    val riskScore: Int,
    val riskLevel: RiskLevel,
    val matchedPatterns: List<String>,
    val suspiciousText: String,
    val screenshot: Bitmap? = null,
    val aiReasoning: String? = null,
    val socraticQuestions: List<String> = emptyList(),
    val interactiveQuestions: List<InteractiveQuestion> = emptyList(),
    val trafficLights: Map<String, SignalStatus> = emptyMap()
) {
    val formattedTime: String
        get() = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(timestamp))

    val formattedDate: String
        get() = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))

    val appDisplayName: String
        get() = when (sourceApp) {
            "jp.naver.line.android" -> "LINE"
            "com.facebook.orca" -> "Messenger"
            "com.facebook.mlite" -> "Messenger Lite"
            "com.whatsapp" -> "WhatsApp"
            "com.whatsapp.w4b" -> "WhatsApp Business"
            else -> sourceApp
        }
}

data class InteractiveQuestion(
    val questionText: String,
    val isSafeAnswerYes: Boolean, // True if "Yes" is the safe answer, False if "No" is safe
    val feedbackSafe: String,
    val feedbackRisk: String
)

enum class RiskLevel(val displayName: String, val description: String) {
    LOW(
        "Low Risk",
        "Some suspicious keywords detected. Stay cautious."
    ),
    MEDIUM(
        "Medium Risk",
        "Multiple scam indicators found. Be careful before sharing personal info or money."
    ),
    HIGH(
        "High Risk",
        "Strong scam patterns detected. This conversation shows classic fraud tactics."
    ),
    CRITICAL(
        "Critical Risk",
        "Extremely dangerous! This matches known scam patterns. Do NOT send money or personal information."
    )
}

enum class SignalStatus {
    GREEN, YELLOW, RED
}

/**
 * Singleton to hold the most recent detection for display.
 * In a production app, you'd use a database or proper state management.
 */
object DetectionRepository {
    private var latestDetection: ScamDetection? = null
    private val recentDetections = mutableListOf<ScamDetection>()

    fun saveDetection(detection: ScamDetection) {
        latestDetection = detection
        recentDetections.add(0, detection)
        // Keep only last 10 detections
        if (recentDetections.size > 10) {
            recentDetections.removeAt(recentDetections.lastIndex)
        }
    }

    fun getLatestDetection(): ScamDetection? = latestDetection

    fun getRecentDetections(): List<ScamDetection> = recentDetections.toList()

    fun clearLatest() {
        latestDetection = null
    }
}
