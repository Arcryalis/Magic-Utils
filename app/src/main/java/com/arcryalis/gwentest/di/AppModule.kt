package com.arcryalis.gwentest.di

import com.arcryalis.gwentest.TestRepository
import com.arcryalis.gwentest.TestRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
interface AppModule {

    @Binds
    fun bindGetNotificationsUseCase(testRepository: TestRepositoryImpl): TestRepository
}