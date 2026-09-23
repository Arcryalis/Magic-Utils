package com.arcryalis.gwentest.core

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun FullScreenDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    content: @Composable BoxScope.(Modifier) -> Unit
) {
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    Dialog(
        onDismissRequest = { onDismiss() },
    ) {
        Card(
            modifier = modifier.padding(vertical = if (isPortrait) {
                96.dp
            } else {
                16.dp
            }),
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ){
                content(Modifier.align(Alignment.Center))
            }
        }
    }
}