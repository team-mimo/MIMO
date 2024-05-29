package com.mimo.android.apis.hubs

import android.util.Log
import retrofit2.Call
import com.mimo.android.apis.common.OnResponseSuccessCallback
import com.mimo.android.apis.common.onResponseFailureCallback
import com.mimo.android.apis.mimoApiService

private const val TAG = "/apis/mimo/hubs"

fun postRegisterHubToHouse(
    accessToken: String,
    postRegisterHubToHomeRequest: PostRegisterHubToHouseRequest,
    onSuccessCallback: (OnResponseSuccessCallback<Unit>)? = null,
    onFailureCallback: (onResponseFailureCallback)? = null
){
    val call = mimoApiService.postRegisterHubToHouse(
        accessToken = accessToken,
        postRegisterHubToHomeRequest = postRegisterHubToHomeRequest
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