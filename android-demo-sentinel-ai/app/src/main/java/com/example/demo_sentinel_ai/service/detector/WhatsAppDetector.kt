package com.example.demo_sentinel_ai.service.detector

import android.view.accessibility.AccessibilityNodeInfo

/**
 * Detector for WhatsApp messaging app.
 * - Inside chat: has message input (EditText/compose) + back button
 * - Chat list: no compose input, shows contact list
 */
class WhatsAppDetector : AppDetector {

    override val supportedPackages = setOf("com.whatsapp", "com.whatsapp.w4b")
    override val appDisplayName = "WhatsApp"

    override fun isInsideChat(root: AccessibilityNodeInfo): Boolean {
        var hasMessageInput = false
        var hasBackButton = false

        NodeTraversal.traverse(root) { node ->
            val viewId = node.viewIdResourceName ?: ""
            val className = node.className?.toString() ?: ""
            val contentDesc = node.contentDescription?.toString()?.lowercase() ?: ""

            if (className.contains("EditText") || viewId.contains("entry") ||
                viewId.contains("input") || viewId.contains("compose")) {
                hasMessageInput = true
            }
            if (contentDesc.contains("back") || contentDesc.contains("navigate up")) {
                hasBackButton = true
            }
        }

        return hasMessageInput && hasBackButton
    }

    override fun extractChatPartner(root: AccessibilityNodeInfo): String? {
        var chatPartner: String? = null

        NodeTraversal.traverse(root) { node ->
            if (chatPartner != null) return@traverse
            val viewId = node.viewIdResourceName ?: ""
            val text = node.text?.toString() ?: ""

            if (text.isNotEmpty() && text.length < 50 && !text.contains("WhatsApp") &&
                (viewId.contains("conversation_contact") || viewId.contains("name") || viewId.contains("title"))) {
                chatPartner = text
            }
        }

        return chatPartner
    }
}
