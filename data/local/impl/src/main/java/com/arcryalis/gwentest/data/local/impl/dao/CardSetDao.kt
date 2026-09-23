package com.arcryalis.gwentest.data.local.impl.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.arcryalis.gwentest.data.local.api.entity.CardSetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardSetDao {

    @Query("SELECT * FROM cardSet")
    fun getSets(): Flow<List<CardSetEntity>>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardSets(cards: List<CardSetEntity>)
}