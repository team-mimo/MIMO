package com.mimo.android.services.gogglelocation

import android.content.Context
import android.location.Geocoder
import java.util.Locale

fun getAddress(lat: Double, lng: Double, context: Context): String? {
    val geocoder = Geocoder(context, Locale.KOREA)
    val address = geocoder.getFromLocation(lat, lng, 1)
    if (!address.isNullOrEmpty()) {
        return removeFirstWord(address[0].getAddressLine(0).toString())
    }
    return null
}

fun removeFirstWord(input: String): String {
    val words = input.split(" ") // 공백을 기준으로 문자열을 분할하여 리스트로 만듭니다.
    return words.drop(1).joinToString(" ") // 첫 번째 단어를 제외한 나머지 단어를 결합합니다.
}