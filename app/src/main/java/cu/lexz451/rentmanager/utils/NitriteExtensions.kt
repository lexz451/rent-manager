package cu.lexz451.rentmanager.utils

import org.dizitart.no2.objects.Cursor
import org.dizitart.no2.objects.ObjectRepository
import org.dizitart.no2.objects.filters.ObjectFilters

fun <T> ObjectRepository<T>.findById(id: Long): Cursor<T> {
    return find(ObjectFilters.eq("__id", id))
}