package com.arcryalis.gwentest.home.impl

import com.arcryalis.gwentest.home.TestUseCase
import javax.inject.Inject

class TestUseCaseImpl @Inject constructor(): TestUseCase {
    override suspend fun invoke(): String = "Android"
}