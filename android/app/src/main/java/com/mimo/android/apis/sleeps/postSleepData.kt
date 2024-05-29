package com.mimo.android.apis.sleeps

import android.util.Log
import com.mimo.android.apis.common.OnResponseSuccessCallback
import com.mimo.android.apis.common.onResponseFailureCallback
import com.mimo.android.apis.mimoApiService
import retrofit2.Call

private const val TAG = "/apis/sleep/postSleepData"

fun postSleepData(
    accessToken: String,
    postSleepDataRequest: PostSleepDataRequest,
    onSuccessCallback: (OnResponseSuccessCallback<Unit>)? = null,
    onFailureCallback: (onResponseFailureCallback)? = null
){
    val call = mimoApiService.postSleepData(
        accessToken = accessToken,
        postSleepDataRequest = postSleepDataRequest
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

data class PostSleepDataRequest(
    val sleepLevel: Int
)