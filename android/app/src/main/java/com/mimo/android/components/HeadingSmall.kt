package com.mimo.android.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import com.mimo.android.components.base.BaseText
import com.mimo.android.components.base.HeadingSmallSizeMatcher
import com.mimo.android.components.base.Size

@Composable
fun HeadingSmall(
    text: String,
    fontSize: Size = Size.md,
    color: Color = Color.White
){
    val _defaultFontSize = HeadingSmallSizeMatcher[Size.md] as TextUnit
    val _fontSize = HeadingSmallSizeMatcher.getOrElse(fontSize) { _defaultFontSize }

    BaseText(
        text = text,
        fontSize = _fontSize,
        bold = true,
        color = color
    )
}