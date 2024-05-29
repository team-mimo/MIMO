package com.mimo.android.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mimo.android.ui.theme.Teal100
import com.mimo.android.utils.getCurrentKoreaTime
import kotlinx.coroutines.delay

@Composable
fun Clock(){
    var currentTime by remember { mutableStateOf(getCurrentKoreaTime()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = getCurrentKoreaTime()
        }
    }

    _Clock(
        hour = currentTime.hour,
        minute = currentTime.minute,
        second = currentTime.second
    )
}

@Composable
private fun _Clock(
    hour: Int,
    minute: Int,
    second: Int
){
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

@Preview
@Composable
private fun ClockPreview(){
    _Clock(hour = 7, minute = 30, second = 12)
}