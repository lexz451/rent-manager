package cu.lexz451.rentmanager.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.lexz451.rentmanager.data.ManagerDatabase
import cu.lexz451.rentmanager.data.model.BlackListRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.dizitart.no2.event.ChangeInfo
import org.dizitart.no2.event.ChangeListener

class BlackListViewModel(db: ManagerDatabase) : ViewModel(), ChangeListener {

    private val blackListStore = db.blackListStore

    private val _records = MutableLiveData<List<BlackListRecord>>()

    val records: LiveData<List<BlackListRecord>> = _records

    init {
        blackListStore.register(this)
        loadData()
    }

    override fun onChange(changeInfo: ChangeInfo?) {
        loadData()
    }

    override fun onCleared() {
        blackListStore.deregister(this)
    }

    private fun loadData() = viewModelScope.launch {
        val records = blackListStore.find().toList()
        _records.postValue(records)
    }

    fun removeRecord(record: BlackListRecord) = viewModelScope.launch {
        blackListStore.remove(record)
    }
}
