package com.voicenotes.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.voicenotes.app.data.local.dao.VoiceNoteDao
import com.voicenotes.app.data.local.entity.VoiceNoteEntity

@Database(
    entities = [VoiceNoteEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class VoiceNoteDatabase : RoomDatabase() {
    abstract fun voiceNoteDao(): VoiceNoteDao
}
