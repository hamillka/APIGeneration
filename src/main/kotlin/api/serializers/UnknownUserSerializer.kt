package api.serializers

import api.models.UnknownUser
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

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
