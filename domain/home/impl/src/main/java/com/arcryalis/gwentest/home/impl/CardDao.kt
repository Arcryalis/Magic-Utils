package com.arcryalis.gwentest.home.impl

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {

    @Query("SELECT EXISTS(SELECT * FROM cards WHERE id = :id)")
    fun getSetExists(id: String): Flow<Boolean>

    @Query("SELECT * FROM cards WHERE id = :id")
    fun getSet(id: String): Flow<List<CardInfo>>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<CardInfo>)

    @Transaction
    suspend fun updateSet(data: List<CardInfo>) {
        insertCards(data)
    }
}