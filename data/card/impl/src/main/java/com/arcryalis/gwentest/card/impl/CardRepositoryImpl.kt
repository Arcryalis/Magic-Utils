package com.arcryalis.gwentest.card.impl

import android.content.Context
import com.arcryalis.gwentest.api.RemoteResponse
import com.arcryalis.gwentest.api.scryfall.ScryfallDataSource
import com.arcryalis.gwentest.api.scryfall.dto.ScryfallSearchDto
import com.arcryalis.gwentest.card.impl.mapper.toDistinctSetEntity
import com.arcryalis.gwentest.card.impl.mapper.toInfoEntity
import com.arcryalis.gwentest.card.impl.mapper.toModel
import com.arcryalis.gwentest.data.card.CardRepository
import com.arcryalis.gwentest.network.R
import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.data.card.model.CardSet
import com.arcryalis.gwentest.data.local.api.CardInfoDataStore
import com.arcryalis.gwentest.data.local.api.CardSetDataStore
import com.arcryalis.gwentest.network.CoilImageCacher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val cardInfoStore: CardInfoDataStore,
    private val cardSetStore: CardSetDataStore,
    private val dataSource: ScryfallDataSource,
    private val imageCacher: CoilImageCacher,
    @ApplicationContext private val context: Context
) : CardRepository {

    override suspend fun downloadSet(setId: String): Boolean {
        val query = "s=${setId}"
        val response = dataSource.getCards(query)
        return handleDownloadSetPage(response)
    }

    private suspend fun handleDownloadSetPage(response: RemoteResponse<ScryfallSearchDto>): Boolean = when (response) {
        is RemoteResponse.Success -> {
            val data = response.data.data

            cardSetStore.insertSets(data.toDistinctSetEntity())
            cardInfoStore.insertCards(data.toInfoEntity())

            imageCacher.queueImageCacheRequests(
                data.map {
                    it.imageUris.small
                }.distinct()
            )

            val hasMore = response.data.hasMore
            val nextPage = response.data.nextPage
            if (hasMore == true && nextPage != null) {
                //Download next page recursively
                val baseUrl = context.getString(R.string.scryfall_base_url)
                val query = nextPage.replace(baseUrl, "")

                val response = dataSource.getCards(query)
                handleDownloadSetPage(response)
            } else {
                true
            }


        }
        is RemoteResponse.Error -> {
            // Do nothing
            false
        }
    }

    override fun getAvailableSets(): Flow<List<CardSet>> = cardSetStore.getSets()
        .map { entities ->
            entities.toModel()
        }

    override fun getSet(setId: String): Flow<List<CardInfo>> = cardInfoStore.getSet(setId)
        .map { entities ->
            entities.toModel()
        }
}