package com.arcryalis.gwentest.home.impl

import androidx.room.Database
import androidx.room.RoomDatabase
import com.arcryalis.gwentest.data.local.api.entity.CardInfoEntity
import com.arcryalis.gwentest.data.local.impl.CardDao

@Database(
    entities = [
        CardInfoEntity::class
    ],
    version = 1
)
abstract class Database : RoomDatabase() {

    abstract fun getCardDao(): CardDao
}