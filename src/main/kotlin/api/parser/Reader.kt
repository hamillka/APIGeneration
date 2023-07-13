package api.parser

import java.io.File

class Reader {
    fun readFile(fileName: String): String {
        var str = File(fileName).readText(Charsets.UTF_8)
        str = str.slice(1 until str.length - 1).trimIndent()

        return str.ifEmpty { error("Empty file") }
    }
}