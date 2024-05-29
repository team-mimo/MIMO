package com.mimo.android.apis.users

import android.util.Log
import com.mimo.android.apis.common.OnResponseSuccessCallback
import com.mimo.android.apis.common.onResponseFailureCallback
import com.mimo.android.apis.mimoApiService
import retrofit2.Call

fun getWakeupTime(
    accessToken: String,
    onSuccessCallback: (OnResponseSuccessCallback<GetWakeupTimeResponse>)? = null,
    onFailureCallback: (onResponseFailureCallback)? = null
){
    val call = mimoApiService.getWakeupTime(accessToken = accessToken)
    call.enqueue(object : retrofit2.Callback<GetWakeupTimeResponse> {
        override fun onResponse(call: Call<GetWakeupTimeResponse>, response: retrofit2.Response<GetWakeupTimeResponse>) {
            Log.i(TAG, response.toString())
            if (response.isSuccessful) {
                onSuccessCallback?.invoke(response.body())
            } else {
                onFailureCallback?.invoke()
            }
        }

        override fun onFailure(call: Call<GetWakeupTimeResponse>, t: Throwable) {
            Log.e(TAG, "Network request failed")
            onFailureCallback?.invoke()
        }
    })
}

data class GetWakeupTimeResponse(
    val wakeupTime: String?
)