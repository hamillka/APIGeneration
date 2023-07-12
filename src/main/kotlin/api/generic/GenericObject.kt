package api.generic

class GenericObject(className: String) {
    private var _attributes: MutableList<GenericAttribute> = mutableListOf();
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

    fun ToStringDataClass(isSerializable: Boolean = true): String {
        var res = ""
        if (isSerializable) res += "@Serializable\n"
        res += "data class $_className("
        _attributes.forEach {
            res += "${it.ToString()}, "
        }
        res += ")\n\n"
        _attributes.forEach {
            if (it.type == "GenericObject") {
                res += (it.value as GenericObject).ToStringDataClass()
            }
        }
        return res
    }

    fun ToString(): String {
        var res = "$_className("
        _attributes.forEach {
            when (it.type) {
                "GenericObject" -> { res += (it.value as GenericObject).ToString() }
                "String" -> { res += "\"${it.value}\"" }
                "String?" -> {
                    when (it.value) {
                        null -> { res += "null" }
                        else -> { res += "\"${it.value}\"" }
                    }
                }
                else -> { res += "${it.value}" }
            }
            res += ", "
        }
        res += ")"
        return res
    }
}