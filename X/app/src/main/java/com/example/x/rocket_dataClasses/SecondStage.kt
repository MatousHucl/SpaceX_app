package com.example.x.rocket_dataClasses

data class SecondStage(
    val burn_time_sec: Double,
    val engines: Int,
    val fuel_amount_tons: Double,
    val payloads: Payloads,
    val reusable: Boolean,
    val thrust: Thrust
)