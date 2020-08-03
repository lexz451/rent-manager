package cu.lexz451.rentmanager.data.model

import cu.lexz451.rentmanager.utils.generateId
import org.dizitart.no2.Document
import org.dizitart.no2.mapper.Mappable
import org.dizitart.no2.mapper.NitriteMapper
import org.dizitart.no2.objects.Id
import java.io.Serializable

data class Price(
    @Id
    var __id: Long,
    var name: String,
    var price: Double
): Mappable, Serializable {
    override fun read(mapper: NitriteMapper?, document: Document?) {
        document?.run {
            __id = get("__id") as Long
            name = get("name") as String
            price = get("price") as Double
        }
    }

    override fun write(mapper: NitriteMapper?): Document {
        return Document().apply {
            put("__id", generateId(__id))
            put("name", name)
            put("price", price)
        }
    }
}