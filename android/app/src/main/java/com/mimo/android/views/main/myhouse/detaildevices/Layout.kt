package com.mimo.android.views.main.myhouse.detaildevices

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mimo.android.apis.houses.Device
import com.mimo.android.components.HeadingLarge
import com.mimo.android.components.HeadingSmall
import com.mimo.android.components.HorizontalScroll
import com.mimo.android.components.Icon
import com.mimo.android.components.ScrollView
import com.mimo.android.components.base.Size
import com.mimo.android.ui.theme.Teal100
import com.mimo.android.viewmodels.convertDeviceTypeToKoreaName

@Composable
fun Layout(
    navController: NavHostController? = null,
    device: Device,
    children: @Composable () -> Unit,
    macAddress: String?,
    onInitializeCallback: (() -> Unit)? = null
){
    fun handleGoPrev() {
        navController?.navigateUp()
    }
    BackHandler {
        handleGoPrev()
    }

    LaunchedEffect(Unit) {
        onInitializeCallback?.invoke()
    }

    ScrollView {
        Icon(imageVector = Icons.Filled.ArrowBack, onClick = ::handleGoPrev)
        Spacer(modifier = Modifier.padding(14.dp))

        HorizontalScroll(
            children = {
                HeadingLarge(text = device.nickname, fontSize = Size.lg)
            }
        )
        Spacer(modifier = Modifier.padding(4.dp))
        HorizontalScroll(
            children = {
                HeadingSmall(text = "MAC 주소 : ${macAddress ?: ""}", fontSize = Size.sm, color = Teal100)
            }
        )
        Spacer(modifier = Modifier.padding(16.dp))

        HeadingSmall(text = "${convertDeviceTypeToKoreaName(device.type)} 설정", fontSize = Size.lg)
        Spacer(modifier = Modifier.padding(8.dp))

        children()
    }
}