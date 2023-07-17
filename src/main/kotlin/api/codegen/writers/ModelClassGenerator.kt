package api.codegen.writers

import api.generic.GenericObject
import java.io.File

class ModelClassGenerator(pathToSave: String, objects: MutableList<GenericObject>) : AbstractClassGenerator("Model", pathToSave) {

    override var _path: String = "/models"
    override val _srcFile: File = getSourceFile()
    override val _dstFile: File = getDestinationFile(pathToSave)
    val _objects: MutableList<GenericObject> = objects

    override fun setReplacements(repl: LinkedHashMap<String, String>?) {
        replacements["<<PACKAGE>>"] = "ktorAutogen"
        replacements["<<DATACLASSES>>"] = getDataClasses()
        replacements["<<STORAGE>>"] = getStorage()
    }

    init {
        setReplacements()
    }

    private fun getDataClasses(): String {
        return _objects[0].toStringDataClass()
    }

    private fun getStorage(): String {
        var res: String = "val ${_objects[0].className.lowercase()}Storage = listOf(\n"
        _objects.forEach {
            res += "$it,\n"
        }
        res += ")\n"
        return res
    }
}