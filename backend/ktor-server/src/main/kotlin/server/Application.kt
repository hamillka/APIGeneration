package server

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ReceiveChannel
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.Duration

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    val command = "java -jar script/api_generation-1.0-SNAPSHOT.jar"

    routing {
        webSocket("/oneparam") {
            readAndRun(incoming, command)
        }

        webSocket("/twoparams") {
            readAndRun(incoming, command)
        }

        webSocket("/threeparams") {
            readAndRun(incoming, command)
        }
    }
}

suspend fun readAndRun(incoming: ReceiveChannel<Frame>, command: String) {
    var data = ""
    for (frame in incoming) {
        frame as? Frame.Text ?: continue
        data += frame.readText()
        break
    }
    run(command, data)
}

fun run(command: String, data: String) {
    try {
        val process = ProcessBuilder()
//            .command("bash", "-c", "$command ./tests/$data")
            .command("cmd", "/c", "$command ./tests/$data")
            .redirectErrorStream(true)
            .start()

        val reader = BufferedReader(InputStreamReader(process.inputStream))
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            println(line)
        }

        val exitCode = process.waitFor()
        println("Command exited with code: $exitCode")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}