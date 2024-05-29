package com.mimo.android.viewmodels

import android.util.Log
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mimo.android.services.health.HealthConnectManager
import com.mimo.android.utils.dateFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.util.Locale

private const val TAG = "MyProfileViewModel"

class MyProfileViewModel: ViewModel() {
    private var _healthConnectManager: HealthConnectManager? = null
    private val _uiState = MutableStateFlow(MyProfileUiState(date = LocalDate.now()))
    val uiState: StateFlow<MyProfileUiState> = _uiState.asStateFlow()

    fun init(healthConnectManager: HealthConnectManager){
        if (_healthConnectManager == null) {
            _healthConnectManager = healthConnectManager
            viewModelScope.launch {
                fetchSleepSessionRecord(date = LocalDate.now())
            }
        }
    }

    fun updateToPrevDate(){
        viewModelScope.launch {
            val prevDate = _uiState.value.date.minusDays(1)
            fetchSleepSessionRecord(date = prevDate)
        }
    }

    fun updateToNextDate(){
        viewModelScope.launch {
            val nextDate = _uiState.value.date.plusDays(1)
            fetchSleepSessionRecord(date = nextDate)
        }
    }

    private fun fetchSleepSessionRecord(date: LocalDate){
        viewModelScope.launch {
            val calendarDate = convertCalendarDate(date)
            val startTime = ZonedDateTime.of(calendarDate.year, calendarDate.month, calendarDate.day, 0, 0, 0, 0, ZoneId.of("Asia/Seoul"))
            val endTime = ZonedDateTime.of(calendarDate.year, calendarDate.month, calendarDate.day, 23, 59, 59, 0, ZoneId.of("Asia/Seoul"))
            val sleepSessionRecordList = _healthConnectManager?.readSleepSessionRecordList(startTime.toInstant(), endTime.toInstant())

            logSleepSessionList(
                startTime = startTime,
                endTime = endTime,
                sleepSessionRecordList = sleepSessionRecordList
            )

            _uiState.update { prevState ->
                prevState.copy(date = date, sleepSessionRecordList = sleepSessionRecordList)
            }
        }
    }
}

data class MyProfileUiState(
    val date: LocalDate,
    val sleepSessionRecordList: List<SleepSessionRecord>? = null
)

data class CalendarDate(
    val year: Int,
    val month: Int,
    val day: Int,
    val dayOfWeek: String
)

fun convertDayOfWeekToString(date: LocalDate): String {
    val dayOfWeek = date.dayOfWeek
    return dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
}

fun convertCalendarDate(date: LocalDate): CalendarDate {
    val year = date.year
    val month = date.monthValue
    val day = date.dayOfMonth
    val dayOfWeek = convertDayOfWeekToString(date)
    return CalendarDate(year, month, day, dayOfWeek)
}

private fun logSleepSessionList(
    startTime: ZonedDateTime,
    endTime: ZonedDateTime,
    sleepSessionRecordList: List<SleepSessionRecord>?
){
    if (sleepSessionRecordList == null) {
        Log.d(TAG, "MIMO가 감지 중")
        Log.d(TAG, "${dateFormatter.format(startTime)} ~ ${dateFormatter.format(endTime)} 까지 수면기록 없음")
        return
    }

    sleepSessionRecordList.forEachIndexed() { sessionIndex, session ->
        val koreanStartTime = dateFormatter.format(session.startTime)
        val koreanEndTime = dateFormatter.format(session.endTime)
        Log.d(TAG, "@@@@@@@ 상세 수면 기록 @@@@@@@")
        Log.d(TAG, "수면 ${sessionIndex + 1} 전체 : $koreanStartTime ~ $koreanEndTime")
        session.stages.forEach() { stage ->
            Log.d(TAG, "${dateFormatter.format(stage.startTime)} ~ ${dateFormatter.format(stage.endTime)} @@ ${meanStage(stage.stage)}")
        }
    }
}

enum class SleepStage {
    UNKNOWN, // 0
    AWAKE, // 1
    SLEEPING, // 2
    OUT_OF_BED, // 3
    LIGHT, // 4
    DEEP, // 5
    REM, // 6
    AWAKE_IN_BED // 7
}

// TODO: 수면기록은 AWAKE(수면 중 깸), LIGHT(얕은 잠), DEEP(깊은 잠), REM(렘 수면) 이렇게 4가지만 찍히는 걸로 확인됨..
// TODO: 따라서 사실 상 이 기록이 찍히게 되면 수면이 시작됐다는 거고 AWAKE가 찍히면 수면 중 깼다는 것...
// TODO: 그니까 이 기록이 찍히면 불 꺼주면 된다. 어차피 깨우는 건 30분 전부터 서서히 켜주면 되니까... 기상 감지는 못해도 괜찮을지도...
fun meanStage(stage: Int): SleepStage {
    if (stage == 1) {
        return SleepStage.AWAKE
    }
    if (stage == 2) {
        return SleepStage.SLEEPING
    }
    if (stage == 3) {
        return SleepStage.OUT_OF_BED
    }
    if (stage == 4) {
        return SleepStage.LIGHT
    }
    if (stage == 5) {
        return SleepStage.DEEP
    }
    if (stage == 6) {
        return SleepStage.REM
    }
    if (stage == 7) {
        return SleepStage.AWAKE_IN_BED
    }
    return SleepStage.UNKNOWN
}