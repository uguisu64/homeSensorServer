package com.example.dataclass

import com.influxdb.annotations.Column
import com.influxdb.annotations.Measurement
import java.time.Instant

@Measurement(name = "ThermoHygroSensor")
data class ThermoHygroInfluxData(
    @Column(tag = true) val tagKey: String,
    @Column val temp: Double,
    @Column val humidity: Double,
    @Column(timestamp = true) val time: Instant
)
