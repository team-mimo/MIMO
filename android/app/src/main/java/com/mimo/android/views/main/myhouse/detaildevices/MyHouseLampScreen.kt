package com.mimo.android.views.main.myhouse.detaildevices

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.mimo.android.apis.houses.Device
import com.mimo.android.viewmodels.MyHouseLampViewModel

@Composable
fun MyHouseLampScreen(
    navController: NavHostController? = null,
    device: Device,
    myHouseLampViewModel: MyHouseLampViewModel
){
    val myHouseLampUiState by myHouseLampViewModel.uiState.collectAsState()

    Layout(
        navController = navController,
        device = device,
        macAddress = myHouseLampUiState.lamp?.macAddress,
        onInitializeCallback = { myHouseLampViewModel.fetchGetDevice(device) },
        children = {

        }
    )
}