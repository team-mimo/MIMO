package com.mimo.android.viewmodels

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mimo.android.MainActivity
import com.mimo.android.R
import com.mimo.android.apis.users.GetMyInfoResponse
import com.mimo.android.apis.users.getMyInfo
import com.mimo.android.services.kakao.logoutWithKakao
import com.mimo.android.utils.preferences.ACCESS_TOKEN
import com.mimo.android.utils.preferences.USER_ID
import com.mimo.android.utils.preferences.getData
import com.mimo.android.utils.preferences.removeData
import com.mimo.android.utils.preferences.saveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun checkAlreadyLoggedIn(
        firstSettingFunnelsViewModel: FirstSettingFunnelsViewModel
    ){
        val storedAccessToken = getData(ACCESS_TOKEN) ?: return

        viewModelScope.launch {
            getMyInfo(
                accessToken = storedAccessToken,
                onSuccessCallback = { data: GetMyInfoResponse? ->
                    if (data == null) {
                        return@getMyInfo
                    }

                    if (!data.hasHome && !data.hasHub) {
                        firstSettingFunnelsViewModel.updateCurrentStep(R.string.fsfunnel_start)
                    }

                    Toast.makeText(
                        MainActivity.getMainActivityContext(),
                        "자동 로그인 되었습니다",
                        Toast.LENGTH_SHORT
                    ).show()
                    _uiState.update { prevState ->
                        prevState.copy(
                            accessToken = storedAccessToken,
                            user = User(
                                id = data.userId.toString(),
                                hasHome = data.hasHome,
                                hasHub = data.hasHub)
                        )
                    }
                },
                onFailureCallback = {
                    Toast.makeText(
                        MainActivity.getMainActivityContext(),
                        "다시 로그인 해주세요",
                        Toast.LENGTH_SHORT
                    ).show()
                    _uiState.update { prevState ->
                        prevState.copy(accessToken = null, user = null)
                    }
                }
            )
        }
    }

    fun login(
        accessToken: String,
        firstSettingFunnelsViewModel: FirstSettingFunnelsViewModel
    ){
        viewModelScope.launch {
            saveData(ACCESS_TOKEN, accessToken)
            getMyInfo(
                accessToken = accessToken,
                onSuccessCallback = { data: GetMyInfoResponse? ->
                    if (data == null) {
                        return@getMyInfo
                    }

                    if (!data.hasHome && !data.hasHub) {
                        firstSettingFunnelsViewModel.updateCurrentStep(R.string.fsfunnel_start)
                    }

                    saveData(USER_ID, data.userId.toString())

                    _uiState.update { prevState ->
                        prevState.copy(
                            accessToken = accessToken,
                            user = User(
                                id = data.userId.toString(),
                                hasHome = data.hasHome,
                                hasHub = data.hasHub)
                            )
                    }
                }
            )
        }
    }

    fun logout(){
        removeData(ACCESS_TOKEN)
        removeData(USER_ID)
        logoutWithKakao()
        _uiState.value = AuthUiState()
    }
}

data class AuthUiState(
    val user: User? = null,
    val accessToken: String? = null
)

data class User(
    val id: String,
    val hasHome: Boolean,
    val hasHub: Boolean
)