package api.parser

import api.generic.GenericAttribute
import api.generic.GenericObject
import api.serializers.NestedObjectSerializer
import api.serializers.UnknownUserSerializer
import kotlinx.serialization.json.Json


class Parser(str: String) {
    private var _jsonStr: String = str

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
        return when (value.toString().toDoubleOrNull()) {
            null -> "String"
            else -> "Int"
        }
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
        val resultAttribute = GenericAttribute(name = sampleAttribute.name, type = sampleAttribute.type, value = null)

        when (sampleAttribute.type) {
            "String" -> resultAttribute.value = "empty"
            "Int" -> resultAttribute.value = (0..8192).random()
            "GenericObject" -> {
                resultAttribute.value = GenericObject(sampleAttribute.name)

                for (attr in (sampleAttribute.value as GenericObject).getAttributes())
                    (resultAttribute.value as? GenericObject)?.addAttribute(attr)

                (resultAttribute.value as? GenericObject)?.addAttribute(
                    GenericAttribute(name = "copied", type = "Boolean", value = true))
//            resultAttribute.value = GenericObject("Empty")
//            ВМЕСТО ПЯТИ СТРОК ВЫШЕ (КОПИРОВАНИЕ) МОЖНО СОЗДАТЬ ПУСТОЙ GENERIC OBJECT ???
            }
        }

        return resultAttribute
    }

    private fun parse(jsonStr: String, className: String): MutableList<GenericObject> {
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

    private fun parseNested(genObj: GenericObject, fieldName: String, fieldValue: String) {
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
}


//fun main() {
//    /*
//    TESTS:
//        "users": [{"id":15,"name":"Andrey","Country": "Russia"}, {"id": 22, "name": "Mikhail", "age": 51, "address":  {"st.": "Lenina", "dom":  "55A"}}]
//        "users": [{"id":15,"name":"Andrey","Country": "Russia"}, {"id": 22, "name": "Mikhail", "age": 51, "address":  {"st.": "Lenina", "dom":  "55A"}}, {"id": 58, "vuz": "BMSTU"}]
//        "users": [{"id":15,"name":"Andrey","Country": "Russia"}, {"id": 22,"name": "Mikhail", "age": 51, "address": {"st.": "Lenina", "dom":  {"first": "55A", "second": "99G"}}}, {"id": 58, "vuz": "BMSTU"}] // FOR RECURSIVE
//
//        "users": [{"id":15,"name":"Andrey","ADDRESS": {"country": "Russia", "city": "moscow", "rayon": "SVAO", "dom": 91}}, {"id": 22, "name": "Mikhail", "age": 51, "address":  {"st.": "Lenina", "dom":  "55A"}}, {"id": 58, "vuz": "BMSTU"}]
//    */
//    val r = Reader()
//    val jsonStr = r.readFile("jsonData/file1.json")
//    val parser = Parser(jsonStr)
//
//    printGenObjects(parser.run())
//}

//// TODO: функция для вывода, удалить
//fun printGenObjects(genObjs: MutableList<GenericObject>) {
//    for (i in 0 until genObjs.size) {
//        (genObjs[i].getAttributes()).forEach {
//            println("NAME=${it.name} TYPE=${it.type} VALUE=${it.value} ")
//
//            if (it.type == "GenericObject") {
//                println("Nested Object with name ${it.name} \n{")
//                val nested = it.value as GenericObject
//                for (elem in nested.getAttributes())
//                    println("${elem.name} ${elem.type} ${elem.value}")
//                println("}\nEND of Nested Object")
//            }
//        }
//        println()
//    }
//}