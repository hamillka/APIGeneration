package api.parser

import java.io.File

class Reader {
    fun readFile(fileName: String): String {
        var str = File(fileName).readText(Charsets.UTF_8)
        val (firstIndex, lastIndex) = Pair(str.indexOf('{'), str.lastIndexOf('}'))
        str = str.slice(firstIndex + 1 until lastIndex - 1).trimIndent()

        return str.ifEmpty { error("Empty file") }
    }
}