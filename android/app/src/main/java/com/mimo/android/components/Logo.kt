package com.mimo.android.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mimo.android.R

@Composable
fun Logo(){
    Image(
        painter = painterResource(id = R.drawable.mimo_logo),
        contentDescription = "mimo logo", // 이미지에 대한 설명
        contentScale = ContentScale.FillBounds,
        modifier = Modifier.size(width = 48.dp, height = 48.dp)
    )
}