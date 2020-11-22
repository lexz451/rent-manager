package cu.lexz451.rentmanager.data.model

import cu.lexz451.rentmanager.ManagerApp
import cu.lexz451.rentmanager.utils.generateId
import org.dizitart.kno2.filters.and
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Indices(
    Index(value = "shift.__id", type = IndexType.NonUnique)
)
data class Report(
    @Id
    var __id: Long,
    var date: LocalDate,
    var shift: Shift,
    var clients: MutableList<Client> = mutableListOf()
) : Mappable, Serializable {

    @Suppress("UNCHECKED_CAST")
    override fun read(mapper: NitriteMapper?, document: Document?) {
        document?.run {
            __id = get("__id") as Long
            date = LocalDate.parse(get("date") as String)
            clients = get("clients") as MutableList<Client>
            shift = Shift().apply {
                this.read(mapper, get("shift") as Document)
            }
        }
    }

    override fun write(mapper: NitriteMapper?): Document {
        return Document().apply {
            put("__id", generateId(__id))
            put("date", date.toString())
            put("clients", clients)
            put("shift", shift.write(mapper))
        }
    }

    fun generateHTMLReport(): String {

        val rooms = clients.count()
        val totalForRooms = clients.sumByDouble { it.paymentDetails.forRoom }

        val productsHtml = mutableListOf<String>()
        val vipProductsHtml = mutableListOf<String>()

        var totalShift = totalForRooms

        val products = clients.map { c -> c.products }.flatten().groupBy { it.name }
        products.forEach { (key: String, value: List<Product>) ->
            productsHtml.add("<li>${key} / Cantidad: ${value.sumBy { it.quantity }} / Importe: ${value.sumByDouble { it.price * it.quantity }}</li>")
            totalShift += value.sumByDouble { it.price * it.quantity }
        }

        val vipRecord = ManagerApp.instance.database.vipStore.find(
            ObjectFilters.eq("shift.__id", shift.__id)
                .and(ObjectFilters.eq("date", date.toString()))
        ).firstOrNull()

        val vipProductsMap = vipRecord?.products?.groupBy { it.name }
        vipProductsMap?.forEach { (key, value) ->
            vipProductsHtml.add("<li>${key} / Cantidad: ${value.sumBy { it.quantity }} / Importe: ${value.sumByDouble { it.price * it.quantity }}</li>")
            totalShift += value.sumByDouble { it.price * it.quantity }
        }

        return "******************************<br>" +
                "<strong>REPORTE</strong><br>" +
                "<span>Fecha: ${date.format(DateTimeFormatter.ofPattern("d/MM/YY"))}</span><br>" +
                "<span>Turno: ${shift.employee}</span><br><br>" +
                "<strong>VENTA<br></strong>" +
                "<span>Habitaciones: ${rooms}</span><br>" +
                "<span>Importe: ${totalForRooms}</span><br><br>" +
                "<strong>Productos</strong><br>" +
                "<ul>${productsHtml.joinToString("")}</ul><br>" +
                "<strong>Reservado</strong><br>" +
                "<ul>${vipProductsHtml.joinToString("")}</ul><br>" +
                "<strong>TOTAL<br></strong>" +
                "<span>${totalShift}</span><br>" +
                "*******************************"
    }
}