package com.arcryalis.gwentest

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.arcryalis.gwentest.camera.CameraRoute
import com.arcryalis.gwentest.camera.CameraScreen
import com.arcryalis.gwentest.game.GameScreen
import com.arcryalis.gwentest.game.GameViewModel
import com.arcryalis.gwentest.game.navigation.GameRoute
import com.arcryalis.gwentest.home.HomeRoute
import com.arcryalis.gwentest.home.HomeScreen
import com.arcryalis.gwentest.navigation.AppNavigator
import com.arcryalis.gwentest.navigation.toGameRoute
import com.arcryalis.gwentest.scheme.SchemeRoute
import com.arcryalis.gwentest.scheme.SchemeScreen

const val NAV_ANIMATION_DURATION = 300

/** Slides the new screen in from the right (direction 1) or left (direction -1). */
private fun slideTransition(direction: Int): ContentTransform =
    (slideInHorizontally(tween(NAV_ANIMATION_DURATION)) { it * direction } +
            fadeIn(tween(NAV_ANIMATION_DURATION))) togetherWith
            (slideOutHorizontally(tween(NAV_ANIMATION_DURATION)) { -it * direction } +
                    fadeOut(tween(NAV_ANIMATION_DURATION)))

@Composable
fun ComposeApp(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(HomeRoute)
    val navigator = remember(backStack) { AppNavigator(backStack) }

    val entryProvider = entryProvider {
        entry<HomeRoute> {
            HomeScreen(
                onNavigateToSchemeScreen = {
                    navigator.navigate(SchemeRoute)
                },
                onNavigateToCameraScreen = {
                    navigator.navigate(CameraRoute)
                },
                onNavigateToGameScreen = { settings ->
                    navigator.navigate(settings.toGameRoute())
                }
            )
        }
        entry<SchemeRoute> {
            SchemeScreen(
                onNavigateBack = navigator::navigateBack,
            )
        }
        entry<CameraRoute> {
            CameraScreen(
                onNavigateBack = navigator::navigateBack,
            )
        }
        entry<GameRoute> { route ->
            val viewModel = hiltViewModel<GameViewModel, GameViewModel.Factory>(
                creationCallback = { factory ->
                    factory.create(route)
                }
            )
            GameScreen(viewModel = viewModel)
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = navigator::navigateBack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider,
            transitionSpec = { slideTransition(direction = 1) },
            popTransitionSpec = { slideTransition(direction = -1) },
        )
    }
}
