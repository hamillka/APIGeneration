package api.codegen.writers

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.createDirectory

abstract class AbstractClassGenerator(className: String, pathToSave: String) {
    private val _className: String = className
    val className
        get() = _className

    abstract val _srcFile: File
    val src
        get() = _srcFile

    abstract val _dstFile: File
    val dst
        get() = _dstFile

    abstract var _path: String

    private val _replacements: LinkedHashMap<String, String> = LinkedHashMap()
    val replacements
        get() = _replacements

    open fun generate(): Boolean {
        writeReplacedContent()
        return true
    }

    abstract fun setReplacements(repl: LinkedHashMap<String, String>? = null)

    private fun writeReplacedContent() {
        dst.writeText("")
        src.forEachLine {
            var line = it
            for (key in replacements.keys) {
                line = line.replace(key, replacements.getValue(key))
            }
            dst.appendText(line + "\n")
        }
    }

    protected fun getSourceFile(): File {
        return File(this.javaClass.getResource("/codegen$_path/Default$className.txt").file)
    }

    protected fun getDestinationFile(pathToSave: String): File {
        val dirPath: Path = Paths.get(pathToSave, _path)
        if (!Files.isDirectory(dirPath)) {
            dirPath.createDirectory()
        }
        return File("$dirPath/$_className.kt")
    }
}