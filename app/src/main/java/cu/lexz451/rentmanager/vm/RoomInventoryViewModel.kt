package cu.lexz451.rentmanager.vm

import androidx.lifecycle.*
import cu.lexz451.rentmanager.data.ManagerDatabase
import cu.lexz451.rentmanager.data.model.Product
import cu.lexz451.rentmanager.data.model.Room
import cu.lexz451.rentmanager.utils.findById
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import org.dizitart.no2.event.ChangeInfo
import org.dizitart.no2.event.ChangeListener
import org.dizitart.no2.objects.filters.ObjectFilters

class RoomInventoryViewModel(
    private val id: Long,
    private val db: ManagerDatabase
) : ViewModel(), ChangeListener {

    private val _room = MutableLiveData<Room>()

    val room: LiveData<Room> = _room

    val products: LiveData<List<Product>> = _room.switchMap {
        return@switchMap liveData {
            emit(it.products.toImmutableList())
        }
    }

    fun addRoomProduct(product: Product) {
        val room = _room.value
        room?.products?.add(product)
        room?.let {
            db.roomStore.update(it)
        }
    }


    fun removeRoomProduct(product: Product) {
        val room = _room.value
        room?.products?.remove(product)
        room?.let {
            db.roomStore.update(it)
        }
    }

    init {
        db.roomStore.register(this)
        load()
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