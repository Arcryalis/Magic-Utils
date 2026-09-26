package com.arcryalis.gwentest.core

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun FullScreenDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    headerEndContent: @Composable RowScope.(Modifier) -> Unit = { },
    content: @Composable ColumnScope.(Modifier) -> Unit,
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
            Column(
                modifier = Modifier.fillMaxSize()
            ){
                DialogHeader(
                    modifier = Modifier,
                    onCloseClicked = onDismiss,
                    headerEndContent = headerEndContent
                )

                content(Modifier)
            }
        }
    }
}


@Composable
private fun DialogHeader(
    modifier: Modifier = Modifier,
    onCloseClicked: () -> Unit = {},
    headerEndContent: @Composable RowScope.(Modifier) -> Unit,
) {
    Row(modifier = modifier) {
        FullScreenDialogIcon(
            imageVector = Icons.AutoMirrored.Default.ArrowBack,
            contentDescription = stringResource(R.string.card_info_back),
            onClicked = onCloseClicked
        )

        Spacer(modifier.weight(1f))

        headerEndContent(Modifier)
    }
}

@Composable
fun FullScreenDialogIcon(
    imageVector: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    onClicked: () -> Unit = {}
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClicked)
    )
}