package com.arcryalis.gwentest.data.local.impl.di

import com.arcryalis.gwentest.data.local.api.CardDataStore
import com.arcryalis.gwentest.data.local.impl.Database
import com.arcryalis.gwentest.data.local.impl.dao.CardDao
import com.arcryalis.gwentest.data.local.impl.datastore.CardDataStoreImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
interface DatastoreModule {

    @Binds
    fun bindCardDataStore(cardDataStore: CardDataStoreImpl): CardDataStore

    companion object {
        @Provides
        fun provideCardDao(db: Database): CardDao = db.getCardDao()
    }
}