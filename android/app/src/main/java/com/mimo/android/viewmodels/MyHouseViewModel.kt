package com.mimo.android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mimo.android.apis.houses.House
import com.mimo.android.apis.houses.PostRegisterHouseRequest
import com.mimo.android.apis.houses.PutChangeHouseNicknameRequest
import com.mimo.android.apis.houses.getHouseList
import com.mimo.android.apis.houses.postRegisterHouse
import com.mimo.android.apis.houses.putChangeCurrentHouse
import com.mimo.android.apis.houses.putChangeHouseNickname
import com.mimo.android.utils.alertError
import com.mimo.android.utils.preferences.ACCESS_TOKEN
import com.mimo.android.utils.preferences.getData
import com.mimo.android.utils.showToast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyHouseViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(MyHouseUiState())
    val uiState: StateFlow<MyHouseUiState> = _uiState.asStateFlow()

    fun updateHouseList(houseList: List<House>){
        _uiState.update { prevState ->
            prevState.copy(houseList = houseList)
        }
    }

    fun getCurrentHouse(
        myHouseUiState: MyHouseUiState? = null,
    ): House? {
        if (myHouseUiState != null) {
            val house = myHouseUiState.houseList.find { house ->
                house.isHome
            }
            return house
        }

        return _uiState.value.houseList.find { house -> house.isHome }
    }

    fun getAnotherHouseList(
        myHouseUiState: MyHouseUiState,
    ): List<House> {
        return myHouseUiState.houseList.filter { house ->
            !house.isHome
        }
    }

    fun queryHouse(
        myHouseUiState: MyHouseUiState,
        houseId: Long
    ): House? {
        return myHouseUiState.houseList.find { house -> house.houseId == houseId }
    }

    fun fetchHouseList(){
        viewModelScope.launch {
            getHouseList(
                accessToken = getData(ACCESS_TOKEN) ?: "",
                onSuccessCallback = { houses ->
                    if (houses == null) {
                        showToast("집 목록을 불러오지 못했어요")
                        return@getHouseList
                    }
                    _uiState.value = MyHouseUiState(
                        houseList = houses,
                        loading = false
                    )
                },
                onFailureCallback = {
                    showToast("집 목록을 불러오지 못했어요")
                }
            )
        }
    }

    fun fetchCreateNewHouse(
        address: String,
        onSuccessCallback: (() -> Unit)? = null,
        onFailureCallback: (() -> Unit)? = null,
    ){
        viewModelScope.launch {
            postRegisterHouse(
                accessToken = getData(ACCESS_TOKEN) ?: "",
                postRegisterHouseRequest = PostRegisterHouseRequest(
                    address = address,
                    nickname = address
                ),
                onSuccessCallback = { onSuccessCallback?.invoke() },
                onFailureCallback = { onFailureCallback?.invoke() }
            )
        }
    }

    fun fetchChangeHouseNickname(
        house: House,
        newNickname: String,
        cb: (() -> Unit)? = null
    ){
        viewModelScope.launch {
            if (newNickname.isEmpty() || house.nickname == newNickname) {
                showToast("집 별칭을 변경했어요")
                cb?.invoke()
                return@launch
            }

            putChangeHouseNickname(
                accessToken = getData(ACCESS_TOKEN) ?: "",
                houseId = house.houseId,
                putChangeHouseNicknameRequest = PutChangeHouseNicknameRequest(
                    nickname = newNickname
                ),
                onSuccessCallback = { data ->
                    showToast("집 별칭을 변경했어요")
                    cb?.invoke()
                },
                onFailureCallback = { alertError() }
            )
        }
    }

    fun changeCurrentHouse(house: House){
        viewModelScope.launch {
            putChangeCurrentHouse(
                accessToken = getData(ACCESS_TOKEN) ?: "",
                houseId = house.houseId,
                onSuccessCallback = {
                    showToast("현재 거주지를 변경했어요")
                    fetchHouseList()
                },
                onFailureCallback = {
                    alertError()
                }
            )
        }
    }
}

data class MyHouseUiState(
    val houseList: List<House> = mutableListOf(),
    val loading: Boolean = true
)