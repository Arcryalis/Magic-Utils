package com.arcryalis.gwentest.card.impl.di

import com.arcryalis.gwentest.card.impl.CardRepositoryImpl
import com.arcryalis.gwentest.data.card.CardRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
interface CardRepositoryModule {

    @Binds
    fun bindCardRepository(cardRepository: CardRepositoryImpl): CardRepository
}