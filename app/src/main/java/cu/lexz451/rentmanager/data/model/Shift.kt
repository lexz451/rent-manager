package cu.lexz451.rentmanager.data.model

import cu.lexz451.rentmanager.utils.generateId
import org.dizitart.no2.Document
import org.dizitart.no2.mapper.Mappable
import org.dizitart.no2.mapper.NitriteMapper
import org.dizitart.no2.objects.Id
import java.time.LocalDateTime

data class Shift(
    @Id
    var __id: Long,
    var employee: String,
    var date: LocalDateTime
) : Mappable {

    constructor(): this(0, "", LocalDateTime.now())

    override fun read(mapper: NitriteMapper?, document: Document?) {
        document?.run {
            this@Shift.__id = get("__id") as Long
            employee = get("name") as String
            date = LocalDateTime.parse(get("date") as String)
        }
    }

    override fun write(mapper: NitriteMapper?): Document {
        return Document().apply {
            put("__id", generateId(this@Shift.__id))
            put("name", employee)
            put("date", date.toString())
        }
    }
}