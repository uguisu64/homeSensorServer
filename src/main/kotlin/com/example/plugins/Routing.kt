package com.example.plugins

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

fun Application.configureRouting() {
    val org    = "soma_home"
    val bucket = "room_sensors"
    val url    = "http://192.168.50.222:8086"
    val token  = "FLpXWJlSFSO3zWMPKqXiDs9XapoSDJe50KjBmAlh5QAJNX7OXudqeILAiCRaYAiHAtcRdxS782YvNqI1h8Rf6g=="

    install(ContentNegotiation) {
        json()
    }

    routing {
        post("/house/sensor/thermohygro") {
            try {
                val thermoHygroData = call.receive<ThermoHygroData>()
                val thermoHygroInfluxData = ThermoHygroInfluxData(
                    tagKey = thermoHygroData.name,
                    temp = thermoHygroData.temp,
                    humidity = thermoHygroData.humidity,
                    time = Instant.now()
                )

                val client = InfluxDBClientKotlinFactory.create(url,token.toCharArray(),org,bucket)
                val writeApi = client.getWriteKotlinApi()

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
