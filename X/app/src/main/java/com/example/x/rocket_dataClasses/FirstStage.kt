package com.example.x.rocket_dataClasses

data class FirstStage(
    val burn_time_sec: Int,
    val engines: Int,
    val fuel_amount_tons: Double,
    val reusable: Boolean,
    val thrust_sea_level: ThrustSeaLevel,
    val thrust_vacuum: ThrustVacuum
)