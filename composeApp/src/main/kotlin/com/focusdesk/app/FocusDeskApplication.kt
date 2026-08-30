package com.focusdesk.app

import android.app.Application
import com.focusdesk.app.di.appModule
import com.focusdesk.core.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class FocusDeskApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@FocusDeskApplication)
            modules(sharedModule, appModule)
        }
    }
}
