package kr.co.company.hwa

import android.app.Application
import kr.co.company.hwa.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CatcherApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@CatcherApp)
            modules(appModule)
        }
    }
}