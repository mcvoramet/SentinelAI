package com.example.demo_sentinel_ai

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import com.adamglin.phosphoricons.regular.PlayCircle
import com.example.demo_sentinel_ai.model.DemoScenario
import com.adamglin.phosphoricons.regular.QrCode
import com.example.demo_sentinel_ai.model.DetectionRepository
import com.example.demo_sentinel_ai.model.RiskLevel
import com.example.demo_sentinel_ai.model.ScamDetection
import com.example.demo_sentinel_ai.service.NotificationHelper
import com.example.demo_sentinel_ai.service.SentinelAccessibilityService
import com.example.demo_sentinel_ai.ui.components.BorderedCard
import com.example.demo_sentinel_ai.ui.components.LargeActionButton
import com.example.demo_sentinel_ai.ui.components.TrustStatus
import com.example.demo_sentinel_ai.ui.screens.ScannerScreen
import com.example.demo_sentinel_ai.ui.screens.WarningScreen
import com.example.demo_sentinel_ai.ui.theme.ActionBlue
import com.example.demo_sentinel_ai.ui.theme.AlertRed
import com.example.demo_sentinel_ai.ui.theme.BackgroundColor
import com.example.demo_sentinel_ai.ui.theme.DemosentinelaiTheme
import com.example.demo_sentinel_ai.ui.theme.SafeGreen
import com.example.demo_sentinel_ai.ui.theme.TextPrimary
import com.adamglin.phosphoricons.regular.ChatCircleText

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check if launched from notification
        val showWarning = intent?.getBooleanExtra(EXTRA_SHOW_WARNING, false) ?: false

        setContent {
            DemosentinelaiTheme {
                var currentScreen by remember {
                    mutableStateOf(if (showWarning) Screen.Warning else Screen.Home)
                }
                var currentLanguage by remember { mutableStateOf(DemoScenario.Language.ENGLISH) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = BackgroundColor // Apply theme background
                ) { innerPadding ->
                    when (currentScreen) {
                        Screen.Home -> {
                            HomeScreen(
                                modifier = Modifier.padding(innerPadding),
                                onShowWarning = { currentScreen = Screen.Warning },
                                onOpenScanner = { language -> 
                                    currentLanguage = language
                                    currentScreen = Screen.Scanner 
                                }
                            )
                        }
                        Screen.Scanner -> {
                            ScannerScreen(
                                onNavigateBack = { currentScreen = Screen.Home },
                                language = currentLanguage,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        Screen.Warning -> {
                            val detection = DetectionRepository.getLatestDetection()
                            if (detection != null) {
                                WarningScreen(
                                    detection = detection,
                                    onDismiss = {
                                        DetectionRepository.clearLatest()
                                        currentScreen = Screen.Home
                                    },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            } else {
                                // No detection data, go back to home
                                currentScreen = Screen.Home
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // Recreate to handle new intent
        if (intent.getBooleanExtra(EXTRA_SHOW_WARNING, false)) {
            recreate()
        }
    }

    private enum class Screen {
        Home, Warning, Scanner
    }

    companion object {
        const val EXTRA_SHOW_WARNING = "show_warning"
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onShowWarning: () -> Unit,
    onOpenScanner: (DemoScenario.Language) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isServiceEnabled by remember { mutableStateOf(isAccessibilityServiceEnabled(context)) }
    var hasNotificationPermission by remember { mutableStateOf(checkNotificationPermission(context)) }
    var latestDetection by remember { mutableStateOf(DetectionRepository.getLatestDetection()) }
    var isThaiLanguage by remember { mutableStateOf(false) }

    // Permission launcher for notifications
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            showTestNotification(context)
        }
    }

    // Refresh status when app resumes
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isServiceEnabled = isAccessibilityServiceEnabled(context)
                hasNotificationPermission = checkNotificationPermission(context)
                latestDetection = DetectionRepository.getLatestDetection()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "SentinelAI",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        // Language Toggle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "English",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (!isThaiLanguage) FontWeight.Bold else FontWeight.Normal,
                color = if (!isThaiLanguage) TextPrimary else TextPrimary.copy(alpha = 0.6f)
            )
            Switch(
                checked = isThaiLanguage,
                onCheckedChange = { isThaiLanguage = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = ActionBlue,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = ActionBlue.copy(alpha = 0.5f)
                )
            )
            Text(
                text = "Thai",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isThaiLanguage) FontWeight.Bold else FontWeight.Normal,
                color = if (isThaiLanguage) TextPrimary else TextPrimary.copy(alpha = 0.6f)
            )
        }

        // Status Card (Hero)
        TrustStatus(isSafe = isServiceEnabled)

        // Setup Required Warning
        if (!isServiceEnabled) {
            BorderedCard(
                borderColor = AlertRed,
                onClick = {
                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }
            ) {
                Text(
                    text = "Setup Required",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = AlertRed,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap here to enable SentinelAI protection in Settings.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Action Buttons
        LargeActionButton(
            text = "Scan QR Code",
            icon = PhosphorIcons.Regular.QrCode,
            onClick = { onOpenScanner(if (isThaiLanguage) DemoScenario.Language.THAI else DemoScenario.Language.ENGLISH) }
        )


        LargeActionButton(
            text = "Check Current Chat",
            icon = PhosphorIcons.Regular.ChatCircleText,
            onClick = {
                SentinelAccessibilityService.triggerManualCheck()
            },
            containerColor = ActionBlue
        )

        // Latest Alert Card
        latestDetection?.let { detection ->
            BorderedCard(
                borderColor = AlertRed,
                onClick = onShowWarning
            ) {
                Text(
                    text = "Review Latest Alert",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AlertRed
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${detection.riskLevel.displayName} detected in ${detection.appDisplayName}. Tap for details.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Run Demo Scenario
        LargeActionButton(
            text = "Run Demo Scenario (Niran)",
            icon = PhosphorIcons.Regular.PlayCircle,
            onClick = {
                if (hasNotificationPermission) {
                    val language = if (isThaiLanguage) DemoScenario.Language.THAI else DemoScenario.Language.ENGLISH
                    SentinelAccessibilityService.triggerDemo(language)
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            },
            containerColor = SafeGreen
        )

        // Debug Breach Button
        Button(
            onClick = {
                val intent = Intent(context, com.example.demo_sentinel_ai.ui.activities.BreachActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        ) {
            Text("Debug: Trigger Breach Overlay")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

private fun showTestNotification(context: Context) {
    // Create a realistic mock detection for investment scam demo
    val mockDetection = DemoScenario.getNiranScenario(DemoScenario.Language.ENGLISH)
    val detection = ScamDetection(
        sourceApp = mockDetection.sourceApp,
        chatPartner = mockDetection.chatPartner,
        riskScore = mockDetection.riskScore,
        riskLevel = mockDetection.riskLevel,
        matchedPatterns = mockDetection.matchedPatterns,
        suspiciousText = mockDetection.suspiciousText,
        screenshot = null,
        aiReasoning = mockDetection.aiReasoning,
        socraticQuestions = mockDetection.socraticQuestions,
        trafficLights = mockDetection.trafficLights
    )
    DetectionRepository.saveDetection(detection)

    NotificationHelper(context).showScamWarning(
        title = "Potential Risk Detected",
        message = "Investment scam pattern detected",
        matchedPatterns = detection.matchedPatterns,
        chatPartner = detection.chatPartner,
        appName = "LINE"
    )
}

private fun checkNotificationPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

private fun isAccessibilityServiceEnabled(context: Context): Boolean {
    val serviceName = "${context.packageName}/${SentinelAccessibilityService::class.java.canonicalName}"
    val enabledServices = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false

    return TextUtils.SimpleStringSplitter(':').apply {
        setString(enabledServices)
    }.any { it.equals(serviceName, ignoreCase = true) }
}
