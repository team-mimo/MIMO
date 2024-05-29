package com.mimo.android.apis.devices.window

import android.util.Log
import com.mimo.android.apis.common.OnResponseSuccessCallback
import com.mimo.android.apis.common.onResponseFailureCallback
import com.mimo.android.apis.devices.light.PutLightRequest
import com.mimo.android.apis.mimoApiService
import com.mimo.android.apis.users.TAG
import retrofit2.Call

fun putWindow(
    accessToken: String,
    putWindowRequest: PutWindowRequest,
    onSuccessCallback: (OnResponseSuccessCallback<Unit>)? = null,
    onFailureCallback: (onResponseFailureCallback)? = null
){
    val call = mimoApiService.putWindow(
        accessToken = accessToken,
        putWindowRequest = putWindowRequest
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

data class PutWindowRequest(
    val windowId: Long,
    val nickname: String,
    val openDegree: Long,
    val isAccessible: Boolean
)