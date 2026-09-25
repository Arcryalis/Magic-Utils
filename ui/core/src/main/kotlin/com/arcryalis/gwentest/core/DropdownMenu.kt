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
    titleLabel: String,
    emptyLabel: String = stringResource(R.string.empty_dropdown_option),
    fieldLabel: (T) -> String? = { it?.toString() },
    onItemSelected: (T) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = fieldLabel(selectedItem) ?: emptyLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(titleLabel) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownItem(
                    item = fieldLabel(item) ?: emptyLabel,
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
private fun <T> DropdownItem(
    item: T?,
    onItemSelected: () -> Unit = {}
) {
    DropdownMenuItem(
        text = {
            Text(
                text = item.toString(),
                fontSize = 16.sp
            )
        },
        onClick = onItemSelected
    )

}