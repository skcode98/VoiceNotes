package com.voicenotes.app.domain.model

import java.time.Instant

data class VoiceNote(
    val id: String,
    val title: String,
    val transcript: String,
    val audioFilePath: String,
    val createdAt: Instant,
    val durationSeconds: Long,
    val language: String,
    val isSynced: Boolean,
    val cloudId: String? = null
)
