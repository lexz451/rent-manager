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

@Indices(
    Index(value = "client.ci", type = IndexType.Fulltext)
)
data class BlackListRecord(
    @Id
    var __id: Long,
    var client: Client,
    var reason: String,
    var date: LocalDate
) : Mappable, Serializable {
    override fun read(mapper: NitriteMapper?, document: Document?) {
        document?.run {
            __id = get("__id") as Long
            client = Client().apply {
                this.read(mapper, get("client") as Document)
            }
            reason = get("reason") as String
            date = LocalDate.parse(get("date") as String)
        }
    }

    override fun write(mapper: NitriteMapper?): Document {
        return Document().apply {
            put("__id", generateId(__id))
            put("client", client.write(mapper))
            put("reason", reason)
            put("date", date.toString())
        }
    }
}