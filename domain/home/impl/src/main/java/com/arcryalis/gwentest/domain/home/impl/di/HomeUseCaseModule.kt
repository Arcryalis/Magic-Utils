package com.arcryalis.gwentest.domain.home.impl.di

import com.arcryalis.gwentest.domain.home.AreCardsAvailableUseCase
import com.arcryalis.gwentest.domain.home.DownloadSchemesUseCase
import com.arcryalis.gwentest.domain.home.GetCardInfoListUseCase
import com.arcryalis.gwentest.domain.home.GetCardSetsUseCase
import com.arcryalis.gwentest.domain.home.impl.AreCardsAvailableUseCaseImpl
import com.arcryalis.gwentest.domain.home.impl.DownloadSchemesUseCaseImpl
import com.arcryalis.gwentest.domain.home.impl.GetCardInfoListUseCaseImpl
import com.arcryalis.gwentest.domain.home.impl.GetCardSetsUseCaseImpl
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