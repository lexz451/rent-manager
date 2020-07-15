package cu.lexz451.rentmanager.utils

import android.content.Context
import cu.lexz451.rentmanager.ManagerApp
import org.dizitart.no2.NitriteId
import org.dizitart.no2.mapper.Mappable

fun Context.app() = ManagerApp.instance

fun Mappable.generateId(idValue: Long): Long {
    if (idValue == 0L) {
        return NitriteId.newId().idValue
    }
    return idValue
}