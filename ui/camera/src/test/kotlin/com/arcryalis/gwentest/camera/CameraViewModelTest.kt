package com.arcryalis.gwentest.camera

import com.arcryalis.gwentest.core.BaseDispatchRule
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertIs

class CameraViewModelTest {

    @get:Rule
    val mainDispatcher = BaseDispatchRule()

    @Test
    fun whenInitialized_thenStateIsPermissionRequired() {
        val sut = CameraViewModel()

        assertIs<CameraState.PermissionRequired>(sut.state.value)
    }
}
