package com.arcryalis.gwentest.card.impl

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
    private val dataSource: ScryfallDataSource,
) : CardRepository {

    override suspend fun downloadSet(setId: String) {
        when (val result = dataSource.getCards("s=${setId}")) {
            is RemoteResponse.Success -> {
                db.insertCards(result.data.map { it.toEntity(setId) })
            }
            is RemoteResponse.Error -> {
                // Do nothing
            }
        }
    }

    override fun isSetAvailable(setId: String): Flow<Boolean> = db.getSetExists(setId)

    override fun getSet(setId: String): Flow<List<CardInfo>> =
        db.getSet(setId).map { entities ->
            entities.map { CardInfo(it.id, it.setId) }
        }
}