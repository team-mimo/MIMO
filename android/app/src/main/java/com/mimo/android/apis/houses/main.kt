package com.mimo.android.apis.houses

import android.util.Log
import com.mimo.android.apis.common.OnResponseSuccessCallback
import com.mimo.android.apis.common.onResponseFailureCallback
import com.mimo.android.apis.mimoApiService
import retrofit2.Call

private const val TAG = "apis/houses/main"

fun postRegisterHouse(
    accessToken: String,
    postRegisterHouseRequest: PostRegisterHouseRequest,
    onSuccessCallback: (OnResponseSuccessCallback<PostRegisterHouseResponse>)? = null,
    onFailureCallback: (onResponseFailureCallback)? = null
){
    val call = mimoApiService.postRegisterHouse(
        accessToken = accessToken,
        postRegisterHouseRequest = postRegisterHouseRequest
    )
    call.enqueue(object : retrofit2.Callback<PostRegisterHouseResponse> {
        override fun onResponse(call: Call<PostRegisterHouseResponse>, response: retrofit2.Response<PostRegisterHouseResponse>) {
            Log.i(TAG, response.toString())
            if (response.isSuccessful) {
                onSuccessCallback?.invoke(response.body())
            } else {
                onFailureCallback?.invoke()
            }
        }

        override fun onFailure(call: Call<PostRegisterHouseResponse>, t: Throwable) {
            Log.e(TAG, "Network request failed")
            onFailureCallback?.invoke()
        }
    })
}

fun postAutoRegisterHubToHouse(
    accessToken: String,
    postRegisterHubToHouseRequest: PostAutoRegisterHubToHouseRequest,
    onSuccessCallback: (OnResponseSuccessCallback<PostAutoRegisterHubToHouseResponse>)? = null,
    onFailureCallback: (onResponseFailureCallback)? = null
){
    val call = mimoApiService.postAutoRegisterHubToHouse(
        accessToken = accessToken,
        postRegisterHubToHouseRequest = postRegisterHubToHouseRequest
    )
    call.enqueue(object : retrofit2.Callback<PostAutoRegisterHubToHouseResponse> {
        override fun onResponse(call: Call<PostAutoRegisterHubToHouseResponse>, response: retrofit2.Response<PostAutoRegisterHubToHouseResponse>) {
            Log.i(TAG, response.toString())
            if (response.isSuccessful) {
                onSuccessCallback?.invoke(response.body())
            } else {
                onFailureCallback?.invoke()
            }
        }

        override fun onFailure(call: Call<PostAutoRegisterHubToHouseResponse>, t: Throwable) {
            Log.e(TAG, "Network request failed")
            onFailureCallback?.invoke()
        }
    })
}

fun getHouseList(
    accessToken: String,
    onSuccessCallback: (OnResponseSuccessCallback<List<House>>)? = null,
    onFailureCallback: (onResponseFailureCallback)? = null
){
    val call = mimoApiService.getHouseList(accessToken = accessToken)
    call.enqueue(object : retrofit2.Callback<List<House>> {
        override fun onResponse(call: Call<List<House>>, response: retrofit2.Response<List<House>>) {
            if (response.isSuccessful) {
                onSuccessCallback?.invoke(response.body())
            } else {
                onFailureCallback?.invoke()
            }
        }

        override fun onFailure(call: Call<List<House>>, t: Throwable) {
            Log.e(TAG, "Network request failed")
            onFailureCallback?.invoke()
        }
    })
}

fun putChangeHouseNickname(
    accessToken: String,
    houseId: Long,
    putChangeHouseNicknameRequest: PutChangeHouseNicknameRequest,
    onSuccessCallback: (OnResponseSuccessCallback<Unit>)? = null,
    onFailureCallback: (onResponseFailureCallback)? = null
){
    val call = mimoApiService.putChangeHouseNickname(
        accessToken = accessToken,
        houseId = houseId,
        putChangeHouseNicknameRequest = putChangeHouseNicknameRequest
    )
    call.enqueue(object : retrofit2.Callback<Unit> {
        override fun onResponse(call: Call<Unit>, response: retrofit2.Response<Unit>) {
            if (response.isSuccessful) {
                onSuccessCallback?.invoke(response.body())
            } else {
                onFailureCallback?.invoke()
            }
        }

        override fun onFailure(call: Call<Unit>, t: Throwable) {
            Log.e(TAG, "putChangeHouseNickname")
            onFailureCallback?.invoke()
        }
    })
}

fun putChangeCurrentHouse(
    accessToken: String,
    houseId: Long,
    onSuccessCallback: (OnResponseSuccessCallback<Unit>)? = null,
    onFailureCallback: (onResponseFailureCallback)? = null
){
    val call = mimoApiService.putChangeCurrentHouse(
        accessToken = accessToken,
        houseId = houseId
    )
    call.enqueue(object : retrofit2.Callback<Unit> {
        override fun onResponse(call: Call<Unit>, response: retrofit2.Response<Unit>) {
            if (response.isSuccessful) {
                onSuccessCallback?.invoke(response.body())
            } else {
                onFailureCallback?.invoke()
            }
        }

        override fun onFailure(call: Call<Unit>, t: Throwable) {
            Log.e(TAG, "putChangeCurrentHouse")
            onFailureCallback?.invoke()
        }
    })
}