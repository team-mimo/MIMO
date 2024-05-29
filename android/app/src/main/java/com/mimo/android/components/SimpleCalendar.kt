package com.mimo.android.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mimo.android.ui.theme.Teal100
import com.mimo.android.viewmodels.convertCalendarDate
import java.time.LocalDate

@Composable
fun SimpleCalendar(
    isToday: Boolean,
    date: LocalDate,
    onClickPrevDate: (() -> Unit)? = null,
    onClickNextDate: (() -> Unit)? = null
){
    fun getGoNextDayButtonColor(): Color {
        if (isToday) {
            return Color.Gray
        }
        return Teal100
    }

    fun showDateString(): String {
        val stateDate = convertCalendarDate(date)
        val todayDate = convertCalendarDate(LocalDate.now())
        if (stateDate.month == todayDate.month && stateDate.day == todayDate.day) {
            return "오늘 (${todayDate.dayOfWeek})"
        }
        return "${stateDate.year}년 ${stateDate.month}월 ${stateDate.day}일 (${stateDate.dayOfWeek})"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.KeyboardArrowLeft,
            size = 32.dp,
            onClick = onClickPrevDate,
            color = Teal100
        )

        HeadingSmall(text = showDateString(), color = Teal100)

        Icon(
            imageVector = Icons.Filled.KeyboardArrowRight,
            size = 32.dp,
            onClick = onClickNextDate,
            color = getGoNextDayButtonColor()
        )
    }
}