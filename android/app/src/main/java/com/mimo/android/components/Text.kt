package com.mimo.android.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import com.mimo.android.components.base.BaseText
import com.mimo.android.components.base.Size
import com.mimo.android.components.base.TextSizeMatcher

@Composable
fun Text(
    text: String,
    fontSize: Size = Size.md,
    color: Color = Color.White,
    bold: Boolean = false
) {

    val _defaultFontSize = TextSizeMatcher[Size.md] as TextUnit
    val _fontSize = TextSizeMatcher.getOrElse(fontSize) { _defaultFontSize }

    BaseText(
        text = text,
        fontSize = _fontSize,
        color = color,
        bold = bold
    )
}