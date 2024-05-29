package com.mimo.android.apis.devices.window

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PUT
import retrofit2.http.Path

interface WindowApiService {
    @Headers("Content-Type: application/json")
    @GET("window/{windowId}")
    fun getWindow (
        @Header("X-AUTH-TOKEN") accessToken: String,
        @Path("windowId") windowId: Long
    ): Call<GetWindowResponse>

    @Headers("Content-Type: application/json")
    @PUT("window")
    fun putWindow(
        @Header("X-AUTH-TOKEN") accessToken: String,
        @Body putWindowRequest: PutWindowRequest
    ): Call<Unit>
}