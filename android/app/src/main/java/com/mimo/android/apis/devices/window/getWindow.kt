package com.mimo.android.apis.devices.window

import android.util.Log
import com.mimo.android.apis.common.OnResponseSuccessCallback
import com.mimo.android.apis.common.onResponseFailureCallback
import com.mimo.android.apis.mimoApiService
import com.mimo.android.apis.users.TAG
import retrofit2.Call

fun getWindow(
    accessToken: String,
    windowId: Long,
    onSuccessCallback: (OnResponseSuccessCallback<GetWindowResponse>)? = null,
    onFailureCallback: (onResponseFailureCallback)? = null
){
    val call = mimoApiService.getWindow(
        accessToken = accessToken,
        windowId = windowId
    )
    call.enqueue(object : retrofit2.Callback<GetWindowResponse> {
        override fun onResponse(call: Call<GetWindowResponse>, response: retrofit2.Response<GetWindowResponse>) {
            Log.i(TAG, response.toString())
            if (response.isSuccessful) {
                onSuccessCallback?.invoke(response.body())
            } else {
                onFailureCallback?.invoke()
            }
        }

        override fun onFailure(call: Call<GetWindowResponse>, t: Throwable) {
            Log.e(TAG, "Network request failed")
            onFailureCallback?.invoke()
        }
    })
}

data class GetWindowResponse(
    val windowId: Long,
    val nickname: String,
    val macAddress: String,
    val openDegree: Long,
    val isAccessible: Boolean
)