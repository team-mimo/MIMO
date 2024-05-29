package com.mimo.android.views.main.myhouse

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mimo.android.apis.houses.*
import com.mimo.android.components.*
import com.mimo.android.components.base.Size
import com.mimo.android.components.devices.*
import com.mimo.android.views.*
import com.mimo.android.ui.theme.*
import com.mimo.android.viewmodels.*

@Composable
fun MyHouseDetailScreen(
    navController: NavHostController,
    house: House,
    myHouseDetailViewModel: MyHouseDetailViewModel,
    qrCodeViewModel: QrCodeViewModel,
    checkCameraPermissionHubToHouse: () -> Unit,
    checkCameraPermissionMachineToHub: () -> Unit,
    myHouseCurtainViewModel: MyHouseCurtainViewModel,
    myHouseLampViewModel: MyHouseLampViewModel,
    myHouseLightViewModel: MyHouseLightViewModel,
    myHouseWindowViewModel: MyHouseWindowViewModel
){
    val myHouseDetailUiState by myHouseDetailViewModel.uiState.collectAsState()
    val devices = myHouseDetailViewModel.getDevices(myHouseDetailUiState)
    val myDeviceList = devices?.myDeviceList
    val anotherDeviceList = devices?.anotherDeviceList
    var isShowScreenModal by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        myHouseDetailViewModel.fetchGetDeviceListByHouseId(house.houseId)
    }

    fun handleGoPrev(){
        navController.navigateUp()
    }
    BackHandler {
        handleGoPrev()
    }

    fun navigateToDetailDeviceScreen(device: Device){
        if (isWindowType(device.type)) {
            navController.navigate("${MyHouseWindowScreenDestination.route}/${device.deviceId}")
            return
        }
        if (isLightType(device.type)) {
            navController.navigate("${MyHouseLightScreenDestination.route}/${device.deviceId}")
            return
        }
        if (isLampType(device.type)) {
            navController.navigate("${MyHouseLampScreenDestination.route}/${device.deviceId}")
            return
        }
        if (isCurtainType(device.type)) {
            navController.navigate("${MyHouseCurtainScreenDestination.route}/${device.deviceId}")
            return
        }
    }

    fun navigateToChangeHouseNicknameScreen(){
        navController.navigate("${ChangeHouseNicknameScreenDestination.route}/${house.houseId}")
    }

    fun handleShowScreenModal(){
        isShowScreenModal = true
    }

    fun handleCloseScreenModal(){
        isShowScreenModal = false
    }

    fun handleClickAddHubModalButton(){
        qrCodeViewModel.initRegisterHubToHouse(
            houseId = house.houseId,
            myHouseDetailViewModel = myHouseDetailViewModel,
            checkCameraPermissionHubToHouse = checkCameraPermissionHubToHouse
        )
    }

    fun handleClickShowHubListButton(){
        navController.navigate("${MyHouseHubListScreenDestination.route}/${house.houseId}")
    }

    ScrollView {
        if (isShowScreenModal) {
            Modal(
                onClose = ::handleCloseScreenModal,
                children = {
                    ScreenModalContent(
                        house = house,
                        onClose = { handleCloseScreenModal() },
                        onClickAddHubModalButton = { handleClickAddHubModalButton() },
                        onClickChangeHouseNicknameButton = { navigateToChangeHouseNicknameScreen() },
                        onClickShowHubListButton = { handleClickShowHubListButton() }
                    )
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Icon(imageVector = Icons.Filled.ArrowBack, onClick = ::handleGoPrev)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    imageVector = Icons.Default.Refresh,
                    size = 32.dp,
                    onClick = { myHouseDetailViewModel.fetchGetDeviceListByHouseId(house.houseId) },
                    color = Teal100
                )
                Icon(
                    imageVector = Icons.Default.Menu,
                    size = 32.dp,
                    onClick = ::handleShowScreenModal
                )
            }
        }

        if (myHouseDetailUiState.loading) {
            LinearProgressbar2()
        } else {
            Spacer(modifier = Modifier.padding(14.dp))
            Header(house = house)

            if (myDeviceList == null) {
                Text(text = "")
            } else {
                if (myDeviceList.isEmpty()) {
                    Text(text = "등록된 기기가 없어요. 기기를 등록해주세요.")
                } else {
                    MyDeviceList(
                        myDeviceList = myDeviceList,
                        myHouseDetailViewModel = myHouseDetailViewModel,
                        onClickNavigateToDetailDeviceScreen = { device -> navigateToDetailDeviceScreen(device) }
                    )
                }
            }
            Spacer(modifier = Modifier.padding(16.dp))
            HeadingSmall(text = "다른 기기")
            Spacer(modifier = Modifier.padding(4.dp))

            if (anotherDeviceList == null) {
                Text(text = "")
            } else {
                if (anotherDeviceList.isEmpty()) {
                    Text(text = "등록된 기기가 없어요.")
                } else {
                    anotherDeviceList.forEachIndexed { index, device ->
                        TransparentCard(
                            borderRadius = 8.dp,
                            children = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                ) {
                                    AnotherDeviceCardTypeRow(device = device)
                                    Spacer(modifier = Modifier.padding(4.dp))
                                    HorizontalScroll(
                                        children = {
                                            Text(text = device.nickname, fontSize = Size.lg)
                                        }
                                    )
                                }
                            }
                        )
                        if (index < anotherDeviceList.size - 1) {
                            Spacer(modifier = Modifier.padding(4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(house: House){
    HorizontalScroll(
        children = {
            HeadingLarge(text = house.nickname, fontSize = Size.lg)
        }
    )
    Spacer(modifier = Modifier.padding(4.dp))
    HorizontalScroll(
        children = {
            HeadingSmall(text = house.address, fontSize = Size.sm, color = Teal100)
        }
    )
    Spacer(modifier = Modifier.padding(12.dp))
}

@Composable
fun ScreenModalContent(
    house: House,
    onClose: () -> Unit,
    onClickAddHubModalButton: () -> Unit,
    onClickChangeHouseNicknameButton: () -> Unit,
    onClickShowHubListButton: () -> Unit
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Gray300,
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = Gray300,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (house.nickname.length > 20) {
                HorizontalScroll(
                    children = {
                        HeadingSmall(text = house.nickname)
                    }
                )
            } else {
                HeadingSmall(text = house.nickname)
            }
            Spacer(modifier = Modifier.padding(8.dp))
            Column {
                Button(text = "집 별칭 변경", onClick = { onClickChangeHouseNicknameButton() })
                Spacer(modifier = Modifier.padding(4.dp))
                Button(text = "허브 목록", onClick = { onClickShowHubListButton() })
                Spacer(modifier = Modifier.padding(4.dp))
                Button(text = "허브 등록", onClick = { onClickAddHubModalButton() })
                Spacer(modifier = Modifier.padding(4.dp))
                Button(
                    text = "닫기", color = Gray600, hasBorder = false,
                    onClick = { onClose() }
                )
            }
        }
    }
}

@Composable
private fun AnotherDeviceCardTypeRow(device: Device){
    if ((isCurtainType(device.type) || isWindowType(device.type)) && device.openDegree == null) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            CardType(text = convertDeviceTypeToKoreaName(device.type))
            DisconnectIcon()
        }
        return
    }

    if ((isLampType(device.type) || isLightType(device.type)) && device.curColor == null) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            CardType(text = convertDeviceTypeToKoreaName(device.type))
            DisconnectIcon()
        }
        return
    }

    CardType(text = convertDeviceTypeToKoreaName(device.type))
}

@Composable
private fun DisconnectIcon(){
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column {
            Spacer(modifier = Modifier.padding(1.dp))
            Icon(imageVector = Icons.Filled.Warning, size = 10.dp, color = Teal100)
        }
        Spacer(modifier = Modifier.padding(2.dp))
        Text(text = "연결 끊김", fontSize = Size.xs, color = Teal100)
    }
}