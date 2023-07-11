package api.codegen.writers

import java.io.File

class AppClassGenerator(pathToSave: String) : AbstractClassGenerator("Application", pathToSave) {

    override var _path: String = ""
    override val _srcFile: File = getSourceFile()
    override val _dstFile: File = getDestinationFile(pathToSave)

    override fun setReplacements(repl: LinkedHashMap<String, String>?) {
        replacements["<<PACKAGE>>"] = "ktorAutogen"
    }

    init {
        setReplacements()
    }
}