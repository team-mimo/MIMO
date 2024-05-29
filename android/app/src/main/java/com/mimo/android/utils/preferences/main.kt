package com.mimo.android.utils.preferences

fun saveData(key: String, value: String) {
    if (sharedPreferences == null) {
        throw Exception("MainActivity에서 createSharedPreferences() 함수를 호출해주세요")
    }
    val editor = sharedPreferences!!.edit()
    editor.putString(key, value)
    editor.apply()
}

fun getData(key: String): String? {
    if (sharedPreferences == null) {
        throw Exception("MainActivity에서 createSharedPreferences() 함수를 호출해주세요")
    }
    return sharedPreferences!!.getString(key, null)
}

fun removeData(key: String) {
    if (sharedPreferences == null) {
        throw Exception("MainActivity에서 createSharedPreferences() 함수를 호출해주세요")
    }
    val editor = sharedPreferences!!.edit()
    editor.remove(key)
    editor.apply()
}