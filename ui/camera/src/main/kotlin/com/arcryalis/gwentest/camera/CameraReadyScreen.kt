package com.arcryalis.gwentest.camera

import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.SurfaceRequest
import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arcryalis.gwentest.core.isPortrait

private const val SCANNING_AREA_WIDTH_FRACTION = 0.8f
private const val SCANNING_AREA_HEIGHT_FRACTION = 0.6f

@Composable
fun CameraReadyScreen(
    state: CameraState.Ready,
    modifier: Modifier = Modifier,
    onInputChange: (String) -> Unit = {}
) {
    if (!state.showCamera) {
        CameraInput(
            value = state.cardName,
            modifier = modifier.fillMaxSize(),
            onValueChange = onInputChange
        )

    } else if (isPortrait()) {
        Column(modifier = modifier) {
            CameraFeed(
                surfaceRequest = state.surfaceRequest,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
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
            CameraFeed(
                surfaceRequest = state.surfaceRequest,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
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
private fun CameraFeed(
    surfaceRequest: SurfaceRequest?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.clipToBounds()
    ) {
        surfaceRequest?.let { request ->
            CameraXViewfinder(
                surfaceRequest = request,
                modifier = Modifier.fillMaxSize()
            )
        }

        ScanningAreaOverlay(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun ScanningAreaOverlay(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth(SCANNING_AREA_WIDTH_FRACTION)
            .fillMaxHeight(SCANNING_AREA_HEIGHT_FRACTION)
            .border(2.dp, Color.White)
    )
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