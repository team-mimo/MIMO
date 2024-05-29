package com.mimo.android.apis.devices.light

import android.util.Log
import com.mimo.android.apis.common.OnResponseSuccessCallback
import com.mimo.android.apis.common.onResponseFailureCallback
import com.mimo.android.apis.mimoApiService
import com.mimo.android.apis.users.TAG
import retrofit2.Call

fun getLight(
    accessToken: String,
    lightId: Long,
    onSuccessCallback: (OnResponseSuccessCallback<GetLightResponse>)? = null,
    onFailureCallback: (onResponseFailureCallback)? = null
){
    val call = mimoApiService.getLight(
        accessToken = accessToken,
        lightId = lightId
    )
    call.enqueue(object : retrofit2.Callback<GetLightResponse> {
        override fun onResponse(call: Call<GetLightResponse>, response: retrofit2.Response<GetLightResponse>) {
            Log.i(TAG, response.toString())
            if (response.isSuccessful) {
                onSuccessCallback?.invoke(response.body())
            } else {
                onFailureCallback?.invoke()
            }
        }

        override fun onFailure(call: Call<GetLightResponse>, t: Throwable) {
            Log.e(TAG, "Network request failed")
            onFailureCallback?.invoke()
        }
    })
}

data class GetLightResponse(
    val lightId: Long,
    val nickname: String,
    val wakeupColor: String,
    val curColor: String,
    val macAddress: String,
    val isAccessible: Boolean
)