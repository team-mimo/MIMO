package com.mimo.android.views.main.sleep

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mimo.android.MainActivity
import com.mimo.android.components.Button
import com.mimo.android.components.HeadingSmall
import com.mimo.android.components.Timer
import com.mimo.android.ui.theme.Teal100
import com.mimo.android.viewmodels.SleepViewModel
import com.mimo.android.R
import com.mimo.android.utils.showToast
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import com.mimo.android.apis.sleeps.PostSleepDataRequest
import com.mimo.android.apis.sleeps.postSleepData
import com.mimo.android.utils.preferences.ACCESS_TOKEN
import com.mimo.android.utils.preferences.getData
import com.mimo.android.viewmodels.SleepUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

private const val TAG = "OnSleepView"

@Composable
fun OnSleepView(
    sleepViewModel: SleepViewModel,
    onRemoveWakeupTime: () -> Unit,
){
    val sleepUiState by sleepViewModel.uiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val maxVolume = 1f

    var job: Job? = null
    var volume by remember { mutableStateOf(0.1f) } // 볼륨 상태 추가
    val mediaPlayer = remember {
        MediaPlayer.create(MainActivity.getMainActivityContext(), R.raw.samsung_over_the_horizon).apply {
            setVolume(volume, volume) // 초기 볼륨 설정
        }
    }

    fun playMusic() {
        mediaPlayer.start()
        job = coroutineScope.launch {
            postSleepData(
                accessToken = getData(ACCESS_TOKEN) ?: "",
                postSleepDataRequest = PostSleepDataRequest(
                    sleepLevel = 6
                ),
                onSuccessCallback = { showToast("MIMO 시작") },
                onFailureCallback = {
                    showToast("오류가 발생했어요.")
                }
            )

            while (true) {
                delay(5000)
                Log.i(com.mimo.android.views.main.sleep.TAG,"현재 볼륨 : ${volume}")
                if (volume < maxVolume) {
                    volume += 0.1f // 볼륨 10% 증가
                    if (volume > maxVolume) volume = maxVolume
                    mediaPlayer.setVolume(volume, volume)
                }
            }
        }
    }

    fun stopMusic() {
        job?.cancel()
        mediaPlayer.stop()
        mediaPlayer.prepare()
    }

    fun handleStopSleep(){
        stopMusic()
        onRemoveWakeupTime()
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    Column {
        Row(
            modifier = Modifier.clickable {
                playMusic() // TODO: 시연을 위해 fake button
            }
        ) {
            HeadingSmall(text = makeTextTimeMinus30Minutes(
                hour = sleepUiState.wakeupTime!!.hour,
                minute = sleepUiState.wakeupTime!!.minute
            ), color = Teal100
            )
            HeadingSmall(text = "부터 MIMO 알람이 울려요")
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            HeadingSmall(text = "MIMO 알람까지 남은 시간")
        }
        Spacer(modifier = Modifier.padding(8.dp))
        Timer(
            targetTime = getMinus30MinutesLocalTime(sleepUiState.wakeupTime!!.hour, sleepUiState.wakeupTime!!.minute, sleepUiState.wakeupTime!!.second),
            onFinish = ::playMusic
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Button(text = "수면 끝", onClick = ::handleStopSleep, width = 300.dp)
        }
        Spacer(modifier = Modifier.weight(2f))
        Spacer(modifier = Modifier.padding(44.dp))
    }
}