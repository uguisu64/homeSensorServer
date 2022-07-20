package com.example

import com.example.dataclass.SettingData
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

fun main(args: Array<String>) {
    if(args.isEmpty()) {
        println("引数にセッティングファイルのパスを入れる(セッティングファイルはJsonファイルで以下の形式)")
        println("{")
        println("""  "org": 組織名""")
        println("""  "bucket": バケット名""")
        println("""  "url": InfluxDBのURL""")
        println("""  "token": InfluxDBのToken""")
        println("}")
        return
    }

    val settingData: SettingData
    try {
        val filePath   = args[0]
        val jsonString = File(filePath).readText()
        settingData = Json.decodeFromString(jsonString)
    }
    catch (e: Exception) {
        println(e)
        return
    }

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting(settingData)
    }.start(wait = true)
}
