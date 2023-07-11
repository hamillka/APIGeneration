package api.codegen.writers

import java.io.File

class ModelClassGenerator(pathToSave: String) : AbstractClassGenerator("Model", pathToSave) {

    override var _path: String = "/models"
    override val _srcFile: File = getSourceFile()
    override val _dstFile: File = getDestinationFile(pathToSave)

    override fun setReplacements(repl: LinkedHashMap<String, String>?) {
        replacements["<<PACKAGE>>"] = "ktorAutogen"
    }

    init {
        setReplacements()
    }
}