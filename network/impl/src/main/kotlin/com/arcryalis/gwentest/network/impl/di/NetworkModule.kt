package com.arcryalis.gwentest.network.impl.di

import android.content.Context
import com.arcryalis.gwentest.network.impl.R
import com.arcryalis.gwentest.network.impl.ScryfallApiHeaderInterceptor
import com.arcryalis.gwentest.network.impl.ScryfallImageHeaderInterceptor
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    private val retroJson = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    @ApiHttpClient
    fun provideApiOkHttpClient(interceptor: ScryfallApiHeaderInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

    @Provides
    @Singleton
    @ImageHttpClient
    fun provideImageOkHttpClient(interceptor: ScryfallImageHeaderInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        @ApiHttpClient client: OkHttpClient,
        @ApplicationContext context: Context
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(context.getString(R.string.scryfall_base_url))
            .client(client)
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addConverterFactory(retroJson.asConverterFactory("application/json".toMediaType()))
            .build()
}
