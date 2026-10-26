package com.arcryalis.gwentest.camera

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arcryalis.gwentest.core.isPortrait

@Composable
fun CameraReadyScreen(
    state: CameraState.Ready,
    modifier: Modifier = Modifier,
    onInputChange: (String) -> Unit = {},
    onCameraError: () -> Unit = {},
    onFrame: (Bitmap, () -> Unit) -> Unit = { _, onProcessed -> onProcessed() },
    onScanClick: () -> Unit = {}
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
                isScanning = state.isScanning,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                onFrame = onFrame,
                onCameraError = onCameraError
            )
            CameraInput(
                value = state.cardName,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                onValueChange = onInputChange
            ) {
                ScanControls(
                    isScanning = state.isScanning,
                    hasCardName = state.cardName.isNotBlank(),
                    onScanClick = onScanClick
                )
            }
        }

    } else {
        Row(modifier = modifier) {
            CameraFeedContent(
                isScanning = state.isScanning,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                onFrame = onFrame,
                onCameraError = onCameraError
            )
            CameraInput(
                value = state.cardName,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                onValueChange = onInputChange
            ) {
                ScanControls(
                    isScanning = state.isScanning,
                    hasCardName = state.cardName.isNotBlank(),
                    onScanClick = onScanClick
                )
            }
        }
    }
}

@Composable
private fun CameraInput(
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {},
    content: @Composable ColumnScope.() -> Unit = {}
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(text = stringResource(R.string.camera_input_label)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        )
        content()
    }
}

@Composable
private fun ScanControls(
    isScanning: Boolean,
    hasCardName: Boolean,
    modifier: Modifier = Modifier,
    onScanClick: () -> Unit = {}
) {
    if (isScanning) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp
            )
            Text(
                text = stringResource(R.string.camera_scanning),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    } else {
        Button(
            onClick = onScanClick,
            modifier = modifier
        ) {
            Text(
                text = stringResource(
                    if (hasCardName) R.string.camera_rescan else R.string.camera_scan
                )
            )
        }
    }
}
