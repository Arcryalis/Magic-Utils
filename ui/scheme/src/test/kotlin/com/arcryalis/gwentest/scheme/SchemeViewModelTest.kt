package com.arcryalis.gwentest.scheme

import com.arcryalis.gwentest.domain.card.GetShuffledCardInfoUseCase
import com.arcryalis.gwentest.scheme.navigation.SchemeRoute
import org.junit.Before
import org.junit.Test

class SchemeViewModelTest {

    private lateinit var route: SchemeRoute
    private lateinit var mockGetShuffledCardInfoUseCase: GetShuffledCardInfoUseCase

    private lateinit var sut: SchemeViewModel

    @Before
    fun setUpSut() {
        route = SchemeRoute(
            setId = "testSet"
        )
        mockGetShuffledCardInfoUseCase = MockGetShuffledCardInfoUseCase()

        val temp = MockCardInfo.cardInfo

        sut = SchemeViewModel(
            route = route,
            getShuffledCardInfoUseCase = mockGetShuffledCardInfoUseCase
        )
    }

    @Test
    fun mockViewModel_when_then() {
        val temp = sut.state.value
    }
}