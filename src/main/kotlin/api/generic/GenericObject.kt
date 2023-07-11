package api.generic

class GenericObject {
    private var _attributes: MutableList<GenericAttribute<*>> = mutableListOf();

    constructor() { }
    constructor(attrs: MutableList<GenericAttribute<*>>)
    {
        _attributes = attrs
    }

    fun <T> addAttribute(attribute: GenericAttribute<T>) : Boolean {
        if (canAddAttribute(attribute))
        {
            _attributes.add(attribute)
            return true
        }
        return false
    }

    private fun <T> canAddAttribute(attribute: GenericAttribute<T>) : Boolean {
        for (attribute2 in _attributes)
        {
            if (attribute.name == attribute2.name)
            {
                return false
            }
        }
        return true
    }

    fun getAttribute(name: String) : GenericAttribute<*>? {
        for (attribute in _attributes)
        {
            if (attribute.name == name)
            {
                return attribute
            }
        }
        return null
    }

    fun getAttributes() : MutableList<GenericAttribute<*>> {
        return _attributes
    }
}
