package api.random

import api.generic.GenericAttribute
import api.generic.GenericAttributeList
import api.generic.GenericObject
import io.github.serpro69.kfaker.faker

class RandomObjectCreator(seed: String? = null) {
    val _random: RandomGenerator
    val _faker = faker {  }
    init {
        _random = if (seed != null) RandomGenerator(seed) else RandomGenerator()
    }

    fun createFakedValue(attr: GenericAttribute): Any? {
        val value = when (attr.name) {
            "name" -> "\"${_faker.name.firstName()}\""
            "surname" -> "\"${_faker.name.lastName()}\""
            "country" -> "\"${_faker.address.country()}\""
            "city" -> "\"${_faker.address.city()}\""
            "email" -> "\"${_faker.internet.safeEmail()}\""
            "phone" -> "\"${_faker.phoneNumber.phoneNumber()}\""
            else -> createValue(attr.type, attr.value)
        }
        return value
    }

    fun createValue(type: String, baseValue: Any?): Any? {
        var value: Any? = null
        when (type) {
            "GenericObject?" -> {
                if (baseValue is GenericObject) {
                    value = createObject(baseValue as GenericObject)
                } else { value = null }
            }
            "String?" -> { value = "\"${_random.nextString(3, 10)}\"" }
            "Int", "Int?" -> { value = _random.nextInt() }
            "Double?" -> { value = _random.nextDouble(-1000.0, 1000.0) }
            "Boolean?" -> { value = _random.nextBoolean() }
            else -> { value = null }
        }
        return value
    }

    fun createAttribute(baseAttribute: GenericAttribute): GenericAttribute {
        return GenericAttribute(baseAttribute.name, baseAttribute.type, createFakedValue(baseAttribute))
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