package cu.lexz451.rentmanager.data.model

import cu.lexz451.rentmanager.utils.generateId
import org.dizitart.no2.Document
import org.dizitart.no2.IndexType
import org.dizitart.no2.mapper.Mappable
import org.dizitart.no2.mapper.NitriteMapper
import org.dizitart.no2.objects.Id
import org.dizitart.no2.objects.Index
import org.dizitart.no2.objects.Indices
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
@Indices(
    Index(value = "shift.__id", type = IndexType.NonUnique)
)
data class VipRecord(
    @Id
    var __id: Long,
    var date: LocalDate,
    var shift: Shift,
    var products: MutableList<Product> = mutableListOf()
) : Mappable, Serializable {
    @Suppress("UNCHECKED_CAST")
    override fun read(mapper: NitriteMapper?, document: Document?) {
        document?.run {
            __id = get("__id") as Long
            date = LocalDate.parse(get("date") as String)
            shift = Shift().apply {
                this.read(mapper, get("shift") as Document)
            }
            products = get("products") as MutableList<Product>
        }
    }

    override fun write(mapper: NitriteMapper?): Document {
        return Document().apply {
            put("__id", generateId(__id))
            put("date", date.toString())
            put("shift", shift.write(mapper))
            put("products", products)
        }
    }
}