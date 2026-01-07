package com.example.demo_sentinel_ai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun FrictionSlider(
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var width by remember { mutableFloatStateOf(0f) }
    val thumbSize = 56.dp // Slightly larger thumb for premium feel
    val thumbSizePx = with(LocalDensity.current) { thumbSize.toPx() }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thumbSize)
            .background(Color.Black.copy(alpha = 0.1f), RoundedCornerShape(thumbSize)) // Subtle dark track
            .onSizeChanged { width = it.width.toFloat() }
            .padding(4.dp)
    ) {
        // Label
        Text(
            text = "Slide to Ignore Risk",
            modifier = Modifier.align(Alignment.Center),
            color = Color.Black.copy(alpha = 0.5f), // Visible on light background
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
        )

        // Thumb
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .size(thumbSize - 8.dp)
                .clip(CircleShape)
                .background(Color.White)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        val maxOffset = width - thumbSizePx
                        val newOffset = (offsetX + delta).coerceIn(0f, maxOffset)
                        offsetX = newOffset
                    },
                    onDragStopped = {
                        val maxOffset = width - thumbSizePx
                        if (offsetX > maxOffset * 0.8f) {
                            // Confirmed
                            offsetX = maxOffset
                            onConfirm()
                        } else {
                            // Reset
                            offsetX = 0f
                        }
                    }
                )
        ) {
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

