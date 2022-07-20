package com.example.plugins

import com.example.dataclass.SettingData
import com.example.dataclass.ThermoHygroData
import com.example.dataclass.ThermoHygroInfluxData
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import kotlinx.coroutines.runBlocking
import java.time.Instant

fun Application.configureRouting(settingData: SettingData) {
    val org    = settingData.org
    val bucket = settingData.bucket
    val url    = settingData.url
    val token  = settingData.token

    install(ContentNegotiation) {
        json()
    }

    routing {
        post("/house/sensor/thermohygro") {
            try {
                //Jsonデータを受け取りデータクラスに格納
                val thermoHygroData = call.receive<ThermoHygroData>()
                //現在時刻の情報を付加したデータクラスの作成
                val thermoHygroInfluxData = ThermoHygroInfluxData(
                    tagKey   = thermoHygroData.name,
                    temp     = thermoHygroData.temp,
                    humidity = thermoHygroData.humidity,
                    time     = Instant.now()
                )

                //InfluxDBに接続
                val client   = InfluxDBClientKotlinFactory.create(url,token.toCharArray(),org,bucket)
                val writeApi = client.getWriteKotlinApi()

                //データの書き込み
                runBlocking {
                    writeApi.writeMeasurement(thermoHygroInfluxData, WritePrecision.NS)
                }

                client.close()

                call.respondText("success")
            }
            catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest,"failed")
            }
        }
    }
}
