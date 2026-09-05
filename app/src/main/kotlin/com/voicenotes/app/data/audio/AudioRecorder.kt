package com.voicenotes.app.data.audio

import android.content.Context
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.Instant

class AudioRecorder(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var audioFilePath: String = ""
    private var recordingStartTime: Long = 0L

    suspend fun startRecording(): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val recordingsDir = File(context.filesDir, "recordings")
            if (!recordingsDir.exists()) {
                recordingsDir.mkdirs()
            }

            audioFilePath = File(
                recordingsDir,
                "recording_${Instant.now().toEpochMilli()}.m4a"
            ).absolutePath

            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(audioFilePath)
                prepare()
                start()
            }

            recordingStartTime = System.currentTimeMillis()
            Result.success(audioFilePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun pauseRecording(): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            mediaRecorder?.pause()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resumeRecording(): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            mediaRecorder?.resume()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun stopRecording(): Result<Long> = withContext(Dispatchers.IO) {
        return@withContext try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            val duration = (System.currentTimeMillis() - recordingStartTime) / 1000
            Result.success(duration)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelRecording(): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            File(audioFilePath).delete()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecordingDuration(): Long {
        return (System.currentTimeMillis() - recordingStartTime) / 1000
    }
}
