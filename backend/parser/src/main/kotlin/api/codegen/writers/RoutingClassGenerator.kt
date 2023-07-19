package api.codegen.writers

import api.generic.GenericObject
import java.io.File

class RoutingClassGenerator(pathToSave: String, objExmp: GenericObject) : AbstractClassGenerator("Routing", pathToSave) {

    override var _codePath: String = "/plugins"
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