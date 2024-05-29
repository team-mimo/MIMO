package com.mimo.android.views.main.myhouse

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mimo.android.MainActivity
import com.mimo.android.apis.houses.House
import com.mimo.android.components.*
import com.mimo.android.components.base.Size
import com.mimo.android.components.devices.HubList
import com.mimo.android.views.MyHouseSimpleDeviceListDestination
import com.mimo.android.ui.theme.*
import com.mimo.android.viewmodels.MyHouseHubListViewModel

@Composable
fun MyHouseHubListScreen(
    navController: NavHostController,
    house: House,
    myHouseHubListViewModel: MyHouseHubListViewModel,
) {
    val myHouseHubListUiState by myHouseHubListViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        myHouseHubListViewModel.fetchHubListByHouseId(house.houseId)
    }

    fun handleClickHub(hubId: Long){
        navController.navigate("${MyHouseSimpleDeviceListDestination.route}/${hubId}")
    }

    fun handleGoPrev() {
        navController.navigateUp()
    }
    BackHandler {
        handleGoPrev()
    }

    ScrollView {
        Icon(imageVector = Icons.Filled.ArrowBack, onClick = ::handleGoPrev)
        if (myHouseHubListUiState.loading) {
            LoadingView(house = house)
        } else {
            Spacer(modifier = Modifier.padding(14.dp))
            Header(house = house)
            Spacer(modifier = Modifier.padding(8.dp))
            HubList(hubList = myHouseHubListUiState.hubList, onClickHub = { hubId -> handleClickHub(hubId = hubId) })
        }
    }
}

@Composable
private fun LoadingView(house: House){
    LinearProgressbar2()
    Header(house)
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
    Spacer(modifier = Modifier.padding(16.dp))
    HeadingSmall(text = "이 장소에 등록된 허브", fontSize = Size.lg)
}

@Preview
@Composable
private fun MyHouseHubListScreenPreview(){
    val navController = NavHostController(MainActivity.getMainActivityContext())
    val myHouseHubListViewModel = MyHouseHubListViewModel()
    val house = House(
        houseId = 1,
        nickname = "상윤이의 자취방",
        address = "낙성대 어딘가",
        isHome = true,
        devices = mutableListOf("조명")
    )

    MyHouseHubListScreen(
        navController = navController,
        myHouseHubListViewModel = myHouseHubListViewModel,
        house = house
    )
}