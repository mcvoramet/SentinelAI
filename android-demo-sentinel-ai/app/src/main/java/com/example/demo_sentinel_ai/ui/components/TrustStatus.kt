package com.example.demo_sentinel_ai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.ShieldCheck
import com.adamglin.phosphoricons.regular.ShieldWarning
import com.example.demo_sentinel_ai.ui.theme.AlertRed
import com.example.demo_sentinel_ai.ui.theme.SafeGreen
import com.example.demo_sentinel_ai.ui.theme.TextPrimary
import com.example.demo_sentinel_ai.ui.theme.TextSecondary

@Composable
fun TrustStatus(isSafe: Boolean = true) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isSafe) Color(0xFFECFDF5) else Color(0xFFFEF2F2),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = if (isSafe) SafeGreen else AlertRed,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = if (isSafe) PhosphorIcons.Regular.ShieldCheck else PhosphorIcons.Regular.ShieldWarning,
            contentDescription = null,
            tint = if (isSafe) SafeGreen else AlertRed,
            modifier = Modifier.size(48.dp)
        )

        Column {
            Text(
                text = if (isSafe) "Protection Active" else "Attention Needed",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Text(
                text = if (isSafe) "Sentinel is scanning for scams." else "We detected a potential risk.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary
                )
            )
        }
    }
}
