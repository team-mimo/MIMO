package com.mimo.android.components.devices

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mimo.android.apis.hubs.SimpleDevice
import com.mimo.android.components.HeadingSmall
import com.mimo.android.components.Text
import com.mimo.android.components.TransparentCard
import com.mimo.android.components.base.Size
import com.mimo.android.ui.theme.Teal100
import com.mimo.android.viewmodels.convertDeviceTypeToKoreaName

@Composable
fun SimpleDeviceList(
    simpleDeviceList: List<SimpleDevice>
){
    if (simpleDeviceList.isEmpty()) {
        Text(text = "연결된 기기가 없어요.")
        return
    }

    Column {
        simpleDeviceList.forEachIndexed { index, simpleDevice ->
            TransparentCard(
                borderRadius = 8.dp,
                children = {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    ) {
                        HeadingSmall(text = simpleDevice.macAddress)
                        Spacer(modifier = Modifier.padding(4.dp))
                        HeadingSmall(text = convertDeviceTypeToKoreaName(simpleDevice.deviceType), fontSize = Size.xs, color = Teal100)
                    }
                }
            )

            if (index < simpleDeviceList.size - 1) {
                Spacer(modifier = Modifier.padding(4.dp))
            }
        }
    }
}