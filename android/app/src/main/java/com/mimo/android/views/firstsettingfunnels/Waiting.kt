package com.mimo.android.views.firstsettingfunnels

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mimo.android.components.HeadingLarge
import com.mimo.android.components.base.Size

@Composable
fun Waiting(
    goNext: () -> Unit
){
    LaunchedEffect(Unit) {
        goNext()
    }

    Column {
        Spacer(modifier = Modifier.padding(30.dp))

        HeadingLarge(text = "허브를 찾고 있어요", fontSize = Size.lg)
        Spacer(modifier = Modifier.padding(4.dp))
        HeadingLarge(text = "잠시만 기다려주세요", fontSize = Size.lg)
    }
}

@Preview
@Composable
fun WaitingPreview(){
    Waiting(
        goNext = {}
    )
}