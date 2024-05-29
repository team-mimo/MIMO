package com.mimo.android.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import com.mimo.android.components.base.BaseText
import com.mimo.android.components.base.HeadingLargeSizeMatcher
import com.mimo.android.components.base.Size

@Composable
fun HeadingLarge(
    text: String,
    fontSize: Size = Size.md,
    color: Color = Color.White
){
    val _defaultFontSize = HeadingLargeSizeMatcher[Size.md] as TextUnit
    val _fontSize = HeadingLargeSizeMatcher.getOrElse(fontSize) { _defaultFontSize }

    BaseText(
        text = text,
        fontSize = _fontSize,
        bold = true,
        color = color
    )
}