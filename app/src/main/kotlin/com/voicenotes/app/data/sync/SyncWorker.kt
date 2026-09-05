package com.voicenotes.app.data.sync

import android.content.Context
import androidx.work.*
import com.voicenotes.app.domain.repository.VoiceNoteRepository
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Sync logic will be implemented with cloud backend
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val SYNC_WORK_NAME = "voice_notes_sync"

        fun scheduleSyncWorker(context: Context) {
            val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(
                15,
                TimeUnit.MINUTES
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                SYNC_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncWorkRequest
            )
        }
    }
}
