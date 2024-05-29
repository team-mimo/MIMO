package com.mimo.android.views.main.myprofile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.SleepSessionRecord
import com.mimo.android.components.HeadingSmall
import com.mimo.android.components.Text
import com.mimo.android.components.base.Size
import com.mimo.android.ui.theme.Teal100
import com.mimo.android.utils.dateFormatter
import com.mimo.android.viewmodels.MyProfileViewModel
import com.mimo.android.viewmodels.meanStage
import java.time.Duration
import java.time.Instant

@Composable
fun SleepStatistics(
    myProfileViewModel: MyProfileViewModel
){
    val myProfileUiState by myProfileViewModel.uiState.collectAsState()

    if (myProfileUiState.sleepSessionRecordList == null) {
        Column {
            Spacer(modifier = Modifier.padding(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "수면 기록이 없어요", color = Teal100)
            }
        }
    } else {
        Column {
            Spacer(modifier = Modifier.padding(16.dp))

            myProfileUiState.sleepSessionRecordList!!.forEachIndexed { sessionIndex, sleepSessionRecord ->

                val koreanStartTime = dateFormatter.format(sleepSessionRecord.startTime)
                val koreanEndTime = dateFormatter.format(sleepSessionRecord.endTime)
                val TotalStageDurationSeconds = getTotalStageDurations(sleepSessionRecord)

                Row {

                    Spacer(modifier = Modifier.weight(1f))

                    HeadingSmall(text = "$koreanStartTime", color = Teal100, fontSize = Size.sm)
                    Spacer(modifier = Modifier.padding(1.dp))
                    HeadingSmall(text = "부터", fontSize = Size.sm)
                    Spacer(modifier = Modifier.padding(3.dp))
                    HeadingSmall(text = "$koreanEndTime", color = Teal100, fontSize = Size.sm)
                    Spacer(modifier = Modifier.padding(1.dp))
                    HeadingSmall(text = "까지", fontSize = Size.sm)
                    Spacer(modifier = Modifier.padding(3.dp))
                    HeadingSmall(text = "${formatDurationBetween(sleepSessionRecord.startTime, sleepSessionRecord.endTime)}", color = Teal100, fontSize = Size.sm)
                    Spacer(modifier = Modifier.padding(3.dp))
                    HeadingSmall(text = "수면", fontSize = Size.sm)

                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.padding(8.dp))

                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    HeadingSmall(text = "깊은 수면")
                    Spacer(modifier = Modifier.padding(16.dp))
                    HeadingSmall(text = formatSeconds(TotalStageDurationSeconds.deepSeconds))
                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.padding(8.dp))

                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    HeadingSmall(text = "얕은 수면")
                    Spacer(modifier = Modifier.padding(16.dp))
                    HeadingSmall(text = formatSeconds(TotalStageDurationSeconds.lightSeconds))
                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.padding(8.dp))

                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    HeadingSmall(text = "렘 수면")
                    Spacer(modifier = Modifier.padding(16.dp))
                    HeadingSmall(text = formatSeconds(TotalStageDurationSeconds.remSeconds))
                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.padding(8.dp))

                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    HeadingSmall(text = "수면 중 깸")
                    Spacer(modifier = Modifier.padding(16.dp))
                    HeadingSmall(text = formatSeconds(TotalStageDurationSeconds.awakeSeconds))
                    Spacer(modifier = Modifier.weight(1f))
                }
                
                Spacer(modifier = Modifier.padding(24.dp))
            }
        }
    }
}

private fun formatDurationBetween(startTime: Instant, endTime: Instant): String {
    val duration = Duration.between(startTime, endTime).abs() // 두 Instant 간의 Duration을 구합니다.
    println(duration.seconds)
    val hours = duration.seconds / 3600
    val minutes = duration.toMinutes() % 60
    val seconds = duration.seconds % 60
    if (duration.seconds >= 3600) {
        return "${hours}시간 ${minutes}분"
    }
    return "${minutes}분"
}

private fun formatSeconds(seconds: Long): String {
    val _hours = seconds / 3600
    val _minutes = (seconds % 3600) / 60
    val _seconds = seconds % 60

    if (_hours > 0) {
        return "${_hours}시간 ${_minutes}분"
    }
    return "${_minutes}분"
}

private data class TotalStageDurationSeconds(
    val awakeSeconds: Long,
    var lightSeconds: Long,
    var deepSeconds: Long,
    var remSeconds: Long
)

private fun getTotalStageDurations(sleepSessionRecord: SleepSessionRecord): TotalStageDurationSeconds{
    var awakeSeconds: Long = 0
    var lightSeconds: Long = 0
    var deepSeconds: Long = 0
    var remSeconds: Long = 0

    for (stage in sleepSessionRecord.stages) {
        val duration = Duration.between(stage.startTime, stage.endTime).abs()
        if (stage.stage == 1) {
            awakeSeconds += duration.seconds
            continue
        }
        if (stage.stage == 4) {
            lightSeconds += duration.seconds
            continue
        }
        if (stage.stage == 5) {
            deepSeconds += duration.seconds
            continue
        }
        if (stage.stage == 6) {
            remSeconds += duration.seconds
            continue
        }
    }

    return TotalStageDurationSeconds(
        awakeSeconds = awakeSeconds,
        lightSeconds = lightSeconds,
        deepSeconds = deepSeconds,
        remSeconds = remSeconds
    )
}