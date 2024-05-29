package com.mimo.android.apis.users

data class PostAccessTokenRequest(
    val accessToken: String
)
data class PostAccessTokenResponse(
    val accessToken: String
)

data class GetMyInfoResponse(
    val userId: Int,
    val hasHome: Boolean,
    val hasHub: Boolean
)