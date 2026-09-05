package com.voicenotes.app.domain.usecase

import com.voicenotes.app.domain.repository.VoiceNoteRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val repository: VoiceNoteRepository
) {
    suspend operator fun invoke(noteId: String) {
        repository.deleteNote(noteId)
    }
}
