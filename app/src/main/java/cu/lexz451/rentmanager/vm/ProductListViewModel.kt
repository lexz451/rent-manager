package cu.lexz451.rentmanager.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.lexz451.rentmanager.data.ManagerDatabase
import cu.lexz451.rentmanager.data.model.Product
import kotlinx.coroutines.launch
import org.dizitart.no2.event.ChangeInfo
import org.dizitart.no2.event.ChangeListener

class ProductListViewModel(
    private val db: ManagerDatabase
): ViewModel(), ChangeListener {

    private val _products = MutableLiveData<List<Product>>()

    val products: LiveData<List<Product>> = _products

    init {
        db.productStore.register(this)
        load()
    }

    override fun onChange(changeInfo: ChangeInfo?) {
        load()
    }

    override fun onCleared() {
        db.productStore.deregister(this)
    }

    private fun load() = viewModelScope.launch {
        val data = db.productStore.find().toList()
        _products.postValue(data)
    }
}