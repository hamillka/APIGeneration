package api.codegen.writers

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

abstract class AbstractClassGenerator(className: String, pathToSave: String) {
    private val _className: String = className
    val className
        get() = _className

    abstract val _srcData: String
    val src
        get() = _srcData

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
        var content = src
        for (key in replacements.keys) {
            content = content.replace(key, replacements.getValue(key))
        }
        dst.appendText(content)
    }

    protected fun getSource(): String {
        return this.javaClass.getResourceAsStream("/codegen$_path/Default$className.txt").reader().readText()
    }

    protected fun getDestinationFile(pathToSave: String): File {
        val dirPath: Path = Paths.get(pathToSave, _path)
        if (!Files.isDirectory(dirPath)) {
            dirPath.toFile().mkdirs()
        }
        return File("$dirPath/$_className.kt")
    }
}