package com.arcryalis.gwentest.domain.card.impl.di

import com.arcryalis.gwentest.domain.card.AreCardsAvailableUseCase
import com.arcryalis.gwentest.domain.card.DownloadSchemesUseCase
import com.arcryalis.gwentest.domain.card.GetCardBackUrl
import com.arcryalis.gwentest.domain.card.GetShuffledCardInfoUseCase
import com.arcryalis.gwentest.domain.card.GetCardSetsUseCase
import com.arcryalis.gwentest.domain.card.GetAllCardCardInfoUseCase
import com.arcryalis.gwentest.domain.card.impl.AreCardsAvailableUseCaseImpl
import com.arcryalis.gwentest.domain.card.impl.DownloadSchemesUseCaseImpl
import com.arcryalis.gwentest.domain.card.impl.GetCardBackUrlImpl
import com.arcryalis.gwentest.domain.card.impl.GetShuffledCardInfoUseCaseImpl
import com.arcryalis.gwentest.domain.card.impl.GetCardSetsUseCaseImpl
import com.arcryalis.gwentest.domain.card.impl.GetAllCardCardInfoUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
interface HomeUseCaseModule {

    @Binds
    fun bindDownloadSchemesUseCase(downloadSchemesUseCase: DownloadSchemesUseCaseImpl): DownloadSchemesUseCase

    @Binds
    fun bindIsSetAvailableUseCase(isSetAvailableUseCase: GetCardSetsUseCaseImpl): GetCardSetsUseCase

    @Binds
    fun bindGetCardInfoUseCase(getCardInfoListUseCase: GetShuffledCardInfoUseCaseImpl): GetShuffledCardInfoUseCase

    @Binds
    fun bindGetAllCardCardInfoUseCase(getAllCardCardInfoUseCase: GetAllCardCardInfoUseCaseImpl): GetAllCardCardInfoUseCase

    @Binds
    fun bindGetCardBackUrl(getCardBackUrl: GetCardBackUrlImpl): GetCardBackUrl

    @Binds
    fun bindAreCardsAvailableUseCase(areCardsAvailableUseCase: AreCardsAvailableUseCaseImpl): AreCardsAvailableUseCase
}
