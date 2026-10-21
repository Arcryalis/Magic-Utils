package com.arcryalis.gwentest.camera

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arcryalis.gwentest.core.isPortrait

@Composable
fun CameraReadyScreen(
    state: CameraState.Ready,
    modifier: Modifier = Modifier,
    onInputChange: (String) -> Unit = {},
    onCameraError: () -> Unit = {}
) {
    if (!state.showCamera) {
        CameraInput(
            value = state.cardName,
            modifier = modifier.fillMaxSize(),
            onValueChange = onInputChange
        )

    } else if (isPortrait()) {
        Column(modifier = modifier) {
            CameraFeedContent(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                onCameraError = onCameraError
            )
            CameraInput(
                value = state.cardName,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                onValueChange = onInputChange
            )
        }

    } else {
        Row(modifier = modifier) {
            CameraFeedContent(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                onCameraError = onCameraError
            )
            CameraInput(
                value = state.cardName,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                onValueChange = onInputChange
            )
        }
    }
}

@Composable
private fun CameraInput(
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {}
) {
    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(text = stringResource(R.string.camera_input_label)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        )
    }
}