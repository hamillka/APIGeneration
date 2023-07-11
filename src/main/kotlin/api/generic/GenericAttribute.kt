package api.generic

class GenericAttribute<T>(name: String, value: T?) {
    private var _name: String
    var name
        get() = _name
        set(v) { _name = v }

    private var _value: T? = null
    var value
        get() = _value
        set(v) { _value = v }

    init {
        _name = name
        _value = value
    }
}