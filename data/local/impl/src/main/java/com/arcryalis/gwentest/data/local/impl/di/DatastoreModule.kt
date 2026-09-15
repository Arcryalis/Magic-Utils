package com.arcryalis.gwentest.data.local.impl.di

import com.arcryalis.gwentest.data.local.api.CardInfoDataStore
import com.arcryalis.gwentest.data.local.api.CardSetDataStore
import com.arcryalis.gwentest.data.local.impl.Database
import com.arcryalis.gwentest.data.local.impl.dao.CardDao
import com.arcryalis.gwentest.data.local.impl.dao.CardSetDao
import com.arcryalis.gwentest.data.local.impl.datastore.CardInfoInfoDataStoreImpl
import com.arcryalis.gwentest.data.local.impl.datastore.CardSetDataStoreImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
interface DatastoreModule {

    @Binds
    fun bindCardDataStore(cardDataStore: CardInfoInfoDataStoreImpl): CardInfoDataStore

    @Binds
    fun bindCardSetDataStore(cardSetDataStore: CardSetDataStoreImpl): CardSetDataStore

    companion object {
        @Provides
        fun provideCardDao(db: Database): CardDao = db.getCardDao()

        @Provides
        fun provideCardSetDao(db: Database): CardSetDao = db.getCardSetDao()
    }
}