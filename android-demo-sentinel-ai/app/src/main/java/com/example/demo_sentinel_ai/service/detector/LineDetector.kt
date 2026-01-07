package com.example.demo_sentinel_ai.service.detector

import android.view.accessibility.AccessibilityNodeInfo

/**
 * Detector for LINE messaging app.
 * - Chat list: has bottom nav (main_tab_container, bnb_*, viewpager)
 * - Inside chat: has chat_ui_* elements, no bottom nav
 */
class LineDetector : AppDetector {

    override val supportedPackages = setOf("jp.naver.line.android")
    override val appDisplayName = "LINE"

    override fun isInsideChat(root: AccessibilityNodeInfo): Boolean {
        var hasChatUI = false
        var hasBottomNav = false

        NodeTraversal.traverse(root) { node ->
            val viewId = node.viewIdResourceName ?: ""
            when {
                viewId.contains("chat_ui_") -> hasChatUI = true
                viewId.contains("main_tab_container") ||
                viewId.contains("bnb_") ||
                viewId.contains("viewpager") -> hasBottomNav = true
            }
        }

        return hasChatUI && !hasBottomNav
    }

    override fun extractChatPartner(root: AccessibilityNodeInfo): String? {
        var chatPartner: String? = null

        NodeTraversal.traverse(root) { node ->
            if (chatPartner != null) return@traverse
            val viewId = node.viewIdResourceName ?: ""
            val text = node.text?.toString() ?: ""

            if (text.isNotEmpty() && text.length < 50 && !text.contains("LINE") &&
                (viewId.contains("title") || viewId.contains("name") || viewId.contains("header"))) {
                chatPartner = text
            }
        }

        return chatPartner
    }
}
