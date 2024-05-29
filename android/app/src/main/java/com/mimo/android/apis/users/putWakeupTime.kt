package com.mimo.android.apis.users

import android.util.Log
import com.mimo.android.apis.common.OnResponseSuccessCallback
import com.mimo.android.apis.common.onResponseFailureCallback
import com.mimo.android.apis.mimoApiService
import retrofit2.Call

fun putWakeupTime(
    accessToken: String,
    putWakeupTimeRequest: PutWakeupTimeRequest,
    onSuccessCallback: (OnResponseSuccessCallback<PutWakeupTimeResponse>)? = null,
    onFailureCallback: (onResponseFailureCallback)? = null
){
    val call = mimoApiService.putWakeupTime(
        accessToken = accessToken,
        putWakeupTimeRequest = putWakeupTimeRequest
    )
    call.enqueue(object : retrofit2.Callback<PutWakeupTimeResponse> {
        override fun onResponse(call: Call<PutWakeupTimeResponse>, response: retrofit2.Response<PutWakeupTimeResponse>) {
            Log.i(TAG, response.toString())
            if (response.isSuccessful) {
                onSuccessCallback?.invoke(response.body())
            } else {
                onFailureCallback?.invoke()
            }
        }

        override fun onFailure(call: Call<PutWakeupTimeResponse>, t: Throwable) {
            Log.e(TAG, "Network request failed")
            onFailureCallback?.invoke()
        }
    })
}

data class PutWakeupTimeRequest(
    val wakeupTime: String?
)

data class PutWakeupTimeResponse(
    val wakeupTime: String?
)