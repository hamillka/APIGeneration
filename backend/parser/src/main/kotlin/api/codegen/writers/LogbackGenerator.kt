package api.codegen.writers

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class LogbackGenerator(pathToSave: String): AbstractClassGenerator("Logback", pathToSave) {
    override var _codePath: String = "/gradle"
    override var _projectPath: String = "src/main/resources"
    override val _srcData: String = getSource()
    override val _dstFile: File = getDestinationFile(pathToSave)

    override fun setReplacements(repl: LinkedHashMap<String, String>?) {  }

    override fun getDestinationFile(pathToSave: String): File {
        val dirPath: Path = Paths.get(pathToSave, _projectPath)
        if (!Files.isDirectory(dirPath)) {
            dirPath.toFile().mkdirs()
        }
        return File("$dirPath/logback.xml")
    }
}