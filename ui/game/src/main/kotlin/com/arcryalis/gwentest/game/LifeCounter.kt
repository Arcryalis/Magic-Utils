package com.arcryalis.gwentest.core

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arcryalis.gwentest.core.theme.GwenTestTheme
import com.arcryalis.gwentest.game.R


@Composable
fun LifeCounter(
    lifeTotal: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.LightGray,
    arrowsVisible: Boolean = true,
    onUpClicked: () -> Unit = {},
    onDownClicked: () -> Unit = {},
    onLongClicked: () -> Unit = {},
) {
    val haptics = LocalHapticFeedback.current
    var boxHeight by remember { mutableFloatStateOf(0f) }
    Column(
        modifier = modifier
            .background(backgroundColor)
            .drawBehind(
                onDraw = { boxHeight = size.height }
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { tapOffset ->
                        handleOnPress(
                            containerHeight = boxHeight,
                            tapYOffset = tapOffset.y,
                            haptics = haptics,
                            onLowerClicked = onDownClicked,
                            onUpperClicked = onUpClicked
                        )
                    },
                    onLongPress = { _ ->
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongClicked()
                    }
                )
            }
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LifeCounterArrow(
            imageVector = Icons.Default.KeyboardArrowUp,
            isVisible = arrowsVisible,
            contentDescription = stringResource(R.string.button_increase),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Text(
            text = lifeTotal,
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 48.sp,
        )

        LifeCounterArrow(
            imageVector = Icons.Default.KeyboardArrowDown,
            isVisible = arrowsVisible,
            contentDescription = stringResource(R.string.button_decrease),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

private fun handleOnPress(
    containerHeight: Float,
    tapYOffset: Float,
    haptics: HapticFeedback,
    onLowerClicked: () -> Unit = {},
    onUpperClicked: () -> Unit = {}
) {
    // check top/bottom. Ignore middle press
    val lowerThreshold = containerHeight / 4
    val upperThreshold = containerHeight - lowerThreshold

    if (tapYOffset >= upperThreshold) {
        haptics.performHapticFeedback(HapticFeedbackType.Confirm) //TODO remove
        onLowerClicked()
    } else if (tapYOffset <= lowerThreshold) {
        haptics.performHapticFeedback(HapticFeedbackType.Reject)
        onUpperClicked()
    }
}

@Composable
private fun LifeCounterArrow(
    imageVector: ImageVector,
    isVisible: Boolean,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    if (isVisible) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = LocalContentColor.current,
            modifier = modifier
                .fillMaxWidth()
                .height(96.dp)
        )
    } else {
        Box(
            modifier = Modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LifeCounterPreview() {
    GwenTestTheme {
        LifeCounter(
            lifeTotal = "100",
            backgroundColor = Color.Red
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LifeCounterWithoutArrowsPreview() {
    GwenTestTheme {
        LifeCounter(
            lifeTotal = "100",
            arrowsVisible = false
        )
    }
}

