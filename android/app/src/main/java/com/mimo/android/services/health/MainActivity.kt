package com.mimo.android.services.health

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.NotificationManagerCompat
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.lifecycleScope
import com.mimo.android.MainActivity
import com.mimo.android.R
import kotlinx.coroutines.launch

fun MainActivity.checkAvailability(): Boolean {
    val healthConnectSdkStatus = HealthConnectClient.getSdkStatus(this)

    if (healthConnectSdkStatus == HealthConnectClient.SDK_UNAVAILABLE) {
        runOnUiThread {
            Toast.makeText(
                this,
                R.string.not_supported_description,
                Toast.LENGTH_SHORT
            ).show()
        }
        return false
    }

    if (healthConnectSdkStatus == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) {
        runOnUiThread {
            Toast.makeText(
                this,
                R.string.not_installed_description,
                Toast.LENGTH_SHORT
            ).show()
        }
        return false
    }

    return true
}

fun MainActivity.createHealthConnectPermissionRequest(
    healthConnectManager: HealthConnectManager,
    context: Context
): ActivityResultLauncher<Set<String>> {
    return registerForActivityResult(healthConnectManager.requestPermissionActivityContract) { granted ->
        lifecycleScope.launch {
            if (granted.isNotEmpty() && healthConnectManager.hasAllPermissions()) {
                Toast.makeText(
                    context,
                    R.string.permission_granted,
                    Toast.LENGTH_SHORT,
                ).show()
            } else {
                AlertDialog.Builder(context)
                    .setMessage(R.string.permission_denied)
                    .setPositiveButton(R.string.ok, null)
                    .show()
            }
        }
    }
}

fun MainActivity.checkHealthConnectPermission(
    showInfo: Boolean,
    healthConnectManager: HealthConnectManager,
    context: Context,
    healthConnectPermissionRequest: ActivityResultLauncher<Set<String>>
) {
    lifecycleScope.launch {
        try {
            if (healthConnectManager.hasAllPermissions()) {
                if (showInfo) {
                    Toast.makeText(
                        context,
                        R.string.permission_granted,
                        Toast.LENGTH_LONG,
                    ).show()
                }
            } else {
                Toast.makeText(
                    context,
                    R.string.permission_denied,
                    Toast.LENGTH_LONG,
                ).show()
                healthConnectPermissionRequest.launch(healthConnectManager.permissions)
            }
        } catch (exception: Exception) {
            Log.e("MainActivity: Health Permission", exception.toString())
            Toast.makeText(context, "Error: $exception", Toast.LENGTH_LONG)
                .show()
        }
    }
}

fun MainActivity.isSleepNotificationPermissionGranted(): Boolean {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.isNotificationListenerAccessGranted(ComponentName(application, SleepNotificationListenerService::class.java))
    }
    else {
        return NotificationManagerCompat.getEnabledListenerPackages(applicationContext).contains(applicationContext.packageName)
    }
}