package com.example.demo_sentinel_ai.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.demo_sentinel_ai.domain.ai.GeminiRepository
import com.example.demo_sentinel_ai.domain.manager.CooldownManager
import com.example.demo_sentinel_ai.model.RiskLevel
import com.example.demo_sentinel_ai.model.ScamDetection
import com.example.demo_sentinel_ai.ui.components.ChatEvidenceCard
import com.example.demo_sentinel_ai.ui.components.FrictionSlider
import com.example.demo_sentinel_ai.ui.components.MapMismatchCard
import com.example.demo_sentinel_ai.ui.components.TrafficLightIndicator
import com.example.demo_sentinel_ai.ui.theme.DemosentinelaiTheme
import kotlinx.coroutines.launch

class BreachActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DemosentinelaiTheme {
                BreachScreen(onDismiss = { finish() })
            }
        }
    }
}

@Composable
fun BreachScreen(onDismiss: () -> Unit) {
    var isVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val geminiRepository = remember { GeminiRepository() }
    
    // State for dynamic content
    var explanationText by remember { mutableStateOf("High probability of scam detected based on recent chat patterns and location mismatch.") }
    var tactics by remember { mutableStateOf(listOf("Urgency", "Secrecy")) }
    var isLoadingAI by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isVisible = true
        
        // Trigger AI Explanation
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
                // Update tactics if AI found more (mock logic for now as explanation doesn't return tactics)
                isLoadingAI = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)) // Scrim
    ) {
        // Glass Panel
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(700.dp),
                color = Color.White.copy(alpha = 0.9f),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Scrollable Content
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            // Traffic Lights
                            TrafficLightIndicator(riskLevel = "HIGH")
                            Spacer(modifier = Modifier.height(32.dp))
                            
                            // Risk Score
                            Text(
                                text = "RISK LEVEL: CRITICAL",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Red
                                )
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = if (isLoadingAI) "Analyzing evidence..." else explanationText,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Evidence Cards
                            MapMismatchCard()
                            Spacer(modifier = Modifier.height(16.dp))
                            ChatEvidenceCard() // Note: Should pass tactics here
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                    
                    // Sticky Footer
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        // Premium Gradient Button
                        Button(
                            onClick = { 
                                CooldownManager.startCooldown()
                                onDismiss() 
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF34C759), Color(0xFF30B350))
                                    ),
                                    shape = RoundedCornerShape(28.dp)
                                ),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(28.dp),
                            contentPadding = PaddingValues(0.dp) // Reset padding for gradient background
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Pause,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(
                                    text = "PAUSE PAYMENT (10m)",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        FrictionSlider(
                            onConfirm = { onDismiss() }
                        )
                    }
                }
            }
        }
    }
}
