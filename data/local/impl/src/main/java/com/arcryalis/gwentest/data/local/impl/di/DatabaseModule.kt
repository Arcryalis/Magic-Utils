package com.arcryalis.gwentest.data.local.impl.di

import android.content.Context
import androidx.room.Room
import com.arcryalis.gwentest.data.local.impl.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Database =
        Room.databaseBuilder(context, Database::class.java, "gwentest.db").build()

}