package com.mimo.android.utils.os

import android.content.Context
import android.util.Log
import com.kakao.sdk.common.util.Utility
import com.mimo.android.MainActivity

const val TAG = "utils/os/MainActivity"

fun MainActivity.printKeyHash(context: Context){
    var keyHash = Utility.getKeyHash(context)
    Log.e(TAG, "기기 해시 키 : ${keyHash}")
}