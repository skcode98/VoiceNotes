package com.voicenotes.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voicenotes.app.domain.model.VoiceNote
import com.voicenotes.app.domain.usecase.SaveNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

data class RecorderUiState(
    val isRecording: Boolean = false,
    val isPaused: Boolean = false,
    val recordingDuration: Long = 0L,
    val audioFilePath: String = "",
    val error: String? = null,
    val isSaving: Boolean = false
)

@HiltViewModel
class RecorderViewModel @Inject constructor(
    private val saveNoteUseCase: SaveNoteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecorderUiState())
    val uiState: StateFlow<RecorderUiState> = _uiState.asStateFlow()

    fun startRecording() {
        _uiState.update { it.copy(isRecording = true, isPaused = false) }
    }

    fun pauseRecording() {
        _uiState.update { it.copy(isPaused = true) }
    }

    fun resumeRecording() {
        _uiState.update { it.copy(isPaused = false) }
    }

    fun stopRecording(audioFilePath: String, durationSeconds: Long) {
        _uiState.update {
            it.copy(
                isRecording = false,
                audioFilePath = audioFilePath,
                recordingDuration = durationSeconds
            )
        }
    }

    fun saveRecording(title: String, transcript: String, language: String = "en") {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isSaving = true) }
                val note = VoiceNote(
                    id = UUID.randomUUID().toString(),
                    title = title.ifBlank { "Voice Note" },
                    transcript = transcript,
                    audioFilePath = _uiState.value.audioFilePath,
                    createdAt = Instant.now(),
                    durationSeconds = _uiState.value.recordingDuration,
                    language = language,
                    isSynced = false
                )
                saveNoteUseCase(note)
                _uiState.update { it.copy(isSaving = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isSaving = false) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
