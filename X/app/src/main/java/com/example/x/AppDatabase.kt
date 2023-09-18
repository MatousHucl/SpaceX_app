package com.example.x

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.x.DatabaseDataSets.CompanyDataEntity
import com.example.x.DatabaseDataSets.LaunchesDataEntity
import com.example.x.DatabaseDataSets.RocketDataEntity

@Database(entities = [LaunchesDataEntity::class, CompanyDataEntity::class, RocketDataEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun setDao(): SetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {

            val  tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "new_app_database2"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}