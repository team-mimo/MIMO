package com.mimo.android.views.main.myhouse.detaildevices

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.mimo.android.apis.houses.Device
import com.mimo.android.viewmodels.MyHouseWindowViewModel

@Composable
fun MyHouseWindowScreen(
    navController: NavHostController? = null,
    device: Device,
    myHouseWindowViewModel: MyHouseWindowViewModel,
){
    val myHouseWindowUiState by myHouseWindowViewModel.uiState.collectAsState()

    Layout(
        navController = navController,
        device = device,
        macAddress = myHouseWindowUiState.window?.macAddress,
        onInitializeCallback = { myHouseWindowViewModel.fetchGetDevice(device) },
        children = {

        }
    )
}