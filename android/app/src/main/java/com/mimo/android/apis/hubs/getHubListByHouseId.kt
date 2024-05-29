package com.mimo.android.apis.hubs

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.mimo.android.apis.common.OnResponseSuccessCallback
import com.mimo.android.apis.common.onResponseFailureCallback
import com.mimo.android.apis.mimoApiService
import retrofit2.Call

private const val TAG = "apis/hubs/getHubListByHouseId"

fun getHubListByHouseId(
    accessToken: String,
    houseId: Long,
    onSuccessCallback: (OnResponseSuccessCallback<List<Hub>>)? = null,
    onFailureCallback: (onResponseFailureCallback)? = null
){
    val call = mimoApiService.getHubListByHouseId(
        accessToken = accessToken,
        houseId = houseId
    )
    call.enqueue(object : retrofit2.Callback<List<Hub>> {
        override fun onResponse(call: Call<List<Hub>>, response: retrofit2.Response<List<Hub>>) {
            if (response.isSuccessful) {
                onSuccessCallback?.invoke(response.body())
            } else {
                onFailureCallback?.invoke()
            }
        }

        override fun onFailure(call: Call<List<Hub>>, t: Throwable) {
            Log.e(TAG, "Network request failed")
            onFailureCallback?.invoke()
        }
    })
}

data class Hub(
    val hubId: Long,
    val serialNumber: String,
    val registeredDttm: String,
    val devices: List<SimpleDevice>
)

data class SimpleDevice(
    val deviceId: Long,
    val macAddress: String,
    val deviceType: String
)