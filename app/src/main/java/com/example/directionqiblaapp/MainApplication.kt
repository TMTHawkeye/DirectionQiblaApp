package com.example.directionqiblaapp

import android.app.Application
import com.example.directionqiblaapp.DI.mainModule
import io.paperdb.Paper
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(androidContext = this@MainApplication)
            modules(mainModule)

            Paper.init(this@MainApplication)
        }
    }
}