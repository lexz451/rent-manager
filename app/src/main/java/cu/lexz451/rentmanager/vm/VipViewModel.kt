package cu.lexz451.rentmanager.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.lexz451.rentmanager.ManagerApp
import cu.lexz451.rentmanager.data.ManagerDatabase
import cu.lexz451.rentmanager.data.model.Product
import cu.lexz451.rentmanager.data.model.Shift
import cu.lexz451.rentmanager.data.model.VipRecord
import cu.lexz451.rentmanager.utils.findById
import kotlinx.coroutines.launch
import org.dizitart.kno2.filters.and
import org.dizitart.no2.event.ChangeInfo
import org.dizitart.no2.event.ChangeListener
import org.dizitart.no2.objects.filters.ObjectFilters
import java.time.LocalDate

class VipViewModel(
    private val db: ManagerDatabase): ViewModel(), ChangeListener {

    private val _record = MutableLiveData<VipRecord>()

    val record: LiveData<VipRecord> = _record

    init {
        db.vipStore.register(this)
        load()
    }

    fun remove(product: Product) = viewModelScope.launch {
        val record = _record.value
        record?.products?.remove(product)
        db.vipStore.update(record)
    }

    fun add(product: Product) {
        val record = _record.value
        record?.products?.add(product)
        if (record != null) {
            db.vipStore.update(record)
            return
        }
        val currentShift = ManagerApp.instance.settings.currentShift
        val currentDate = LocalDate.now()
        val shift = db.shiftStore.find(ObjectFilters.eq("__id", currentShift)).first()
        val newRecord = VipRecord(0, currentDate, shift)
        newRecord.products.add(product)
        db.vipStore.insert(newRecord)

        val storeProduct = db.productStore.findById(product.__id).first()
        storeProduct.quantity = storeProduct.quantity - product.quantity
        db.productStore.update(storeProduct)
    }

    private fun load() = viewModelScope.launch {
        val currentShift = ManagerApp.instance.settings.currentShift
        val currentDate = LocalDate.now()
        val record = db.vipStore
            .find(ObjectFilters.eq("date", currentDate.toString())
                .and(ObjectFilters.eq("shift.__id", currentShift))).firstOrNull()
        record?.let {
            _record.postValue(it)
        }
    }

    override fun onChange(changeInfo: ChangeInfo?) {
        load()
    }

    override fun onCleared() {
        db.vipStore.deregister(this)
    }
}