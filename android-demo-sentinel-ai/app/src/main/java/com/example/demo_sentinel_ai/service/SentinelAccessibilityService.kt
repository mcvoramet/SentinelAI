package com.example.demo_sentinel_ai.service

import android.accessibilityservice.AccessibilityService
import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Display
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.demo_sentinel_ai.analysis.ScamPatternAnalyzer
import com.example.demo_sentinel_ai.model.DetectionRepository
import com.example.demo_sentinel_ai.model.RiskLevel
import com.example.demo_sentinel_ai.model.ScamDetection
import com.example.demo_sentinel_ai.service.detector.DetectorRegistry
import com.example.demo_sentinel_ai.service.detector.NodeTraversal
import java.util.concurrent.Executor

/**
 * Main accessibility service that orchestrates scam detection across messaging apps.
 */
class SentinelAccessibilityService : AccessibilityService() {

    private lateinit var notificationHelper: NotificationHelper
    private var lastAnalyzedText = ""
    private var accumulatedRiskScore = 0
    private var lastWarningTime = 0L
    private var isProcessingWarning = false

    private val mainHandler = Handler(Looper.getMainLooper())
    private val mainExecutor = Executor { command -> mainHandler.post(command) }

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(this)
        Log.d(TAG, "Service started")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Monitoring: ${DetectorRegistry.supportedPackages.joinToString { DetectorRegistry.getAppDisplayName(it) }}")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val packageName = event.packageName?.toString() ?: return
        if (packageName !in DetectorRegistry.supportedPackages) return

        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> resetContext()
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> processScreenContent(packageName)
        }
    }

    private fun processScreenContent(packageName: String) {
        if (isProcessingWarning) return
        if (System.currentTimeMillis() - lastWarningTime < WARNING_COOLDOWN_MS) return

        val detector = DetectorRegistry.getDetector(packageName) ?: return
        val rootNode = rootInActiveWindow ?: return

        try {
            val appName = detector.appDisplayName
            val isInsideChat = detector.isInsideChat(rootNode)

            if (!isInsideChat) {
                Log.d(TAG, "[$appName] Outside Chat")
                return
            }

            val chatPartner = detector.extractChatPartner(rootNode)
            Log.d(TAG, "[$appName] Inside Chat: ${chatPartner ?: "Unknown"}")

            val screenText = extractText(rootNode)
            if (screenText == lastAnalyzedText || screenText.length < 20) return
            lastAnalyzedText = screenText

            val result = ScamPatternAnalyzer.analyze(screenText)
            if (result.score > 0) {
                accumulatedRiskScore += result.score
                if (accumulatedRiskScore >= ScamPatternAnalyzer.RISK_THRESHOLD) {
                    triggerWarning(packageName, result, screenText, chatPartner)
                }
            }
        } finally {
            rootNode.recycle()
        }
    }

    private fun triggerWarning(
        packageName: String,
        result: ScamPatternAnalyzer.AnalysisResult,
        screenText: String,
        chatPartner: String?
    ) {
        isProcessingWarning = true
        lastWarningTime = System.currentTimeMillis()
        accumulatedRiskScore = 0

        val relevantText = ScamPatternAnalyzer.extractRelevantText(screenText, result.matchedPatterns)
        captureScreenshotAndNotify(packageName, result, relevantText, chatPartner)
    }

    private fun captureScreenshotAndNotify(
        packageName: String,
        result: ScamPatternAnalyzer.AnalysisResult,
        relevantText: String,
        chatPartner: String?
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                takeScreenshot(Display.DEFAULT_DISPLAY, mainExecutor, object : TakeScreenshotCallback {
                    override fun onSuccess(screenshot: ScreenshotResult) {
                        val bitmap = try {
                            Bitmap.wrapHardwareBuffer(screenshot.hardwareBuffer, screenshot.colorSpace)
                                ?.copy(Bitmap.Config.ARGB_8888, false)
                        } catch (e: Exception) { null }
                        finally { screenshot.hardwareBuffer.close() }

                        saveAndNotify(packageName, result, relevantText, bitmap, chatPartner)
                        isProcessingWarning = false
                    }

                    override fun onFailure(errorCode: Int) {
                        saveAndNotify(packageName, result, relevantText, null, chatPartner)
                        isProcessingWarning = false
                    }
                })
            } catch (e: Exception) {
                saveAndNotify(packageName, result, relevantText, null, chatPartner)
                isProcessingWarning = false
            }
        } else {
            saveAndNotify(packageName, result, relevantText, null, chatPartner)
            isProcessingWarning = false
        }
    }

    private fun saveAndNotify(
        packageName: String,
        result: ScamPatternAnalyzer.AnalysisResult,
        relevantText: String,
        screenshot: Bitmap?,
        chatPartner: String?
    ) {
        val detection = ScamDetection(
            sourceApp = packageName,
            chatPartner = chatPartner,
            riskScore = result.score,
            riskLevel = result.riskLevel,
            matchedPatterns = result.matchedPatterns,
            suspiciousText = relevantText,
            screenshot = screenshot
        )
        DetectionRepository.saveDetection(detection)

        val title = when (result.riskLevel) {
            RiskLevel.CRITICAL -> "CRITICAL: Scam Detected!"
            RiskLevel.HIGH -> "HIGH RISK: Scam Warning"
            RiskLevel.MEDIUM -> "WARNING: Suspicious Activity"
            RiskLevel.LOW -> "CAUTION: Stay Alert"
        }

        val message = buildString {
            chatPartner?.let { append("Chat with: $it\n") }
            append("Detected in ${DetectorRegistry.getAppDisplayName(packageName)}\n")
            append("Keywords: ${result.matchedPatterns.take(3).joinToString(", ")}")
            if (result.matchedPatterns.size > 3) append(" +${result.matchedPatterns.size - 3} more")
        }

        notificationHelper.showScamWarning(
            title = title,
            message = message,
            matchedPatterns = result.matchedPatterns,
            chatPartner = chatPartner,
            appName = DetectorRegistry.getAppDisplayName(packageName)
        )
        Log.d(TAG, "Alert: ${result.riskLevel} - ${result.matchedPatterns.size} patterns")
    }

    private fun extractText(root: AccessibilityNodeInfo): String {
        val builder = StringBuilder()
        NodeTraversal.traverse(root) { node ->
            node.text?.let { builder.append(it).append(" ") }
        }
        return builder.toString()
    }

    private fun resetContext() {
        accumulatedRiskScore = 0
        lastAnalyzedText = ""
    }

    override fun onInterrupt() {}
    override fun onDestroy() { super.onDestroy(); Log.d(TAG, "Service stopped") }

    companion object {
        private const val TAG = "SentinelAI"
        private const val WARNING_COOLDOWN_MS = 10_000L
    }
}
