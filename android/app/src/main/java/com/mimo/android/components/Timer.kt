package com.mimo.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mimo.android.ui.theme.Teal100
import com.mimo.android.utils.KoreaTime
import com.mimo.android.utils.getCurrentKoreaTime
import com.mimo.android.viewmodels.MyTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@Composable
fun Timer(
    targetTime: LocalTime,
    onFinish: (() -> Unit)? = null
){
    var currentTime by remember { mutableStateOf(getCurrentKoreaTime()) }
    val coroutineScope = rememberCoroutineScope()
    var remainingTime by remember { mutableStateOf(calculateRemainingTime(targetTime)) }

    // LaunchedEffect로 타이머 시작
    LaunchedEffect(targetTime) {
        coroutineScope.launch {
            while (remainingTime > 0) {
                delay(1000L) // 1초마다 갱신
                remainingTime = calculateRemainingTime(targetTime)
            }
            onFinish?.invoke()
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = getCurrentKoreaTime()
        }
    }

    _Timer(
        remainingTime = remainingTime
    )
}

@Composable
private fun _Timer(
    remainingTime: Long
){
    val t = formatTime(remainingTime)
    val hour = t.hour
    val minute = t.minute
    val second = t.second

    Box {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Box(
                modifier = Modifier
                    .width(250.dp)
                    .height(63.dp)
                    .background(Color.Transparent)
                    .border(
                        width = 3.dp,
                        color = Teal100,
                        shape = RoundedCornerShape(8.dp), // 선택된 항목을 강조하는 테두리
                    )
            )
        }
        Column {
            Spacer(modifier = Modifier.padding(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                androidx.compose.material3.Text(
                    text =  hour.toString().padStart(2, '0'),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Teal100
                )
                Spacer(modifier = Modifier.padding(8.dp))
                androidx.compose.material3.Text(
                    text = ":",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Teal100
                )
                Spacer(modifier = Modifier.padding(8.dp))
                androidx.compose.material3.Text(
                    text = minute.toString().padStart(2, '0'),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Teal100
                )
                Spacer(modifier = Modifier.padding(8.dp))
                androidx.compose.material3.Text(
                    text = ":",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Teal100
                )
                Spacer(modifier = Modifier.padding(8.dp))
                androidx.compose.material3.Text(
                    text = second.toString().padStart(2, '0'),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Teal100
                )
            }
        }
    }
}

// 남은 시간을 계산하는 함수
fun calculateRemainingTime(targetTime: LocalTime): Long {
    val now = LocalTime.now()
    val remainingSeconds = now.until(targetTime, ChronoUnit.SECONDS)
    return if (remainingSeconds < 0) 0 else remainingSeconds
}

// 시간을 hh:mm:ss 형식으로 변환하는 함수
fun formatTime(seconds: Long): MyTime {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return MyTime(hours.toInt(), minutes.toInt(), secs.toInt())
}