package com.arcryalis.gwentest

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.arcryalis.gwentest.core.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute : NavKey

@Composable
fun ComposeApp(
    modifier: Modifier = Modifier
) {
    val navigationState = rememberNavigationState(
        startRoute = HomeRoute,
        topLevelRoutes = setOf(HomeRoute)
    )
    val navigator = remember { Navigator(navigationState) }

    val entryProvider = entryProvider<NavKey> {
        entry<HomeRoute> {
            HomeScreen()
        }
    }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        NavDisplay(
            modifier = Modifier.padding(innerPadding),
            entries = navigationState.toEntries(entryProvider),
            onBack = { navigator.goBack() }
        )
    }
}
