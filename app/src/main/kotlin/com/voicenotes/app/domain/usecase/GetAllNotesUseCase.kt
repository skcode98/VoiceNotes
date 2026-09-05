package com.voicenotes.app.domain.usecase

import com.voicenotes.app.domain.model.VoiceNote
import com.voicenotes.app.domain.repository.VoiceNoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllNotesUseCase @Inject constructor(
    private val repository: VoiceNoteRepository
) {
    operator fun invoke(): Flow<List<VoiceNote>> {
        return repository.getAllNotes()
    }
}
