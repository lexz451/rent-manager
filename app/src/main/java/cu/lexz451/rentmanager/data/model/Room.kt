package cu.lexz451.rentmanager.data.model

import cu.lexz451.rentmanager.utils.generateId
import org.dizitart.no2.Document
import org.dizitart.no2.mapper.Mappable
import org.dizitart.no2.mapper.NitriteMapper
import org.dizitart.no2.objects.Id
import java.io.Serializable

data class Room(
    @Id
    var __id: Long,
    var tag: String,
    var client: Client?,
    var products: MutableList<Product> = mutableListOf()
) : Mappable, Serializable {

    constructor(): this(0, "", null)

    @Suppress("UNCHECKED_CAST")
    override fun read(mapper: NitriteMapper?, document: Document?) {
        document?.run {
            this@Room.__id = get("__id") as Long
            tag = get("tag") as String
            client = get("client")?.let {
                Client().apply {
                    this.read(mapper, it as Document)
                }
            }
            products = get("products") as MutableList<Product>
        }
    }

    override fun write(mapper: NitriteMapper?): Document {
        return Document().apply {
            put("__id", generateId(this@Room.__id))
            put("tag", tag)
            put("client", client?.write(mapper))
            put("products", products)
        }
    }
}