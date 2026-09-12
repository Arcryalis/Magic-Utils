package com.arcryalis.gwentest.home

interface IsSetAvailableUseCase {
    suspend operator fun invoke(): String
}