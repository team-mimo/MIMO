package com.mimo.android.components.devices

import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.mimo.android.ui.theme.*

@Composable
fun Switch(
    value: Boolean,
    onToggle: (() -> Unit)? = null
){
    var _value by remember { mutableStateOf(value) }

    androidx.compose.material3.Switch(
        checked = _value,
        onCheckedChange = {
            _value = !_value
            onToggle?.invoke()
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = Teal100, // 체크된 상태의 썸 색상 변경
            uncheckedThumbColor = Teal300, // 체크되지 않은 상태의 썸 색상 변경
            checkedTrackColor = Teal600, // 체크된 상태의 트랙 색상 변경
            uncheckedTrackColor = Teal600 // 체크되지 않은 상태의 트랙 색상 변경
        )
    )
}