package api.generic

open class GenericAttribute(name: String, type: String, value: Any?) {
    private var _name: String
    var name
        get() = _name
        set(v) { _name = v }

    private var _type: String
    var type
        get() = _type
        set(v) { _type = v }

    private var _value: Any? = null
    open var value
        get() = _value
        set(v) { _value = v }

    init {
        _name = name
        _type = type
        _value = value
    }

    override fun toString(): String {
        when(_type) {
            "GenericObject?" -> { return "val $_name: ${(_value as GenericObject).className}?" }
            else -> { return "val $_name: $_type" }
        }
    }
}