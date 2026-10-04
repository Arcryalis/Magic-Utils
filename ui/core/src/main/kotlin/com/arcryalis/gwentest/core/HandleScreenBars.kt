package com.arcryalis.gwentest.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource


@Composable
fun HandleScreenBars(
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
    setTopContent: ((@Composable () -> Unit)?) -> Unit = {},
    setBottomContent: ((@Composable () -> Unit)?) -> Unit = {},
    topContent: (@Composable () -> Unit)? = {
        onNavigateBack?.let { DefaultTopNavigationBar(onNavigateBack) }
    },
    bottomContent: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    DisposableEffect(Unit) {
        setTopContent {
            topContent?.invoke()
        }

        setBottomContent {
            bottomContent?.invoke()
        }

        onDispose {
            setTopContent(null)
            setBottomContent(null)
        }
    }

    Box(
        modifier = modifier,
    ) {
        content()
    }
}

@Composable
fun DefaultTopNavigationBar(
    onNavigateBack: () -> Unit = {}
) {
    Row(
        modifier = Modifier,
    ) {
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
