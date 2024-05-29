package com.mimo.android.views.main.myhouse.detaildevices

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.mimo.android.apis.houses.Device
import com.mimo.android.viewmodels.MyHouseCurtainViewModel

@Composable
fun MyHouseCurtainScreen(
    navController: NavHostController? = null,
    device: Device,
    myHouseCurtainViewModel: MyHouseCurtainViewModel
){
    val myHouseCurtainUiState by myHouseCurtainViewModel.uiState.collectAsState()

    Layout(
        navController = navController,
        device = device,
        macAddress = myHouseCurtainUiState.curtain?.macAddress,
        onInitializeCallback = { myHouseCurtainViewModel.fetchGetDevice(device) },
        children = {

        }
    )
}