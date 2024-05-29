package com.mimo.android.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun HorizontalScroll(
    children: @Composable () -> Unit
){
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
    ) {
        children()
    }
}