package api

import api.codegen.KTORCodeGen
import api.parser.Parser
import api.parser.Reader
import api.random.RandomObjectCreator
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    var filename: String
    var repeats: Int = 0
    var seed: String? = null
    when (args.size)
    {
        0 -> { throw Exception("Too less arguments. JSON-Filename should be") }
        1 -> {
            filename = args[0]
        }
        2 -> {
            filename = args[0]
            repeats = args[1].toInt()
        }
        3 -> {
            filename = args[0]
            repeats = args[1].toInt()
            seed = args[2]
        }
        else -> { throw Exception("Too much arguments") }
    }
    if (!File(filename).exists()) {
        throw Exception("Unexisting file")
    }

    val jsonStr: String = Reader().readFile(filename)
    val objects = Parser(jsonStr).run()
    objects += RandomObjectCreator(seed).createObjects(objects[0], repeats)

    val codegen = KTORCodeGen("./autogen")
    val res = codegen.generateCode(objects)
    if (!res) { throw Exception("Something go wrong") }

    buildJar()
}

fun buildJar() {
    Runtime.getRuntime().exec("cmd /c cd autogen & gradle jar & cd build/libs & echo java -jar ktorAutogen-0.0.1.jar > run.bat").waitFor()
}
