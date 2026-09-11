package com.arcryalis.gwentest.home.impl

import kotlinx.coroutines.flow.Flow
import retrofit2.http.Query

@Dao
interface CardDao {


    @Query("SELECT EXISTS(SELECT * FROM cards WHERE id = :id)")
    fun getSetExists(setId: String): Flow<Boolean>

    @Query("SELECT * FROM cards WHERE id = :id")
    fun getSet(id: String): Flow<List<CardInfo>>

    @Transaction
    suspend fun updateSet(data: List<CardInfo>)

}