package com.arcryalis.gwentest.card.impl

import android.content.Context
import com.arcryalis.gwentest.api.RemoteResponse
import com.arcryalis.gwentest.api.scryfall.ScryfallDataSource
import com.arcryalis.gwentest.api.scryfall.dto.ScryfallDataDto
import com.arcryalis.gwentest.card.impl.mapper.toDistinctSetEntity
import com.arcryalis.gwentest.card.impl.mapper.toInfoEntity
import com.arcryalis.gwentest.card.impl.mapper.toModel
import com.arcryalis.gwentest.data.card.CardRepository
import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.data.card.model.CardSet
import com.arcryalis.gwentest.data.local.api.CardInfoDataStore
import com.arcryalis.gwentest.data.local.api.CardSetDataStore
import com.arcryalis.gwentest.network.CoilImageCacher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.distinct

class CardRepositoryImpl @Inject constructor(
    private val cardInfoStore: CardInfoDataStore,
    private val cardSetStore: CardSetDataStore,
    private val dataSource: ScryfallDataSource,
    private val imageCacher: CoilImageCacher,
    @ApplicationContext private val context: Context
) : CardRepository {

    companion object {

        // TODO move to settings
        // Search api currently doesn't support this. Hardcode for now
        const val CARD_BACK_URL = "https://backs.scryfall.io/large/1/b/1b2396d4-9048-439d-96bd-354288518841.jpg?1665006146"
    }

    override suspend fun downloadSchemes(): Boolean {
        imageCacher.queueImageCacheRequest(CARD_BACK_URL)

        return handleDownloadSetPage(1)
    }

    private suspend fun handleDownloadSetPage(pageToFetch: Int): Boolean {
        val response = dataSource.getSchemes(pageToFetch)
        return when (response) {
            is RemoteResponse.Success -> {
                val data = response.data.data

                cardSetStore.insertSets(data.toDistinctSetEntity())
                cardInfoStore.insertCards(data.toInfoEntity())

                cacheFaceImages(data)

                val hasMore = response.data.hasMore
                if (hasMore) {
                    //Download next page recursively
                    handleDownloadSetPage(pageToFetch + 1)
                } else {
                    true
                }
            }

            is RemoteResponse.Error -> {
                // Do nothing
                false
            }
        }
    }

    private fun cacheFaceImages(data: List<ScryfallDataDto>) {
        val smallImages = data.mapNotNull {
            it.imageUris?.small
        }
        val largeImages = data.mapNotNull {
            it.imageUris?.large
        }

        imageCacher.queueImageCacheRequests(
            (smallImages + largeImages).distinct()
        )
    }

    override fun getAvailableSets(): Flow<List<CardSet>> = cardSetStore.getSets()
        .map { entities ->
            entities.toModel()
        }

    override fun getSet(setId: String): Flow<List<CardInfo>> = cardInfoStore.getCardSet(setId)
        .map { entities ->
            entities.toModel()
        }
}