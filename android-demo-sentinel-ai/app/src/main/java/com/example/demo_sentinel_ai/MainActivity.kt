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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.demo_sentinel_ai.model.DetectionRepository
import com.example.demo_sentinel_ai.model.RiskLevel
import com.example.demo_sentinel_ai.model.ScamDetection
import com.example.demo_sentinel_ai.service.NotificationHelper
import com.example.demo_sentinel_ai.service.SentinelAccessibilityService
import com.example.demo_sentinel_ai.ui.screens.WarningScreen
import com.example.demo_sentinel_ai.ui.theme.DemosentinelaiTheme

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

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (currentScreen) {
                        Screen.Home -> {
                            HomeScreen(
                                modifier = Modifier.padding(innerPadding),
                                onShowWarning = { currentScreen = Screen.Warning }
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
        Home, Warning
    }

    companion object {
        const val EXTRA_SHOW_WARNING = "show_warning"
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onShowWarning: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isServiceEnabled by remember { mutableStateOf(isAccessibilityServiceEnabled(context)) }
    var hasNotificationPermission by remember { mutableStateOf(checkNotificationPermission(context)) }
    var latestDetection by remember { mutableStateOf(DetectionRepository.getLatestDetection()) }

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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "SentinelAI",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Protecting you from digital fraud",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Status Card
        StatusCard(isEnabled = isServiceEnabled)

        Spacer(modifier = Modifier.height(8.dp))

        // Setup Card
        if (!isServiceEnabled) {
            SetupCard(
                onOpenSettings = {
                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }
            )
        }

        // Show latest detection card if available
        latestDetection?.let { detection ->
            Spacer(modifier = Modifier.height(8.dp))
            LatestDetectionCard(
                detection = detection,
                onClick = onShowWarning
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Test Notification Button
        TestNotificationButton(
            hasPermission = hasNotificationPermission,
            onRequestPermission = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            },
            onShowNotification = { showTestNotification(context) }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun StatusCard(isEnabled: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isEnabled) "Protection Active" else "Protection Disabled",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isEnabled)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isEnabled)
                    "Monitoring LINE, WhatsApp, Messenger"
                else
                    "Enable Accessibility Service to start",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isEnabled)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
private fun SetupCard(onOpenSettings: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Setup Required",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "1. Tap the button below\n2. Find \"SentinelAI\" in the list\n3. Enable the service",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onOpenSettings,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open Accessibility Settings")
            }
        }
    }
}

@Composable
private fun LatestDetectionCard(
    detection: ScamDetection,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Latest Alert",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${detection.riskLevel.displayName} in ${detection.appDisplayName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = "Tap to view details",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun TestNotificationButton(
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    onShowNotification: () -> Unit
) {
    OutlinedButton(
        onClick = {
            if (hasPermission) {
                onShowNotification()
            } else {
                onRequestPermission()
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Text(if (hasPermission) "Test Notification" else "Grant Notification Permission")
    }

    Text(
        text = if (hasPermission)
            "Tap to test if notifications work correctly"
        else
            "Permission required to show scam warnings",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

private fun showTestNotification(context: Context) {
    // Create a realistic mock detection for investment scam demo
    val mockDetection = ScamDetection(
        sourceApp = "jp.naver.line.android",
        chatPartner = "Jennifer Wong",
        riskScore = 12,
        riskLevel = RiskLevel.CRITICAL,
        matchedPatterns = listOf(
            "guaranteed profit",
            "การันตีกำไร",
            "USDT",
            "investment opportunity",
            "ลงทุน",
            "limited time"
        ),
        suspiciousText = "Hi! I'm a senior investment analyst. I have a guaranteed profit opportunity - " +
                "50% returns monthly on USDT investments. This is limited time only. " +
                "การันตีกำไร 100% ลงทุนขั้นต่ำ 10,000 บาท",
        screenshot = null
    )
    DetectionRepository.saveDetection(mockDetection)

    NotificationHelper(context).showScamWarning(
        title = "CRITICAL: Scam Detected!",
        message = "Investment scam detected",
        matchedPatterns = mockDetection.matchedPatterns,
        chatPartner = mockDetection.chatPartner,
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
