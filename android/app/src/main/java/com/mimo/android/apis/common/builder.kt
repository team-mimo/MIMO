package com.mimo.android.apis.common

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

inline fun <reified ApiService> createApiService(baseUrl: String): ApiService {
    // 로깅 인터셉터 생성
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // 로그 수준 설정 (BODY: 요청 및 응답 헤더와 본문)
    }

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl) // 예시 API baseURL
        .addConverterFactory(GsonConverterFactory.create()) // JSON 파서 지정
        .client(okHttpClient)
        .build()

    return retrofit.create(ApiService::class.java)
}