package api.parser

import api.generic.GenericAttribute
import api.generic.GenericObject
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

data class NestedObject(val className: String, val details: JsonObject)

// RECURSIVE//////
//object NestedObjectSerializer : KSerializer<List<NestedObject>> {
//    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("NestedObject") {
//        element<JsonElement>("className")
//        element<JsonElement>("details")
//    }
//
//    override fun deserialize(decoder: Decoder): List<NestedObject> {
//        val jsonInput = decoder as? JsonDecoder ?: error("Can be deserialized only by JSON")
//        val json = jsonInput.decodeJsonElement().jsonObject
//
//        val details = json.toMutableMap()
//
//        val nestedList = mutableListOf<NestedObject>()
//        nestedList.add(NestedObject("TEST", json))
//
//        for (element in details) {
////            println("KEY=${element.key} VALUE=${element.value} TYPE=${element.value::class.simpleName}")
//            if (element.value::class.simpleName == "JsonObject") {
//                val nesObj = NestedObject(element.key, element.value.jsonObject)
//                val nesObjs = Json.decodeFromString(NestedObjectSerializer, string = nesObj.details.toString())
//                for (obj in nesObjs)
//                    nestedList.add(obj)
//            }
//        }
//        return nestedList
//    }
//
//    override fun serialize(encoder: Encoder, value: List<NestedObject>) {
//        error("Serialization is not implemented")
//    }
//}


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


fun main() {/*
    TESTS:
        "users": [{"id":15,"name":"Andrey","Country": "Russia"}, {"id": 22, "name": "Mikhail", "age": 51, "address":  {"st.": "Lenina", "dom":  "55A"}}]
        "users": [{"id":15,"name":"Andrey","Country": "Russia"}, {"id": 22, "name": "Mikhail", "age": 51, "address":  {"st.": "Lenina", "dom":  "55A"}}, {"id": 58, "vuz": "BMSTU"}]
        "users": [{"id":15,"name":"Andrey","Country": "Russia"}, {"id": 22,"name": "Mikhail", "age": 51, "address": {"st.": "Lenina", "dom":  {"first": "55A", "second": "99G"}}}, {"id": 58, "vuz": "BMSTU"}] // FOR RECURSIVE

        "users": [{"id":15,"name":"Andrey","ADDRESS": {"country": "Russia", "city": "moscow", "rayon": "SVAO", "dom": 91}}, {"id": 22, "name": "Mikhail", "age": 51, "address":  {"st.": "Lenina", "dom":  "55A"}}, {"id": 58, "vuz": "BMSTU"}]
    */

    val jsonStr: String = readln()

    val (index, className) = getTableName(jsonStr)

    val objs = Json.decodeFromString(
        UnknownUserSerializer,
        string = jsonStr.slice(index + 1 until jsonStr.length),
    )

    val genObjs = mutableListOf<GenericObject>()

    for (obj in objs) {
        val genObj = GenericObject(className)
        val idAttrib = GenericAttribute(name = "id", type = "Int", value = obj.id)
        genObj.addAttribute(idAttrib)

//        for (element in obj.details.keys) {
        for (entry in obj.details.entries) {
            var type: String? = entry.value::class.simpleName

            when (type) {
                "JsonObject" -> {
//                    val nestedObj = NestedObject(element, obj.details[element] as JsonObject)
                    val str = entry.key + entry.value.toString()
                    val nestedObj = Json.decodeFromString(
                        NestedObjectSerializer, string = str
                    ) // string = nestedObj.details.toString()
                    val nestedGenObj = GenericObject(nestedObj.className)
                    for (elem in nestedObj.details.keys) {
                        nestedGenObj.addAttribute(
                            GenericAttribute(
                                name = elem,
                                type = checkIntOrString(nestedObj.details[elem].toString()),
                                value = nestedObj.details[elem]
                            )
                        )
                    }
                    genObj.addAttribute(
                        GenericAttribute(
                            name = nestedObj.className, type = "GenericObject", value = nestedGenObj
                        )
                    )
                }

                else -> type = checkIntOrString(entry.value.toString())
            }

            genObj.addAttribute(GenericAttribute(name = entry.key, type = type, value = entry.value))
        }
        genObjs.add(genObj)
    }


    println("GEN_OBJS LIST")
    for (i in 0 until genObjs.size) {
        (genObjs[i].getAttributes()).forEach {
            println("NAME=${it.name} TYPE=${it.type} VALUE=${it.value} ")
            if (it.type == "GenericObject") {
                println("Nested Object")
                val ral = it.value as GenericObject
                for (elem in ral.getAttributes()) {
                    println("${elem.name} ${elem.type} ${elem.value}")
                }
            }
        }
        println()
    }
}


fun getTableName(str: String): Pair<Int, String> {
    val index = str.indexOf(':')
    val className = str.slice(0 until index).trim('"')

    return Pair(index, className)
}

fun checkIntOrString(value: Any): String {
    return when (value.toString().toDoubleOrNull()) {
        null -> "String"
        else -> "Int"
    }
}
