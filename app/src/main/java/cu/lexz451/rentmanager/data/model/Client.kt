package cu.lexz451.rentmanager.data.model

import cu.lexz451.rentmanager.ManagerApp
import cu.lexz451.rentmanager.utils.findById
import cu.lexz451.rentmanager.utils.generateId
import org.dizitart.no2.Document
import org.dizitart.no2.IndexType
import org.dizitart.no2.mapper.Mappable
import org.dizitart.no2.mapper.NitriteMapper
import org.dizitart.no2.objects.Id
import org.dizitart.no2.objects.Index
import org.dizitart.no2.objects.Indices
import org.dizitart.no2.objects.ObjectFilter
import org.dizitart.no2.objects.filters.ObjectFilters
import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Indices(
    Index(value = "ci", type = IndexType.NonUnique),
    Index(value = "name", type = IndexType.Fulltext)
)
data class Client(
    @Id
    var __id: Long,
    var ci: String,
    var name: String,
    var companion: Client?,
    var room: Long,
    var products: MutableList<Product>,
    var checkInDate: LocalDateTime,
    var checkOutDate: LocalDateTime?,
    var paymentDetails: PaymentDetails
): Mappable, Serializable {

    constructor() : this(0L, "", "", null, -1,
        mutableListOf<Product>(), LocalDateTime.now(), null,
        PaymentDetails(.0, .0, .0, .0, .0, 0, 0L)
    )

    override fun read(mapper: NitriteMapper?, document: Document?) {
        document?.run {
            this@Client.__id = get("__id") as Long
            ci = get("ci") as String
            name = get("name") as String
            companion = get("companion")?.let {
                Client().apply {
                    this.read(mapper, it as Document)
                }
            }
            room = get("room") as Long
            @Suppress("UNCHECKED_CAST")
            products = get("products") as MutableList<Product>
            checkInDate = LocalDateTime.parse(get("checkInDate") as String)
            checkOutDate = get("checkOutDate")?.let { LocalDateTime.parse(it as String) }
            paymentDetails = get("paymentDetails") as PaymentDetails
        }
    }

    fun generateHTMLReport(): String {

        val productsHtml = products.map {
            "<li>${it.name} / Cantidad: ${it.quantity} / Importe: ${it.quantity * it.price}</li>"
        }

        val shift = ManagerApp.instance.database.shiftStore
            .findById(paymentDetails.shift).first()

        return "******************************<br>" +
                "<strong>CLIENTE</strong><br>" +
                "<span>C.I: ${ci}</span><br>" +
                "<span>Nombre: ${name}</span><br><br>" +
                "<strong>ACOMPAÑANTE<br></strong>" +
                "<span>C.I: ${companion?.ci}</span><br>" +
                "<span>Nombre: ${companion?.name}</span><br><br>" +
                "<strong>TURNO</strong><br>" +
                "<span>${shift.employee}</span><br><br>" +
                "<strong>ENTRADA</strong><br>" +
                "<span>${checkInDate.format(DateTimeFormatter.ofPattern("d/MM/YY - h:mm a"))}</span><br><br>" +
                "<strong>SALIDA</strong><br>" +
                "<span>${checkOutDate?.format(DateTimeFormatter.ofPattern("d/MM/YY - h:mm a"))}</span><br><br>" +
                "<strong>HORAS EXTRA</strong><br>" +
                "<span>${paymentDetails.extraHours}</span><br><br>" +
                "<strong>CONSUMO</strong>" +
                "<ul>${productsHtml.joinToString("")}</ul><br>" +
                "<strong>A PAGAR</strong><br>" +
                "<span>Habitación: ${paymentDetails.forRoom}</span><br>" +
                "<span>Consumo: ${paymentDetails.forProducts}</span><br>" +
                "<span>Total: ${paymentDetails.total}</span><br><br>" +
                "<strong>PAGO</strong><br>" +
                "<span style='color: #66BB6A;'>Pagado: ${paymentDetails.paid}</span><br>" +
                "<span style='color: #CF6679;'>Devolución: ${paymentDetails.paidReturn}</span><br>" +
                "*******************************"
    }

    override fun write(mapper: NitriteMapper?): Document {
        return Document().apply {
            put("__id", generateId(this@Client.__id))
            put("ci", ci)
            put("name", name)
            put("companion", companion?.write(mapper))
            put("room", room)
            put("products", products)
            put("checkInDate", checkInDate.toString())
            put("checkOutDate", checkOutDate?.toString())
            put("paymentDetails", paymentDetails)
        }
    }
}