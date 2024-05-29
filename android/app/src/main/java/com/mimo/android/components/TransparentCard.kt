package com.mimo.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mimo.android.ui.theme.Teal400
import com.mimo.android.ui.theme.Teal900

@Composable
fun TransparentCard(
    children: @Composable () -> Unit,
    borderRadius: Dp = 16.dp
){

    val borderShape = RoundedCornerShape(borderRadius)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = Teal900,
                shape = borderShape
            )
            .background(
                color = Teal400.copy(alpha = 0.2f),
                shape = borderShape
            )
    ) {
        children()
    }
}

@Preview
@Composable
fun TransparentCardPreview(){
    TransparentCard(
        borderRadius = 8.dp,
        children = {
            Column {
                HeadingLarge(text = "Hello")
                Spacer(modifier = Modifier.padding(30.dp))
                HeadingSmall(text = "World")
            }
        }
    )
}