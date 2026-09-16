package com.arcryalis.gwentest.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arcryalis.gwentest.core.theme.GwenTestTheme


@Composable
fun LoadingScreen(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier.align(Alignment.Center)
        ) {
            CircularProgressIndicator(
                modifier = modifier.align(Alignment.CenterHorizontally)
            )

            Text(
                text = text,
                modifier = modifier
                    .padding(top = 16.dp)
                    .width(180.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    GwenTestTheme {
        LoadingScreen(
            text = "Important loading information"
        )
    }
}