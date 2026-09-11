package com.arcryalis.gwentest.home.impl

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        CardInfo::class
    ],
    version = 1
)
abstract class Database : RoomDatabase() {

    abstract fun getCardDao(): CardDao
}