package com.mimo.android.apis.devices.lamp

import android.util.Log
import com.mimo.android.apis.common.OnResponseSuccessCallback
import com.mimo.android.apis.common.onResponseFailureCallback
import com.mimo.android.apis.mimoApiService
import com.mimo.android.apis.users.TAG
import retrofit2.Call

fun putLamp(
    accessToken: String,
    putLampRequest: PutLampRequest,
    onSuccessCallback: (OnResponseSuccessCallback<Unit>)? = null,
    onFailureCallback: (onResponseFailureCallback)? = null
){
    val call = mimoApiService.putLamp(
        accessToken = accessToken,
        putLampRequest = putLampRequest
    )
    call.enqueue(object : retrofit2.Callback<Unit> {
        override fun onResponse(call: Call<Unit>, response: retrofit2.Response<Unit>) {
            Log.i(TAG, response.toString())
            if (response.isSuccessful) {
                onSuccessCallback?.invoke(response.body())
            } else {
                onFailureCallback?.invoke()
            }
        }

        override fun onFailure(call: Call<Unit>, t: Throwable) {
            Log.e(TAG, "Network request failed")
            onFailureCallback?.invoke()
        }
    })
}

data class PutLampRequest(
    val lampId: Long,
    val nickname: String,
    val wakeupColor: String,
    val curColor: String,
    val isAccessible: Boolean
)