package com.mimo.android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mimo.android.apis.devices.lamp.GetLampResponse
import com.mimo.android.apis.houses.Device
import com.mimo.android.utils.showToast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyHouseLampViewModel: ViewModel(){
    private val _uiState = MutableStateFlow(MyHouseLampUiState())
    val uiState: StateFlow<MyHouseLampUiState> = _uiState.asStateFlow()

    fun fetchGetDevice(device: Device){

        showToast("${device.deviceId}")

        // TODO...
        viewModelScope.launch {

        }
    }
}

data class MyHouseLampUiState(
    val lamp: GetLampResponse? = null
)