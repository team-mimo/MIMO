package com.mimo.android.components.devices

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mimo.android.apis.houses.Device
import com.mimo.android.components.*
import com.mimo.android.components.base.Size
import com.mimo.android.ui.theme.Teal100
import com.mimo.android.viewmodels.MyHouseDetailViewModel
import com.mimo.android.viewmodels.convertDeviceTypeToKoreaName
import com.mimo.android.viewmodels.isCurtainType
import com.mimo.android.viewmodels.isLampType
import com.mimo.android.viewmodels.isLightType
import com.mimo.android.viewmodels.isWindowType

@Composable
fun MyDeviceList(
    myDeviceList: List<Device>?,
    myHouseDetailViewModel: MyHouseDetailViewModel? = null,
    onClickNavigateToDetailDeviceScreen: ((device: Device) -> Unit)? = null,
){
    fun handleToggleDevice(device: Device, nextValue: Boolean) {
        myHouseDetailViewModel?.fetchToggleDevice(device, nextValue)
    }

    fun handleControlRangeDevice(device: Device, nextValue: Float){
        myHouseDetailViewModel?.fetchControlDevice(device, nextValue)
    }

    if (myDeviceList == null) {
        return
    }

    if (myDeviceList.isEmpty()) {
        Text(text = "등록된 허브가 없어요.")
        return
    }

    Column {
        myDeviceList.forEachIndexed { index, device ->
            TransparentCard(
                borderRadius = 8.dp,
                children = {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Row (
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            CardType(text = convertDeviceTypeToKoreaName(device.type))
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowRight,
                                size = 24.dp,
                                onClick = { onClickNavigateToDetailDeviceScreen?.invoke(device) }
                            )
                        }
                        Spacer(modifier = Modifier.padding(6.dp))

                        HorizontalScroll {
                            HeadingSmall(text = device.nickname, fontSize = Size.sm)
                        }
                        Spacer(modifier = Modifier.padding(4.dp))

                        DeviceController(
                            device = device,
                            onToggleDevice = { nextValue ->
                                handleToggleDevice(
                                    device = device,
                                    nextValue = nextValue
                                )
                            },
                            onControlRangeDevice = { nextValue ->
                                handleControlRangeDevice(
                                    device = device,
                                    nextValue = nextValue
                                )
                            }
                        )

                        Spacer(modifier = Modifier.padding(2.dp))
                    }
                }
            )

            if (index < myDeviceList.size - 1) {
                Spacer(modifier = Modifier.padding(4.dp))
            }
        }
    }
}

@Composable
private fun DeviceController(
    device: Device,
    onToggleDevice: ((nextValue: Boolean) -> Unit)? = null,
    onControlRangeDevice: ((nextValue: Float) -> Unit)? = null
){
    if ((isCurtainType(device.type) || isWindowType(device.type))) {
        if (device.openDegree == null) {
            WarningIcon()
            return
        }

        if (isCurtainType(device.type)) {
            RangeController(
                leftDesc = "닫힘",
                rightDesc = "열림",
                value = device.openDegree.toFloat(),
                onChange = { nextValue -> onControlRangeDevice?.invoke(nextValue) }
            )
            return
        }

        if (isWindowType(device.type)) {
            RangeController(
                leftDesc = "닫힘",
                rightDesc = "열림",
                value = device.openDegree.toFloat(),
                onChange = { nextValue -> onControlRangeDevice?.invoke(nextValue) }
            )
        }

        return
    }

    if (isLightType(device.type) || isLampType(device.type)) {
        if (device.curColor == null) {
            WarningIcon()
            return
        }

        val curValue = device.curColor.toInt() == 1

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Switch(value = curValue, onToggle = { onToggleDevice?.invoke(!curValue) })
        }
    }
}

@Composable
fun WarningIcon(){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Spacer(modifier = Modifier.padding(1.dp))
            Icon(imageVector = Icons.Filled.Warning, size = 18.dp, color = Teal100)
        }
        Spacer(modifier = Modifier.padding(2.dp))
        Text(text = "연결 끊김", color = Teal100)
    }
}

@Preview
@Composable
private fun MyDeviceListPreview(){
    MyDeviceList(myDeviceList = fakeGetMyDeviceList())
}

fun fakeGetMyDeviceList(): List<Device>{
    return mutableListOf(
        Device(
            userId = -1,
            hubId = 1,
            deviceId = 2,
            type = "light",
            nickname = "수지의 기깔난 조명",
            isAccessible = true,
            curColor = 30,
            openDegree = null
        ),
        Device(
            userId = -1,
            hubId = 1,
            deviceId = 3,
            type = "lamp",
            nickname = "윤지의 감성 넘치는 무드등",
            isAccessible = true,
            curColor = 30,
            openDegree = null
        ),
        Device(
            userId = -1,
            hubId = 1,
            deviceId = 4,
            type = "curtain",
            nickname = "동휘의 멋있는 커튼",
            isAccessible = true,
            curColor = null,
            openDegree = 30
        ),
        Device(
            userId = -1,
            hubId = 1,
            deviceId = 5,
            type = "window",
            nickname = "상윤이의 창문",
            isAccessible = true,
            curColor = null,
            openDegree = 80
        ),
    )
}