package api.codegen.writers

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class BuildGradleGenerator(pathToSave: String): AbstractClassGenerator("BuildGradle", pathToSave) {
    override var _codePath: String = "/gradle"
    override var _projectPath: String = ""
    override val _srcData: String = getSource()
    override val _dstFile: File = getDestinationFile(pathToSave)

    override fun setReplacements(repl: LinkedHashMap<String, String>?) {  }

    override fun getDestinationFile(pathToSave: String): File {
        val dirPath: Path = Paths.get(pathToSave)
        if (!Files.isDirectory(dirPath)) {
            dirPath.toFile().mkdirs()
        }
        return File("$dirPath/build.gradle.kts")
    }
}