package com.arcryalis.gwentest.home

interface TestUseCase {
    suspend operator fun invoke(setId: String): String
}