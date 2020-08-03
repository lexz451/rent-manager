package cu.lexz451.rentmanager.vm

import androidx.lifecycle.*
import cu.lexz451.rentmanager.data.ManagerDatabase
import cu.lexz451.rentmanager.data.model.BlackListRecord
import cu.lexz451.rentmanager.data.model.Client
import cu.lexz451.rentmanager.utils.findById
import kotlinx.coroutines.launch
import org.dizitart.no2.objects.filters.ObjectFilters
import java.time.LocalDateTime

class RoomAddClientViewModel(
    private val id: Long,
    private val db: ManagerDatabase
) : ViewModel() {

    val _ci = MutableLiveData<String>("")
    val _name = MutableLiveData<String>("")
    val _comp_ci = MutableLiveData<String>("")
    val _comp_name = MutableLiveData<String>("")

    val blacklistClient: LiveData<BlackListRecord?> = _ci.switchMap {
        val blacklist = db.blackListStore.find(ObjectFilters.eq("client.ci", it)).firstOrNull()
        return@switchMap liveData {
            emit(blacklist)
        }
    }

    fun addClient() = viewModelScope.launch {
        val newClient = Client()
        newClient.ci = _ci.value!!
        newClient.name = _name.value!!
        newClient.checkInDate = LocalDateTime.now()
        newClient.room = id
        val companion = Client()
        companion.ci = _comp_ci.value!!
        companion.name = _comp_name.value!!
        companion.checkInDate = LocalDateTime.now()
        companion.room = id
        newClient.companion = companion

        val room = db.roomStore.findById(id).first()
        room.client = newClient

        db.roomStore.update(room)
    }

}