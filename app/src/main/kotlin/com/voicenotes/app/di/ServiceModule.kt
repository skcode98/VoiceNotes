package com.voicenotes.app.di

import android.content.Context
import com.voicenotes.app.data.audio.AudioPlayer
import com.voicenotes.app.data.audio.AudioRecorder
import com.voicenotes.app.data.cloud.FirebaseCloudService
import com.voicenotes.app.data.transcription.TranscriptionService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Singleton
    @Provides
    fun provideAudioRecorder(
        @ApplicationContext context: Context
    ): AudioRecorder {
        return AudioRecorder(context)
    }

    @Singleton
    @Provides
    fun provideAudioPlayer(
        @ApplicationContext context: Context
    ): AudioPlayer {
        return AudioPlayer(context)
    }

    @Singleton
    @Provides
    fun provideTranscriptionService(
        @ApplicationContext context: Context
    ): TranscriptionService {
        return TranscriptionService(context)
    }

    @Singleton
    @Provides
    fun provideFirebaseCloudService(
        @ApplicationContext context: Context
    ): FirebaseCloudService {
        return FirebaseCloudService(context)
    }
}
