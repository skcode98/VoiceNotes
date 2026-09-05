package com.voicenotes.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voicenotes.app.domain.model.VoiceNote
import com.voicenotes.app.domain.usecase.SaveNoteUseCase
import com.voicenotes.app.domain.repository.VoiceNoteRepository
import com.voicenotes.app.domain.usecase.DeleteNoteUseCase
import com.voicenotes.app.data.audio.AudioPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NoteDetailUiState(
    val note: VoiceNote? = null,
    val isEditing: Boolean = false,
    val editedTitle: String = "",
    val editedTranscript: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isPlaying: Boolean = false,
    val playbackPosition: Long = 0L
)

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val saveNoteUseCase: SaveNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val repository: VoiceNoteRepository,
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteDetailUiState())
    val uiState: StateFlow<NoteDetailUiState> = _uiState.asStateFlow()

    fun loadNote(noteId: String) {
        if (_uiState.value.note?.id == noteId) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val note = repository.getNoteById(noteId)
            if (note != null && note.audioFilePath.isNotBlank()) {
                audioPlayer.preparePlayer(note.audioFilePath)
            }
            _uiState.update {
                it.copy(
                    note = note,
                    editedTitle = note?.title.orEmpty(),
                    editedTranscript = note?.transcript.orEmpty(),
                    isLoading = false
                )
            }
        }
    }

    fun setNote(note: VoiceNote) {
        _uiState.update {
            it.copy(
                note = note,
                editedTitle = note.title,
                editedTranscript = note.transcript
            )
        }
    }

    fun toggleEditMode() {
        _uiState.update { it.copy(isEditing = !it.isEditing) }
    }

    fun updateTitle(newTitle: String) {
        _uiState.update { it.copy(editedTitle = newTitle) }
    }

    fun updateTranscript(newTranscript: String) {
        _uiState.update { it.copy(editedTranscript = newTranscript) }
    }

    fun saveChanges() {
        viewModelScope.launch {
            try {
                val current = _uiState.value.note ?: return@launch
                val updated = current.copy(
                    title = _uiState.value.editedTitle,
                    transcript = _uiState.value.editedTranscript
                )
                saveNoteUseCase(updated)
                _uiState.update { it.copy(isEditing = false, note = updated) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun togglePlayback() {
        viewModelScope.launch {
            if (_uiState.value.isPlaying) audioPlayer.pause() else audioPlayer.play()
            _uiState.update { it.copy(isPlaying = !_uiState.value.isPlaying) }
        }
    }

    fun deleteNote() {
        viewModelScope.launch {
            _uiState.value.note?.let { deleteNoteUseCase(it.id) }
        }
    }

    override fun onCleared() {
        viewModelScope.launch { audioPlayer.stop() }
        super.onCleared()
    }

    fun updatePlaybackPosition(position: Long) {
        _uiState.update { it.copy(playbackPosition = position) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
