package com.mimo.android.apis.controls

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ControlsApiService {
    @Headers("Content-Type: application/json")
    @POST("control")
    fun postControl(
        @Header("X-AUTH-TOKEN") accessToken: String,
        @Body postControlRequest: PostControlRequest
    ): Call<Unit>
}