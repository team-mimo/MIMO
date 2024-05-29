package com.mimo.android.components

import com.mimo.android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@Composable
fun BackgroundImage(children: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 이미지를 백그라운드에 표시
        Image(
            painter = painterResource(id = R.drawable.background_image),
            contentDescription = "mimo background image", // 이미지에 대한 설명
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize(), // 이미지를 백그라운드 전체 크기로 설정
        )

        children()
    }
}