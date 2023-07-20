package api.codegen.writers

import api.generic.GenericObject
import java.io.File

class RouteClassGenerator(pathToSave: String, objExmp: GenericObject) : AbstractClassGenerator("Route", pathToSave) {

    override var _codePath: String = "/routes"
    override var _projectPath: String = "src/main/kotlin"
    override val _srcData: String = getSource()
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