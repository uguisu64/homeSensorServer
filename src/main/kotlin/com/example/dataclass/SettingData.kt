package com.example.dataclass

@kotlinx.serialization.Serializable
data class SettingData(
    val org: String,
    val bucket: String,
    val url: String,
    val token: String
)
