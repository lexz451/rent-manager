package cu.lexz451.rentmanager.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.lexz451.rentmanager.data.ManagerDatabase
import cu.lexz451.rentmanager.data.model.Shift
import cu.lexz451.rentmanager.utils.findById
import kotlinx.coroutines.launch

class ShiftDetailViewModel(
    private val id: Long,
    private val db: ManagerDatabase) : ViewModel() {

    private val _shift = MutableLiveData<Shift>()

    val shift: LiveData<Shift> = _shift

    init {
        load()
    }

    fun save() = viewModelScope.launch {
        db.shiftStore.update(_shift.value!!, true)
    }

    fun remove() = viewModelScope.launch {
        db.shiftStore.remove(_shift.value!!)
    }

    private fun load() = viewModelScope.launch {
        val data = db.shiftStore.findById(id)
        _shift.postValue(data.firstOrNull() ?: Shift())
    }
}