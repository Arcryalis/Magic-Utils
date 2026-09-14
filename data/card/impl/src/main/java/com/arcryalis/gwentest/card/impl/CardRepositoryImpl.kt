package com.arcryalis.gwentest.card.impl

import android.util.Log
import com.arcryalis.gwentest.api.RemoteResponse
import com.arcryalis.gwentest.api.scryfall.ScryfallDataSource
import com.arcryalis.gwentest.card.impl.mapper.toEntity
import com.arcryalis.gwentest.data.card.CardInfo
import com.arcryalis.gwentest.data.card.CardRepository
import com.arcryalis.gwentest.data.local.api.CardDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val db: CardDataStore,
    private val dataSource: ScryfallDataSource
) : CardRepository {

    companion object {
        const val LOG_TAG = "CardRepositoryImpl"
    }

    override suspend fun downloadSet(setId: String) {
        Log.i(LOG_TAG, "Downloading set $setId")
        val result = dataSource.getCards("s=${setId}")
        Log.i(LOG_TAG, "Download result $result")

        when (result) {
            is RemoteResponse.Success -> {
                //TODO handle pagination
                db.insertCards(result.data.data.map { it.toEntity(setId) })
            }
            is RemoteResponse.Error -> {
                // Do nothing
            }
        }
    }

    override fun isSetAvailable(setId: String): Flow<Boolean> = db.getSetExists(setId)

    override fun getSet(setId: String): Flow<List<CardInfo>> =
        db.getSet(setId).map { entities ->
            Log.i(LOG_TAG, "Found ${entities.size} entities with id $setId")
            entities.map {  //TODO mapper
                CardInfo(
                    name = it.name,
                    imageUrl = it.smallImageUrl,
                    isOngoing = it.isOngoing
                )
            }
        }
}