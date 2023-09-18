package com.example.x

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.x.DatabaseDataSets.CompanyDataEntity
import com.example.x.DatabaseDataSets.LaunchesDataEntity
import com.example.x.DatabaseDataSets.RocketDataEntity


@Dao
interface SetDao {

    //launches interface
    @Update
    fun updateLaunch(launch: LaunchesDataEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertLaunch(set: LaunchesDataEntity)

    @Query("SELECT * FROM LaunchesData WHERE idLaunch = :id")
    fun getLaunchById(id: String): LaunchesDataEntity?

    @Transaction
    fun insertOrUpdateLaunch(launch: LaunchesDataEntity) {
        val existingLaunch = getLaunchById(launch.idLaunch)
        if (existingLaunch == null) {
            insertLaunch(launch)
        } else {
            updateLaunch(launch)
        }
    }

    @Query("SELECT * FROM LaunchesData")
    fun getAllLaunches(): List<LaunchesDataEntity>

    @Query("DELETE FROM LaunchesData")
    fun deleteAllLaunches()

    @Query("SELECT * FROM LaunchesData WHERE success = 1")
    fun getSuccessfulLaunches(): List<LaunchesDataEntity>

    @Query("SELECT * FROM LaunchesData WHERE success = 0")
    fun getFailedLaunches(): List<LaunchesDataEntity>

    //company interface
    @Update
    fun updateCompany(launch: CompanyDataEntity)

    @Query("DELETE FROM CompanyData")
    fun deleteCompanyData()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCompanyData(set: CompanyDataEntity)

    @Query("SELECT * FROM CompanyData")
    fun getAllCompany(): List<CompanyDataEntity>

    @Query("SELECT * FROM CompanyData WHERE id = :id")
    fun getCompanyById(id: String): CompanyDataEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCompany(set: CompanyDataEntity)

    @Transaction
    fun insertOrUpdateCompany(company: CompanyDataEntity) {
        val existingCompany = getCompanyById(company.id)
        if (existingCompany == null) {
            insertCompany(company)
        } else {
            updateCompany(company)
        }
    }

    //rocket interface
    @Update
    fun updateRocket(launch: RocketDataEntity)

    @Query("DELETE FROM RocketData")
    fun deleteRocketData()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertRocketData(set: RocketDataEntity)

    @Query("SELECT * FROM RocketData")
    fun getAllRocket(): List<RocketDataEntity>

    @Query("SELECT * FROM RocketData WHERE name = 'Falcon 1'")
    fun getFalcon1(): List<RocketDataEntity>

    @Query("SELECT * FROM RocketData WHERE name = 'Falcon 9'")
    fun getFalcon9(): List<RocketDataEntity>

    @Query("SELECT * FROM RocketData WHERE name = 'Falcon Heavy'")
    fun getFalconHeavy(): List<RocketDataEntity>

    @Query("SELECT * FROM RocketData WHERE name = 'Starship'")
    fun getStarship(): List<RocketDataEntity>

    @Query("SELECT * FROM RocketData WHERE id = :id")
    fun getRocketById(id: String): RocketDataEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertRocket(set: RocketDataEntity)

    @Transaction
    fun insertOrUpdateRocket(rocket: RocketDataEntity) {
        val existingRocket = getRocketById(rocket.id)
        if (existingRocket == null) {
            insertRocket(rocket)
        } else {
            updateRocket(rocket)
        }
    }


}