package com.example.demo_sentinel_ai.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.demo_sentinel_ai.R

@Composable
fun MapMismatchCard() {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Physically Impossible Transaction",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Map Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                .background(Color(0xFF1E1E1E)) // Fallback dark background
        ) {
            // 1. Static Map Image (Local Placeholder)
            Image(
                painter = painterResource(id = R.drawable.img_map_placeholder),
                contentDescription = "Location Map",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            // 2. Premium Overlay (Gradients & Path)
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Dim the map slightly for text readability if it loads
                drawRect(Color.Black.copy(alpha = 0.3f))

                val start = Offset(size.width * 0.2f, size.height * 0.7f)
                val end = Offset(size.width * 0.8f, size.height * 0.3f)
                
                // Draw Curved Dashed Line
                val path = Path().apply {
                    moveTo(start.x, start.y)
                    cubicTo(
                        start.x + size.width * 0.2f, start.y,
                        end.x - size.width * 0.2f, end.y,
                        end.x, end.y
                    )
                }

                drawPath(
                    path = path,
                    color = Color(0xFFFF3B30), // Premium Red
                    style = Stroke(
                        width = 4.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f)),
                        cap = StrokeCap.Round
                    )
                )
                
                // Draw "You" Dot (Blue Pulse Effect)
                drawCircle(
                    color = Color(0xFF007AFF).copy(alpha = 0.3f),
                    radius = 12.dp.toPx(),
                    center = start
                )
                drawCircle(
                    color = Color(0xFF007AFF),
                    radius = 6.dp.toPx(),
                    center = start
                )
                
                // Draw "Merchant" Dot (Red Warning Effect)
                drawCircle(
                    color = Color(0xFFFF3B30).copy(alpha = 0.3f),
                    radius = 12.dp.toPx(),
                    center = end
                )
                drawCircle(
                    color = Color(0xFFFF3B30),
                    radius = 6.dp.toPx(),
                    center = end
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Distance: 500 km",
            style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
    }
}
