package com.mimo.android.apis.devices.lamp

import android.util.Log
import com.mimo.android.apis.common.OnResponseSuccessCallback
import com.mimo.android.apis.common.onResponseFailureCallback
import com.mimo.android.apis.devices.curtain.GetCurtainResponse
import com.mimo.android.apis.mimoApiService
import com.mimo.android.apis.users.TAG
import retrofit2.Call

fun getLamp(
    accessToken: String,
    lampId: Long,
    onSuccessCallback: (OnResponseSuccessCallback<GetLampResponse>)? = null,
    onFailureCallback: (onResponseFailureCallback)? = null
){
    val call = mimoApiService.getLamp(
        accessToken = accessToken,
        lampId = lampId
    )
    call.enqueue(object : retrofit2.Callback<GetLampResponse> {
        override fun onResponse(call: Call<GetLampResponse>, response: retrofit2.Response<GetLampResponse>) {
            Log.i(TAG, response.toString())
            if (response.isSuccessful) {
                onSuccessCallback?.invoke(response.body())
            } else {
                onFailureCallback?.invoke()
            }
        }

        override fun onFailure(call: Call<GetLampResponse>, t: Throwable) {
            Log.e(TAG, "Network request failed")
            onFailureCallback?.invoke()
        }
    })
}

data class GetLampResponse(
    val lampId: Long,
    val nickname: String,
    val wakeupColor: String,
    val curColor: String,
    val macAddress: String,
    val isAccessible: Boolean
)