package com.arcryalis.gwentest.camera

import android.content.ClipData
import android.graphics.Bitmap
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arcryalis.gwentest.core.isPortrait
import kotlinx.coroutines.launch

@Composable
fun CameraReadyScreen(
    state: CameraState.Ready,
    modifier: Modifier = Modifier,
    onInputChange: (String) -> Unit = {},
    onInputFocused: () -> Unit = {},
    onSubmitClick: () -> Unit = {},
    onOpenLinkClick: (String) -> Unit = {},
    onCameraError: () -> Unit = {},
    onFrame: (Bitmap, () -> Unit) -> Unit = { _, onProcessed -> onProcessed() },
    onScanClick: () -> Unit = {},
    onPermissionRequestClick: () -> Unit = {},
    onUseManualInputClick: () -> Unit = {}
) {
    val resultsList: @Composable (Modifier) -> Unit = { listModifier ->
        CardSearchResultsList(
            searchResults = state.searchResults,
            storeLink = state.storeLink,
            modifier = listModifier,
            onOpenLinkClick = onOpenLinkClick
        )
    }

    if (state.camera == CameraMode.Manual) {
        Column(modifier = modifier) {
            CardNameInput(
                state = state,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = onInputChange,
                onFocused = onInputFocused,
                onSubmitClick = onSubmitClick
            )
            resultsList(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        }
        return
    }

    val cameraSection: @Composable (Modifier) -> Unit = { sectionModifier ->
        CameraSection(
            state = state,
            modifier = sectionModifier,
            onInputChange = onInputChange,
            onInputFocused = onInputFocused,
            onSubmitClick = onSubmitClick,
            onCameraError = onCameraError,
            onFrame = onFrame,
            onScanClick = onScanClick,
            onPermissionRequestClick = onPermissionRequestClick,
            onUseManualInputClick = onUseManualInputClick
        )
    }

    if (isPortrait()) {
        Column(modifier = modifier) {
            cameraSection(Modifier.weight(2f).fillMaxWidth())
            resultsList(Modifier.weight(3f).fillMaxWidth())
        }
    } else {
        Row(modifier = modifier) {
            cameraSection(Modifier.weight(2f).fillMaxHeight())
            resultsList(Modifier.weight(3f).fillMaxHeight())
        }
    }
}

@Composable
private fun CameraSection(
    state: CameraState.Ready,
    modifier: Modifier = Modifier,
    onInputChange: (String) -> Unit = {},
    onInputFocused: () -> Unit = {},
    onSubmitClick: () -> Unit = {},
    onCameraError: () -> Unit = {},
    onFrame: (Bitmap, () -> Unit) -> Unit = { _, onProcessed -> onProcessed() },
    onScanClick: () -> Unit = {},
    onPermissionRequestClick: () -> Unit = {},
    onUseManualInputClick: () -> Unit = {}
) {
    Column(modifier = modifier) {
        CameraFeedContent(
            isScanning = (state.camera as? CameraMode.Available)?.isScanning == true,
            hasCardName = state.cardName.isNotBlank(),
            permissionRequired = state.camera == CameraMode.PermissionRequired,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            onFrame = onFrame,
            onCameraError = onCameraError,
            onScanClick = onScanClick,
            onPermissionRequestClick = onPermissionRequestClick,
            onUseManualInputClick = onUseManualInputClick
        )
        CardNameInput(
            state = state,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = onInputChange,
            onFocused = onInputFocused,
            onSubmitClick = onSubmitClick
        )
    }
}

@Composable
private fun CardNameInput(
    state: CameraState.Ready,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {},
    onFocused: () -> Unit = {},
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
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { if (it.isFocused) onFocused() }
        )

        Box(
            modifier = Modifier.width(36.dp),
            contentAlignment = Alignment.Center
        ) {
            if (state.searchResults == CardSearchResultState.Loading) {
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
private fun CardSearchResultsList(
    searchResults: CardSearchResultState,
    storeLink: StoreLinkState,
    modifier: Modifier = Modifier,
    onOpenLinkClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    when (searchResults) {
        CardSearchResultState.NoResults -> CardSearchResultsMessage(
            text = stringResource(R.string.camera_results_empty),
            modifier = modifier
        )

        CardSearchResultState.Error -> CardSearchResultsMessage(
            text = stringResource(R.string.camera_results_error),
            modifier = modifier
        )

        else -> {
            val names = (searchResults as? CardSearchResultState.Results)?.names.orEmpty()
            LazyColumn(modifier = modifier) {
                items(names) { name ->
                    CardSearchResultItem(
                        name = name,
                        storeLink = storeLink,
                        onCopyClick = {
                            scope.launch {
                                clipboard.setClipEntry(ClipEntry(ClipData.newPlainText(name, name)))
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                                    Toast.makeText(context, R.string.camera_copied, Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        onOpenLinkClick = { onOpenLinkClick(name) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CardSearchResultItem(
    name: String,
    storeLink: StoreLinkState,
    onCopyClick: () -> Unit = {},
    onOpenLinkClick: () -> Unit = {}
) {
    ListItem(
        headlineContent = { Text(text = name) },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onCopyClick) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = stringResource(R.string.camera_copy_name)
                    )
                }
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (storeLink == StoreLinkState.Loading(name)) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        IconButton(
                            onClick = onOpenLinkClick,
                            enabled = storeLink !is StoreLinkState.Loading
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                                contentDescription = stringResource(R.string.camera_open_cardmarket)
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun CardSearchResultsMessage(
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
