package com.mimo.android.views.firstsettingfunnels

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mimo.android.viewmodels.Hub
import com.mimo.android.components.HeadingLarge
import com.mimo.android.components.HeadingSmall
import com.mimo.android.components.base.Size
import com.mimo.android.ui.theme.Teal100

@Composable
fun DirectRegister(
    hub: Hub?,
    goNext: () -> Unit,
    redirectAfterCatchError: () -> Unit,
){

    LaunchedEffect(Unit) {
        goNext()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Spacer(modifier = Modifier.padding(30.dp))

        HeadingSmall(text = hub?.address ?: "", color = Teal100, fontSize = Size.sm)

        Spacer(modifier = Modifier.padding(16.dp))

        HeadingLarge(text = "허브에 등록되었어요!", fontSize = Size.lg)
        Spacer(modifier = Modifier.padding(4.dp))
        HeadingLarge(text = "메인 화면으로 이동할게요", fontSize = Size.lg)
    }
}

@Preview
@Composable
fun DirectRegisterPreview(){
    DirectRegister(
        hub = Hub(
            address = "경기도 고양시 일산서구 산현로12"
        ),
        goNext = {},
        redirectAfterCatchError = {}
    )
}