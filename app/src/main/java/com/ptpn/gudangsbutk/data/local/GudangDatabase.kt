package com.ptpn.gudangsbutk.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ItemEntity::class], version = 1)
abstract class GudangDatabase: RoomDatabase() {
    abstract fun surveyDao(): GudangDao

    companion object {
        @Volatile
        private var INSTANCE: GudangDatabase? = null

        fun getInstance(context: Context): GudangDatabase =
                INSTANCE ?: synchronized(this) {
                    Room.databaseBuilder(
                            context.applicationContext,
                            GudangDatabase::class.java,
                            "gudang_database.db"
                    ).build().apply {
                        INSTANCE = this
                    }
                }
    }
}