package com.arcryalis.gwentest.camera

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arcryalis.gwentest.core.isPortrait

@Composable
fun CameraReadyScreen(
    state: CameraState.Ready,
    modifier: Modifier = Modifier,
    onInputChange: (String) -> Unit = {},
    onSubmitClick: () -> Unit = {},
    onCameraError: () -> Unit = {},
    onFrame: (Bitmap, () -> Unit) -> Unit = { _, onProcessed -> onProcessed() },
    onScanClick: () -> Unit = {}
) {
    if (!state.showCamera) {
        Column(modifier = modifier) {
            CardNameInput(
                state = state,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = onInputChange,
                onSubmitClick = onSubmitClick
            )
            CameraResultsList(
                results = state.results,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        }

    } else if (isPortrait()) {
        Column(modifier = modifier) {
            CameraSection(
                state = state,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                onInputChange = onInputChange,
                onSubmitClick = onSubmitClick,
                onCameraError = onCameraError,
                onFrame = onFrame,
                onScanClick = onScanClick
            )
            CameraResultsList(
                results = state.results,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        }

    } else {
        Row(modifier = modifier) {
            CameraSection(
                state = state,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                onInputChange = onInputChange,
                onSubmitClick = onSubmitClick,
                onCameraError = onCameraError,
                onFrame = onFrame,
                onScanClick = onScanClick
            )
            CameraResultsList(
                results = state.results,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
private fun CameraSection(
    state: CameraState.Ready,
    modifier: Modifier = Modifier,
    onInputChange: (String) -> Unit = {},
    onSubmitClick: () -> Unit = {},
    onCameraError: () -> Unit = {},
    onFrame: (Bitmap, () -> Unit) -> Unit = { _, onProcessed -> onProcessed() },
    onScanClick: () -> Unit = {}
) {
    Column(modifier = modifier) {
        CameraFeedContent(
            isScanning = state.isScanning,
            hasCardName = state.cardName.isNotBlank(),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            onFrame = onFrame,
            onCameraError = onCameraError,
            onScanClick = onScanClick
        )
        CardNameInput(
            state = state,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = onInputChange,
            onSubmitClick = onSubmitClick
        )
    }
}

@Composable
private fun CardNameInput(
    state: CameraState.Ready,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {},
    onSubmitClick: () -> Unit = {}
) {
    Row(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = state.cardName,
            onValueChange = onValueChange,
            label = { Text(text = stringResource(R.string.camera_input_label)) },
            singleLine = true,
            isError = state.showMinLengthError,
            supportingText = if (state.showMinLengthError) {
                { Text(text = stringResource(R.string.camera_input_min_length)) }
            } else {
                null
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSubmitClick() }),
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier.width(36.dp),
            contentAlignment = Alignment.Center
        ) {
            if (state.results == CameraResultsState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(28.dp))
            } else {
                IconButton(
                    onClick = onSubmitClick,
                    enabled = state.canSubmit
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = stringResource(R.string.camera_submit)
                    )
                }
            }
        }
    }
}

@Composable
private fun CameraResultsList(
    results: CameraResultsState,
    modifier: Modifier = Modifier
) {
    when (results) {
        CameraResultsState.Empty -> CameraResultsMessage(
            text = stringResource(R.string.camera_results_empty),
            modifier = modifier
        )

        CameraResultsState.Error -> CameraResultsMessage(
            text = stringResource(R.string.camera_results_error),
            modifier = modifier
        )

        else -> {
            val names = (results as? CameraResultsState.Results)?.names.orEmpty()
            LazyColumn(modifier = modifier) {
                items(names) { name ->
                    ListItem(headlineContent = { Text(text = name) })
                }
            }
        }
    }
}

@Composable
private fun CameraResultsMessage(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}
