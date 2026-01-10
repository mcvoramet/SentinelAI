package com.example.demo_sentinel_ai.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TrafficLightIndicator(
    riskLevel: String, // "HIGH", "MEDIUM", "LOW"
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Chat Light
            SignalDot(
                color = if (riskLevel == "HIGH") Color.Red else Color.Green,
                isPulsing = riskLevel == "HIGH"
            )
            // Location Light
            SignalDot(
                color = if (riskLevel == "HIGH") Color.Red else Color.Green,
                isPulsing = riskLevel == "HIGH"
            )
            // Trust Light
            SignalDot(
                color = if (riskLevel == "HIGH" || riskLevel == "MEDIUM") Color.Yellow else Color.Green,
                isPulsing = false
            )
        }
        
        if (riskLevel == "HIGH") {
            Text(
                text = "CRITICAL: Location Mismatch",
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun SignalDot(color: Color, isPulsing: Boolean) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by if (isPulsing) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Reverse
            )
        )
    } else {
        remember { mutableStateOf(1f) }
    }
    
    val alpha by if (isPulsing) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 0.6f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Reverse
            )
        )
    } else {
        remember { mutableStateOf(1f) }
    }

    Box(
        modifier = Modifier
            .size(12.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .background(color, CircleShape)
    )
}
