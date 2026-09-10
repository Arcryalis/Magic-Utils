package com.arcryalis.gwentest

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arcryalis.gwentest.core.HomeScreen

@Composable
fun ComposeApp(
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        HomeScreen(
            modifier = Modifier.padding(innerPadding)
        )
    }
}