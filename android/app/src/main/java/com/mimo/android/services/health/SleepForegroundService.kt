package com.mimo.android.services.health

import com.mimo.android.R
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.mimo.android.apis.sleeps.postPhoneOn
import com.mimo.android.utils.preferences.ACCESS_TOKEN
import com.mimo.android.utils.preferences.getData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val TAG = "SleepForegroundService"

class SleepForegroundService: Service() {

    private val _stepFlow = MutableStateFlow<String?>(null)
    var stepFlow: StateFlow<String?> = _stepFlow

    // 핸드폰 동작여부 감지하기 위한 브로드캐스트 리시버
    private lateinit var screenOnReceiver: BroadcastReceiver

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> {
                stopSelf()
                unregisterReceiver(screenOnReceiver)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    // 핸드폰 화면 켜지는 거 인식해서 동작
    private fun initializeScreenOnReceiver() {
        screenOnReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_USER_PRESENT) {
                    // 여기에 "api/v1/sleep-data/phone-on" 으로 POST 요청만 보내면 됨!
                    Log.i(TAG, "사용자가 잠금해제해서 핸드폰을 켰음")
                    postPhoneOn(
                        accessToken = getData(ACCESS_TOKEN) ?: "",
                        onSuccessCallback = {},
                        onFailureCallback = {}
                    )
                }
            }
        }

        val filter = IntentFilter(Intent.ACTION_USER_PRESENT)
        registerReceiver(screenOnReceiver, filter)
    }

    private fun start(){
        initializeScreenOnReceiver()

        val notification = NotificationCompat
            .Builder(this, "running_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("MIMO")
            .setContentText("수면 감지 중")
            .build()

        startForeground(1, notification)
    }

    enum class Actions {
        START, STOP
    }
}