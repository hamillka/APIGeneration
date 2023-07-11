package api.parser

import kotlinx.serialization.*
import kotlinx.serialization.json.*

import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

data class UnknownUser(val id: Int, val details: JsonObject)

object UnknownUserSerializer : KSerializer<List<UnknownUser>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("UnknownUser") {
        element<Int>("id")
        element<JsonElement>("details")
    }

    override fun deserialize(decoder: Decoder): List<UnknownUser> {
        val jsonInput = decoder as? JsonDecoder ?: error("Can be deserialized only by JSON")
        val json = jsonInput.decodeJsonElement().jsonArray

        val users = mutableListOf<UnknownUser>()

        for (element in json) {
            val id = element.jsonObject.getValue("id").jsonPrimitive.content
            val details = element.jsonObject.toMutableMap()
            details.remove("id")
            users.add(UnknownUser(id.toInt(), JsonObject(details)))
        }
        return users
    }

    override fun serialize(encoder: Encoder, value: List<UnknownUser>) {
        error("Serialization is not implemented")
    }
}

fun main() {
    val objs = Json.decodeFromString(
        UnknownUserSerializer,
        """[{"id":15,"name":"Andrey","Country": "Russia"}, {"id": 22, "name": "Mikhail", "age": 51, "address":  {"st.":  "Lenina", "dom":  "55A"}}]"""
    )

    for (obj in objs) {
        print("name=id\t\t\t    type=${obj.id::class.simpleName}\t\t\t  value=${obj.id}\n")
        for (element in obj.details.keys) {
            print("name=${element}\t\t\t    type=${obj.details[element]!!::class.simpleName}\t\t\t  value=${obj.details[element]}\n")
        }
    }
}
