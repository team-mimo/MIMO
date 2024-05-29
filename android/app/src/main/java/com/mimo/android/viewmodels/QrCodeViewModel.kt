package com.mimo.android.viewmodels

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mimo.android.MainActivity
import com.mimo.android.apis.hubs.PostRegisterHubToHouseRequest
import com.mimo.android.apis.hubs.postRegisterHubToHouse
import com.mimo.android.utils.preferences.ACCESS_TOKEN
import com.mimo.android.utils.preferences.getData
import com.mimo.android.utils.showToast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "QrCodeViewModel"

class QrCodeViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(QrCodeUiState())
    val uiState: StateFlow<QrCodeUiState> = _uiState.asStateFlow()

    private var _myHouseDetailViewModel: MyHouseDetailViewModel? = null

    fun initRegisterFirstSetting(qrCode: String){
        _uiState.value = QrCodeUiState(qrCode = qrCode)
    }

    fun initRegisterHubToHouse(
        houseId: Long,
        myHouseDetailViewModel: MyHouseDetailViewModel? = null,
        checkCameraPermissionHubToHouse: () -> Unit
    ){
        _myHouseDetailViewModel = myHouseDetailViewModel
        _uiState.value = QrCodeUiState(selectedHouseId = houseId)
        checkCameraPermissionHubToHouse()
    }

    fun registerHubToHouse(qrCode: String){
        viewModelScope.launch {
            val accessToken = getData(ACCESS_TOKEN)
            if (accessToken == null) {
                Log.e(TAG, "accessToken이 없음")
                return@launch
            }

            val houseId = _uiState.value.selectedHouseId
            if (houseId == null) {
                Log.e(TAG, "houseId가 Null임...")
                return@launch
            }

            Log.i(TAG, "houseId가 ${houseId}인 집에 허브등록시도")

            postRegisterHubToHouse(
                accessToken = accessToken,
                postRegisterHubToHomeRequest = PostRegisterHubToHouseRequest(
                    serialNumber = qrCode,
                    houseId = houseId
                ),
                onSuccessCallback = {
                    showToast("허브를 등록했어요")
                    _myHouseDetailViewModel?.fetchGetDeviceListByHouseId(houseId)
                },
                onFailureCallback = {}
            )
        }
    }

    // 초기세팅을 하는 경우 (이 경우 집 정보는 나중에 받음)
    fun setFirstSetting(qrCode: String){
        _uiState.value = QrCodeUiState(qrCode = qrCode)
    }

    // 허브를 집에 등록하는 경우
    fun setHubToHome(){

    }

    // 기기를 허브에 등록하는 경우

    fun removeQrCode(){
        _uiState.value = QrCodeUiState(qrCode = null)
    }
}

data class QrCodeUiState (
    val qrCode: String? = null,
    val selectedHouseId: Long? = null,
    val selectedHubSerialNumber: String? = null,
    val selectedMachineId: Long? = null
)