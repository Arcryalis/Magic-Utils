package com.example.gwentest.di

import com.example.gwentest.TestRepository
import com.example.gwentest.TestRepositoryImpl
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