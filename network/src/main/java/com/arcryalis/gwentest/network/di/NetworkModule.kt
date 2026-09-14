package com.arcryalis.gwentest.network.di

import com.arcryalis.gwentest.network.ScryfallHeaderInterceptor
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(scryfallInterceptor: ScryfallHeaderInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(scryfallInterceptor)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.scryfall.com/")
            .client(client)
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(retroJson.asConverterFactory("application/json".toMediaType()))
            .build()

    private val retroJson = Json { ignoreUnknownKeys = true }
}
