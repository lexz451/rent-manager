package cu.lexz451.rentmanager.data

import android.content.Context
import android.content.SharedPreferences

class ManagerSettings private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("${context.packageName}_preferences", Context.MODE_PRIVATE)

    var currentRoomPrice: Long
        get() = prefs.getLong("baseRoomPrice", 0)
        set(value) {
            prefs.edit().putLong("baseRoomPrice", value).apply()
        }

    val extraRoomPrice: Double
        get() = prefs.getString("extra_room_price", "0")?.toDouble() ?: .0

    var rooms: String?
        get() = prefs.getString("rooms", "6")
        set(value) {
            prefs.edit().putString("rooms", value).apply()
        }

    var currentShift: Long
        get() = prefs.getLong("shift", 0)
        set(value) {
            prefs.edit().putLong("shift", value).apply()
        }

    companion object {
        private var instance: ManagerSettings? = null

        fun initSettings(context: Context): ManagerSettings {
            return instance ?: ManagerSettings(context).also { instance = it }
        }
    }
}