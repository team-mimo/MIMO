package com.mimo.android.viewmodels

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mimo.android.apis.sleeps.PostSleepDataRequest
import com.mimo.android.apis.sleeps.postSleepData
import com.mimo.android.apis.users.GetWakeupTimeResponse
import com.mimo.android.apis.users.PutWakeupTimeRequest
import com.mimo.android.apis.users.PutWakeupTimeResponse
import com.mimo.android.apis.users.deleteWakeupTime
import com.mimo.android.apis.users.getWakeupTime
import com.mimo.android.apis.users.putWakeupTime
import com.mimo.android.utils.alertError
import com.mimo.android.utils.preferences.ACCESS_TOKEN
import com.mimo.android.utils.preferences.getData
import com.mimo.android.utils.showToast
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "SleepViewModel"

class SleepViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(SleepUiState())
    val uiState: StateFlow<SleepUiState> = _uiState.asStateFlow()

    fun fetchGetWakeupTime(
        onStartSleepForegroundService: (() -> Unit)? = null
    ){
//        fakeFetchGetWakeupTime(null, onStartSleepForegroundService)
//        return

        viewModelScope.launch {
            getWakeupTime(
                accessToken = getData(ACCESS_TOKEN) ?: "",
                onSuccessCallback = { data: GetWakeupTimeResponse? ->
                    if (data == null) {
                        return@getWakeupTime
                    }
                    if (data.wakeupTime == null) {
                        _uiState.value = SleepUiState(
                            wakeupTime = null,
                            loading = false
                        )
                        return@getWakeupTime
                    }

                    _uiState.value = SleepUiState(
                        wakeupTime = convertStringWakeupTimeToMyTime(data.wakeupTime),
                        loading = false
                    )
                    onStartSleepForegroundService?.invoke()
                },
                onFailureCallback = {
                    Log.e(TAG, "fetchGetWakeupTime")
                    alertError()
                }
            )
        }
    }

//    private fun fakeFetchGetWakeupTime(
//        time: String?,
//        onStartSleepForegroundService: (() -> Unit)? = null
//    ){
//        viewModelScope.launch {
//            delay(300)
//            val data = GetWakeupTimeResponse(
//                wakeupTime = time
//            )
//
//            if (data.wakeupTime == null) {
//                _uiState.value = SleepUiState(
//                    wakeupTime = null,
//                    loading = false
//                )
//                return@launch
//            }
//
//            _uiState.value = SleepUiState(
//                wakeupTime = convertStringWakeupTimeToMyTime(data.wakeupTime),
//                loading = false
//            )
//            onStartSleepForegroundService?.invoke()
//        }
//    }

    fun fetchPutWakeupTime(
        time: MyTime,
        onStartSleepForegroundService: (() -> Unit)? = null
    ){
//        onStartSleepForegroundService?.invoke()
//        fakeFetchPutWakeupTime(time)
//        return

        viewModelScope.launch {
            putWakeupTime(
                accessToken = getData(ACCESS_TOKEN) ?: "",
                putWakeupTimeRequest = PutWakeupTimeRequest(
                    wakeupTime = convertMyTimeToStringWakeupTime(time)
                ),
                onSuccessCallback = { data: PutWakeupTimeResponse? ->
                    if (data?.wakeupTime == null) {
                        return@putWakeupTime
                    }
                    _uiState.value = SleepUiState(
                        wakeupTime = convertStringWakeupTimeToMyTime(data.wakeupTime),
                        loading = false
                    )
                    onStartSleepForegroundService?.invoke()
                },
                onFailureCallback = {
                    alertError()
                }
            )
        }
    }

//    private fun fakeFetchPutWakeupTime(time: MyTime){
//        viewModelScope.launch {
//            delay(300)
//            _uiState.value = SleepUiState(
//                wakeupTime = time,
//                loading = false
//            )
//        }
//    }

    fun fetchDeleteWakeupTime(
        onStopSleepForegroundService: (() -> Unit)? = null
    ){
//        onStopSleepForegroundService?.invoke()
//        fakeFetchDeleteWakeupTime()
//        return

        viewModelScope.launch {
            deleteWakeupTime(
                accessToken = getData(ACCESS_TOKEN) ?: "",
                onSuccessCallback = {
                    _uiState.value = SleepUiState(
                        wakeupTime = null,
                        loading = false
                    )
                    onStopSleepForegroundService?.invoke()
                },
                onFailureCallback = {
                    showToast("오류가 발생했습니다.")
                }
            )
        }
    }

//    private fun fakeFetchDeleteWakeupTime(){
//        viewModelScope.launch {
//            delay(300)
//            _uiState.value = SleepUiState(
//                wakeupTime = null,
//                loading = false
//            )
//        }
//    }
}

data class SleepUiState(
    val wakeupTime: MyTime? = null,
    val loading: Boolean = true
)

fun convertStringWakeupTimeToMyTime(stringWakeupTime: String): MyTime {
    val splitTime = stringWakeupTime.split(":")
    return MyTime(
        hour = splitTime[0].toInt(),
        minute = splitTime[1].toInt(),
        second = splitTime[2].toInt()
    )
}

fun convertMyTimeToStringWakeupTime(myTime: MyTime): String {
    val hour = myTime.hour
    val minute = myTime.minute
    val second = myTime.second
    return "${pad(hour)}:${pad(minute)}:${pad(second)}"
}

fun pad(t: Int): String {
    if (t < 10) {
        return "0${t}"
    }
    return "$t"
}

data class MyTime(
    val hour: Int,
    val minute: Int,
    val second: Int
)