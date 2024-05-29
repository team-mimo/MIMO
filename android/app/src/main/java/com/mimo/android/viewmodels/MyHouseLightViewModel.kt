package com.mimo.android.viewmodels

import com.mimo.android.apis.devices.light.GetLightResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mimo.android.apis.houses.Device
import com.mimo.android.utils.showToast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyHouseLightViewModel: ViewModel(){
    private val _uiState = MutableStateFlow(MyHouseLightUiState())
    val uiState: StateFlow<MyHouseLightUiState> = _uiState.asStateFlow()

    fun fetchGetDevice(device: Device){

        showToast("${device.deviceId}")

        // TODO...
        viewModelScope.launch {

        }
    }
}

data class MyHouseLightUiState(
    val light: GetLightResponse? = null
)