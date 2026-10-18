package com.arcryalis.gwentest.domain.card.impl.di

import com.arcryalis.gwentest.domain.card.DownloadSchemesUseCase
import com.arcryalis.gwentest.domain.card.GetCardBackUrlUseCase
import com.arcryalis.gwentest.domain.card.GetShuffledCardInfoUseCase
import com.arcryalis.gwentest.domain.card.GetCardSetsUseCase
import com.arcryalis.gwentest.domain.card.GetAllCardInfoUseCase
import com.arcryalis.gwentest.domain.card.impl.DownloadSchemesUseCaseImpl
import com.arcryalis.gwentest.domain.card.impl.GetCardBackUrlUseCaseImpl
import com.arcryalis.gwentest.domain.card.impl.GetShuffledCardInfoUseCaseImpl
import com.arcryalis.gwentest.domain.card.impl.GetCardSetsUseCaseImpl
import com.arcryalis.gwentest.domain.card.impl.GetAllCardInfoUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
interface CardUseCaseModule {

    @Binds
    fun bindDownloadSchemesUseCase(downloadSchemesUseCase: DownloadSchemesUseCaseImpl): DownloadSchemesUseCase

    @Binds
    fun bindGetCardSetsUseCase(getCardSetsUseCase: GetCardSetsUseCaseImpl): GetCardSetsUseCase

    @Binds
    fun bindGetShuffledCardInfoUseCase(getShuffledCardInfoUseCase: GetShuffledCardInfoUseCaseImpl): GetShuffledCardInfoUseCase

    @Binds
    fun bindGetAllCardInfoUseCase(getAllCardInfoUseCase: GetAllCardInfoUseCaseImpl): GetAllCardInfoUseCase

    @Binds
    fun bindGetCardBackUrl(getCardBackUrl: GetCardBackUrlUseCaseImpl): GetCardBackUrlUseCase
}
