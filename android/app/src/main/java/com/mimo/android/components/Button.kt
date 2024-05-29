package com.mimo.android.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mimo.android.ui.theme.Teal600
import com.mimo.android.ui.theme.Teal700

@Composable fun Button(
    onClick: (() -> Unit)? = null,
    text: String,
    width: Dp? = null,
    color: Color = Teal600,
    hasBorder: Boolean = true
){

    val modifierWidth = if (width != null) Modifier.width(width) else Modifier.fillMaxWidth()
    val border = if (hasBorder) BorderStroke(2.dp, Teal700) else null

    fun handleClick(){
        onClick?.invoke()
    }

    Button(
        onClick = ::handleClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = color),
        modifier = modifierWidth.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        border = border
    ) {
        HeadingSmall(text = text)
    }
}