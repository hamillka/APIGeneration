package api.codegen.writers

import java.io.File

class RouteClassGenerator(pathToSave: String) : AbstractClassGenerator("Route", pathToSave) {

    override var _path: String = "/routes"
    override val _srcFile: File = getSourceFile()
    override val _dstFile: File = getDestinationFile(pathToSave)

    override fun setReplacements(repl: LinkedHashMap<String, String>?) {
        replacements["<<PACKAGE>>"] = "ktorAutogen"
    }

    init {
        setReplacements()
    }
}