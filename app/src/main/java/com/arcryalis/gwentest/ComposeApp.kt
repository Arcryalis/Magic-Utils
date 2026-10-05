package com.arcryalis.gwentest

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.arcryalis.gwentest.game.GameScreen
import com.arcryalis.gwentest.game.GameViewModel
import com.arcryalis.gwentest.game.navigation.GameRoute
import com.arcryalis.gwentest.home.HomeScreen
import com.arcryalis.gwentest.home.navigation.HomeRoute
import com.arcryalis.gwentest.scheme.SchemeScreen
import com.arcryalis.gwentest.scheme.SchemeViewModel
import com.arcryalis.gwentest.scheme.navigation.SchemeRoute

const val NAV_ANIMATION_DURATION = 300

@Composable
fun ComposeApp(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(HomeRoute)

    val entryProvider = entryProvider {
        entry<HomeRoute> {
            HomeScreen(
                onNavigateToSchemeScreen = {
                    backStack.add(SchemeRoute)
                },
                onNavigateToGameScreen = { players, setId ->
                    backStack.add(
                        GameRoute(players, setId)
                    )
                }
            )
        }
        entry<SchemeRoute> { route ->
            val viewModel = hiltViewModel<SchemeViewModel, SchemeViewModel.Factory>(
                creationCallback = { factory ->
                    factory.create(route)
                }
            )
            SchemeScreen(
                viewModel = viewModel,
                onNavigateBack = { backStack.removeLastOrNull() },
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

    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider,
            transitionSpec = {
                // Slide in from right
                (slideInHorizontally(animationSpec = tween(NAV_ANIMATION_DURATION)) { fullWidth -> fullWidth } + fadeIn(animationSpec = tween(NAV_ANIMATION_DURATION))) togetherWith
                        (slideOutHorizontally(animationSpec = tween(NAV_ANIMATION_DURATION)) { fullWidth -> -fullWidth } + fadeOut(animationSpec = tween(NAV_ANIMATION_DURATION)))
            },
            popTransitionSpec = {
                // Slide in from left
                (slideInHorizontally(animationSpec = tween(400)) { fullWidth -> -fullWidth } + fadeIn(animationSpec = tween(NAV_ANIMATION_DURATION))) togetherWith
                        (slideOutHorizontally(animationSpec = tween(400)) { fullWidth -> fullWidth } + fadeOut(animationSpec = tween(NAV_ANIMATION_DURATION)))
            },
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        )
    }
}
