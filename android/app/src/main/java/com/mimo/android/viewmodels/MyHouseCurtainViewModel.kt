package com.mimo.android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mimo.android.apis.devices.curtain.GetCurtainResponse
import com.mimo.android.apis.houses.Device
import com.mimo.android.utils.showToast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyHouseCurtainViewModel: ViewModel(){
    private val _uiState = MutableStateFlow(MyHouseCurtainUiState())
    val uiState: StateFlow<MyHouseCurtainUiState> = _uiState.asStateFlow()

    fun fetchGetDevice(device: Device){

        showToast("${device.deviceId}")

        // TODO...
        viewModelScope.launch {

        }
    }
}

data class MyHouseCurtainUiState(
    val curtain: GetCurtainResponse? = null
)