package com.mimo.android.views.main.myhouse.detaildevices

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.mimo.android.apis.houses.Device
import com.mimo.android.viewmodels.MyHouseLightViewModel

@Composable
fun MyHouseLightScreen(
    navController: NavHostController? = null,
    device: Device,
    myHouseLightViewModel: MyHouseLightViewModel,
){
    val myHouseLightUiState by myHouseLightViewModel.uiState.collectAsState()

    Layout(
        navController = navController,
        device = device,
        macAddress = myHouseLightUiState.light?.macAddress,
        onInitializeCallback = { myHouseLightViewModel.fetchGetDevice(device) },
        children = {

        }
    )
}