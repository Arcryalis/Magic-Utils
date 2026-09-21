package com.arcryalis.gwentest.card.impl

import android.content.Context
import com.arcryalis.gwentest.remote.RemoteResponse
import com.arcryalis.gwentest.remote.scryfall.ScryfallDataSource
import com.arcryalis.gwentest.remote.scryfall.dto.ScryfallDataDto
import com.arcryalis.gwentest.card.impl.mapper.toDistinctSetEntity
import com.arcryalis.gwentest.card.impl.mapper.toInfoEntity
import com.arcryalis.gwentest.card.impl.mapper.toModel
import com.arcryalis.gwentest.data.card.CardRepository
import com.arcryalis.gwentest.data.card.impl.R
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

    override suspend fun doCardsExistLocally(): Boolean = cardInfoStore.doCardsExistLocally()

    override suspend fun downloadSchemes(): Boolean {
        imageCacher.queueImageCacheRequest(context.getString(R.string.card_back_url))

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

    override fun getSet(setId: String): Flow<List<CardInfo>> {
        val cardBackUrl = context.getString(R.string.card_back_url)
        return cardInfoStore.getCardSet(setId).map { entities ->
            entities.toModel(cardBackUrl)
        }
    }
}