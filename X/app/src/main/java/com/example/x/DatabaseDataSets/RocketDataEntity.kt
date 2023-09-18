package com.example.x.DatabaseDataSets

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.x.rocket_dataClasses.Diameter


@Entity(tableName = "RocketData")
data class RocketDataEntity(
    @ColumnInfo(name = "name") val name: String,
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "company") val company: String,
    @ColumnInfo(name = "cost per launch") val costPerLaunch: Int,
    @Embedded(prefix = "Diameter") val diameter: Diameter,
    @ColumnInfo(name = "description") val description: String
)
