package com.example.demo_sentinel_ai.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.demo_sentinel_ai.MainActivity
import com.example.demo_sentinel_ai.R

/**
 * Handles all notification-related operations for SentinelAI.
 */
class NotificationHelper(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when potential scam patterns are detected"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
                enableLights(true)
                lightColor = Color.RED
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun vectorToBitmap(drawableId: Int, sizeDp: Int = 48): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableId)!!
        val sizePx = (sizeDp * context.resources.displayMetrics.density).toInt()
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, sizePx, sizePx)
        drawable.draw(canvas)
        return bitmap
    }

    fun showScamWarning(
        title: String,
        message: String,
        matchedPatterns: List<String> = emptyList(),
        chatPartner: String? = null,
        appName: String? = null
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.EXTRA_SHOW_WARNING, true)
            putExtra(EXTRA_WARNING_PATTERNS, matchedPatterns.toTypedArray())
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build compact content text - show most critical info first
        val compactText = chatPartner?.let { "Risk detected: $it" }
            ?: "Potential fraud risk detected"

        // Build expanded content with more details
        val expandedStyle = NotificationCompat.InboxStyle()
            .setBigContentTitle(title)

        chatPartner?.let {
            expandedStyle.addLine("Suspect: $it")
        }
        appName?.let {
            expandedStyle.addLine("App: $it")
        }
        if (matchedPatterns.isNotEmpty()) {
            expandedStyle.addLine("Keywords detected: ${matchedPatterns.take(4).joinToString(", ")}")
            if (matchedPatterns.size > 4) {
                expandedStyle.addLine("And ${matchedPatterns.size - 4} other risk indicators.")
            }
        }
        expandedStyle.addLine("")
        expandedStyle.addLine("Tap to review risk analysis.")

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_warning)
            .setLargeIcon(vectorToBitmap(R.drawable.ic_warning))
            .setContentTitle(title)
            .setContentText(compactText)
            .setStyle(expandedStyle)
            .setSubText(appName)
            .setColor(Color.parseColor("#BE123C")) // AlertRed from theme
            .setColorized(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "sentinel_warnings"
        const val CHANNEL_NAME = "Scam Warnings"
        const val NOTIFICATION_ID = 1001
        const val EXTRA_WARNING_PATTERNS = "warning_patterns"
    }
}
