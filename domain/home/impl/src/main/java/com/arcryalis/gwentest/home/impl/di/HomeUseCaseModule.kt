package com.arcryalis.gwentest.home.impl.di

import com.arcryalis.gwentest.home.GetCardInfoListUseCase
import com.arcryalis.gwentest.home.IsSetAvailableUseCase
import com.arcryalis.gwentest.home.TestUseCase
import com.arcryalis.gwentest.home.impl.GetCardInfoListUseCaseImpl
import com.arcryalis.gwentest.home.impl.IsSetAvailableUseCaseImpl
import com.arcryalis.gwentest.home.impl.TestUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
interface HomeUseCaseModule {

    @Binds
    fun bindTestUseCase(testUseCase: TestUseCaseImpl): TestUseCase

    @Binds
    fun bindIsSetAvailableUseCase(isSetAvailableUseCase: IsSetAvailableUseCaseImpl): IsSetAvailableUseCase

    @Binds
    fun bindGetCardInfoUseCase(getCardInfoListUseCase: GetCardInfoListUseCaseImpl): GetCardInfoListUseCase
}