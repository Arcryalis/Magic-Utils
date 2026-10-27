package com.arcryalis.gwentest.data.local.impl.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arcryalis.gwentest.data.local.api.entity.CardInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardInfoDao {

    @Query("SELECT EXISTS(SELECT * FROM cardInfo)")
    fun doCardsExistLocally(): Flow<Boolean>

    @Query("SELECT * FROM cardInfo WHERE setId = :setId")
    fun getCardSet(setId: String): Flow<List<CardInfoEntity>>

    @Query("SELECT * FROM cardInfo")
    fun getAllCards(): Flow<List<CardInfoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<CardInfoEntity>)
}