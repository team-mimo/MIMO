package com.mimo.android.apis.users

import retrofit2.Call
import retrofit2.http.*

interface UsersApiService {
    @Headers("Content-Type: application/json")
    @POST("auth")
    fun postAccessToken(
        @Body accessTokenRequest: PostAccessTokenRequest
    ): Call<PostAccessTokenResponse>

    @Headers("Content-Type: application/json")
    @GET("user/myInfo")
    fun getMyInfo(
        @Header("X-AUTH-TOKEN") accessToken: String
    ): Call<GetMyInfoResponse>

    @Headers("Content-Type: application/json")
    @PUT("user/wakeup-time")
    fun putWakeupTime(
        @Header("X-AUTH-TOKEN") accessToken: String,
        @Body putWakeupTimeRequest: PutWakeupTimeRequest
    ): Call<PutWakeupTimeResponse>

    @Headers("Content-Type: application/json")
    @GET("user/wakeup-time")
    fun getWakeupTime(
        @Header("X-AUTH-TOKEN") accessToken: String,
    ): Call<GetWakeupTimeResponse>

    @Headers("Content-Type: application/json")
    @DELETE("user/wakeup-time")
    fun deleteWakeupTime(
        @Header("X-AUTH-TOKEN") accessToken: String,
    ): Call<Unit>
}