package com.example.dataclass

@kotlinx.serialization.Serializable
data class ThermoHygroData(
    val name: String,
    val temp: Double,
    val humidity: Double
)
