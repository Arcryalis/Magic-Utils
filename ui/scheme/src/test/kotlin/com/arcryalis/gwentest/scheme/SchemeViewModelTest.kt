package com.arcryalis.gwentest.scheme

import com.arcryalis.gwentest.core.BaseDispatchRule
import com.arcryalis.gwentest.domain.card.GetShuffledCardInfoUseCase
import com.arcryalis.gwentest.data.card.TestCardInfo
import com.arcryalis.gwentest.data.card.model.CardInfo
import com.arcryalis.gwentest.domain.card.GetAllCardCardInfoUseCase
import com.arcryalis.gwentest.domain.card.MockGetAllCardCardInfoUseCase
import com.arcryalis.gwentest.domain.card.MockGetShuffledCardInfoUseCase
import com.arcryalis.gwentest.scheme.navigation.SchemeRoute
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class SchemeViewModelTest {

    companion object {
        const val DEFAULT_SET_ID = "defaultSet"
    }

    @get:Rule
    val mainDispatcher = BaseDispatchRule()

    private val testScope = TestScope(mainDispatcher.testDispatcher)

    private lateinit var route: SchemeRoute
    private lateinit var mockGetAllCardInfoUseCase: GetAllCardCardInfoUseCase

    private lateinit var sut: SchemeViewModel

    fun setUpSut(
        availableCards: List<CardInfo>
    ) {
        route = SchemeRoute

        mockGetAllCardInfoUseCase = MockGetAllCardCardInfoUseCase(
            cardInfo = availableCards
        )

        sut = SchemeViewModel(
            route = route,
            getAllCardCardInfoUseCase = mockGetAllCardInfoUseCase
        )
    }
}