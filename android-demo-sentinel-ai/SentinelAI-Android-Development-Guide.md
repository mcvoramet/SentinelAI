# SentinelAI Android Development Guide

> **Project:** SentinelAI - Mobile Fraud Prevention Engine
> **Hackathon:** Digital Fraud Prevention (Thailand)
> **Timeline:** 1 Week
> **Last Updated:** January 2026

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Technical Feasibility Assessment](#technical-feasibility-assessment)
3. [Development Environment Setup](#development-environment-setup)
4. [Project Structure](#project-structure)
5. [Core Implementation](#core-implementation)
6. [Testing Guide](#testing-guide)
7. [Development Timeline](#development-timeline)
8. [References](#references)

---

## Project Overview

### Problem Statement

Thailand faces an industrial-scale scam epidemic:
- **60-115 billion baht** in annual losses
- Only **4% victim recovery rate**
- "Willing Victim Paradox" — victims resist intervention due to emotional investment (romance scams, pig butchering)

### Solution: SentinelAI

A mobile-embedded threat detector that catches scams **during the grooming phase**, before payment occurs.

### Three Flows

| Flow | Type | Description | Technical Status |
|------|------|-------------|------------------|
| **Flow 1** | Proactive QR/Payment Monitoring | Detect risk when opening banking apps | ❌ Blocked by Thai banks |
| **Flow 2** | Proactive Chat Monitoring | Monitor LINE/Messenger/WhatsApp for scam patterns | ✅ **Feasible** |
| **Flow 3** | On-Demand Check | User-initiated QR/phone verification | ✅ **Feasible** |

### Key Innovation

- Catches users during "grooming phase" before payment
- Uses **Socratic questioning** for behavioral intervention (not blocking)
- Thai + English multilingual support

---

## Technical Feasibility Assessment

### What WORKS (No Root Required)

| Capability | How | Status |
|------------|-----|--------|
| Read visible screen text from chat apps | Android Accessibility Service API | ✅ Works |
| Detect app launches (LINE, WhatsApp, etc.) | AccessibilityEvent TYPE_WINDOW_STATE_CHANGED | ✅ Works |
| Detect if user is on phone call | READ_PHONE_STATE + BroadcastReceiver | ✅ Works |
| Scan QR codes in your own app | Camera permission + ML Kit / ZXing | ✅ Works |
| Show warning notifications | Standard Notification API | ✅ Works |
| Show overlay UI | SYSTEM_ALERT_WINDOW permission | ✅ Works |

### What DOES NOT Work

| Capability | Why | Alternative |
|------------|-----|-------------|
| Monitor Thai banking apps (K PLUS, SCB Easy, Bangkok Bank) | Banks actively detect and **block** any Accessibility Service | Position as "future Knox integration" |
| Read full chat history | WhatsApp/LINE encrypt local data | Read **visible screen** only (sufficient) |
| Read messages in background | Only works when chat app is in foreground | Use NotificationListenerService for notification previews |
| Intercept QR scans from other apps | No camera access to other apps | Provide own QR scanner (Flow 3) |

### Critical Finding: Thai Banking Apps Block Accessibility Services

**Evidence:**
- K PLUS: "app wouldn't work as long as accessibility services weren't switched off"
- Bangkok Bank: "Mobile Banking cannot proceed... accessibility service is operating on your mobile phone"
- Thai Bankers Association coordinates security measures across member banks

**Implication:** Your app and Thai banking apps **cannot coexist** with Accessibility Service enabled.

**Recommended Narrative:** "We catch scams BEFORE users reach the payment stage, so banking app integration isn't needed for core protection."

---

## Development Environment Setup

### Prerequisites

| Tool | Version | Installation |
|------|---------|--------------|
| Android Studio | Latest (Ladybug or newer) | `brew install --cask android-studio` or [Download](https://developer.android.com/studio) |
| JDK | 17+ | Bundled with Android Studio |
| Android SDK | API 34 | Installed via Android Studio |

### Create New Project

1. Open Android Studio
2. **File → New → New Project**
3. Select **"Empty Activity"** (Phone and Tablet)
4. Click **Next**

### Project Configuration

| Field | Value |
|-------|-------|
| Name | `SentinelAI` |
| Package name | `com.sentinelai.app` |
| Language | **Kotlin** |
| Minimum SDK | **API 26 (Android 8.0)** |
| Build configuration language | Kotlin DSL |

5. Click **Finish**

### Why These Choices?

- **Kotlin:** Official Google-recommended language since 2019
- **API 26:** Covers 95%+ of devices, all required Accessibility APIs available
- **Empty Activity:** Uses Jetpack Compose (modern UI), minimal boilerplate

---

## Project Structure

```
SentinelAI/
├── app/
│   ├── src/main/
│   │   ├── java/com/sentinelai/app/
│   │   │   ├── MainActivity.kt                    # Main UI entry point
│   │   │   ├── SentinelAccessibilityService.kt    # Core monitoring service
│   │   │   ├── ScamDetectionEngine.kt             # Pattern matching logic
│   │   │   ├── NotificationHelper.kt              # Warning notifications
│   │   │   └── ui/
│   │   │       ├── screens/
│   │   │       │   ├── HomeScreen.kt              # Main dashboard
│   │   │       │   ├── ScannerScreen.kt           # QR scanner (Flow 3)
│   │   │       │   └── WarningScreen.kt           # Scam warning UI
│   │   │       └── theme/
│   │   │           └── Theme.kt                   # App theming
│   │   ├── res/
│   │   │   ├── xml/
│   │   │   │   └── accessibility_service_config.xml
│   │   │   ├── values/
│   │   │   │   └── strings.xml
│   │   │   └── drawable/                          # Icons, images
│   │   └── AndroidManifest.xml                    # Permissions & service declarations
│   └── build.gradle.kts                           # Dependencies
└── README.md
```

---

## Core Implementation

### 1. AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- Required Permissions -->
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.SentinelAI">

        <!-- Main Activity -->
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.SentinelAI">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!-- Accessibility Service (Core Feature) -->
        <service
            android:name=".SentinelAccessibilityService"
            android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE"
            android:exported="false">
            <intent-filter>
                <action android:name="android.accessibilityservice.AccessibilityService" />
            </intent-filter>
            <meta-data
                android:name="android.accessibilityservice"
                android:resource="@xml/accessibility_service_config" />
        </service>

    </application>

</manifest>
```

### 2. res/xml/accessibility_service_config.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<accessibility-service xmlns:android="http://schemas.android.com/apk/res/android"
    android:accessibilityEventTypes="typeWindowStateChanged|typeWindowContentChanged|typeNotificationStateChanged"
    android:accessibilityFeedbackType="feedbackGeneric"
    android:accessibilityFlags="flagRetrieveInteractiveWindows|flagReportViewIds|flagIncludeNotImportantViews"
    android:canRetrieveWindowContent="true"
    android:canPerformGestures="false"
    android:notificationTimeout="100"
    android:settingsActivity=".MainActivity"
    android:packageNames="jp.naver.line.android,com.facebook.orca,com.whatsapp,com.facebook.mlite"
    android:description="@string/accessibility_service_description" />
```

**Package Names Reference:**

| App | Package Name |
|-----|--------------|
| LINE | `jp.naver.line.android` |
| Facebook Messenger | `com.facebook.orca` |
| Messenger Lite | `com.facebook.mlite` |
| WhatsApp | `com.whatsapp` |
| WhatsApp Business | `com.whatsapp.w4b` |

### 3. res/values/strings.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">SentinelAI</string>
    <string name="accessibility_service_description">
        SentinelAI protects you from scams by monitoring messaging apps for
        fraud patterns. This service reads on-screen text to identify suspicious
        conversations involving investment schemes, romance scams, and other
        fraudulent activities. Your data stays on your device and is never
        uploaded to external servers.
    </string>
</resources>
```

### 4. SentinelAccessibilityService.kt

```kotlin
package com.sentinelai.app

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.app.NotificationCompat

class SentinelAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "SentinelAI"
        private const val CHANNEL_ID = "sentinel_warnings"
        private const val NOTIFICATION_ID = 1001

        // Target apps to monitor
        private val TARGET_PACKAGES = setOf(
            "jp.naver.line.android",      // LINE
            "com.facebook.orca",           // Messenger
            "com.facebook.mlite",          // Messenger Lite
            "com.whatsapp",                // WhatsApp
            "com.whatsapp.w4b"             // WhatsApp Business
        )
    }

    // Scam detection keywords (Thai + English)
    private val scamPatterns = listOf(
        // Investment scams (Thai)
        "การันตีกำไร", "ผลตอบแทนสูง", "ลงทุนด่วน", "กำไร 100%",
        "รายได้เสริม", "ทำเงินออนไลน์", "รวยเร็ว", "ไม่มีความเสี่ยง",

        // Urgency / pressure tactics (Thai)
        "รีบด่วน", "วันนี้วันสุดท้าย", "โอกาสสุดท้าย", "เหลืออีกแค่",
        "ต้องตัดสินใจวันนี้", "พรุ่งนี้ไม่ทัน",

        // Secrecy (Thai)
        "อย่าบอกใคร", "เก็บเป็นความลับ", "ระหว่างเราสองคน",

        // Money transfer (Thai)
        "โอนเงิน", "เลขบัญชี", "พร้อมเพย์", "โอนมาก่อน", "ค่าธรรมเนียม",
        "จ่ายก่อน", "โอนค่า",

        // Crypto scams
        "USDT", "Bitcoin", "คริปโต", "crypto", "wallet address",
        "Binance", "mining", "ขุดเหรียญ",

        // Investment scams (English)
        "guaranteed profit", "guaranteed return", "100% safe",
        "no risk", "double your money", "investment opportunity",
        "passive income", "financial freedom",

        // Romance scam patterns
        "send me money", "Western Union", "MoneyGram",
        "stuck at airport", "need money urgently", "pay customs",
        "release my funds", "frozen account",

        // Urgency (English)
        "act now", "limited time", "urgent", "don't tell anyone",
        "keep this between us", "secret opportunity"
    )

    // Risk scoring thresholds
    private var accumulatedRiskScore = 0
    private var lastAnalyzedText = ""
    private var currentChatPartner = ""

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Log.d(TAG, "SentinelAI Service created")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "✅ SentinelAI Accessibility Service connected and running")

        // Configure service info programmatically if needed
        serviceInfo?.apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                        AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS or
                   AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
            notificationTimeout = 100
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val packageName = event.packageName?.toString() ?: return

        // Only process target messaging apps
        if (packageName !in TARGET_PACKAGES) return

        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                Log.d(TAG, "Window changed: $packageName")
                // Reset context when switching apps/chats
                resetChatContext()
            }

            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                processScreenContent(packageName)
            }
        }
    }

    private fun processScreenContent(packageName: String) {
        val rootNode = rootInActiveWindow ?: return

        try {
            val screenText = extractAllText(rootNode)

            // Avoid re-analyzing same content
            if (screenText == lastAnalyzedText || screenText.length < 10) {
                return
            }
            lastAnalyzedText = screenText

            // Analyze for scam patterns
            val analysisResult = analyzeForScams(screenText)

            if (analysisResult.riskScore > 0) {
                Log.d(TAG, "⚠️ Risk detected in $packageName:")
                Log.d(TAG, "   Score: ${analysisResult.riskScore}")
                Log.d(TAG, "   Patterns: ${analysisResult.matchedPatterns}")

                accumulatedRiskScore += analysisResult.riskScore

                // Show warning if accumulated risk exceeds threshold
                if (accumulatedRiskScore >= 3) {
                    showScamWarning(analysisResult)
                }
            }
        } finally {
            rootNode.recycle()
        }
    }

    private fun extractAllText(node: AccessibilityNodeInfo): String {
        val textBuilder = StringBuilder()
        extractTextRecursive(node, textBuilder)
        return textBuilder.toString()
    }

    private fun extractTextRecursive(node: AccessibilityNodeInfo, builder: StringBuilder) {
        // Extract text from current node
        node.text?.let {
            builder.append(it).append(" ")
        }
        node.contentDescription?.let {
            builder.append(it).append(" ")
        }

        // Recursively process children
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            try {
                extractTextRecursive(child, builder)
            } finally {
                child.recycle()
            }
        }
    }

    data class AnalysisResult(
        val riskScore: Int,
        val matchedPatterns: List<String>,
        val riskLevel: RiskLevel
    )

    enum class RiskLevel {
        NONE, LOW, MEDIUM, HIGH, CRITICAL
    }

    private fun analyzeForScams(text: String): AnalysisResult {
        val lowerText = text.lowercase()
        val matchedPatterns = mutableListOf<String>()
        var riskScore = 0

        for (pattern in scamPatterns) {
            if (lowerText.contains(pattern.lowercase())) {
                matchedPatterns.add(pattern)
                riskScore += when {
                    // High-risk patterns
                    pattern in listOf("การันตีกำไร", "guaranteed profit", "โอนเงิน",
                                     "send me money", "อย่าบอกใคร", "don't tell anyone") -> 3
                    // Medium-risk patterns
                    pattern in listOf("ลงทุน", "USDT", "crypto", "urgent", "รีบด่วน") -> 2
                    // Low-risk patterns
                    else -> 1
                }
            }
        }

        val riskLevel = when {
            riskScore >= 10 -> RiskLevel.CRITICAL
            riskScore >= 6 -> RiskLevel.HIGH
            riskScore >= 3 -> RiskLevel.MEDIUM
            riskScore >= 1 -> RiskLevel.LOW
            else -> RiskLevel.NONE
        }

        return AnalysisResult(riskScore, matchedPatterns, riskLevel)
    }

    private fun showScamWarning(result: AnalysisResult) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("warning_patterns", result.matchedPatterns.toTypedArray())
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val warningTitle = when (result.riskLevel) {
            RiskLevel.CRITICAL -> "🚨 Critical Scam Warning!"
            RiskLevel.HIGH -> "⚠️ High Risk Detected"
            RiskLevel.MEDIUM -> "⚡ Suspicious Patterns Found"
            else -> "💡 Stay Alert"
        }

        val warningText = buildString {
            append("Detected: ")
            append(result.matchedPatterns.take(3).joinToString(", "))
            if (result.matchedPatterns.size > 3) {
                append(" +${result.matchedPatterns.size - 3} more")
            }
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(warningTitle)
            .setContentText(warningText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(
                "$warningText\n\nTap to learn more about these scam tactics."
            ))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)

        Log.d(TAG, "📢 Warning notification shown: $warningTitle")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Scam Warnings",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when potential scam patterns are detected"
                enableVibration(true)
            }

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun resetChatContext() {
        accumulatedRiskScore = 0
        lastAnalyzedText = ""
        currentChatPartner = ""
    }

    override fun onInterrupt() {
        Log.d(TAG, "SentinelAI Service interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "SentinelAI Service destroyed")
    }
}
```

### 5. MainActivity.kt (Basic Setup Screen)

```kotlin
package com.sentinelai.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SentinelAIHomeScreen()
            }
        }
    }
}

