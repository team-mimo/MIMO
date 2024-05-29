package com.mimo.android.services.kakao

import android.content.Context
import android.util.Log
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient

const val TAG = "services/kakao/oauth"

fun loginWithKakao(
    context: Context,
    onSuccessCallback: ((oauthToken: OAuthToken) -> Unit)? = null,
    onFailureCallback: (() -> Unit)? = null
){
// 카카오계정으로 로그인 공통 callback 구성
// 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "카카오계정으로 로그인 실패", error)
            onFailureCallback?.invoke()
        } else if (token != null) {
            Log.i(TAG, "카카오계정으로 로그인 성공 accessToken = ${token.accessToken}")
            onSuccessCallback?.invoke(token)
        }
    }

    // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            if (error != null) {
                Log.e(TAG, "카카오톡으로 로그인 실패", error)

                // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    return@loginWithKakaoTalk
                }

                // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            } else if (token != null) {
                Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                onSuccessCallback?.invoke(token)
            }
        }
    } else {
        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
    }
}

fun logoutWithKakao(
    callback: (() -> Unit)? = null
){
    callback?.invoke()

    // 로그아웃
    UserApiClient.instance.logout { error ->
        if (error != null) {
            Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
        }
        else {
            Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
        }
    }
}