package api

import api.codegen.KTORCodeGen
import api.parser.Parser
import api.parser.Reader

fun main() {
    val r = Reader()
    val jsonStr = r.javaClass.getResource("/tests/file1.json")?.let { r.readFile(it.file) }
    val codegen = KTORCodeGen("./test")
    if (jsonStr != null) codegen.generateCode(Parser(jsonStr.toString()).run())
    else throw NullPointerException("Unexisting file")
}