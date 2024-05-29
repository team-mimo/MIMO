package com.mimo.android.apis.devices.curtain

import android.util.Log
import com.mimo.android.apis.common.OnResponseSuccessCallback
import com.mimo.android.apis.common.onResponseFailureCallback
import com.mimo.android.apis.mimoApiService
import com.mimo.android.apis.users.TAG
import retrofit2.Call

fun getCurtain(
    accessToken: String,
    curtainId: Long,
    onSuccessCallback: (OnResponseSuccessCallback<GetCurtainResponse>)? = null,
    onFailureCallback: (onResponseFailureCallback)? = null
){
    val call = mimoApiService.getCurtain(
        accessToken = accessToken,
        curtainId = curtainId
    )
    call.enqueue(object : retrofit2.Callback<GetCurtainResponse> {
        override fun onResponse(call: Call<GetCurtainResponse>, response: retrofit2.Response<GetCurtainResponse>) {
            Log.i(TAG, response.toString())
            if (response.isSuccessful) {
                onSuccessCallback?.invoke(response.body())
            } else {
                onFailureCallback?.invoke()
            }
        }

        override fun onFailure(call: Call<GetCurtainResponse>, t: Throwable) {
            Log.e(TAG, "Network request failed")
            onFailureCallback?.invoke()
        }
    })
}

data class GetCurtainResponse(
    val curtainId: Long,
    val nickname: String,
    val macAddress: String,
    val openDegree: Long,
    val isAccessible: Boolean
)