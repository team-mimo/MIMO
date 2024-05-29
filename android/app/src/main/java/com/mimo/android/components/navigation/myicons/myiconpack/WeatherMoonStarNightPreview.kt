package com.mimo.android.components.navigation.myicons.myiconpack

import androidx.compose.foundation.Image
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import com.mimo.android.components.navigation.myicons.MyIconPack
import com.mimo.android.ui.theme.*

@Preview
@Composable
private fun WeatherMoonStarNightPreview(){
    Surface {
        Image(
            imageVector = MyIconPack.WeatherMoonStarNightIcon259075,
            contentDescription = null,
//            modifier = Modifier.background(color = Teal900),
            colorFilter = ColorFilter.tint(Teal400)
        )
    }
}