package com.mimo.android.services.gogglelocation

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.mimo.android.MainActivity
import com.mimo.android.viewmodels.UserLocation

@SuppressLint("MissingPermission")
fun MainActivity.launchGoogleLocationAndAddress(
    cb: (userLocation: UserLocation?) -> Unit
) {
    val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
        .addOnSuccessListener { success: Location? ->
            success?.let { location ->
                val address = getAddress(
                    lat = location.latitude,
                    lng = location.longitude,
                    context = this
                )
                cb(
                    UserLocation(
                    location = location,
                    address = address
                )
                )
            }
        }
        .addOnFailureListener { fail ->
            cb(null)
        }
}