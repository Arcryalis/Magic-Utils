package com.arcryalis.gwentest.remote.impl.di

import com.arcryalis.gwentest.remote.scryfall.ScryfallDataSource
import com.arcryalis.gwentest.remote.impl.ScryfallDataSourceImpl
import com.arcryalis.gwentest.remote.impl.api.ScryfallApi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface ScryfallModule {

    @Binds
    fun bindScryfallDataSource(scryfallDataSource: ScryfallDataSourceImpl) : ScryfallDataSource

    companion object {
        @Provides
        @Singleton
        fun provideScryfallApi(retrofit: Retrofit): ScryfallApi =
            retrofit.create(ScryfallApi::class.java)
    }

}