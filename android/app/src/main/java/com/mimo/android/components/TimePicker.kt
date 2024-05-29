package com.mimo.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextAlign
import com.mimo.android.ui.theme.Teal100

data class TimePickerTime(
    val hour: Int,
    val minute: Int
)

@Composable
fun TimePicker(
    hour: Int = 7,
    minute: Int = 30,
    onChangeValue: ((time: TimePickerTime) -> Unit)? = null
) {
    val _hours = (0..23).toList()
    val _minutes = (0..59).toList()

    _hours.plus(null)
    _hours.plus(null)
    _hours.plus(null)
    _hours.plus(null)
    _minutes.plus(null)
    _minutes.plus(null)
    _minutes.plus(null)
    _minutes.plus(null)

    var selectedHour by remember { mutableStateOf(hour) }
    var selectedMinute by remember { mutableStateOf(minute) }

    onChangeValue?.invoke(TimePickerTime(selectedHour, selectedMinute))

    val hourState = rememberLazyListState(initialFirstVisibleItemIndex = selectedHour)
    val minuteState = rememberLazyListState(initialFirstVisibleItemIndex = selectedMinute)
    val coroutineScope = rememberCoroutineScope()

    Box {
        Column {
            Spacer(modifier = Modifier.padding(25.dp))
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
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.align(Alignment.Center),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                TimePickerColumn(
                    items = _hours,
                    selectedItem = selectedHour,
                    onItemSelected = { selectedHour = it },
                    state = hourState,
                    coroutineScope = coroutineScope,
                    label = "Hour",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(24.dp))
                TimePickerColumn(
                    items = _minutes,
                    selectedItem = selectedMinute,
                    onItemSelected = { selectedMinute = it },
                    state = minuteState,
                    coroutineScope = coroutineScope,
                    label = "Minute",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun TimePickerColumn(
    items: List<Int>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
    state: LazyListState,
    coroutineScope: CoroutineScope,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        Text(text = label, fontSize = 24.sp, fontWeight = FontWeight.Bold)
//        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier.height(180.dp)
        ) {
            LazyColumn(
                state = state,
                contentPadding = PaddingValues(vertical = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(items.size) { index ->
                    val item = items[index]
                    val isSelected = selectedItem == item
                    val alpha = if (isSelected) 1f else 0.3f

                    Text(
                        text = item.toString().padStart(2, '0'),
                        fontSize = 32.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .alpha(alpha),
                        textAlign = TextAlign.Center,
                        color = Teal100
                    )
                }
            }
        }
    }

    LaunchedEffect(state) {
        coroutineScope.launch {
            state.scrollToItem(selectedItem)
        }
    }

    LaunchedEffect(state.firstVisibleItemScrollOffset) {
        val index = state.firstVisibleItemIndex
        onItemSelected(items[index])
        coroutineScope.launch {
            state.animateScrollToItem(index)
        }
    }
}

@Preview
@Composable
fun PreviewTimePicker() {
    TimePicker()
}
