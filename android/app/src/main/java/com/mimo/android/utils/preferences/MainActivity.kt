package com.mimo.android.utils.preferences

import android.content.Context
import android.content.SharedPreferences
import com.mimo.android.MainActivity

var sharedPreferences: SharedPreferences? = null

fun MainActivity.createSharedPreferences(){
    if (sharedPreferences == null) {
        sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    }
}