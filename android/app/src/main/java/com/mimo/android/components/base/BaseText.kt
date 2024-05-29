package com.mimo.android.components.base

import androidx.compose.runtime.Composable
import androidx.compose.material.Text
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit

@Composable fun BaseText(
    text: String,
    fontSize: TextUnit,
    bold: Boolean,
    color: Color
) {

    val fontWeight = if (bold) FontWeight.Bold else null

    Text(
        text = text,
        style = TextStyle(
            color = color,
            fontSize = fontSize,
            fontWeight = fontWeight
        )
    )
}