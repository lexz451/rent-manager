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

@Indices(
    Index(value = "name", type = IndexType.Fulltext)
)
data class Product(
    @Id
    var __id: Long,
    var name: String,
    var quantity: Int,
    var price: Double
) : Mappable, Serializable {

    constructor() : this(0L, "", 0, .0)

    override fun read(mapper: NitriteMapper?, document: Document?) {
        document?.run {
            this@Product.__id = get("__id") as Long
            name = get("name") as String
            quantity = get("quantity") as Int
            price = get("price") as Double
        }
    }

    override fun write(mapper: NitriteMapper?): Document {
        return Document().apply {
            put("__id", generateId(this@Product.__id))
            put("name", name)
            put("quantity", quantity)
            put("price", price)
        }
    }

}