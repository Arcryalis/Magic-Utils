package com.arcryalis.gwentest.data.local.impl.di

import com.arcryalis.gwentest.data.local.api.CardInfoDataStore
import com.arcryalis.gwentest.data.local.api.CardSetDataStore
import com.arcryalis.gwentest.data.local.impl.AppDatabase
import com.arcryalis.gwentest.data.local.impl.dao.CardInfoDao
import com.arcryalis.gwentest.data.local.impl.dao.CardSetDao
import com.arcryalis.gwentest.data.local.impl.datastore.CardInfoDataStoreImpl
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
    fun bindCardDataStore(cardDataStore: CardInfoDataStoreImpl): CardInfoDataStore

    @Binds
    fun bindCardSetDataStore(cardSetDataStore: CardSetDataStoreImpl): CardSetDataStore

    companion object {
        @Provides
        fun provideCardInfoDao(db: AppDatabase): CardInfoDao = db.getCardInfoDao()

        @Provides
        fun provideCardSetDao(db: AppDatabase): CardSetDao = db.getCardSetDao()
    }
}