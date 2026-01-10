package com.example.demo_sentinel_ai.model

import com.example.demo_sentinel_ai.R
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
        val trafficLights: Map<String, SignalStatus>,
        val mockScreenshotResId: Int? = null,
        val interactiveQuestions: List<InteractiveQuestion> = emptyList()
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
                ),
                mockScreenshotResId = R.drawable.demo_screenshot_niran,
                interactiveQuestions = listOf(
                    InteractiveQuestion(
                        questionText = "Have you met Jennifer Wong in person?",
                        isSafeAnswerYes = true,
                        feedbackSafe = "Good. Meeting in person builds trust, but still be cautious.",
                        feedbackRisk = "Risk! Scammers often build relationships online without ever meeting."
                    ),
                    InteractiveQuestion(
                        questionText = "Did she ask you to keep this investment secret?",
                        isSafeAnswerYes = false, // "No" is safe (she didn't ask), "Yes" is risk (she asked)
                        feedbackSafe = "Good. Legitimate investments don't require secrecy.",
                        feedbackRisk = "Warning! Scammers use secrecy to prevent you from getting advice."
                    ),
                    InteractiveQuestion(
                        questionText = "Does the return rate (50% monthly) seem realistic?",
                        isSafeAnswerYes = false,
                        feedbackSafe = "Correct. High returns with 'guarantees' are a major red flag.",
                        feedbackRisk = "Danger! 50% monthly returns are impossible for legitimate investments."
                    )
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
                ),
                mockScreenshotResId = R.drawable.demo_screenshot_niran,
                interactiveQuestions = listOf(
                    InteractiveQuestion(
                        questionText = "คุณเคยเจอ Jennifer Wong ตัวจริงหรือไม่?",
                        isSafeAnswerYes = true,
                        feedbackSafe = "ดีมาก การเจอกันตัวจริงช่วยสร้างความมั่นใจ แต่ยังต้องระวัง",
                        feedbackRisk = "ความเสี่ยง! มิจฉาชีพมักสร้างความสัมพันธ์ออนไลน์โดยไม่เคยเจอหน้า"
                    ),
                    InteractiveQuestion(
                        questionText = "เขาขอให้คุณเก็บเรื่องการลงทุนนี้เป็นความลับใช่ไหม?",
                        isSafeAnswerYes = false,
                        feedbackSafe = "ดีมาก การลงทุนที่ถูกกฎหมายไม่จำเป็นต้องปิดบัง",
                        feedbackRisk = "คำเตือน! มิจฉาชีพใช้ความลับเพื่อป้องกันไม่ให้คุณขอคำปรึกษา"
                    ),
                    InteractiveQuestion(
                        questionText = "ผลตอบแทน 50% ต่อเดือน ดูเป็นไปได้จริงหรือไม่?",
                        isSafeAnswerYes = false,
                        feedbackSafe = "ถูกต้อง ผลตอบแทนสูงเกินจริงและ 'การันตี' เป็นสัญญาณอันตราย",
                        feedbackRisk = "อันตราย! ผลตอบแทน 50% ต่อเดือนเป็นไปไม่ได้สำหรับการลงทุนที่ถูกกฎหมาย"
                    )
                )
            )
        }
    }

    fun getQRScenario(language: Language): ScenarioData {
        return when (language) {
            Language.ENGLISH -> ScenarioData(
                sourceApp = "com.google.android.apps.camera",
                appDisplayName = "Camera/Scanner",
                chatPartner = "Unknown Source",
                matchedPatterns = listOf(
                    "verify account",
                    "claim prize",
                    "click link",
                    "suspicious url"
                ),
                suspiciousText = "Scan successful. To claim your $500 gift card, verify your bank account here: http://secure-bank-login.xyz/verify",
                aiReasoning = "CRITICAL: Malicious QR Code detected. The URL 'http://secure-bank-login.xyz' is a known phishing site attempting to steal banking credentials. The domain was created only 2 days ago.",
                socraticQuestions = listOf(
                    "Did you expect a payment request from this QR code?",
                    "Is the website URL exactly what you expect (e.g., myshop.com)?",
                    "Why is it asking for bank details for a 'gift'?"
                ),
                trafficLights = mapOf(
                    "url_reputation" to SignalStatus.RED,
                    "domain_age" to SignalStatus.RED,
                    "context" to SignalStatus.YELLOW
                ),
                mockScreenshotResId = R.drawable.demo_screenshot_qr,
                interactiveQuestions = listOf(
                    InteractiveQuestion(
                        questionText = "Did you intend to scan this QR code?",
                        isSafeAnswerYes = true,
                        feedbackSafe = "Ok. But always verify where the QR code takes you.",
                        feedbackRisk = "Warning! Scanning unknown QR codes is dangerous."
                    ),
                    InteractiveQuestion(
                        questionText = "Does the URL (secure-bank-login.xyz) look correct?",
                        isSafeAnswerYes = false,
                        feedbackSafe = "Correct. The URL looks suspicious and generic.",
                        feedbackRisk = "Danger! This URL does not match any official bank website."
                    ),
                    InteractiveQuestion(
                        questionText = "Is it reasonable to ask for bank passwords for a gift card?",
                        isSafeAnswerYes = false,
                        feedbackSafe = "Correct. Legitimate giveaways never ask for passwords.",
                        feedbackRisk = "Stop! Never share your banking password for a prize."
                    )
                )
            )
            Language.THAI -> ScenarioData(
                sourceApp = "com.google.android.apps.camera",
                appDisplayName = "กล้อง/สแกนเนอร์",
                chatPartner = "ไม่ระบุแหล่งที่มา",
                matchedPatterns = listOf(
                    "ยืนยันบัญชี", // verify account
                    "รับรางวัล", // claim prize
                    "คลิกลิงก์", // click link
                    "ลิงก์น่าสงสัย" // suspicious url
                ),
                suspiciousText = "สแกนสำเร็จ เพื่อรับบัตรของขวัญ 500 บาท กรุณายืนยันบัญชีธนาคารของคุณที่นี่: http://secure-bank-login.xyz/verify",
                aiReasoning = "วิกฤต: ตรวจพบ QR Code อันตราย URL 'http://secure-bank-login.xyz' เป็นเว็บไซต์ปลอมที่พยายามขโมยข้อมูลธนาคาร โดเมนนี้เพิ่งถูกสร้างขึ้นเมื่อ 2 วันก่อน",
                socraticQuestions = listOf(
                    "คุณคาดหวังว่าจะมีการขอข้อมูลการชำระเงินจาก QR code นี้หรือไม่?",
                    "ชื่อเว็บไซต์ URL ตรงกับที่คุณรู้จักหรือไม่ (เช่น myshop.com)?",
                    "ทำไมถึงมีการขอข้อมูลธนาคารสำหรับการ 'แจกรางวัล'?"
                ),
                trafficLights = mapOf(
                    "url_reputation" to SignalStatus.RED,
                    "domain_age" to SignalStatus.RED,
                    "context" to SignalStatus.YELLOW
                ),
                mockScreenshotResId = R.drawable.demo_screenshot_qr,
                interactiveQuestions = listOf(
                    InteractiveQuestion(
                        questionText = "คุณตั้งใจจะสแกน QR Code นี้หรือไม่?",
                        isSafeAnswerYes = true,
                        feedbackSafe = "โอเค แต่ควรตรวจสอบเสมอว่า QR Code พาไปที่ไหน",
                        feedbackRisk = "คำเตือน! การสแกน QR Code ที่ไม่รู้จักเป็นเรื่องอันตราย"
                    ),
                    InteractiveQuestion(
                        questionText = "URL (secure-bank-login.xyz) ดูถูกต้องหรือไม่?",
                        isSafeAnswerYes = false,
                        feedbackSafe = "ถูกต้อง URL นี้ดูน่าสงสัยและไม่เป็นทางการ",
                        feedbackRisk = "อันตราย! URL นี้ไม่ตรงกับเว็บไซต์ธนาคารที่เป็นทางการ"
                    ),
                    InteractiveQuestion(
                        questionText = "สมเหตุสมผลหรือไม่ที่จะขอรหัสผ่านธนาคารเพื่อแลกของรางวัล?",
                        isSafeAnswerYes = false,
                        feedbackSafe = "ถูกต้อง การแจกของรางวัลจริงไม่เคยขอรหัสผ่าน",
                        feedbackRisk = "หยุด! ห้ามบอกรหัสผ่านธนาคารเพื่อแลกของรางวัลเด็ดขาด"
                    )
                )
            )
        }
    }
}
