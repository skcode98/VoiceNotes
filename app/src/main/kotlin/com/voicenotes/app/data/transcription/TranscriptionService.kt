package com.voicenotes.app.data.transcription

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.withContext
import java.util.Locale

class TranscriptionService(private val context: Context) {
    private var speechRecognizer: SpeechRecognizer? = null
    private var isCapturing = false
    private var selectedLanguage = "en"
    private val transcriptParts = mutableListOf<String>()
    private var latestPartial = ""
    private var completion: CompletableDeferred<Result<String>>? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    suspend fun identifyLanguage(text: String): Result<String> =
        withContext(Dispatchers.Default) {
            return@withContext try {
                val languageIdentifier = LanguageIdentification.getClient()
                var language = "en"
                languageIdentifier.identifyLanguage(text)
                    .addOnSuccessListener { detectedLanguage ->
                        language = detectedLanguage
                    }
                Result.success(language)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun translateText(
        text: String,
        sourceLanguage: String,
        targetLanguage: String
    ): Result<String> = withContext(Dispatchers.Default) {
        return@withContext try {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguage)
                .setTargetLanguage(targetLanguage)
                .build()

            val translator = Translation.getClient(options)
            var translatedText = text
            translator.translate(text)
                .addOnSuccessListener { result ->
                    translatedText = result
                }
            Result.success(translatedText)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateTranscript(audioFilePath: String, language: String): Result<String> =
        withContext(Dispatchers.Main.immediate) {
            // SpeechRecognizer works from the live microphone, not from an m4a file.
            // The recorder starts this session before audio capture and this method
            // returns the transcript accumulated for that recording.
            selectedLanguage = language.ifBlank { selectedLanguage }
            if (audioFilePath.isBlank()) {
                Result.failure(IllegalArgumentException("Audio file path is empty"))
            } else {
                finishLiveTranscription()
            }
        }

    suspend fun startLiveTranscription(language: String): Result<Unit> =
        withContext(Dispatchers.Main.immediate) {
            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                return@withContext Result.failure(
                    IllegalStateException("Speech recognition is unavailable on this device")
                )
            }

            selectedLanguage = language.ifBlank { "en" }
            transcriptParts.clear()
            latestPartial = ""
            isCapturing = true
            completion = CompletableDeferred()
            speechRecognizer?.destroy()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).also { recognizer ->
                recognizer.setRecognitionListener(listener)
                startListening(recognizer)
            }
            Result.success(Unit)
        }

    suspend fun finishLiveTranscription(): Result<String> =
        withContext(Dispatchers.Main.immediate) {
            isCapturing = false
            mainHandler.removeCallbacksAndMessages(null)
            speechRecognizer?.stopListening()
            val result = withTimeoutOrNull(1500L) { completion?.await() }
                ?: Result.success(currentTranscript())
            speechRecognizer?.destroy()
            speechRecognizer = null
            completion = null
            result
        }

    private val listener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) = Unit
        override fun onBeginningOfSpeech() = Unit
        override fun onRmsChanged(rmsdB: Float) = Unit
        override fun onBufferReceived(buffer: ByteArray?) = Unit
        override fun onEndOfSpeech() = Unit
        override fun onPartialResults(results: Bundle?) {
            latestPartial = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.firstOrNull()
                .orEmpty()
        }

        override fun onResults(results: Bundle?) {
            results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.firstOrNull()
                ?.takeIf { it.isNotBlank() }
                ?.let { result ->
                    if (transcriptParts.lastOrNull() != result) transcriptParts += result
                    latestPartial = ""
                }
            if (isCapturing) {
                speechRecognizer?.let { recognizer ->
                    mainHandler.postDelayed({ if (isCapturing) startListening(recognizer) }, 150L)
                }
            } else {
                completion?.complete(Result.success(currentTranscript()))
            }
        }

        override fun onError(error: Int) {
            if (isCapturing && error != SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) {
                speechRecognizer?.let { recognizer ->
                    mainHandler.postDelayed({ if (isCapturing) startListening(recognizer) }, 250L)
                }
            } else {
                completion?.complete(
                    Result.failure(IllegalStateException("Speech recognition failed: $error"))
                )
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) = Unit
    }

    private fun startListening(recognizer: SpeechRecognizer) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.forLanguageTag(selectedLanguage).toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        recognizer.startListening(intent)
    }

    private fun currentTranscript(): String =
        (transcriptParts + latestPartial)
            .filter { it.isNotBlank() }
            .distinct()
            .joinToString(" ")

    fun release() {
        isCapturing = false
        mainHandler.removeCallbacksAndMessages(null)
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}
