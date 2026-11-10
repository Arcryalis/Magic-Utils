package com.arcryalis.gwentest.data.local.impl.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arcryalis.gwentest.data.local.api.entity.CardSetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardSetDao {

    @Query("SELECT * FROM cardSet")
    fun getSets(): Flow<List<CardSetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(cards: List<CardSetEntity>)
}