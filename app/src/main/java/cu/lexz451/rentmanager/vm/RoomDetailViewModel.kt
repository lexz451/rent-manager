package cu.lexz451.rentmanager.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.lexz451.rentmanager.ManagerApp
import cu.lexz451.rentmanager.data.ManagerDatabase
import cu.lexz451.rentmanager.data.model.Product
import cu.lexz451.rentmanager.data.model.Room
import cu.lexz451.rentmanager.utils.findById
import kotlinx.coroutines.launch
import org.dizitart.no2.event.ChangeInfo
import org.dizitart.no2.event.ChangeListener

class RoomDetailViewModel(
    private val id: Long,
    private val db: ManagerDatabase
) : ViewModel(), ChangeListener {

    private val _room = MutableLiveData<Room>()

    val room: LiveData<Room> = _room

    init {
        db.roomStore.register(this)
        load()
    }

    fun removeClientProduct(product: Product) = viewModelScope.launch {
        val room = _room.value
        room?.client?.products?.remove(product)
        db.roomStore.update(room)
    }

    fun addClientProduct(product: Product) = viewModelScope.launch {
        val room = _room.value
        room?.client?.products?.add(product)
        db.roomStore.update(room)
    }

    override fun onChange(changeInfo: ChangeInfo?) {
        load()
    }

    override fun onCleared() {
        db.roomStore.deregister(this)
    }

    private fun load() = viewModelScope.launch {
        val data = db.roomStore.findById(id)
        _room.postValue(data.first())
    }
}