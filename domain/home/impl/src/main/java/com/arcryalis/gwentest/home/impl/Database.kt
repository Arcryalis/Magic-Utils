package com.arcryalis.gwentest.home.impl

@Database(
    entities = [
        CardInfo::class
    ],
    version = 1
)
abstract class Database : RoomDatabase() {

    abstract fun getCardDao(): CardDao
}