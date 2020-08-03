package cu.lexz451.rentmanager.vm

import androidx.lifecycle.*
import cu.lexz451.rentmanager.ManagerApp
import cu.lexz451.rentmanager.data.ManagerDatabase
import cu.lexz451.rentmanager.data.model.Room
import cu.lexz451.rentmanager.data.model.Shift
import cu.lexz451.rentmanager.data.model.VipRecord
import kotlinx.coroutines.launch
import org.dizitart.kno2.filters.and
import org.dizitart.no2.event.ChangeInfo
import org.dizitart.no2.event.ChangeListener
import org.dizitart.no2.objects.filters.ObjectFilters
import java.time.LocalDate
import java.time.LocalDateTime

class RoomListViewModel(
    private val db: ManagerDatabase) : ViewModel(), ChangeListener {

    private val _rooms = MutableLiveData<List<Room>>()
    private val _shifts = MutableLiveData<MutableList<Shift>>()
    private val _record = MutableLiveData<VipRecord?>()

    val rooms: LiveData<List<Room>> = _rooms
    val shifts: LiveData<MutableList<Shift>> = _shifts
    val record: LiveData<VipRecord?> = _record

    init {
        db.shiftStore.register(this)
        db.roomStore.register(this)
        db.vipStore.register(this)
        load()
    }

    override fun onChange(changeInfo: ChangeInfo?) {
        load()
    }

    override fun onCleared() {
        db.roomStore.deregister(this)
        db.shiftStore.deregister(this)
        db.vipStore.deregister(this)
    }

    fun add() = viewModelScope.launch {
        val count = _rooms.value?.size ?: 0
        val room = Room()
        room.tag = "#${count + 1}"
        db.roomStore.insert(room)
    }

    fun remove(room: Room) = viewModelScope.launch {
        db.roomStore.remove(room)
    }

    fun updateShift(shift: Shift) = viewModelScope.launch {
        shift.date = LocalDateTime.now()
        db.shiftStore.update(shift)
        ManagerApp.instance.settings.currentShift = shift.__id
    }

    private fun load() = viewModelScope.launch {
        val data = db.roomStore.find().toList()
        _rooms.postValue(data)

        val shifts = db.shiftStore.find().toList()
        _shifts.postValue(shifts)

        val currentShift = ManagerApp.instance.settings.currentShift

        val today = LocalDate.now()
        val todayRecord = db.vipStore.find(
            ObjectFilters.eq("date", today)
                .and(ObjectFilters.eq("shift.__id", currentShift))
        ).firstOrNull()
        _record.postValue(todayRecord)
    }
}
