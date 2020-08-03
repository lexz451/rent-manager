package cu.lexz451.rentmanager.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.lexz451.rentmanager.data.ManagerDatabase
import cu.lexz451.rentmanager.data.model.Price
import kotlinx.coroutines.launch
import org.dizitart.no2.event.ChangeInfo
import org.dizitart.no2.event.ChangeListener

class PriceListViewModel(
    private val db: ManagerDatabase
) : ViewModel(), ChangeListener {

    private val _prices = MutableLiveData<List<Price>>()

    val prices: LiveData<List<Price>> = _prices

    init {
        db.priceStore.register(this)
        load()
    }

    override fun onChange(changeInfo: ChangeInfo?) {
        load()
    }

    fun updatePrice(price: Price) = viewModelScope.launch {
        db.priceStore.update(price, true)
    }

    override fun onCleared() {
        super.onCleared()
        db.priceStore.deregister(this)
    }

    private fun load() = viewModelScope.launch {
        val data = db.priceStore.find().toList()
        _prices.postValue(data)
    }
}