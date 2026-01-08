package com.example.demo_sentinel_ai.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.Warning
import com.adamglin.phosphoricons.regular.XCircle
import com.example.demo_sentinel_ai.domain.ai.GeminiRepository
import com.example.demo_sentinel_ai.model.RiskLevel
import com.example.demo_sentinel_ai.model.ScamDetection
import com.example.demo_sentinel_ai.ui.components.BorderedCard
import com.example.demo_sentinel_ai.ui.components.LargeActionButton
import com.example.demo_sentinel_ai.ui.theme.AlertRed
import com.example.demo_sentinel_ai.ui.theme.BackgroundColor
import com.example.demo_sentinel_ai.ui.theme.DemosentinelaiTheme
import com.example.demo_sentinel_ai.ui.theme.TextPrimary
import com.example.demo_sentinel_ai.ui.theme.TextSecondary
import kotlinx.coroutines.launch

class BreachActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DemosentinelaiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundColor
                ) {
                    BreachScreen(onDismiss = { finish() })
                }
            }
        }
    }
}

@Composable
fun BreachScreen(onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    val geminiRepository = remember { GeminiRepository() }
    
    // State
    var explanationText by remember { mutableStateOf("High probability of scam detected based on recent chat patterns.") }
    var isLoadingAI by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val mockDetection = ScamDetection(
            sourceApp = "LINE",
            riskScore = 85,
            riskLevel = RiskLevel.CRITICAL,
            matchedPatterns = listOf("Urgency", "Secrecy"),
            suspiciousText = "Transfer quickly before it's gone!"
        )
        
        scope.launch {
            geminiRepository.generateExplanation(mockDetection).collect { explanation ->
                explanationText = explanation.explanation
                isLoadingAI = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        // Header Icon
        Icon(
            imageVector = PhosphorIcons.Regular.Warning,
            contentDescription = null,
            tint = AlertRed,
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Title
        Text(
            text = "Potential Risk Detected",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Explanation
        Text(
            text = if (isLoadingAI) "Analyzing conversation patterns..." else explanationText,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Evidence 1
        BorderedCard(borderColor = AlertRed) {
            Text(
                text = "Suspicious Activity",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = AlertRed),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "High-pressure tactics detected. 'Urgency' and 'Secrecy' keywords found.",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Evidence 2
        BorderedCard(borderColor = AlertRed) {
            Text(
                text = "Location Mismatch",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = AlertRed),
                textAlign = TextAlign.Center
            )
             Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "The recipient's location does not match their claimed region.",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Actions
        LargeActionButton(
            text = "Pause & Exit Chat",
            icon = PhosphorIcons.Regular.XCircle,
            onClick = onDismiss,
            containerColor = AlertRed
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, TextSecondary.copy(alpha = 0.5f))
        ) {
            Text(
                text = "I understand the risk",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}
