package com.arcryalis.gwentest.home.impl

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

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