package com.example.demo_sentinel_ai.service.detector

import android.view.accessibility.AccessibilityNodeInfo

/**
 * Interface for app-specific chat detection logic.
 */
interface AppDetector {
    val supportedPackages: Set<String>
    val appDisplayName: String

    fun isInsideChat(root: AccessibilityNodeInfo): Boolean
    fun extractChatPartner(root: AccessibilityNodeInfo): String?
}

/**
 * Shared utility for traversing accessibility node trees.
 */
object NodeTraversal {
    fun traverse(node: AccessibilityNodeInfo, action: (AccessibilityNodeInfo) -> Unit) {
        action(node)
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { child ->
                try {
                    traverse(child, action)
                } finally {
                    child.recycle()
                }
            }
        }
    }
}
