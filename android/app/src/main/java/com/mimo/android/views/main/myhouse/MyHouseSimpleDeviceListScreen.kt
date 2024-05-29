package com.mimo.android.views.main.myhouse

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mimo.android.apis.hubs.Hub
import com.mimo.android.components.*
import com.mimo.android.components.base.Size
import com.mimo.android.components.devices.SimpleDeviceList
import com.mimo.android.ui.theme.*
import com.mimo.android.utils.convertStringDateToKoreaTime

@Composable
fun MyHouseSimpleDeviceListScreen(
    navController: NavHostController,
    hub: Hub,
) {
    fun handleGoPrev() {
        navController.navigateUp()
    }
    BackHandler {
        handleGoPrev()
    }

    ScrollView {
        Icon(imageVector = Icons.Filled.ArrowBack, onClick = ::handleGoPrev)
        Spacer(modifier = Modifier.padding(14.dp))

        HorizontalScroll(
            children = {
                HeadingLarge(text = hub.serialNumber, fontSize = Size.lg)
            }
        )
        Spacer(modifier = Modifier.padding(4.dp))
        HorizontalScroll(
            children = {
                HeadingSmall(text = "등록일 : ${convertStringDateToKoreaTime(hub.registeredDttm)}", fontSize = Size.sm, color = Teal100)
            }
        )
        Spacer(modifier = Modifier.padding(16.dp))

        HeadingSmall(text = "연결된 기기", fontSize = Size.lg)
        Spacer(modifier = Modifier.padding(8.dp))

        SimpleDeviceList(simpleDeviceList = hub.devices)
    }
}