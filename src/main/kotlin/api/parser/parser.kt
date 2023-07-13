package api.parser

import api.generic.GenericAttribute
import api.generic.GenericObject
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import java.io.File

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


fun main() {/*
    TESTS:
        "users": [{"id":15,"name":"Andrey","Country": "Russia"}, {"id": 22, "name": "Mikhail", "age": 51, "address":  {"st.": "Lenina", "dom":  "55A"}}]
        "users": [{"id":15,"name":"Andrey","Country": "Russia"}, {"id": 22, "name": "Mikhail", "age": 51, "address":  {"st.": "Lenina", "dom":  "55A"}}, {"id": 58, "vuz": "BMSTU"}]
        "users": [{"id":15,"name":"Andrey","Country": "Russia"}, {"id": 22,"name": "Mikhail", "age": 51, "address": {"st.": "Lenina", "dom":  {"first": "55A", "second": "99G"}}}, {"id": 58, "vuz": "BMSTU"}] // FOR RECURSIVE

        "users": [{"id":15,"name":"Andrey","ADDRESS": {"country": "Russia", "city": "moscow", "rayon": "SVAO", "dom": 91}}, {"id": 22, "name": "Mikhail", "age": 51, "address":  {"st.": "Lenina", "dom":  "55A"}}, {"id": 58, "vuz": "BMSTU"}]
    */

    val jsonStr = readFile("jsonData/file1.json")
    val (index, className) = getTableName(jsonStr)

    println("TABLE NAME $className")

    val genObjs = parse(jsonStr.slice(index + 1 until jsonStr.length), className)

    checkValidity(genObjs)

    val completeObject = findCompleteObject(genObjs).first
    makeAllObjectsCompleted(genObjs, completeObject)

    printGenObjects(genObjs)
}

fun readFile(fileName: String): String {
    val str = File(fileName).readText(Charsets.UTF_8)
    return str.ifEmpty { error("Empty file") }
}

fun getTableName(str: String): Pair<Int, String> {
    val index = str.indexOf(':')
    val className = str.slice(0 until index).trim('"')

    return Pair(index, className)
}

fun printGenObjects(genObjs: MutableList<GenericObject>) {
    for (i in 0 until genObjs.size) {
        (genObjs[i].getAttributes()).forEach {
            println("NAME=${it.name} TYPE=${it.type} VALUE=${it.value} ")

            if (it.type == "GenericObject") {
                println("Nested Object with name ${it.name} \n{")
                val nested = it.value as GenericObject
                for (elem in nested.getAttributes())
                    println("${elem.name} ${elem.type} ${elem.value}")
                println("}\nEND of Nested Object")
            }
        }
        println()
    }
}

fun checkType(value: Any): String {
    return when (value.toString().toDoubleOrNull()) {
        null -> "String"
        else -> "Int"
    }
}

fun checkValidity(genObjs: MutableList<GenericObject>) {
    val (completeObject, maxSize) = findCompleteObject(genObjs)

    if (genObjs.count { it.getAttributes().size == maxSize } > 1)
        error("JSON string is not valid (two objects are complete)")

    val fullAttributesNames = mutableSetOf<String>()
    completeObject.getAttributes().forEach { fullAttributesNames.add(it.name) }

    for (obj in genObjs)
        for (attr in obj.getAttributes())
            if (attr.name !in fullAttributesNames)
                error("JSON string is not valid (object with id=${obj.getAttribute("id")?.value} " +
                            "has field \"${attr.name}\" that is not represented in complete object)")
}

fun findCompleteObject(genObjs: MutableList<GenericObject>): Pair<GenericObject, Int> {
    var (completeObject, maxSize) = Pair(genObjs[0], genObjs[0].getAttributes().size)
    for (i in 1 until genObjs.size)
        if (genObjs[i].getAttributes().size > maxSize) {
            completeObject = genObjs[i]
            maxSize = genObjs[i].getAttributes().size
        }

    return Pair(completeObject, maxSize)
}

fun makeAllObjectsCompleted(genObjs: MutableList<GenericObject>, completeObject: GenericObject) {
    for (standardAttribute in completeObject.getAttributes())
        for (obj in genObjs) {
            val objAttributesNames = mutableSetOf<String>()
            obj.getAttributes().forEach { objAttributesNames.add(it.name) }

            if (standardAttribute.name !in objAttributesNames) {
                val defaultAttribute = createDefaultAttribute(standardAttribute)
                obj.addAttribute(defaultAttribute)
            }
        }
}

fun createDefaultAttribute(sampleAttribute: GenericAttribute): GenericAttribute {
    val resultAttribute = GenericAttribute(name = sampleAttribute.name, type = sampleAttribute.type, value = null)

    when (sampleAttribute.type) {
        "String" -> resultAttribute.value = "empty"
        "Int" -> resultAttribute.value = (0..8192).random()
        "GenericObject" -> {
            resultAttribute.value = GenericObject(sampleAttribute.name)

            for (attr in (sampleAttribute.value as GenericObject).getAttributes())
                (resultAttribute.value as GenericObject).addAttribute(attr)

            (resultAttribute.value as? GenericObject)?.addAttribute(
                GenericAttribute(name = "copied", type = "Boolean", value = true))
//            resultAttribute.value = GenericObject("Empty")
//            ВМЕСТО ПЯТИ СТРОК ВЫШЕ (КОПИРОВАНИЕ) МОЖНО СОЗДАТЬ ПУСТОЙ GENERIC OBJECT ???
        }
    }

    return resultAttribute
}

fun parse(jsonStr: String, className: String): MutableList<GenericObject> {
    val genObjs = mutableListOf<GenericObject>()

    val objs = Json.decodeFromString(
        UnknownUserSerializer,
        string = jsonStr,
    )

    for (obj in objs) {
        val genObj = GenericObject(className)
        val idAttrib = GenericAttribute(name = "id", type = "Int", value = obj.id)
        genObj.addAttribute(idAttrib)

        for (entry in obj.details.entries) {
            var type: String? = entry.value::class.simpleName

            when (type) {
                "JsonObject" -> parseNested(genObj, entry.key, entry.value.toString())
                else -> type = checkType(entry.value)
            }

            genObj.addAttribute(GenericAttribute(name = entry.key, type = type, value = entry.value))
        }
        genObjs.add(genObj)
    }

    return genObjs
}

fun parseNested(genObj: GenericObject, fieldName: String, fieldValue: String) {
    // val nestedObj = NestedObject(element, obj.details[element] as JsonObject)
    val str = fieldName + fieldValue
    val nestedObj = Json.decodeFromString(
        NestedObjectSerializer, string = str) // string = nestedObj.details.toString()

    val nestedGenObj = GenericObject(nestedObj.className)
    for (elem in nestedObj.details.keys)
        nestedGenObj.addAttribute(GenericAttribute(
            name = elem, type = checkType(nestedObj.details[elem].toString()), value = nestedObj.details[elem]))

    genObj.addAttribute(GenericAttribute(name = nestedObj.className, type = "GenericObject", value = nestedGenObj))
}