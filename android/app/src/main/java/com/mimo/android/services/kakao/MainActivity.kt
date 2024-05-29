package com.mimo.android.services.kakao

import android.content.Context
import com.kakao.sdk.common.KakaoSdk
import com.mimo.android.BuildConfig
import com.mimo.android.MainActivity

fun MainActivity.initializeKakaoSdk(context: Context){
    // TODO: KAKAO SDK 초기화, key 환경변수로 관리 필요(AndroidManifest도)
    KakaoSdk.init(context, BuildConfig.kakao_sdk_appkey)
}