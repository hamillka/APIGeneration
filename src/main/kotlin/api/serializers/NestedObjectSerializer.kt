package api.serializers

import api.models.NestedObject
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject


/*
// RECURSIVE//////
object NestedObjectSerializer : KSerializer<List<NestedObject>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("NestedObject") {
        element<JsonElement>("className")
        element<JsonElement>("details")
    }

    override fun deserialize(decoder: Decoder): List<NestedObject> {
        val jsonInput = decoder as? JsonDecoder ?: error("Can be deserialized only by JSON")
        val json = jsonInput.decodeJsonElement().jsonObject

        val details = json.toMutableMap()

        val nestedList = mutableListOf<NestedObject>()
        nestedList.add(NestedObject("TEST", json))

        for (element in details) {
//            println("KEY=${element.key} VALUE=${element.value} TYPE=${element.value::class.simpleName}")
            if (element.value::class.simpleName == "JsonObject") {
                val nesObj = NestedObject(element.key, element.value.jsonObject)
                val nesObjs = Json.decodeFromString(NestedObjectSerializer, string = nesObj.details.toString())
                for (obj in nesObjs)
                    nestedList.add(obj)
            }
        }
        return nestedList
    }

    override fun serialize(encoder: Encoder, value: List<NestedObject>) {
        error("Serialization is not implemented")
    }
}
*/

// NOT RECURSIVE
object NestedObjectSerializer : KSerializer<NestedObject> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("NestedObject") {
        element<JsonElement>("className")
        element<JsonElement>("details")
    }

    override fun deserialize(decoder: Decoder): NestedObject {
        val jsonInput = decoder as? JsonDecoder ?: error("Can be deserialized only by JSON")
        val className = jsonInput.decodeJsonElement().toString()

        val json = jsonInput.decodeJsonElement().jsonObject

        val details = json.toMutableMap()

        return NestedObject(className, JsonObject(details))
    }

    override fun serialize(encoder: Encoder, value: NestedObject) {
        error("Serialization is not implemented")
    }
}
