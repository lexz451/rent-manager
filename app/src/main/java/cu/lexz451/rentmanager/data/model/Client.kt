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
import java.time.Duration
import java.util.*

@Indices(
    Index(value = "ci", type = IndexType.NonUnique),
    Index(value = "name", type = IndexType.Fulltext)
)
data class Client(
    @Id
    var id: Long,
    var ci: String,
    var name: String,
    var companion: Client?,
    var room: Int,
    var products: MutableList<Product>,
    var checkInDate: Long,
    var checkOutDate: Long?,
    var paymentDetails: PaymentDetails
): Mappable, Serializable {

    constructor() : this(0L, "", "", null, -1,
        mutableListOf<Product>(), 0, null,
        PaymentDetails(.0, .0, .0))

    override fun read(mapper: NitriteMapper?, document: Document?) {
        document?.run {
            this@Client.id = get("__id") as Long
            ci = get("_ci") as String
            name = get("_name") as String
            companion = get("_companion")?.let {
                Client().apply {
                    this.read(mapper, it as Document)
                }
            }
            room = get("_room") as Int
            @Suppress("UNCHECKED_CAST")
            products = get("_products") as MutableList<Product>
            checkInDate = get("_checkInDate") as Long
            checkOutDate = get("_checkOutDate") as? Long
            paymentDetails = get("_paymentDetails") as PaymentDetails
        }
    }

    override fun write(mapper: NitriteMapper?): Document {
        return Document().apply {
            put("__id", generateId(this@Client.id))
            put("_ci", ci)
            put("_name", name)
            put("_companion", companion)
            put("_room", room)
            put("_products", products)
            put("_checkInDate", checkInDate)
            put("_checkOutDate", checkOutDate)
            put("_paymentDetails", paymentDetails)
        }
    }
}