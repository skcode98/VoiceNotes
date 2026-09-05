package com.voicenotes.app.data.repository

import com.voicenotes.app.data.local.dao.VoiceNoteDao
import com.voicenotes.app.data.local.entity.VoiceNoteEntity
import com.voicenotes.app.domain.model.VoiceNote
import com.voicenotes.app.domain.repository.VoiceNoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VoiceNoteRepositoryImpl @Inject constructor(
    private val voiceNoteDao: VoiceNoteDao
) : VoiceNoteRepository {

    override fun getAllNotes(): Flow<List<VoiceNote>> {
        return voiceNoteDao.getAllNotes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getNoteById(id: String): VoiceNote? {
        return voiceNoteDao.getNoteById(id)?.toDomain()
    }

    override fun searchNotes(query: String): Flow<List<VoiceNote>> {
        return voiceNoteDao.searchNotes(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveNote(voiceNote: VoiceNote) {
        voiceNoteDao.insertNote(voiceNote.toEntity())
    }

    override suspend fun updateNote(voiceNote: VoiceNote) {
        voiceNoteDao.updateNote(voiceNote.toEntity())
    }

    override suspend fun deleteNote(id: String) {
        voiceNoteDao.deleteNoteById(id)
    }

    override suspend fun syncNotes(): Result<Unit> {
        return try {
            // TODO: Implement cloud sync
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun VoiceNoteEntity.toDomain(): VoiceNote {
        return VoiceNote(
            id = id,
            title = title,
            transcript = transcript,
            audioFilePath = audioFilePath,
            createdAt = createdAt,
            durationSeconds = durationSeconds,
            language = language,
            isSynced = isSynced,
            cloudId = cloudId
        )
    }

    private fun VoiceNote.toEntity(): VoiceNoteEntity {
        return VoiceNoteEntity(
            id = id,
            title = title,
            transcript = transcript,
            audioFilePath = audioFilePath,
            createdAt = createdAt,
            durationSeconds = durationSeconds,
            language = language,
            isSynced = isSynced,
            cloudId = cloudId
        )
    }
}
