package api.codegen.writers

import java.io.File

class AppClassGenerator(pathToSave: String) : AbstractClassGenerator("Application", pathToSave) {

    override var _codePath: String = ""
    override var _projectPath: String = "src/main/kotlin"
    override val _srcData: String = getSource()
    override val _dstFile: File = getDestinationFile(pathToSave)

    override fun setReplacements(repl: LinkedHashMap<String, String>?) {
        replacements["<<PACKAGE>>"] = "ktorAutogen"
    }

    init {
        setReplacements()
    }
}