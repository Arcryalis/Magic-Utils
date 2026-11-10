package com.arcryalis.gwentest.core

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownMenu(
    items: List<T>,
    selectedItem: T,
    modifier: Modifier = Modifier,
    titleLabel: String? = null,
    emptyLabel: String = stringResource(R.string.empty_dropdown_option),
    fieldLabel: (T) -> String? = { it?.toString() },
    leadingContent: (@Composable (T) -> Unit)? = null,
    onItemSelected: (T) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = fieldLabel(selectedItem) ?: emptyLabel,
            onValueChange = {},
            readOnly = true,
            label = titleLabel?.let { { Text(it) } },
            leadingIcon = leadingContent?.let { content -> { content(selectedItem) } },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownItem(
                    label = fieldLabel(item) ?: emptyLabel,
                    leadingContent = leadingContent?.let { content -> { content(item) } },
                    onItemSelected = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun DropdownItem(
    label: String,
    leadingContent: (@Composable () -> Unit)? = null,
    onItemSelected: () -> Unit = {}
) {
    DropdownMenuItem(
        text = {
            if (label.isNotEmpty()) {
                Text(
                    text = label,
                    fontSize = 16.sp
                )
            }
        },
        leadingIcon = leadingContent,
        onClick = onItemSelected
    )
}
