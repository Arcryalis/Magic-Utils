package com.arcryalis.gwentest.home.impl.di

import com.arcryalis.gwentest.home.AreCardsAvailableUseCase
import com.arcryalis.gwentest.home.DownloadSchemesUseCase
import com.arcryalis.gwentest.home.GetCardInfoListUseCase
import com.arcryalis.gwentest.home.GetCardSetsUseCase
import com.arcryalis.gwentest.home.impl.AreCardsAvailableUseCaseImpl
import com.arcryalis.gwentest.home.impl.DownloadSchemesUseCaseImpl
import com.arcryalis.gwentest.home.impl.GetCardInfoListUseCaseImpl
import com.arcryalis.gwentest.home.impl.GetCardSetsUseCaseImpl
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
    fun bindGetCardInfoUseCase(getCardInfoListUseCase: GetCardInfoListUseCaseImpl): GetCardInfoListUseCase

    @Binds
    fun bindAreCardsAvailableUseCase(areCardsAvailableUseCase: AreCardsAvailableUseCaseImpl): AreCardsAvailableUseCase
}