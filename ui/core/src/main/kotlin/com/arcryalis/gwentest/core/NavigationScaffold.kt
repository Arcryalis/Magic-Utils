package com.arcryalis.gwentest.core

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun NavigationScaffold(
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
    content: @Composable (Modifier) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            if (onNavigateBack != null) {
                DefaultTopNavigationBar(
                    onNavigateBack = onNavigateBack
                )
            }
        },
    ) { innerPadding ->
        content(
            Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun DefaultTopNavigationBar(
    onNavigateBack: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.height(24.dp),
    ) {
        if (onNavigateBack != null) {
            IconButton(
                onClick = onNavigateBack,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.navigation_back)
                )
            }
        }
    }
}
