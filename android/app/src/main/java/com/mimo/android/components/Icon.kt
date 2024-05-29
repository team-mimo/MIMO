package com.mimo.android.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.*
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier

@Composable
fun Icon(
    imageVector: ImageVector,
    size: Dp = 32.dp,
    color: Color = Color.White,
    onClick: (() -> Unit)? = null,
){

    fun handleClick(){
        onClick?.invoke()
    }

    Icon(
        imageVector = imageVector,
        contentDescription = "",
        modifier = Modifier.size(size).clickable(onClick = ::handleClick),
        tint = color,
    )
}