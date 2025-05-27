package com.fakeco.security

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main application class for FakeCo Security app.
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection.
 */
@HiltAndroidApp
class FakeCoApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Initialize any app-wide components here
    }
}