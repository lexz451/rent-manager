package cu.lexz451.rentmanager.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.lexz451.rentmanager.ManagerApp
import cu.lexz451.rentmanager.data.ManagerDatabase
import cu.lexz451.rentmanager.data.model.BlackListRecord
import cu.lexz451.rentmanager.data.model.Client
import cu.lexz451.rentmanager.data.model.Price
import cu.lexz451.rentmanager.data.model.Report
import cu.lexz451.rentmanager.utils.AlarmScheduler
import cu.lexz451.rentmanager.utils.findById
import kotlinx.coroutines.launch
import org.dizitart.kno2.filters.and
import org.dizitart.no2.objects.filters.ObjectFilters
import java.time.LocalDate
import java.time.LocalDateTime

class RoomPaymentViewModel(
    private val db: ManagerDatabase
) : ViewModel() {

    private val _prices = MutableLiveData<List<Price>>()

    val prices: LiveData<List<Price>> = _prices

    init {
        load()
    }

    fun processClient(client: Client) = viewModelScope.launch {
        val room = db.roomStore.findById(client.room).first()

        // Rest products in general inventory
        val clientProducts = client.products
        for (clientProduct in clientProducts) {
            val quantity = clientProduct.quantity
            val product = db.productStore.findById(clientProduct.__id).firstOrNull()
            if (product != null) {
                product.quantity = product.quantity - quantity
                db.productStore.update(product)
            }
        }
        // Add client data to Report
        val currentDate = LocalDate.now()
        val currentShift = ManagerApp.instance.settings.currentShift
        val report = db.reportStore.find(
            ObjectFilters.eq("date", currentDate.toString())
                .and(ObjectFilters.eq("shift.__id", currentShift))).firstOrNull()

        if (report == null) {
            // Create a new Report
            val shift = db.shiftStore.findById(currentShift).first()
            val newReport = Report(0, currentDate, shift)
            newReport.clients.add(client)
            db.reportStore.insert(newReport)
        } else {
            report.clients.add(client)
            db.reportStore.update(report)
        }

        // Remove client from Room
        room.client = null
        db.roomStore.update(room)

        // Save Client
        client.checkOutDate = LocalDateTime.now()
        db.clientStore.update(client, true)
    }

    fun processClientAndBlackList(record: BlackListRecord) = viewModelScope.launch {
        // Add client to black list
        processClient(record.client).invokeOnCompletion {
            db.blackListStore.insert(record)
        }

        // Process client
    }

    private fun load() = viewModelScope.launch {
        val data = db.priceStore.find().toList()
        _prices.postValue(data)
    }
}