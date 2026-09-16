package com.arcryalis.gwentest

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.arcryalis.gwentest.home.HomeScreen
import com.arcryalis.gwentest.scheme.SchemeScreen
import com.arcryalis.gwentest.scheme.SchemeViewModel
import com.arcryalis.gwentest.scheme.navigation.SchemeRoute
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute //: NavKey

@Composable
fun ComposeApp(
    modifier: Modifier = Modifier
) {
    val backStack = remember { mutableStateListOf<Any>(HomeRoute) }

//    val navigationState = rememberNavigationState(
//        startRoute = HomeRoute,
//        topLevelRoutes = setOf(HomeRoute)
//    )
//    val navigator = remember { Navigator(navigationState) }

    val entryProvider = entryProvider {
        entry<HomeRoute> {
            HomeScreen(onNavigateToSchemeScreen = { setId ->
                backStack.add(SchemeRoute(setId))
//                navigator.navigate(SchemeRoute(setId = setId))
            })
        }
        entry<SchemeRoute> { route ->
            val viewModel = hiltViewModel<SchemeViewModel, SchemeViewModel.Factory>(
                creationCallback = { factory ->
                    factory.create(route)
                }
            )
//            CompositionLocalProvider {
//                val viewModel = schemeViewModel(
//                    route = route,
//                )
            SchemeScreen(viewModel = viewModel)
//            }
        }
    }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider,
            modifier = Modifier.padding(innerPadding),
        )
    }
}
