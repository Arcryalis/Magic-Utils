package com.arcryalis.gwentest.data.local.impl

import androidx.room.Database
import androidx.room.RoomDatabase
import com.arcryalis.gwentest.data.local.api.entity.CardInfoEntity
import com.arcryalis.gwentest.data.local.api.entity.CardSetEntity
import com.arcryalis.gwentest.data.local.impl.dao.CardInfoDao
import com.arcryalis.gwentest.data.local.impl.dao.CardSetDao

@Database(
    entities = [
        CardInfoEntity::class,
        CardSetEntity::class
    ],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getCardInfoDao(): CardInfoDao

    abstract fun getCardSetDao(): CardSetDao
}