package api.codegen

import api.generic.GenericObject

abstract class AbstractCodeGen(genPath: String = ".") {
    private var _path: String
    var path
        get() = _path
        set(newPath) { _path = newPath }
    init {
        _path = genPath
    }

    abstract fun generateCode(objects: MutableList<GenericObject>): Boolean
}