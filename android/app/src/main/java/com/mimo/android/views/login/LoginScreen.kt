package com.mimo.android.views.login

import com.mimo.android.R
import com.mimo.android.components.*
import com.mimo.android.components.base.Size
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mimo.android.MainActivity
import com.mimo.android.apis.users.postAccessToken
import com.mimo.android.services.kakao.loginWithKakao
import com.mimo.android.ui.theme.*
import com.mimo.android.utils.showToast
import com.mimo.android.viewmodels.AuthViewModel
import com.mimo.android.viewmodels.FirstSettingFunnelsViewModel

private const val TAG = "LoginScreen"

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    firstSettingFunnelsViewModel: FirstSettingFunnelsViewModel
){
    var loading by remember { mutableStateOf(false) }

    fun handleLoginWithKakao(){
        if (loading) {
            showToast("잠시만 기다려주세요")
            return
        }
        loading = true
        loginWithKakao(
            context = MainActivity.getMainActivityContext(),
            onSuccessCallback = { oauthToken ->
                Log.i(TAG, "kakao accessToken=${oauthToken.accessToken}")
                postAccessToken(
                    accessToken = oauthToken.accessToken,
                    onSuccessCallback = { data ->
                        if (data == null) {
                            Log.e(TAG, "데이터가 없음...")
                            return@postAccessToken
                        }
                        Log.i(TAG, "우리 토큰 받아오기 성공!!!! ${data.accessToken}")
                        authViewModel.login(
                            accessToken = data.accessToken,
                            firstSettingFunnelsViewModel = firstSettingFunnelsViewModel
                        )
                        showToast("로그인 되었습니다.")
                        loading = false
                    },
                    onFailureCallback = {
                        showToast("다시 로그인 해주세요.")
                        loading = false
                    }
                )
            },
            onFailureCallback = {
                showToast("카카오 로그인 실패")
                loading = false
            }
        )
    }

    _LoginScreen(onLoginWithKakao = ::handleLoginWithKakao)
}

@Preview
@Composable
private fun _LoginScreen(
    onLoginWithKakao: (() -> Unit)? = null
){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "당신의 쾌적한 아침을 위한")
        HeadingLarge(text = "MIMO", fontSize = Size.xl2, color = Teal100)

        Spacer(modifier = Modifier.padding(8.dp))

        Box(){
            SocialLoginButton(
                onClick = { onLoginWithKakao?.invoke() },
                desc = "KaKao",
                imageResource = R.drawable.kakao_login_large_narrow
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun SocialLoginButton(
    onClick: (() -> Unit)? = null,
    desc: String,
    imageResource: Int
){
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = imageResource),
            contentDescription = "${desc} Login Button",
            modifier = Modifier
                .width(330.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp)) // border-radius를 16dp로 설정
                .clickable { onClick?.invoke() }
        )
    }
}

@Preview
@Composable
private fun _LoginScreenPreview(){
    _LoginScreen()
}