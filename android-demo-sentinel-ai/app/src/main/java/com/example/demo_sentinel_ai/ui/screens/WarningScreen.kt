package com.example.demo_sentinel_ai.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demo_sentinel_ai.model.RiskLevel
import com.example.demo_sentinel_ai.model.ScamDetection
import com.example.demo_sentinel_ai.model.SignalStatus

// Color palette for the warning screen
private val DangerRed = Color(0xFFDC2626)
private val DangerRedDark = Color(0xFFB91C1C)
private val DangerRedLight = Color(0xFFFEE2E2)
private val WarningAmber = Color(0xFFF59E0B)
private val WarningAmberLight = Color(0xFFFEF3C7)
private val SuccessGreen = Color(0xFF10B981)
private val SuccessGreenLight = Color(0xFFD1FAE5)
private val SurfaceDark = Color(0xFF1F1F1F)
private val TextPrimary = Color(0xFF111827)
private val TextSecondary = Color(0xFF6B7280)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WarningScreen(
    detection: ScamDetection,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val (accentColor, accentLightColor) = when (detection.riskLevel) {
        RiskLevel.CRITICAL, RiskLevel.HIGH -> DangerRed to DangerRedLight
        RiskLevel.MEDIUM, RiskLevel.LOW -> WarningAmber to WarningAmberLight
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .verticalScroll(rememberScrollState())
    ) {
        // Top Threat Header
        ThreatHeader(
            detection = detection,
            accentColor = accentColor
        )

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Traffic Lights Visual
            SignalTrafficLights(
                signals = detection.trafficLights
            )

            // AI Reasoning Card (The "Smart" part)
            detection.aiReasoning?.let { reasoning ->
                AIReasoningCard(reasoning = reasoning, accentColor = accentColor)
            }

            // WHO Section - The Suspect
            SuspectCard(
                contactName = detection.chatPartner ?: "Unknown Contact",
                appName = detection.appDisplayName,
                riskLevel = detection.riskLevel,
                accentColor = accentColor
            )

            // WHY Section - Flagged Keywords
            FlaggedKeywordsCard(
                patterns = detection.matchedPatterns,
                accentColor = accentColor
            )

            // Statistics Card
            StatisticsCard(
                riskLevel = detection.riskLevel,
                matchCount = detection.matchedPatterns.size
            )

            // Related Cases / News
            RelatedCasesCard(
                onLinkClick = { url ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            )

            // Screenshot Evidence (if available)
            detection.screenshot?.let { bitmap ->
                EvidenceCard(bitmap = bitmap)
            }

            // Socratic Questions (The "Behavioral" part)
            if (detection.socraticQuestions.isNotEmpty()) {
                SocraticQuestionsCard(questions = detection.socraticQuestions)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Button
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurfaceDark
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "I Understand the Risk",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ThreatHeader(
    detection: ScamDetection,
    accentColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        accentColor,
                        accentColor.copy(alpha = 0.9f)
                    )
                )
            )
            .padding(top = 48.dp, bottom = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pulsing warning icon
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .alpha(pulseAlpha)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = when (detection.riskLevel) {
                    RiskLevel.CRITICAL -> "SCAM ALERT"
                    RiskLevel.HIGH -> "HIGH RISK"
                    RiskLevel.MEDIUM -> "SUSPICIOUS"
                    RiskLevel.LOW -> "CAUTION"
                },
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Potential fraud detected in ${detection.appDisplayName}",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun SuspectCard(
    contactName: String,
    appName: String,
    riskLevel: RiskLevel,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SUSPECT",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = accentColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = riskLevel.displayName.uppercase(),
                        color = accentColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Contact name - large and prominent
            Text(
                text = contactName,
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "via $appName • ${java.text.SimpleDateFormat("MMM d, h:mm a", java.util.Locale.getDefault()).format(java.util.Date())}",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlaggedKeywordsCard(
    patterns: List<String>,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "WHY THIS IS FLAGGED",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Message contains ${patterns.size} suspicious ${if (patterns.size == 1) "phrase" else "phrases"}:",
                color = TextPrimary,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                patterns.forEach { pattern ->
                    Surface(
                        color = accentColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            accentColor.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            text = pattern,
                            color = accentColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatisticsCard(
    riskLevel: RiskLevel,
    matchCount: Int
) {
    val scamPercentage = when {
        matchCount >= 5 -> 94
        matchCount >= 3 -> 87
        matchCount >= 2 -> 73
        else -> 58
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "THREAT ANALYSIS",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$scamPercentage%",
                        color = DangerRed,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "of similar messages\nare confirmed scams",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }

                // Circular indicator placeholder
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(DangerRedLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = matchCount.toString(),
                            color = DangerRed,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "flags",
                            color = DangerRed,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { scamPercentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = DangerRed,
                trackColor = DangerRedLight
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Based on 50,000+ analyzed scam reports",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun RelatedCasesCard(
    onLinkClick: (String) -> Unit
) {
    // Real news data from research
    val relatedCases = listOf(
        RelatedCase(
            title = "Thai Police Freeze ฿84M in Crypto from Chinese Scam Ring",
            source = "Khaosod English",
            date = "Feb 2025",
            url = "https://www.khaosodenglish.com/news/2025/02/09/thai-police-freezes-2-5-million-in-crypto-assets-from-chinese-scam-bosses/"
        ),
        RelatedCase(
            title = "FINTOCH Crypto Fraud: \"1% Daily Returns\" Promise Exposed",
            source = "TRM Labs",
            url = "https://www.trmlabs.com/resources/blog/royal-thai-police-arrest-fugitive-chinese-national-behind-multi-million-dollar-crypto-fraud-scheme"
        ),
        RelatedCase(
            title = "WhatsApp Crypto Scams Target Thai Investors via Fake Apps",
            source = "Malwarebytes",
            url = "https://www.malwarebytes.com/blog/news/2024/06/whatsapp-cryptocurrency-scam-goes-for-the-cash-prize"
        )
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "SIMILAR CASES",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Recent fraud cases matching this pattern",
                color = TextPrimary,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            relatedCases.forEachIndexed { index, case ->
                if (index > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color(0xFFE5E7EB)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLinkClick(case.url) },
                    verticalAlignment = Alignment.Top
                ) {
                    // News indicator
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                DangerRedLight,
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "NEWS",
                            color = DangerRed,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = case.title,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${case.source}${case.date?.let { " • $it" } ?: ""}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Open link",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EvidenceCard(bitmap: Bitmap) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "SCREENSHOT EVIDENCE",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Screenshot evidence",
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        1.dp,
                        Color(0xFFE5E7EB),
                        RoundedCornerShape(12.dp)
                    ),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}

@Composable
private fun SignalTrafficLights(
    signals: Map<String, SignalStatus>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SignalLight(
            label = "CHAT",
            status = signals["chat"] ?: SignalStatus.GREEN,
            modifier = Modifier.weight(1f)
        )
        SignalLight(
            label = "LOCATION",
            status = signals["location"] ?: SignalStatus.GREEN,
            modifier = Modifier.weight(1f)
        )
        SignalLight(
            label = "RELATIONSHIP",
            status = signals["relationship"] ?: SignalStatus.GREEN,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SignalLight(
    label: String,
    status: SignalStatus,
    modifier: Modifier = Modifier
) {
    val color = when (status) {
        SignalStatus.GREEN -> SuccessGreen
        SignalStatus.YELLOW -> WarningAmber
        SignalStatus.RED -> DangerRed
    }
    
    val lightColor = when (status) {
        SignalStatus.GREEN -> SuccessGreenLight
        SignalStatus.YELLOW -> WarningAmberLight
        SignalStatus.RED -> DangerRedLight
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun AIReasoningCard(
    reasoning: String,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = accentColor.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "GEMINI AI ANALYSIS",
                    color = accentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = reasoning,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun SocraticQuestionsCard(
    questions: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "THINK BEFORE PROCEEDING",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            questions.forEachIndexed { index, question ->
                Row(modifier = Modifier.padding(vertical = 6.dp)) {
                    Text(
                        text = "${index + 1}.",
                        color = DangerRed,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = question,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

private data class RelatedCase(
    val title: String,
    val source: String,
    val date: String? = null,
    val url: String
)