@Composable
fun SentinelAIHomeScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🛡️ SentinelAI",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Protecting you from digital fraud",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Setup Required",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Enable Accessibility Service to start protection:",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Open Accessibility Settings")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Find \"SentinelAI\" in the list and enable it",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Status indicator (simplified)
        Text(
            text = "After enabling, SentinelAI will monitor:\n• LINE\n• WhatsApp\n• Messenger",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}
```

### 6. build.gradle.kts (App Level Dependencies)

Add these dependencies to `app/build.gradle.kts`:

```kotlin
dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // QR Code scanning (for Flow 3)
    implementation("com.google.mlkit:barcode-scanning:17.2.0")

    // CameraX (for QR scanner)
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
}
```

---

## Testing Guide

### On Emulator (Initial Development)

1. **Create Emulator:**
   ```
   Android Studio → Tools → Device Manager → Create Device
   Select: Pixel 6 → API 34 (Android 14) → Finish
   ```

2. **Run App:**
   - Click green ▶️ button
   - Select emulator

3. **Enable Accessibility Service:**
   ```
   Settings → Accessibility → Downloaded apps → SentinelAI → Enable
   ```

4. **Limitations on Emulator:**
   - Cannot install LINE/WhatsApp (requires phone number)
   - Can test service lifecycle and UI only

### On Physical Device (Required for Full Testing)

1. **Enable Developer Mode:**
   ```
   Settings → About Phone → Tap "Build Number" 7 times
   ```

2. **Enable USB Debugging:**
   ```
   Settings → Developer Options → USB Debugging → ON
   ```

3. **Connect & Run:**
   - Connect phone via USB
   - Accept "Allow USB debugging" prompt
   - Click ▶️ → Select your device

4. **Enable Accessibility Service:**
   ```
   Settings → Accessibility → Installed Services → SentinelAI → Enable → Confirm
   ```

5. **Test Flow 2:**
   - Open LINE/WhatsApp/Messenger
   - View chat conversations
   - Check Logcat for detected patterns:
     ```
     Filter: "SentinelAI"
     Look for: "⚠️ Risk detected" messages
     ```

### Testing Commands (Logcat)

```bash
# View SentinelAI logs only
adb logcat -s SentinelAI

