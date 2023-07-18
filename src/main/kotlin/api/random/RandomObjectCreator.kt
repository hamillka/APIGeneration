package api.random

import api.generic.GenericAttribute
import api.generic.GenericAttributeList
import api.generic.GenericObject

class RandomObjectCreator(seed: Int? = null) {
    val _random: RandomGenerator
    init {
        _random = if (seed != null) RandomGenerator(seed) else RandomGenerator()
    }

    fun createValue(type: String, baseValue: Any?): Any? {
        var value: Any? = null
        when (type) {
            "GenericObject?" -> {
                if (baseValue is GenericObject) {
                    value = createObject(baseValue as GenericObject)
                } else { value = null }
            }
            "String?" -> { value = _random.nextString(3, 10) }
            "Int", "Int?" -> { value = _random.nextInt() }
            "Double?" -> { value = _random.nextDouble(-1000.0, 1000.0) }
            "Boolean?" -> { value = _random.nextBoolean() }
            else -> { value = null }
        }
        return value
    }

    fun createAttribute(baseAttribute: GenericAttribute): GenericAttribute {
        return GenericAttribute(baseAttribute.name, baseAttribute.type, createValue(baseAttribute.type, baseAttribute.value))
    }

    fun createAttributeList(baseAttribute: GenericAttributeList): GenericAttributeList {
        val baseValues: MutableList<Any?> = baseAttribute.value as MutableList<Any?>
        val len = baseValues.size
        val lst = GenericAttributeList(baseAttribute.name, baseAttribute.type)
        for (i in 1..len) {
            lst.appendValue(createValue(baseAttribute.type, baseValues[0]))
        }
        return lst
    }

    fun createObject(baseObj: GenericObject): GenericObject {
        val res = GenericObject(baseObj.className)
        baseObj.getAttributes().forEach {
            val name = it.name
            val type = it.type
            var value: Any? = null
            if (it is GenericAttributeList) {
                res.addAttribute(createAttributeList(it))
            } else {
                res.addAttribute(createAttribute(it))
            }
        }
        return res
    }

    fun createObjects(baseObj: GenericObject, num: Int): MutableList<GenericObject> {
        val lst: MutableList<GenericObject> = mutableListOf()
        for (i in 1..num) {
            lst.add(createObject(baseObj))
        }
        return lst
    }
}