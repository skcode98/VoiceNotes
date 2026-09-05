package com.voicenotes.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voicenotes.app.data.audio.AudioRecorder
import com.voicenotes.app.data.transcription.TranscriptionService
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
    private val saveNoteUseCase: SaveNoteUseCase,
    private val audioRecorder: AudioRecorder,
    private val transcriptionService: TranscriptionService
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecorderUiState())
    val uiState: StateFlow<RecorderUiState> = _uiState.asStateFlow()

    fun startRecording(language: String = "en") {
        viewModelScope.launch {
            audioRecorder.startRecording().fold(
                onSuccess = { path ->
                    transcriptionService.startLiveTranscription(language)
                    _uiState.update { it.copy(isRecording = true, isPaused = false, audioFilePath = path) }
                },
                onFailure = { error -> _uiState.update { it.copy(error = error.message) } }
            )
        }
    }

    fun pauseRecording() {
        viewModelScope.launch {
            audioRecorder.pauseRecording()
            _uiState.update { it.copy(isPaused = true) }
        }
    }

    fun resumeRecording() {
        viewModelScope.launch {
            audioRecorder.resumeRecording()
            _uiState.update { it.copy(isPaused = false) }
        }
    }

    fun stopRecording(audioFilePath: String, durationSeconds: Long) {
        viewModelScope.launch {
            val duration = audioRecorder.stopRecording().getOrDefault(durationSeconds)
            _uiState.update {
                it.copy(
                    isRecording = false,
                    audioFilePath = it.audioFilePath.ifBlank { audioFilePath },
                    recordingDuration = duration
                )
            }
        }
    }

    fun cancelRecording() {
        viewModelScope.launch {
            audioRecorder.cancelRecording()
            transcriptionService.release()
            _uiState.update { it.copy(isRecording = false, audioFilePath = "", recordingDuration = 0L) }
        }
    }

    fun saveRecording(title: String, transcript: String, language: String = "en") {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isSaving = true) }
                val generatedTranscript = if (transcript.isBlank()) {
                    transcriptionService.generateTranscript(_uiState.value.audioFilePath, language).getOrDefault("")
                } else {
                    transcript
                }
                val note = VoiceNote(
                    id = UUID.randomUUID().toString(),
                    title = title.ifBlank { "Voice Note" },
                    transcript = generatedTranscript,
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
