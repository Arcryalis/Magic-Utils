package com.arcryalis.gwentest.data.local.impl.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.arcryalis.gwentest.data.local.api.entity.CardInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {

    @Query("SELECT EXISTS(SELECT * FROM cards WHERE id = :id)")
    fun getSetExists(id: String): Flow<Boolean>

    @Query("SELECT * FROM cards WHERE id = :id")
    fun getSet(id: String): Flow<List<CardInfoEntity>>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<CardInfoEntity>)
}