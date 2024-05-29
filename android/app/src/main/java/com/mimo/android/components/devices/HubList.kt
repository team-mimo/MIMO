package com.mimo.android.components.devices

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mimo.android.apis.hubs.Hub
import com.mimo.android.apis.hubs.SimpleDevice
import com.mimo.android.components.HeadingSmall
import com.mimo.android.components.Icon
import com.mimo.android.components.Text
import com.mimo.android.components.TransparentCard
import com.mimo.android.components.base.Size
import com.mimo.android.ui.theme.Teal100

@Composable
fun HubList(
    hubList: List<Hub>?,
    onClickHub: ((hubId: Long) -> Unit)? = null
){
    if (hubList == null) {
        return
    }

    if (hubList.isEmpty()) {
        Text(text = "등록된 허브가 없어요.")
        return
    }

    Column {
        hubList.forEachIndexed { index, hub ->
            TransparentCard(
                borderRadius = 8.dp,
                children = {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Column {
                            HeadingSmall(text = hub.serialNumber)
                            Spacer(modifier = Modifier.padding(4.dp))
                            HeadingSmall(text = "연결된 기기 ${hub.devices.size}개", fontSize = Size.xs, color = Teal100)
                        }
                        Icon(imageVector = Icons.Filled.KeyboardArrowRight, onClick = { onClickHub?.invoke(hub.hubId) })
                    }
                }
            )

            if (index < hubList.size - 1) {
                Spacer(modifier = Modifier.padding(4.dp))
            }
        }
    }
}

@Preview
@Composable
private fun HubListPreview(){
    val hubList = fakeGetHubListByHouseId()
    HubList(hubList = hubList)
}

fun fakeGetHubListByHouseId(): List<Hub>{
    val simpleDevice1 = SimpleDevice(
        deviceId = 1,
        macAddress = "aaaaaaa",
        deviceType = "조명"
    )
    val simpleDevice2 = SimpleDevice(
        deviceId = 2,
        macAddress = "bbbbbbb",
        deviceType = "창문"
    )
    val simpleDevice3 = SimpleDevice(
        deviceId = 3,
        macAddress = "cccccccc",
        deviceType = "커튼"
    )
    val simpleDevice4 = SimpleDevice(
        deviceId = 4,
        macAddress = "ddddddd",
        deviceType = "무드등"
    )
    val simpleDeviceList = mutableListOf(
        simpleDevice1, simpleDevice2, simpleDevice3, simpleDevice4
    )

    return mutableListOf(
        Hub(
            hubId = 1,
            serialNumber = "AAA123123",
            registeredDttm = "2024-05-15T18:14:45.216Z",
            devices = simpleDeviceList
        ),
        Hub(
            hubId = 2,
            serialNumber = "BBB123123",
            registeredDttm = "2024-05-15T18:14:45.216Z",
            devices = simpleDeviceList
        ),
        Hub(
            hubId = 3,
            serialNumber = "CCC123123",
            registeredDttm = "2024-05-15T18:14:45.216Z",
            devices = simpleDeviceList
        ),
        Hub(
            hubId = 4,
            serialNumber = "DDD123123",
            registeredDttm = "2024-05-15T18:14:45.216Z",
            devices = simpleDeviceList
        ),
        Hub(
            hubId = 5,
            serialNumber = "EEE123123",
            registeredDttm = "2024-05-15T18:14:45.216Z",
            devices = mutableListOf()
        )
    )
}