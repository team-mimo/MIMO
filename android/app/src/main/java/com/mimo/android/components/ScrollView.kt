package com.mimo.android.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*

@Composable
fun ScrollView(
    height: Dp = 700.dp,
    children: @Composable () -> Unit
){
    Column(
        modifier = Modifier
            .size(height)
            .verticalScroll(rememberScrollState())
    ) {
        children()
        Spacer(modifier = Modifier.padding(32.dp))
    }
}