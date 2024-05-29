package com.mimo.android.apis.houses

import android.util.Log
import com.mimo.android.apis.common.OnResponseSuccessCallback
import com.mimo.android.apis.common.onResponseFailureCallback
import com.mimo.android.apis.mimoApiService
import retrofit2.Call

private const val TAG = "apis/houses/getDeviceListByHouseId"

fun getDeviceListByHouseId(
    accessToken: String,
    houseId: Long,
    onSuccessCallback: (OnResponseSuccessCallback<GetDeviceListByHouseIdResponse>)? = null,
    onFailureCallback: (onResponseFailureCallback)? = null
){
    val call = mimoApiService.getDeviceListByHouseId(
        accessToken = accessToken,
        houseId = houseId
    )
    call.enqueue(object : retrofit2.Callback<GetDeviceListByHouseIdResponse> {
        override fun onResponse(call: Call<GetDeviceListByHouseIdResponse>, response: retrofit2.Response<GetDeviceListByHouseIdResponse>) {
            if (response.isSuccessful) {
                onSuccessCallback?.invoke(response.body())
            } else {
                onFailureCallback?.invoke()
            }
        }

        override fun onFailure(call: Call<GetDeviceListByHouseIdResponse>, t: Throwable) {
            Log.e(TAG, "Network request failed")
            onFailureCallback?.invoke()
        }
    })
}