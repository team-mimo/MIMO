package com.mimo.android.apis.devices.curtain

import retrofit2.Call
import retrofit2.http.*

interface CurtainApiService {
    @Headers("Content-Type: application/json")
    @GET("curtain/{curtainId}")
    fun getCurtain (
        @Header("X-AUTH-TOKEN") accessToken: String,
        @Path("curtainId") curtainId: Long
    ): Call<GetCurtainResponse>

    @Headers("Content-Type: application/json")
    @PUT("curtain")
    fun putCurtain(
        @Header("X-AUTH-TOKEN") accessToken: String,
        @Body putCurtainRequest: PutCurtainRequest
    ): Call<Unit>
}