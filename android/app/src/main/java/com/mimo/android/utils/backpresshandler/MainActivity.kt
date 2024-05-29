package com.mimo.android.utils.backpresshandler

import android.content.Context
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.mimo.android.MainActivity

fun MainActivity.initializeWhenTwiceBackPressExitApp(context: Context){
    // 뒤로가기 2번 눌러 앱 종료하기
    // jetpack compose의 BackHandler의 우선순위가 더 높기 때문에 앱종료시키고 싶지 않다면 BackHandler를 정의하면 됨
    // 네비게이션으로 이동한 상태여도 네비게이션 뒤로가기 우선순위가 더 높음
    var backKeyPressedTime = 0L
    val onBackPressedCallback: OnBackPressedCallback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis()
                Toast.makeText(
                    context,
                    "뒤로 버튼을 한번 더 누르시면 종료됩니다.",
                    Toast.LENGTH_SHORT).show()
            } else {
                finish()
            }
        }
    }

    onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
}