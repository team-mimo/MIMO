package com.mimo.android.views.firstsettingfunnels

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mimo.android.components.*
import com.mimo.android.components.base.*
import com.mimo.android.ui.theme.*

@Composable
fun ConfirmRegister(
    location: String,
    onConfirm: () -> Unit
){
    Column {
        Spacer(modifier = Modifier.padding(30.dp))

        HeadingLarge(text = "현재 위치에", fontSize = Size.lg)
        Spacer(modifier = Modifier.padding(4.dp))
        HeadingLarge(text = "허브를 등록할게요", fontSize = Size.lg)
        Spacer(modifier = Modifier.padding(8.dp))
        HeadingSmall(text = location, color = Teal100, fontSize = Size.sm)

        Spacer(modifier = Modifier.weight(1f))
        Button(text = "완료", onClick = onConfirm)
    }
}

@Preview
@Composable
fun ConfirmRegisterPreview(){
    ConfirmRegister(
        location = "서울특별시 강남구 테헤란로 212",
        onConfirm = {}
    )
}