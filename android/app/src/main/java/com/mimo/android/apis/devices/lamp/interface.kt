package com.mimo.android.apis.devices.lamp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PUT
import retrofit2.http.Path

interface LampApiService {
    @Headers("Content-Type: application/json")
    @GET("lamp/{lampId}")
    fun getLamp (
        @Header("X-AUTH-TOKEN") accessToken: String,
        @Path("lampId") lampId: Long
    ): Call<GetLampResponse>

    @Headers("Content-Type: application/json")
    @PUT("lamp")
    fun putLamp(
        @Header("X-AUTH-TOKEN") accessToken: String,
        @Body putLampRequest: PutLampRequest
    ): Call<Unit>
}