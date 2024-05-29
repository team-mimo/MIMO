package com.mimo.android.views.main.myhouse

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mimo.android.apis.houses.House
import com.mimo.android.viewmodels.QrCodeViewModel
import com.mimo.android.components.*
import com.mimo.android.components.base.Size
import com.mimo.android.views.*
import com.mimo.android.ui.theme.Gray300
import com.mimo.android.ui.theme.Gray600
import com.mimo.android.ui.theme.Teal100
import com.mimo.android.ui.theme.Teal400
import com.mimo.android.ui.theme.Teal900
import com.mimo.android.viewmodels.MyHouseViewModel
import com.mimo.android.viewmodels.convertDeviceTypeToKoreaName

private const val TAG = "MyHomeScreen"

@Composable
fun MyHouseScreen(
    navController: NavHostController,
    myHouseViewModel: MyHouseViewModel,
    qrCodeViewModel: QrCodeViewModel? = null,
    checkCameraPermissionHubToHouse: () -> Unit,
    checkCameraPermissionMachineToHub: (() -> Unit)? = null,
){
    val myHouseUiState by myHouseViewModel.uiState.collectAsState()
    var isShowCardModal by remember { mutableStateOf(false) }
    var selectedHouse by remember { mutableStateOf<House?>(null) }
    var isShowCreateHouseButtonModal by remember { mutableStateOf(false) }

    val currentHouse = myHouseViewModel.getCurrentHouse(myHouseUiState)
    val anotherHouseList = myHouseViewModel.getAnotherHouseList(myHouseUiState)

    LaunchedEffect(Unit) {
        myHouseViewModel.fetchHouseList()
    }

    fun navigateToCreateHouseConfirmScreen(){
        navController.navigate(CreateHouseConfirmScreenDestination.route)
    }

    fun navigateToMyHouseDetailScreen(house: House){
        navController.navigate("${MyHouseDetailScreenDestination.route}/${house.houseId}")
    }

    fun navigateToChangeHouseNicknameScreen(house: House){
        navController.navigate("${ChangeHouseNicknameScreenDestination.route}/${house.houseId}")
    }

    fun handleShowCreateHouseButtonModal(){
        isShowCreateHouseButtonModal = true
    }

    fun handleCloseCreateHouseButtonModal(){
        isShowCreateHouseButtonModal = false
    }

    fun handleShowCardModal(house: House){
        isShowCardModal = true
        selectedHouse = house
    }

    fun handleCloseCardModal(){
        isShowCardModal = false
        selectedHouse = null
    }

    fun handleClickChangeCurrentHouseModalButton(house: House){
        handleCloseCardModal()
        if (currentHouse?.houseId == house.houseId) {
            return
        }
        myHouseViewModel.changeCurrentHouse(house)
    }

    fun handleClickAddHubModalButton(house: House){
        handleCloseCardModal()
        qrCodeViewModel?.initRegisterHubToHouse(
            houseId = house.houseId,
            checkCameraPermissionHubToHouse = checkCameraPermissionHubToHouse
        )
    }

    ScrollView {
        if (isShowCreateHouseButtonModal) {
            Modal(
                onClose = ::handleCloseCreateHouseButtonModal,
                children = {
                    CreateHouseButtonModalContent(
                        onClose = ::handleCloseCreateHouseButtonModal,
                        onClickCreateHouseWithCurrentLocation = ::navigateToCreateHouseConfirmScreen
                    )
                }
            )
        }

        if (isShowCardModal && selectedHouse != null) {
            Modal(
                onClose = ::handleCloseCardModal,
                children = {
                    CardModalContent(
                        isCurrentHouse = currentHouse?.houseId == selectedHouse!!.houseId,
                        house = selectedHouse!!,
                        onClose = ::handleCloseCardModal,
                        onClickChangeCurrentHouseModalButton = ::handleClickChangeCurrentHouseModalButton,
                        onClickAddHubModalButton = ::handleClickAddHubModalButton,
                        onClickChangeHouseNicknameModalButton = ::navigateToChangeHouseNicknameScreen
                    )
                }
            )
        }

        if (myHouseUiState.loading) {
            HeadingLarge(text = "우리 집", fontSize = Size.lg)
            LinearProgressbar()
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                HeadingLarge(text = "우리 집", fontSize = Size.lg)
                ButtonSmall(text = "집 추가", onClick = ::handleShowCreateHouseButtonModal)
            }

            Spacer(modifier = Modifier.padding(14.dp))

            HeadingSmall(text = "현재 거주지", fontSize = Size.lg)
            Spacer(modifier = Modifier.padding(4.dp))


            if (currentHouse == null) {
                Text(text = "등록된 거주지가 없어요")
            }
            if (currentHouse != null) {
                Card(
                    house = currentHouse,
                    isCurrentHome = true,
                    onClick = { house -> navigateToMyHouseDetailScreen(house) },
                    onClickMenu = { house -> handleShowCardModal(house) },
                    onLongClick = {}
                )
            }

            Spacer(modifier = Modifier.padding(16.dp))

            HeadingSmall(text = "다른 거주지")
            Spacer(modifier = Modifier.padding(4.dp))
            if (anotherHouseList.isEmpty()) {
                Text(text = "등록된 거주지가 없어요")
            }
            if (anotherHouseList.isNotEmpty()) {
                anotherHouseList.forEachIndexed { index, anotherHouse ->
                    Card(
                        house = anotherHouse,
                        isCurrentHome = false,
                        onClick = { house -> navigateToMyHouseDetailScreen(house) },
                        onClickMenu = { house -> handleShowCardModal(house) },
                        onLongClick = {}
                    )

                    if (index < anotherHouseList.size) {
                        Spacer(modifier = Modifier.padding(4.dp))
                    }
                }
            }
        }
        }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Card(
    house: House,
    isCurrentHome: Boolean,
    onClick: ((house: House) -> Unit)? = null,
    onLongClick: ((house: House) -> Unit)? = null,
    onClickMenu: ((house: House) -> Unit)? = null
){
    Box(
        modifier = Modifier.combinedClickable(
            onClick = { onClick?.invoke(house) },
            onLongClick = {
                if (!isCurrentHome) {
                    onLongClick?.invoke(house)
                }
            }
        )
    ){
        TransparentCard(
            borderRadius = 8.dp,
            children = {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .height(96.dp)) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Row {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                house.devices.forEach { deviceType -> CardType(text = convertDeviceTypeToKoreaName(deviceType)) }
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.Menu,
                            size = 24.dp,
                            onClick = { onClickMenu?.invoke(house) }
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    HorizontalScroll(
                        children = {
                            HeadingSmall(text = house.nickname, Size.lg)
                        }
                    )

                    Spacer(modifier = Modifier.padding(4.dp))

                    HorizontalScroll(
                        children = {
                            HeadingSmall(text = house.address, fontSize = Size.xs, color = Teal100)
                        }
                    )
                }
            }
        )
    }
}

