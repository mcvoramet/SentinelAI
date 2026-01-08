package com.example.demo_sentinel_ai.model

import com.example.demo_sentinel_ai.model.RiskLevel
import com.example.demo_sentinel_ai.model.SignalStatus

/**
 * Defines the "Niran vs Jennifer Wong" demo scenario with bilingual support.
 */
object DemoScenario {

    enum class Language {
        ENGLISH, THAI
    }

    data class ScenarioData(
        val sourceApp: String = "jp.naver.line.android",
        val appDisplayName: String = "LINE",
        val chatPartner: String,
        val riskScore: Int = 85,
        val riskLevel: RiskLevel = RiskLevel.CRITICAL,
        val matchedPatterns: List<String>,
        val suspiciousText: String,
        val aiReasoning: String,
        val socraticQuestions: List<String>,
        val trafficLights: Map<String, SignalStatus>
    )

    fun getNiranScenario(language: Language): ScenarioData {
        return when (language) {
            Language.ENGLISH -> ScenarioData(
                chatPartner = "Jennifer Wong",
                matchedPatterns = listOf(
                    "guaranteed profit",
                    "don't tell anyone",
                    "urgent",
                    "investment opportunity"
                ),
                suspiciousText = "I have a guaranteed profit opportunity. 50% returns monthly. Don't tell anyone, this is a secret. You need to act urgent.",
                aiReasoning = "High risk detected due to 'guaranteed profit' claims and secrecy requests. The recipient location (Rayong) does not match your current location (Bangkok).",
                socraticQuestions = listOf(
                    "Have you ever met Jennifer Wong in person?",
                    "Why are they asking you to keep this transaction secret?",
                    "If it's guaranteed, why do they need your money?"
                ),
                trafficLights = mapOf(
                    "chat" to SignalStatus.RED,
                    "location" to SignalStatus.RED,
                    "relationship" to SignalStatus.YELLOW
                )
            )
            Language.THAI -> ScenarioData(
                chatPartner = "Jennifer Wong",
                matchedPatterns = listOf(
                    "การันตีกำไร", // guaranteed profit
                    "ห้ามบอกใคร", // don't tell anyone
                    "ด่วนที่สุด", // urgent
                    "โอกาสการลงทุน" // investment opportunity
                ),
                suspiciousText = "มีโอกาสการลงทุนที่การันตีกำไร 50% ต่อเดือน ห้ามบอกใครนะ เป็นความลับ ต้องรีบตัดสินใจด่วนที่สุด",
                aiReasoning = "ตรวจพบความเสี่ยงสูงจากการอ้างผลกำไรที่แน่นอนและการขอให้ปิดเป็นความลับ ตำแหน่งผู้รับเงิน (ระยอง) ไม่ตรงกับตำแหน่งของคุณ (กรุงเทพฯ)",
                socraticQuestions = listOf(
                    "คุณเคยเจอ Jennifer Wong ตัวจริงหรือไม่?",
                    "ทำไมเขาถึงขอให้คุณเก็บเรื่องนี้เป็นความลับ?",
                    "ถ้าได้กำไรแน่นอน ทำไมเขาถึงต้องการเงินของคุณ?"
                ),
                trafficLights = mapOf(
                    "chat" to SignalStatus.RED,
                    "location" to SignalStatus.RED,
                    "relationship" to SignalStatus.YELLOW
                )
            )
        }
    }
}
