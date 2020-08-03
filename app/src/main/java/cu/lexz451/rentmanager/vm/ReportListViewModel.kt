package cu.lexz451.rentmanager.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.lexz451.rentmanager.data.ManagerDatabase
import cu.lexz451.rentmanager.data.model.Report
import kotlinx.coroutines.launch
import org.dizitart.no2.event.ChangeInfo
import org.dizitart.no2.event.ChangeListener
import java.io.File
import java.time.format.DateTimeFormatter

class ReportListViewModel(
    private val db: ManagerDatabase
) : ViewModel(), ChangeListener {

    private val _reports = MutableLiveData<List<Report>>()

    val reports: LiveData<List<Report>> = _reports

    init {
        db.reportStore.register(this)
        load()
    }

    override fun onChange(changeInfo: ChangeInfo?) {
        load()
    }

    override fun onCleared() {
        super.onCleared()
        db.reportStore.deregister(this)
    }

    private fun load() = viewModelScope.launch {
        val data = db.reportStore.find().toList()
        _reports.postValue(data)
    }

    fun removeReport(report: Report) = viewModelScope.launch {
        db.reportStore.remove(report)
    }

}