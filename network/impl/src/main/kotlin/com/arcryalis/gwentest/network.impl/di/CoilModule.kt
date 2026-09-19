package com.arcryalis.gwentest.network.impl.di

import android.content.Context
import coil.ImageLoader
import com.arcryalis.gwentest.network.CoilImageCacher
import com.arcryalis.gwentest.network.impl.CoilImageCacherImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
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
    ): CoilImageCacher = CoilImageCacherImpl(context)

}
