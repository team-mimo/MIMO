package com.mimo.android.views.firstsettingfunnels

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.mimo.android.R
import com.mimo.android.components.*
import com.mimo.android.components.base.*
import com.mimo.android.ui.theme.*

@Preview
@Composable
fun Start(
    checkCameraPermission: (() -> Unit)? = null
){
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Logo()
        Spacer(modifier = Modifier.padding(4.dp))

        HeadingLarge(text = "MIMO", fontSize = Size.lg)
        HeadingLarge(text = "허브 등록을 시작할게요", fontSize = Size.lg)
        Spacer(modifier = Modifier.padding(12.dp))
        TransparentCard(
            children = {
                Row {
                    Row(
                        modifier = Modifier.padding(vertical = 32.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GifImage(R.drawable.moon_animation_image)
                        Column {
                            HeadingSmall(text = "이제부터 MIMO가", fontSize = Size.lg, color = Teal50)
                            Text(text = "당신의 수면 활동을 도와드릴게요")
                        }
                    }
                }
            }
        )
        Spacer(modifier = Modifier.padding(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.padding(8.dp))
        HeadingSmall(text = "MIMO와 함께하면", fontSize = Size.lg)
        Spacer(modifier = Modifier.padding(16.dp))

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "수면의 질을 높여")
                    HeadingSmall(text = "편안하게 잠을 잘 수 있어요", fontSize = Size.md, color = Teal100)
                }
                Icon(imageVector = Icons.Outlined.CheckCircle, color = Teal100)
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "수면 사이클에 맞춰")
                    HeadingSmall(text = "상쾌하게 기상할 수 있어요", fontSize = Size.md, color = Teal100)
                }
                Icon(imageVector = Icons.Outlined.CheckCircle, color = Teal100)
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "생체 데이터를 통해")
                    HeadingSmall(text = "최적의 수면 환경을 만들 수 있어요", fontSize = Size.md, color = Teal100)
                }
                Icon(imageVector = Icons.Outlined.CheckCircle, color = Teal100)
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(text = "허브 등록하기", onClick = checkCameraPermission)
    }
}