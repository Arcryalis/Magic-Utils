package com.arcryalis.gwentest.core

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
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
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onUpClicked: () -> Unit = {},
    onDownClicked: () -> Unit = {},
    onClicked: () -> Unit = {},
    onLongClicked: () -> Unit = {},
) {
    val haptics = LocalHapticFeedback.current
    Card(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.background(backgroundColor)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
//                    .pointerInput(Unit) {
//                        detectTapGestures(
//                            onPress = { tapOffset ->
//                                if (pressInTopHalf(tapOffset)) {
//                                    onUpPressed()
//                                } else {
//                                    onDownPressed()
//                                }
//                            },
//                            onLongPress = { tapOffset ->
//                                if (tapOffset)
////                                zoomOffset = if (zoomed) Offset.Zero else
////                                    calculateOffset(tapOffset, size)
////                                zoomed = !zoomed
//                            }
//                        )
//                    }
            ) {
                LifeCounterButton(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = stringResource(R.string.button_increase),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = onUpClicked
                )

                Text(
                    text = lifeTotal,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .combinedClickable(
                            onClick = onClicked,
                            onLongClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                onLongClicked()
                            },
                            onLongClickLabel = stringResource(R.string.button_set)
                        ),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 48.sp,
                )

                LifeCounterButton(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.button_decrease),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = onDownClicked,
                )
            }
        }
    }
}

@Composable
private fun LifeCounterButton(
    imageVector: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    onClick: () -> Unit = {},
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier
            .fillMaxWidth()
            .height(96.dp)
            .clickable(onClick = onClick)
//            .combinedClickable(
//                onClick = onPressed,
//                onLongClick = {
//                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
//                    onLongClicked()
//                },
//                onLongClickLabel = stringResource(R.string.button_set)
//            )
    )
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
