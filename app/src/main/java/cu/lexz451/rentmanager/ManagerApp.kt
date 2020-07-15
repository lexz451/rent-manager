package cu.lexz451.rentmanager

import android.app.Application
import cu.lexz451.rentmanager.data.ManagerDatabase
import cu.lexz451.rentmanager.data.ManagerSettings

class ManagerApp : Application() {

    lateinit var settings: ManagerSettings
    lateinit var database: ManagerDatabase

    override fun onCreate() {
        super.onCreate()
        instance = this
        settings = ManagerSettings.initSettings(this)
        database = ManagerDatabase.initDatabase(this)
    }

    companion object {
        lateinit var instance: ManagerApp
    }
}