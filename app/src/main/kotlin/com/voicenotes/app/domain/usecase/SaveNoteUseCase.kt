package com.voicenotes.app.domain.usecase

import com.voicenotes.app.domain.model.VoiceNote
import com.voicenotes.app.domain.repository.VoiceNoteRepository
import javax.inject.Inject

class SaveNoteUseCase @Inject constructor(
    private val repository: VoiceNoteRepository
) {
    suspend operator fun invoke(voiceNote: VoiceNote) {
        repository.saveNote(voiceNote)
    }
}
