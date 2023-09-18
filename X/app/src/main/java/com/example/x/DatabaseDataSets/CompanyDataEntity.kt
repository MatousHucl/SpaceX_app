package com.example.x.DatabaseDataSets

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.x.company_dataClasses.Headquarters

@Entity(tableName = "CompanyData")
data class CompanyDataEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "ceo") val ceo: String,
    @ColumnInfo(name = "coo") val coo: String,
    @ColumnInfo(name = "employees") val employees: Int,
    @ColumnInfo(name = "founded") val founded: Int,
    @Embedded(prefix = "headquarters") val headquarters: Headquarters,
    @ColumnInfo(name = "summary") val summary: String
)

