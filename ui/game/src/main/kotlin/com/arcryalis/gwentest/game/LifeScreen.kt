package com.arcryalis.gwentest.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.arcryalis.gwentest.core.theme.GwenTestTheme


@Composable
fun LifeCounterScreen(
    modifier: Modifier = Modifier,
) {
    Row {
        LifeCounter(
            lifeTotal = "100",
            backgroundColor = Color.Red.copy(
                alpha = 0.3f
            ),
            modifier = Modifier.weight(1f)
        )
        LifeCounter(
            lifeTotal = "100",
            backgroundColor = Color.Blue.copy(
                alpha = 0.3f
            ),
            modifier = Modifier
                .weight(1f)
                .rotate(180f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LifeCounterScreenPreview() {
    GwenTestTheme {
        LifeCounterScreen()
    }
}