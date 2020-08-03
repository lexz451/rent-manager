package cu.lexz451.rentmanager.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.lexz451.rentmanager.data.ManagerDatabase
import cu.lexz451.rentmanager.data.model.Product
import cu.lexz451.rentmanager.utils.findById
import kotlinx.coroutines.launch
import org.dizitart.no2.objects.filters.ObjectFilters

class ProductListDialogViewModel(
    private val roomId: Long,
    private val db: ManagerDatabase
) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()

    val products: LiveData<List<Product>> = _products

    init {
        load()
    }

    private fun load() = viewModelScope.launch {
        if (roomId != 0L) {
            val room = db.roomStore.findById(roomId).first()
            _products.postValue(room.products)
        } else {
            val products = db.productStore.find().toList()
            _products.postValue(products)
        }
    }
}