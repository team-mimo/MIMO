package com.mimo.android.apis.devices.curtain

import android.util.Log
import com.mimo.android.apis.common.OnResponseSuccessCallback
import com.mimo.android.apis.common.onResponseFailureCallback
import com.mimo.android.apis.mimoApiService
import com.mimo.android.apis.users.TAG
import retrofit2.Call

fun putCurtain(
    accessToken: String,
    putCurtainRequest: PutCurtainRequest,
    onSuccessCallback: (OnResponseSuccessCallback<Unit>)? = null,
    onFailureCallback: (onResponseFailureCallback)? = null
){
    val call = mimoApiService.putCurtain(
        accessToken = accessToken,
        putCurtainRequest = putCurtainRequest
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

data class PutCurtainRequest(
    val curtainId: Long,
    val nickname: String,
    val openDegree: Long,
    val isAccessible: Boolean
)