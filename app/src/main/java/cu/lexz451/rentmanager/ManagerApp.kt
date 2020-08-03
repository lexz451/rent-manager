package cu.lexz451.rentmanager

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationManagerCompat
import cu.lexz451.rentmanager.data.ManagerDatabase
import cu.lexz451.rentmanager.data.ManagerSettings
import cu.lexz451.rentmanager.utils.NotificationHelper

class ManagerApp : Application() {

    lateinit var settings: ManagerSettings
    lateinit var database: ManagerDatabase

    override fun onCreate() {
        super.onCreate()
        instance = this
        settings = ManagerSettings.initSettings(this)
        database = ManagerDatabase.initDatabase(this)

        NotificationHelper.createNotificationChannel(this,
            NotificationManagerCompat.IMPORTANCE_HIGH, false,
            getString(R.string.app_name), "App notification channel.")
    }

    companion object {
        lateinit var instance: ManagerApp
        init {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}