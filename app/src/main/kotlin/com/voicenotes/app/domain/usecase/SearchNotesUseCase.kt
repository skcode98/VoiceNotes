package com.voicenotes.app.domain.usecase

import com.voicenotes.app.domain.model.VoiceNote
import com.voicenotes.app.domain.repository.VoiceNoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchNotesUseCase @Inject constructor(
    private val repository: VoiceNoteRepository
) {
    operator fun invoke(query: String): Flow<List<VoiceNote>> {
        return if (query.isBlank()) {
            repository.getAllNotes()
        } else {
            repository.searchNotes(query)
        }
    }
}
