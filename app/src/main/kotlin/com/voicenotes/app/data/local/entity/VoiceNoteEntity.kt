package com.voicenotes.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "voice_notes")
data class VoiceNoteEntity(
    @PrimaryKey
    val id: String = "",
    val title: String = "",
    val transcript: String = "",
    @ColumnInfo(name = "audio_file_path")
    val audioFilePath: String = "",
    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Instant.now(),
    @ColumnInfo(name = "duration_seconds")
    val durationSeconds: Long = 0,
    val language: String = "en",
    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = false,
    @ColumnInfo(name = "sync_timestamp")
    val syncTimestamp: Instant? = null,
    @ColumnInfo(name = "cloud_id")
    val cloudId: String? = null
)
