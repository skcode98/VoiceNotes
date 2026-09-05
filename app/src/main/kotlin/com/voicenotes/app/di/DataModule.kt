package com.voicenotes.app.di

import android.content.Context
import androidx.room.Room
import com.voicenotes.app.data.local.database.VoiceNoteDatabase
import com.voicenotes.app.data.local.preferences.AppPreferences
import com.voicenotes.app.data.repository.VoiceNoteRepositoryImpl
import com.voicenotes.app.domain.repository.VoiceNoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideVoiceNoteDatabase(
        @ApplicationContext context: Context
    ): VoiceNoteDatabase {
        return Room.databaseBuilder(
            context,
            VoiceNoteDatabase::class.java,
            "voice_notes_db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideVoiceNoteDao(database: VoiceNoteDatabase) = database.voiceNoteDao()

    @Singleton
    @Provides
    fun provideAppPreferences(
        @ApplicationContext context: Context
    ): AppPreferences {
        return AppPreferences(context)
    }

    @Singleton
    @Provides
    fun provideVoiceNoteRepository(
        voiceNoteDao: com.voicenotes.app.data.local.dao.VoiceNoteDao
    ): VoiceNoteRepository {
        return VoiceNoteRepositoryImpl(voiceNoteDao)
    }
}
