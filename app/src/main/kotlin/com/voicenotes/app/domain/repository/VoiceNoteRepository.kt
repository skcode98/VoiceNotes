package com.voicenotes.app.domain.repository

import com.voicenotes.app.domain.model.VoiceNote
import kotlinx.coroutines.flow.Flow

interface VoiceNoteRepository {
    fun getAllNotes(): Flow<List<VoiceNote>>
    suspend fun getNoteById(id: String): VoiceNote?
    fun searchNotes(query: String): Flow<List<VoiceNote>>
    suspend fun saveNote(voiceNote: VoiceNote)
    suspend fun updateNote(voiceNote: VoiceNote)
    suspend fun deleteNote(id: String)
    suspend fun syncNotes(): Result<Unit>
}
