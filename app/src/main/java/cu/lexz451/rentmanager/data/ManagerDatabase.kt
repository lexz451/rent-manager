package cu.lexz451.rentmanager.data

import android.content.Context
import org.dizitart.kno2.nitrite
import org.dizitart.no2.Nitrite
import java.io.File

class ManagerDatabase private constructor(context: Context) {




    private val db: Nitrite = nitrite {
        file = File(context.filesDir, DATABASE_FILENAME)
    }

    companion object {
        const val DATABASE_FILENAME = "manager.db"

        private var instance: ManagerDatabase? = null

        fun initDatabase(context: Context): ManagerDatabase {
            return instance ?: ManagerDatabase(context).also { instance = it }
        }
    }
}