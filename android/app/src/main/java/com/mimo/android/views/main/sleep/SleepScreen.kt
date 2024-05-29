package com.mimo.android.views.main.sleep

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mimo.android.components.*
import com.mimo.android.components.base.Size
import com.mimo.android.ui.theme.Teal100
import com.mimo.android.utils.showToast
import com.mimo.android.viewmodels.MyHouseViewModel
import com.mimo.android.viewmodels.MyTime
import com.mimo.android.viewmodels.SleepViewModel
import java.time.LocalTime

private const val TAG = "SleepScreen"

@Composable
fun SleepScreen(
    navController: NavHostController? = null,
    isActiveSleepForegroundService: Boolean,
    onStartSleepForegroundService: (() -> Unit)? = null,
    onStopSleepForegroundService: (() -> Unit)? = null,
    myHouseViewModel: MyHouseViewModel,
    sleepViewModel: SleepViewModel
){
    val sleepUiState by sleepViewModel.uiState.collectAsState()
    val currentHouseNickname = myHouseViewModel.getCurrentHouse()?.nickname

    var selectedHour by remember { mutableStateOf(7) }
    var selectedMinute by remember { mutableStateOf(30) }

    LaunchedEffect(Unit) {
        sleepViewModel.fetchGetWakeupTime(
            onStartSleepForegroundService = onStartSleepForegroundService
        )
    }

    fun handleSetWakeupTime(){
//        val minus30MinutesLocalTime = getMinus30MinutesLocalTime(selectedHour, selectedMinute, 0)
//        if (calculateRemainingTime(minus30MinutesLocalTime).toInt() == 0) {
//            showToast("30분 전 시간을 설정해주세요")
//            return
//        }

        sleepViewModel.fetchPutWakeupTime(
            time = MyTime(
                hour = selectedHour,
                minute = selectedMinute,
                second = 0
            ),
            onStartSleepForegroundService = onStartSleepForegroundService,
        )
    }

    fun handleRemoveWakeupTime(){
        sleepViewModel.fetchDeleteWakeupTime(
            onStopSleepForegroundService = onStopSleepForegroundService
        )
    }

    Column {
        HeadingLarge(text = "수면과 기상", fontSize = Size.lg)

        if (sleepUiState.loading) {
            LinearProgressbar()
        } else {
            Spacer(modifier = Modifier.padding(14.dp))
            if (currentHouseNickname != null) {
                HorizontalScroll {
                    HeadingSmall(text = "$currentHouseNickname", color = Teal100)
                    HeadingSmall(text = "에서")
                }
            }
            Spacer(modifier = Modifier.padding(4.dp))
            if (sleepUiState.wakeupTime == null) {
                Row {
                    HeadingSmall(text = "기상 30분 전", color = Teal100)
                    HeadingSmall(text = "부터 깨워드릴게요")
                }
                Spacer(modifier = Modifier.weight(2f))
                TimePicker(
                    hour = selectedHour,
                    minute = selectedMinute,
                    onChangeValue = { time: TimePickerTime ->
                        selectedHour = time.hour
                        selectedMinute = time.minute
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ){
                    Text(
                        text = "${makeTextTimeMinus30Minutes(selectedHour, selectedMinute)}부터 천천히 깨워드려요",
                        color = Teal100
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ){
                    if (isActiveSleepForegroundService) {
                        Button(text = "수면 끝", onClick = ::handleRemoveWakeupTime, width = 300.dp)
                    } else {
                        Button(
                            text = "수면 시작",
                            onClick = ::handleSetWakeupTime,
                            width = 300.dp
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(2f))
                Spacer(modifier = Modifier.padding(44.dp))
            } else {
                OnSleepView(
                    sleepViewModel = sleepViewModel,
                    onRemoveWakeupTime = ::handleRemoveWakeupTime
                )
            }
        }
    }
}

fun getMinus30MinutesLocalTime(
    hour: Int,
    minute: Int,
    second: Int
): LocalTime{
    return LocalTime.of(hour, minute, second).minusMinutes(30)
}

fun makeTextTimeMinus30Minutes(
    hour: Int,
    minute: Int
): String {
    // 30분 전의 시간을 계산
    var newHour = hour
    var newMinute = minute - 30

    if (newMinute < 0) {
        newMinute += 60
        newHour -= 1
        if (newHour < 0) {
            newHour = 23
        }
    }

    // 시간과 분을 두 자리 문자열로 변환
    val newHourString = newHour.toString().padStart(2, '0')
    val newMinuteString = newMinute.toString().padStart(2, '0')

    // 새로운 시간을 hh시mm분 형식의 문자열로 반환
    return "${newHourString}시 ${newMinuteString}분"
}

@Preview
@Composable
private fun SleepScreenPreview(){
    val myHouseViewModel = MyHouseViewModel()
    val sleepViewModel = SleepViewModel()

    SleepScreen(
        isActiveSleepForegroundService = false,
        myHouseViewModel = myHouseViewModel,
        sleepViewModel = sleepViewModel
    )
}