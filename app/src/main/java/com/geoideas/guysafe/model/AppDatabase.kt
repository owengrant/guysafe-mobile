package com.geoideas.guysafe.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.geoideas.guysafe.model.dao.ZoneDao
import com.geoideas.guysafe.model.entity.Zone

@Database(entities = arrayOf(Zone::class), version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun zonesDao(): ZoneDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(ctx: Context): AppDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null) return tempInstance
            synchronized(this) {
                    val instance = Room.databaseBuilder(ctx, AppDatabase::class.java,"guysafe")
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                    return instance
                }
            }
        }
}