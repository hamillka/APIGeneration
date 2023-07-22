package api

import api.codegen.KTORCodeGen
import api.parser.Parser
import api.parser.Reader
import api.random.RandomObjectCreator
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

fun main(args: Array<String>) {
    val filename: String
    var repeats = 0
    var seed: String? = null
    var flag = false
    when (args.size)
    {
        0 -> { throw Exception("Too less arguments. JSON-Filename should be") }
        1 -> {
            filename = args[0]
        }
        2 -> {
            filename = args[0]
            repeats = if (args[1].last() == '@') {
                flag = true
                args[1].slice(0 until args[1].length - 1).toInt()
            }
            else
                args[1].toInt()
        }
        3 -> {
            filename = args[0]
            repeats = args[1].toInt()
            seed = if (args[2].last() == '@') {
                flag = true
                args[2].slice(0 until args[2].length - 1)
            }
            else
                args[2]
        }
        else -> { throw Exception("Too much arguments") }
    }
    if (!File(filename).exists()) {
        throw Exception("Unexisting file")
    }

    val jsonStr: String = Reader().readFile(filename)
    var objects = Parser(jsonStr).run()
    if (flag)
        objects = RandomObjectCreator(seed).createObjects(objects[0], repeats)
    else
        objects += RandomObjectCreator(seed).createObjects(objects[0], repeats)

    val codegen = KTORCodeGen("./autogen")
    val res = codegen.generateCode(objects)
    if (!res) { throw Exception("Something go wrong") }

    buildJar()
}

fun buildJar() {
//    val command = "cd autogen && gradle jar && cd build/libs && echo 'java -jar ktorAutogen-0.0.1.jar' > run.sh && chmod +x run.sh"
    val command = "cd autogen && gradle jar && cd build/libs && echo java -jar ktorAutogen-0.0.1.jar > run.bat"

    val process = ProcessBuilder()
//        .command("bash", "-c", command)
        .command("cmd", "/c", command)
        .redirectErrorStream(true)
        .start()

    val reader = BufferedReader(InputStreamReader(process.inputStream))
    var line: String?
    while (reader.readLine().also { line = it } != null) {
        println(line)
    }

}
