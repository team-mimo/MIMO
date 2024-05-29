package com.mimo.android.apis.devices.light

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PUT
import retrofit2.http.Path

interface LightApiService {
    @Headers("Content-Type: application/json")
    @GET("light/{lightId}")
    fun getLight (
        @Header("X-AUTH-TOKEN") accessToken: String,
        @Path("lightId") lightId: Long
    ): Call<GetLightResponse>

    @Headers("Content-Type: application/json")
    @PUT("light")
    fun putLight(
        @Header("X-AUTH-TOKEN") accessToken: String,
        @Body putLightRequest: PutLightRequest
    ): Call<Unit>
}