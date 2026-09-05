package com.voicenotes.app.data.local.dao

import androidx.room.*
import com.voicenotes.app.data.local.entity.VoiceNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VoiceNoteDao {
    @Query("SELECT * FROM voice_notes ORDER BY created_at DESC")
    fun getAllNotes(): Flow<List<VoiceNoteEntity>>

    @Query("SELECT * FROM voice_notes WHERE id = :id")
    suspend fun getNoteById(id: String): VoiceNoteEntity?

    @Query("SELECT * FROM voice_notes WHERE title LIKE '%' || :query || '%' OR transcript LIKE '%' || :query || '%' ORDER BY created_at DESC")
    fun searchNotes(query: String): Flow<List<VoiceNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: VoiceNoteEntity)

    @Update
    suspend fun updateNote(note: VoiceNoteEntity)

    @Delete
    suspend fun deleteNote(note: VoiceNoteEntity)

    @Query("DELETE FROM voice_notes WHERE id = :id")
    suspend fun deleteNoteById(id: String)

    @Query("SELECT COUNT(*) FROM voice_notes")
    suspend fun getNoteCount(): Int
}
