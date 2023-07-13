package api.parser

import java.io.File

class Reader {
    fun readFile(fileName: String): String {
        val str = File(fileName).readText(Charsets.UTF_8)
        return str.ifEmpty { error("Empty file") }
    }
}