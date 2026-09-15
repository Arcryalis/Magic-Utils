package com.arcryalis.gwentest.network.di

import android.content.Context
import coil.ImageLoader
import com.arcryalis.gwentest.network.CoilImageCacher
import com.arcryalis.gwentest.network.ScryfallHeaderInterceptor
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
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object CoilModule {

    @Provides
    @Singleton
    fun provideCoilImageLoader(
        @ApplicationContext context: Context,
        httpClient: OkHttpClient
    ): ImageLoader = ImageLoader.Builder(context)
        .okHttpClient(httpClient)
        .build()

    @Provides
    @Singleton
    fun provideCoilImageCacher(
        @ApplicationContext context: Context,
    ): CoilImageCacher = CoilImageCacher(context)
}
