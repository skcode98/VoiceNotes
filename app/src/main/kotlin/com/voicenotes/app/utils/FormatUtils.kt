package com.voicenotes.app.utils

object FormatUtils {
    fun formatDuration(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60

        return when {
            hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, secs)
            minutes > 0 -> String.format("%02d:%02d", minutes, secs)
            else -> String.format("00:%02d", secs)
        }
    }

    fun getPreviewText(text: String, maxLength: Int = 100): String {
        return if (text.length > maxLength) {
            text.substring(0, maxLength) + "..."
        } else {
            text
        }
    }
}
