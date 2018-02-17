package ru.nextf.measurements

import android.app.Application
import android.content.Context
import android.os.StrictMode

/**
 * Created by addd on 20.12.2017.
 */
class MyApp : Application() {
    override fun onCreate() {
        ru.nextf.measurements.MyApp.Companion.instance = this.applicationContext

        super.onCreate()
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }

    companion object {
        lateinit var instance: Context
            private set
    }
}