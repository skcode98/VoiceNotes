package com.voicenotes.app.data.audio

import android.content.Context
import android.media.MediaPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AudioPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var onPlaybackCompleted: (() -> Unit)? = null

    suspend fun preparePlayer(audioFilePath: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(audioFilePath)
                    setOnCompletionListener {
                        onPlaybackCompleted?.invoke()
                    }
                    prepare()
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun play(): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            mediaPlayer?.start()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun pause(): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            mediaPlayer?.pause()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun stop(): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            mediaPlayer?.apply {
                stop()
                release()
            }
            mediaPlayer = null
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun seekTo(positionMs: Int): Result<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                mediaPlayer?.seekTo(positionMs)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    fun getDuration(): Int {
        return mediaPlayer?.duration ?: 0
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    fun setOnPlaybackCompleted(callback: () -> Unit) {
        onPlaybackCompleted = callback
    }
}
