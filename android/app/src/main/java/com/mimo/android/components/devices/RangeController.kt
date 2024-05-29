package com.mimo.android.components.devices

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mimo.android.components.Text
import com.mimo.android.ui.theme.Teal400
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow

@Composable
fun RangeController(
    leftDesc: String,
    rightDesc: String,
    value: Float,
    onChange: (nextValue: Float) -> Unit
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){

        var _value by remember { mutableStateOf(value) }

        Row (
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.Start
        ){
            Text(text = leftDesc)
        }

        Slider(
            value = _value, // 초기 값
            onValueChangeFinished = {
                onChange(_value)
            },
            onValueChange = { nextValue ->

//                                CoroutineScope(Dispatchers.Main).launch {
//                                    nextValue
//                                        // 디바운스를 적용하여 슬라이더 값 변경 이벤트를 지연시킵니다.
//                                        .debounce(300)
//                                        // collectLatest를 사용하여 가장 최신 값을 수신합니다.
//                                        .collectLatest { value ->
//                                            // 디바운스된 값으로 작업을 수행합니다.
//                                            handleSliderValue(value)
//                                        }
//                                }

                _value = nextValue
            },
            valueRange = 0f..100f, // 슬라이더 값 범위
            steps = 10000, // 슬라이더의 이동 단위
            modifier = Modifier.width(240.dp), // 슬라이더의 너비 지정
            colors = SliderDefaults.colors(
                activeTickColor = Teal400,
                inactiveTickColor = Color.Gray,
                thumbColor = Teal400, // 슬라이더 썸 색상
                activeTrackColor = Teal400, // 활성화된 슬라이더 트랙의 색상
                inactiveTrackColor = Color.Gray // 비활성화된 슬라이더 트랙의 색상
            ),
            // 옵션 설명
//                            value: Float, // 슬라이더의 현재 값.
//                            onValueChange: (Float) -> Unit, // 슬라이더 값이 변경될 때 호출되는 콜백 함수.
//                            valueRange: ClosedFloatingPointRange<Float>, // 슬라이더 값의 범위를 지정하는 범위 객체.
//                            steps: Int = 0, // 슬라이더의 이동 단위. 값이 0보다 큰 경우, 슬라이더는 주어진 값에 따라 고정된 단계로 이동합니다.
//                            onValueChangeFinished: (() -> Unit)? = null, // 슬라이더 값을 변경한 후 호출되는 콜백 함수.
//                            colors: SliderColors = SliderDefaults.colors(), // 슬라이더의 색상을 지정하는 객체.
//                            modifier: Modifier = Modifier, // Modifier를 사용하여 슬라이더에 적용할 수 있는 수정자.
//                            enabled: Boolean = true, // 슬라이더가 활성화되었는지 여부를 나타내는 부울 값.
//                            onGloballyPositioned: OnGloballyPositionedModifier = null, // 슬라이더가 전역 위치를 설정할 때 호출되는 콜백 함수.
//                            interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }, // 슬라이더의 상호 작용 소스를 지정하는 객체.
//                            onValueChangeInteractionSource: MutableInteractionSource = interactionSource, // 슬라이더 값 변경 시 상호 작용 소스를 지정하는 객체.
//                            onValueChangeFinishedInteractionSource: MutableInteractionSource = interactionSource, // 슬라이더 값 변경 후 상호 작용 소스를 지정하는 객체.
//                            )
        )

        Row (
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.End
        ){
            Text(text = rightDesc)
        }
    }
}

//@Preview
//@Composable
//private fun RangeControllerPreview(){
//    Column {
//        RangeController(leftDesc = "어둡게", rightDesc = "밝게")
//        RangeController(leftDesc = "닫힘", rightDesc = "열림")
//    }
//}