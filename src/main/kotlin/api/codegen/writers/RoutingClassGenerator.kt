package api.codegen.writers

import java.io.File

class RoutingClassGenerator(pathToSave: String) : AbstractClassGenerator("Routing", pathToSave) {

    override var _path: String = "/plugins"
    override val _srcFile: File = getSourceFile()
    override val _dstFile: File = getDestinationFile(pathToSave)

    override fun setReplacements(repl: LinkedHashMap<String, String>?) {
        replacements["<<PACKAGE>>"] = "ktorAutogen"
    }

    init {
        setReplacements()
    }
}