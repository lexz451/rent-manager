package cu.lexz451.rentmanager.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.lexz451.rentmanager.data.ManagerDatabase
import cu.lexz451.rentmanager.data.model.Shift
import kotlinx.coroutines.launch
import org.dizitart.no2.event.ChangeInfo
import org.dizitart.no2.event.ChangeListener

class ShiftListViewModel(
    private val db: ManagerDatabase): ViewModel(), ChangeListener {

    private val _shifts = MutableLiveData<List<Shift>>()

    val shifts: LiveData<List<Shift>> = _shifts

    init {
        db.shiftStore.register(this)
        load()
    }

    override fun onChange(changeInfo: ChangeInfo?) {
        load()
    }

    override fun onCleared() {
        db.shiftStore.deregister(this)
    }

    private fun load() = viewModelScope.launch {
        val data = db.shiftStore.find().toList()
        _shifts.postValue(data)
    }
}
