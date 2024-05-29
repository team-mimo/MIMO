package com.mimo.android.apis.hubs

import retrofit2.Call
import retrofit2.http.*

interface HubsApiService {
    @Headers("Content-Type: application/json")
    @POST("hubs")
    fun postRegisterHubToHouse(
        @Header("X-AUTH-TOKEN") accessToken: String,
        @Body postRegisterHubToHomeRequest: PostRegisterHubToHouseRequest
    ): Call<Unit>

    @Headers("Content-Type: application/json")
    @GET("hubs/list")
    fun getHubListByHouseId(
        @Header("X-AUTH-TOKEN") accessToken: String,
        @Query("houseId") houseId: Long
    ): Call<List<Hub>>
}