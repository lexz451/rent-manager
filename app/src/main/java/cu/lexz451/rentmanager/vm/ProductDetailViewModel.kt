package cu.lexz451.rentmanager.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.lexz451.rentmanager.data.ManagerDatabase
import cu.lexz451.rentmanager.data.model.Product
import cu.lexz451.rentmanager.utils.findById
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val id: Long,
    private val db: ManagerDatabase
) : ViewModel() {

    private val _product = MutableLiveData<Product>()

    val product: LiveData<Product> = _product

    init {
        load()
    }

    fun save() = viewModelScope.launch {
        db.productStore.update(_product.value!!, true)
    }

    fun remove() = viewModelScope.launch {
        db.productStore.remove(_product.value!!)
    }

    private fun load() = viewModelScope.launch {
        val data = db.productStore.findById(id)
        _product.postValue(data.firstOrNull() ?: Product())
    }
}