@Composable
fun CardModalContent(
    isCurrentHouse: Boolean,
    house: House,
    onClose: () -> Unit,
    onClickChangeCurrentHouseModalButton: (house: House) -> Unit,
    onClickAddHubModalButton: (house: House) -> Unit,
    onClickChangeHouseNicknameModalButton: (house: House) -> Unit
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
                if (!isCurrentHouse) {
                    Button(text = "현재 거주지로 변경", onClick = { onClickChangeCurrentHouseModalButton(house) })
                    Spacer(modifier = Modifier.padding(4.dp))
                }
                Button(text = "집 별칭 변경", onClick = { onClickChangeHouseNicknameModalButton(house) })
                Spacer(modifier = Modifier.padding(4.dp))
                Button(text = "허브 등록", onClick = { onClickAddHubModalButton(house) })
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
fun CreateHouseButtonModalContent(
    onClose: () -> Unit,
    onClickCreateHouseWithCurrentLocation: () -> Unit
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
            HeadingSmall(text = "집 추가하기")
            Spacer(modifier = Modifier.padding(8.dp))
            Button(text = "현재 위치로 추가", onClick = onClickCreateHouseWithCurrentLocation)
            Spacer(modifier = Modifier.padding(4.dp))
            Button(
                text = "닫기", color = Gray600, hasBorder = false,
                onClick = onClose
            )
        }
    }
}

@Preview
@Composable
private fun MyHouseScreenPreview(){
    val navController = NavHostController(LocalContext.current)
    val houseList: List<House> = arrayListOf(
        House(
            houseId = 1,
            isHome = true,
            devices = arrayListOf("조명", "무드등"),
            nickname = "상윤이의 자취방",
            address = "서울특별시 관악구 봉천동 1234-56"
        ),
        House(
            houseId = 2,
            isHome = false,
            devices = arrayListOf("조명", "창문", "커튼"),
            nickname = "상윤이의 본가",
            address = "경기도 고양시 일산서구 산현로12"
        ),
        House(
            houseId = 3,
            isHome = false,
            devices = arrayListOf("조명", "커튼"),
            nickname = "싸피",
            address = "서울특별시 강남구 테헤란로 212"
        )
    )

    val myHouseViewModel = MyHouseViewModel()
    myHouseViewModel.updateHouseList(houseList)

    MyHouseScreen(
        navController = navController,
        myHouseViewModel = myHouseViewModel,
        checkCameraPermissionHubToHouse = {}
    )
}