package cu.lexz451.rentmanager.data

import android.content.Context
import cu.lexz451.rentmanager.data.model.*
import net.ozaydin.serkan.easy_csv.EasyCsv
import org.dizitart.kno2.getRepository
import org.dizitart.kno2.nitrite
import org.dizitart.no2.Nitrite
import java.io.File

class ManagerDatabase private constructor(context: Context) {

    private val db: Nitrite = nitrite {
        file = File(context.filesDir, DATABASE_FILENAME)
    }

    val clientStore = db.getRepository<Client>()
    val productStore = db.getRepository<Product>()
    val reportStore = db.getRepository<Report>()
    val roomStore = db.getRepository<Room>()
    val shiftStore = db.getRepository<Shift>()
    val vipStore = db.getRepository<VipRecord>()
    val priceStore = db.getRepository<Price>()
    val blackListStore = db.getRepository<BlackListRecord>()

    fun clear() {

    }

    companion object {
        const val DATABASE_FILENAME = "manager.db"

        private var instance: ManagerDatabase? = null

        fun initDatabase(context: Context): ManagerDatabase {
            return instance ?: ManagerDatabase(context).also { instance = it }
        }
    }
}