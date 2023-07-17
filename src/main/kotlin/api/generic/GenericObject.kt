package api.generic

class GenericObject(className: String) {
    private var _attributes: MutableList<GenericAttribute> = mutableListOf()
    private var _className: String = className
    val className
        get() = _className

    constructor(className: String, attrs: MutableList<GenericAttribute>) : this(className) {
        _attributes = attrs
    }

    fun addAttribute(attribute: GenericAttribute) : Boolean {
        if (canAddAttribute(attribute))
        {
            _attributes.add(attribute)
            return true
        }
        return false
    }

    private fun canAddAttribute(attribute: GenericAttribute) : Boolean {
        for (attribute2 in _attributes)
        {
            if (attribute.name == attribute2.name)
            {
                return false
            }
        }
        return true
    }

    fun getAttribute(name: String) : GenericAttribute? {
        for (attribute in _attributes)
        {
            if (attribute.name == name)
            {
                return attribute
            }
        }
        return null
    }

    fun getAttributes() : MutableList<GenericAttribute> {
        return _attributes
    }

    fun toStringDataClass(isSerializable: Boolean = true): String {
        var res = ""
        if (isSerializable) res += "@Serializable\n"
        res += "data class $_className("
        _attributes.forEach {
            res += "$it, "
        }
        if (_attributes.isNotEmpty()) res = res.slice(0..(res.length - 3))
        res += ")\n\n"
        _attributes.forEach {
            if (it.type == "GenericObject?") {
                res += (it.value as GenericObject).toStringDataClass()
            }
        }
        return res
    }

    override fun toString(): String {
        var res = "$_className("
        _attributes.forEach {
            res += "${it.name}="
            if (it is GenericAttributeList) {
                res += "listOf("
                (it.value as MutableList<Any?>).forEach { it2 ->
                    res += "${it2.toString()}, "
                }
                if ((it.value as MutableList<Any?>).isNotEmpty()) res = res.slice(0..(res.length - 3))
                res += ")"
            }
            else {
                when (it.type) {
                    "GenericObject?" -> { res += (it.value as GenericObject).toString() }
                    else -> { res += "${it.value}" }
                }
            }
            res += ", "
        }
        if (_attributes.isNotEmpty()) res = res.slice(0..(res.length - 3))
        res += ")"
        return res
    }
}