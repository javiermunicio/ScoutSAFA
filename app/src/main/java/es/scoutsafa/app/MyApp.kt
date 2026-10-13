package es.scoutsafa.app

import android.app.Application
import es.scoutsafa.app.di.appModule
import es.scoutsafa.app.notifications.NotificationHelper
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannels(this)
        startKoin {
            androidContext(this@MyApp)
            modules(appModule)
        }
    }
}
