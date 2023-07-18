package api.parser

import api.generic.GenericAttribute
import api.generic.GenericAttributeList
import api.generic.GenericObject
import api.models.UnknownUser
import api.serializers.NestedObjectSerializer
import api.serializers.UnknownUserSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlin.random.Random


class Parser(str: String) {
    private var _jsonStr: String = str
    private lateinit var _allObjs: List<UnknownUser>

    fun run(): MutableList<GenericObject> {
        val (index, className) = getTableName()

        val genObjs = parse(_jsonStr.slice(index + 1 until _jsonStr.length), className)

        checkValidity(genObjs)

        val completeObject = findCompleteObject(genObjs).first
        makeAllObjectsCompleted(genObjs, completeObject)

        return genObjs
    }

    private fun getTableName(): Pair<Int, String> {
        val index = _jsonStr.indexOf(':')
        val className = _jsonStr.slice(0 until index).trim('"')

        return Pair(index, className)
    }

    private fun checkType(value: Any): String {
        if (value.toString() == "null")
            return "nullable"

        if (value::class.simpleName == "JsonObject")
            return "GenericObject?"

        if (value::class.simpleName == "JsonArray")  {
            val temp = findTypeOfNullListElems(value as JsonElement)
            return "List<$temp>"
        }

        val type: String = when (value.toString().toIntOrNull()) {
            null -> when(value.toString().toDoubleOrNull()) {
                null -> when(value.toString().toBooleanStrictOrNull()) {
                    true, false -> "Boolean"
                    null -> "String"
                }
                else -> "Double"
            }
            else -> "Int"
        }
        return "$type?"
    }

    private fun findTypeOfNullListElems(value: JsonElement): String {
        for (elem in value.jsonArray)
            if (elem.toString() != "null")
                return checkType(elem.toString())

        return "String?"
    }

    private fun whichNullableType(fieldName: String): String {
        for (obj in _allObjs)
            for (entry in obj.details.entries)
                if ((entry.key == fieldName) && (entry.value.toString() != "null"))
                    return checkType(entry.value)

        return "String?"
    }

    private fun checkValidity(genObjs: MutableList<GenericObject>) {
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

    private fun findCompleteObject(genObjs: MutableList<GenericObject>): Pair<GenericObject, Int> {
        var (completeObject, maxSize) = Pair(genObjs[0], genObjs[0].getAttributes().size)
        for (i in 1 until genObjs.size)
            if (genObjs[i].getAttributes().size > maxSize) {
                completeObject = genObjs[i]
                maxSize = genObjs[i].getAttributes().size
            }

        return Pair(completeObject, maxSize)
    }

    private fun makeAllObjectsCompleted(genObjs: MutableList<GenericObject>, completeObject: GenericObject) {
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

    private fun createDefaultAttribute(sampleAttribute: GenericAttribute): GenericAttribute {
        if (sampleAttribute is GenericAttributeList)
            return createDefaultAttributeList(sampleAttribute)

        val resultAttribute = GenericAttribute(name = sampleAttribute.name, type = sampleAttribute.type, value = null)

        when (sampleAttribute.type) {
            "String?" -> resultAttribute.value = "empty"
            "Int?" -> resultAttribute.value = Random.nextInt(0, 8192)
            "Double?" -> resultAttribute.value = Random.nextDouble(0.0, 10000.0)
            "Boolean?" -> resultAttribute.value = Random.nextBoolean()
            "GenericObject?" -> {
                resultAttribute.value = GenericObject(sampleAttribute.name)

                for (attr in (sampleAttribute.value as GenericObject).getAttributes())
                    (resultAttribute.value as? GenericObject)?.addAttribute(attr)

            }
        }

        return resultAttribute
    }

    private fun createDefaultAttributeList(sampleAttribute: GenericAttribute): GenericAttributeList {
        val attrList = GenericAttributeList(name = sampleAttribute.name, type = sampleAttribute.type,
            value = mutableListOf())
        if (sampleAttribute.type == "nullable") {
            attrList.type = "String?"
        }
        return attrList
    }

    private fun parse(jsonStr: String, className: String): MutableList<GenericObject> {
        val genObjs = mutableListOf<GenericObject>()

        _allObjs = Json.decodeFromString(
            UnknownUserSerializer,
            string = jsonStr,
        )

        for (obj in _allObjs) {
            val genObj = GenericObject(className)
            val idAttrib = GenericAttribute(name = "id", type = "Int", value = obj.id)
            genObj.addAttribute(idAttrib)

            for (entry in obj.details.entries) {
                var type: String = checkType(entry.value)
                var listAttributes = GenericAttributeList(entry.key, type)

                if (type == "GenericObject?")
                        parseNested(genObj, entry.key, entry.value.toString())
                else if (type.startsWith("List"))
                    listAttributes = createAttrsList(entry.key, entry.value)
                else if (type == "nullable")
                    type = whichNullableType(entry.key)

                if (type.startsWith("List"))
                    genObj.addAttribute(listAttributes)
                else
                    genObj.addAttribute(GenericAttribute(name = entry.key, type = type, value = entry.value))
            }
            genObjs.add(genObj)
        }

        return genObjs
    }

    private fun createAttrsList(attrName: String, value: JsonElement): GenericAttributeList {
        val type = findTypeOfNullListElems(value)

        for (elem in value.jsonArray)
            if (elem.toString() != "null" &&  checkType(elem.toString()) !in type) {
                error("Field has list value with different types of elements")
            }

        val attrsList = GenericAttributeList(name = attrName, type = type)
        for (elem in value.jsonArray)
            attrsList.appendValue(elem.toString())

        return attrsList
    }

    private fun parseNested(genObj: GenericObject, fieldName: String, fieldValue: String) {
        // val nestedObj = NestedObject(element, obj.details[element] as JsonObject)
        val str = fieldName + fieldValue
        val nestedObj = Json.decodeFromString(
            NestedObjectSerializer, string = str) // string = nestedObj.details.toString()

        val nestedGenObj = GenericObject(nestedObj.className)
        for (elem in nestedObj.details.keys) {
            var type = checkType(nestedObj.details[elem].toString())
            if (type == "nullable") type = whichNullableType(elem)

            nestedGenObj.addAttribute(GenericAttribute(
                name = elem, type = type, value = nestedObj.details[elem]))
        }

        genObj.addAttribute(GenericAttribute(name = nestedObj.className, type = "GenericObject?", value = nestedGenObj))
    }
}

//// TODO: функция для вывода, удалить
//fun printGenObjects(genObjs: MutableList<GenericObject>) {
//    for (i in 0 until genObjs.size) {
//        (genObjs[i].getAttributes()).forEach {
//            println("NAME=${it.name} TYPE=${it.type} VALUE=${it.value} ")
//        }
//        println()
//    }
//}