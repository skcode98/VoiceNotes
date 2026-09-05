package com.voicenotes.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VoiceNotesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
