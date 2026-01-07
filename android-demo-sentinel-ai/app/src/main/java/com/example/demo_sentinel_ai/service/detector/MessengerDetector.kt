package com.example.demo_sentinel_ai.service.detector

import android.view.accessibility.AccessibilityNodeInfo

/**
 * Detector for Facebook Messenger app.
 * - Inside chat: has message composer + back navigation
 * - Chat list: shows thread list, no composer
 */
class MessengerDetector : AppDetector {

    override val supportedPackages = setOf("com.facebook.orca", "com.facebook.mlite")
    override val appDisplayName = "Messenger"

    override fun isInsideChat(root: AccessibilityNodeInfo): Boolean {
        var hasComposer = false
        var hasBackButton = false

        NodeTraversal.traverse(root) { node ->
            val viewId = node.viewIdResourceName ?: ""
            val className = node.className?.toString() ?: ""
            val contentDesc = node.contentDescription?.toString()?.lowercase() ?: ""

            if (className.contains("EditText") || viewId.contains("composer") ||
                viewId.contains("input") || viewId.contains("message_text")) {
                hasComposer = true
            }
            if (contentDesc.contains("back") || contentDesc.contains("navigate up") ||
                contentDesc.contains("return")) {
                hasBackButton = true
            }
        }

        return hasComposer && hasBackButton
    }

    override fun extractChatPartner(root: AccessibilityNodeInfo): String? {
        var chatPartner: String? = null

        NodeTraversal.traverse(root) { node ->
            if (chatPartner != null) return@traverse
            val viewId = node.viewIdResourceName ?: ""
            val text = node.text?.toString() ?: ""

            if (text.isNotEmpty() && text.length < 50 &&
                !text.contains("Messenger") && !text.contains("Facebook") &&
                (viewId.contains("thread_name") || viewId.contains("title") || viewId.contains("name"))) {
                chatPartner = text
            }
        }

        return chatPartner
    }
}
