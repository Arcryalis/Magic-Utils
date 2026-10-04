package com.arcryalis.gwentest

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@Composable
fun ComposeApp(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(HomeRoute)
    var topBarContent by remember { mutableStateOf<(@Composable () -> Unit)?>(null) }
    var bottomBarContent by remember { mutableStateOf<(@Composable () -> Unit)?>(null) }

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
                },
                setBottomBarContent = { bottomBarContent = it }
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
                setTopBarContent = { topBarContent = it },
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
        topBar = { topBarContent?.invoke() },
        bottomBar = { bottomBarContent?.invoke() }
    ) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        )
    }
}
