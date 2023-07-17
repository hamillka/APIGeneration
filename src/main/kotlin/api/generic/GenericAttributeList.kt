package api.generic

class GenericAttributeList(name: String, type: String, value: MutableList<Any?>? = null): GenericAttribute(name, type, value) {
    private var _value: MutableList<Any?> = mutableListOf()
    override var value: Any?
        get() = _value
        set(v) { _value = v as MutableList<Any?> }

    init {
        if (value != null) _value = value
    }

    fun appendValue(v: Any?) {
        _value.add(v)
    }

    override fun toString(): String {
        when(type) {
            "GenericObject?" -> {
                if (_value.isNotEmpty()) return "val $name: List<${(_value[0] as GenericObject).className}?>"
                return "val: $name: List<Any?>?"
            }
            else -> { return "val $name: List<$type>" }
        }
    }
}