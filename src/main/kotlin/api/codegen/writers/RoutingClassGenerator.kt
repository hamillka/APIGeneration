package api.codegen.writers

import api.generic.GenericObject
import java.io.File

class RoutingClassGenerator(pathToSave: String, objExmp: GenericObject) : AbstractClassGenerator("Routing", pathToSave) {

    override var _path: String = "/plugins"
    override val _srcFile: File = getSourceFile()
    override val _dstFile: File = getDestinationFile(pathToSave)
    val _objExmp: GenericObject = objExmp

    override fun setReplacements(repl: LinkedHashMap<String, String>?) {
        replacements["<<PACKAGE>>"] = "ktorAutogen"
        replacements["<<CLASSNAME>>"] = _objExmp.className.lowercase()
    }

    init {
        setReplacements()
    }
}