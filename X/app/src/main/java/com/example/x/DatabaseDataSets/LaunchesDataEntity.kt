package com.example.x.DatabaseDataSets

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LaunchesData")
data class LaunchesDataEntity(
    @ColumnInfo(name = "idLaunch") val idLaunch: String,
    @ColumnInfo(name = "name") val name: String,
    @PrimaryKey
    @ColumnInfo(name = "flight_number") val flight_number: Int,
    @ColumnInfo(name = "date_utc") val date_utc: String,
    @ColumnInfo(name = "success") val success: Boolean,
    @ColumnInfo(name = "details") val details: String
)