# Clear logs and start fresh
adb logcat -c && adb logcat -s SentinelAI
```

---

## Development Timeline

| Day | Focus | Deliverable |
|-----|-------|-------------|
| **Day 1** | Environment setup, project creation | App runs on device |
| **Day 2** | Accessibility Service implementation | Can read screen text from chat apps |
| **Day 3** | Scam detection engine | Pattern matching with Thai keywords |
| **Day 4** | Warning UI & notifications | Shows alerts when scam detected |
| **Day 5** | Flow 3: QR scanner implementation | On-demand QR verification |
| **Day 6** | UI polish, demo flow | Complete user journey |
| **Day 7** | Testing, bug fixes, demo practice | Demo-ready app |

---

## Demo Script (90 seconds)

### Setup
- Phone with SentinelAI installed
- LINE app with test conversation containing scam keywords
- QR code for on-demand scan demo

### Flow

1. **[0-15s] Introduction**
   - Show app home screen
   - "SentinelAI protects users from digital fraud"

2. **[15-45s] Flow 2: Chat Monitoring**
   - Open LINE
   - View conversation with scam keywords
   - Show notification appearing
   - "We detect pig-butchering and romance scam patterns in real-time"

3. **[45-70s] Socratic Intervention**
   - Tap notification
   - Show warning screen with questions:
     - "Have you met this person in real life?"
     - "Are they asking you to keep this secret?"
   - "We use behavioral intervention, not blocking"

4. **[70-90s] Flow 3: On-Demand**
   - Open QR scanner
   - Scan suspicious QR
   - Show risk assessment
   - "Users can verify any QR code before payment"

---

## References

### Android Documentation
- [Accessibility Service Guide](https://developer.android.com/guide/topics/ui/accessibility/service)
- [AccessibilityService API](https://developer.android.com/reference/android/accessibilityservice/AccessibilityService)
- [AccessibilityNodeInfo](https://developer.android.com/reference/android/view/accessibility/AccessibilityNodeInfo)

### Security Research
- [FLAG_SECURE vs Accessibility Services](https://wwws.nightwatchcybersecurity.com/2020/03/23/flag_secure-and-accessibility-services-a11y/)
- [Banking Apps Blocking Accessibility](https://medium.com/wultra-blog/blocking-untrusted-accessibility-readers-on-android-82a20ed60aa5)

### Thai Banking Context
- K PLUS blocks accessibility services
- Bangkok Bank blocks accessibility services
- Thai Bankers Association security coordination

---

## Appendix: Scam Keywords Database

### Thai Keywords (Investment Scams)
```
การันตีกำไร, ผลตอบแทนสูง, ลงทุนด่วน, กำไร 100%, รายได้เสริม,
ทำเงินออนไลน์, รวยเร็ว, ไม่มีความเสี่ยง
```

### Thai Keywords (Urgency/Pressure)
```
รีบด่วน, วันนี้วันสุดท้าย, โอกาสสุดท้าย, เหลืออีกแค่,
ต้องตัดสินใจวันนี้, พรุ่งนี้ไม่ทัน
```

### Thai Keywords (Secrecy)
```
อย่าบอกใคร, เก็บเป็นความลับ, ระหว่างเราสองคน
```

### Thai Keywords (Money Transfer)
```
โอนเงิน, เลขบัญชี, พร้อมเพย์, โอนมาก่อน, ค่าธรรมเนียม, จ่ายก่อน
```

### Crypto Scam Keywords
```
USDT, Bitcoin, คริปโต, crypto, wallet address, Binance, mining, ขุดเหรียญ
```

### English Keywords
```
guaranteed profit, guaranteed return, 100% safe, no risk,
double your money, investment opportunity, passive income,
act now, limited time, urgent, don't tell anyone,
send me money, Western Union, MoneyGram
```

---

## Contact & Support

For questions about this implementation, refer to:
- Original project brief: `Sentinel AI - Digital Fraud Hackathon.md`
- Android Developers documentation

---

*Document generated: January 2026*
*For: SentinelAI Hackathon Team*
