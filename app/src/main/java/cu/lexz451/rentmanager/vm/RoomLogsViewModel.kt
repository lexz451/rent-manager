package cu.lexz451.rentmanager.vm

import androidx.lifecycle.*
import cu.lexz451.rentmanager.data.ManagerDatabase
import cu.lexz451.rentmanager.data.model.Client
import cu.lexz451.rentmanager.data.model.Room
import cu.lexz451.rentmanager.utils.findById
import kotlinx.coroutines.launch
import org.dizitart.kno2.filters.and
import org.dizitart.no2.event.ChangeInfo
import org.dizitart.no2.event.ChangeListener
import org.dizitart.no2.objects.filters.ObjectFilters

class RoomLogsViewModel(
    private val id: Long,
    private val db: ManagerDatabase) : ViewModel(), ChangeListener {

    private val _clients = MutableLiveData<List<Client>>()

    val clients: LiveData<List<Client>> = _clients

    init {
        db.clientStore.register(this)
        load()
    }

    override fun onChange(changeInfo: ChangeInfo?) {
        load()
    }

    override fun onCleared() {
        db.clientStore.deregister(this)
    }

    private fun load() = viewModelScope.launch {
        val data = db.clientStore.find(ObjectFilters.eq("room", id)).toList()
        data.sortBy { client ->
            return@sortBy client.checkOutDate
        }

        _clients.postValue(data)
    }

    fun deleteLogs() = viewModelScope.launch {
        db.clientStore.remove(ObjectFilters.eq("room", id))
    }

    fun deleteLog(client: Client) = viewModelScope.launch {
        db.clientStore.remove(ObjectFilters.eq("room", id).and(ObjectFilters.eq("__id", client.__id)))
    }

}