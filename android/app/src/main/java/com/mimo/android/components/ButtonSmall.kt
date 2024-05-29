package com.mimo.android.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mimo.android.components.base.Size
import com.mimo.android.ui.theme.Teal600
import com.mimo.android.ui.theme.Teal700

@Composable
fun ButtonSmall(
    onClick: (() -> Unit)? = null,
    text: String,
){

    fun handleClick(){
        onClick?.invoke()
    }

    androidx.compose.material.Button(
        onClick = ::handleClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Teal600
        ),
        modifier = Modifier.width(100.dp).height(40.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(2.dp, Teal700)
    ) {
        HeadingSmall(text = text, fontSize = Size.xs)
    }
}