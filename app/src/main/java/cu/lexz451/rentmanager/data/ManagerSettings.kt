package cu.lexz451.rentmanager.data

import android.content.Context
import android.content.SharedPreferences
import cu.lexz451.rentmanager.ManagerApp

class ManagerSettings private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

    var baseRoomPrice
        get() = prefs.getStringSet("baseRoomPrice", setOf())
        set(value) {
            prefs.edit().putStringSet("baseRoomPrice", value).apply()
        }

    companion object {
        private var instance: ManagerSettings? = null

        fun initSettings(context: Context): ManagerSettings {
            return instance ?: ManagerSettings(context).also { instance = it }
        }
    }
}