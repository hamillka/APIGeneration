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
    
}
