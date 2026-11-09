package com.arcryalis.gwentest.core

import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat

@Composable
fun rememberPermissionRequest(
    permission: String,
    onPermissionResult: (granted: Boolean, shouldShowRationale: Boolean) -> Unit,
): () -> Unit {
    val activity = LocalActivity.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        onPermissionResult(
            granted,
            activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(it, permission)
            } ?: true,
        )
    }

    return remember(permissionLauncher, permission) {
        { permissionLauncher.launch(permission) }
    }
}
