package com.mimo.android.views.main.myprofile

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mimo.android.viewmodels.AuthViewModel
import com.mimo.android.components.Button
import com.mimo.android.components.HeadingLarge
import com.mimo.android.components.HeadingSmall
import com.mimo.android.components.Icon
import com.mimo.android.components.Modal
import com.mimo.android.components.ScrollView
import com.mimo.android.components.Text
import com.mimo.android.components.base.Size
import com.mimo.android.services.health.HealthConnectManager
import com.mimo.android.ui.theme.Gray300
import com.mimo.android.ui.theme.Gray600
import com.mimo.android.utils.preferences.ACCESS_TOKEN
import com.mimo.android.utils.preferences.getData
import com.mimo.android.viewmodels.MyProfileViewModel
import com.mimo.android.R
import com.mimo.android.components.LinearProgressbar
import com.mimo.android.components.SimpleCalendar
import com.mimo.android.viewmodels.convertCalendarDate
import java.time.LocalDate

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyProfileScreen(
    navController: NavHostController,
    healthConnectManager: HealthConnectManager,
    myProfileViewModel: MyProfileViewModel,
    authViewModel: AuthViewModel,
){
    val myProfileUiState by myProfileViewModel.uiState.collectAsState()
    var isShowScreenModal by remember { mutableStateOf(false) }

    fun isToday(): Boolean {
        val stateDate = convertCalendarDate(myProfileUiState.date)
        val todayDate = convertCalendarDate(LocalDate.now())
        return stateDate.month == todayDate.month && stateDate.day == todayDate.day
    }

    fun handleClickPrevDate(){
        myProfileViewModel.updateToPrevDate()
    }

    fun handleClickNextDate(){
        if (isToday()) {
            return
        }
        myProfileViewModel.updateToNextDate()
    }

    fun handleShowScreenModal(){
        isShowScreenModal = true
    }

    fun handleCloseScreenModal(){
        isShowScreenModal = false
    }

    LaunchedEffect(Unit) {
        myProfileViewModel.init(healthConnectManager)
    }

    ScrollView {
        if (isShowScreenModal) {
            Modal(
                onClose = ::handleCloseScreenModal,
                children = {
                    ScreenModalContent(
                        onClose = { handleCloseScreenModal() },
                        onClickLogoutButton = { authViewModel.logout() }
                    )
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            HeadingLarge(text = "내 정보", fontSize = Size.lg)
            Column(
                modifier = Modifier.height(42.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    size = 32.dp,
                    onClick = ::handleShowScreenModal
                )
            }
        }
        Spacer(modifier = Modifier.padding(14.dp))
        HeadingSmall(text = "수면 통계", fontSize = Size.lg)
        Spacer(modifier = Modifier.padding(16.dp))
        SimpleCalendar(
            isToday = isToday(),
            date = myProfileUiState.date,
            onClickPrevDate = ::handleClickPrevDate,
            onClickNextDate = ::handleClickNextDate
        )
        Spacer(modifier = Modifier.padding(4.dp))
        SleepStatistics(myProfileViewModel = myProfileViewModel)
        Spacer(modifier = Modifier.padding(40.dp))
    }
}

@Composable
fun ScreenModalContent(
    onClose: () -> Unit,
    onClickLogoutButton: () -> Unit
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Gray300,
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = Gray300,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(text = "로그아웃", onClick = { onClickLogoutButton() })
            Spacer(modifier = Modifier.padding(4.dp))
            Button(
                text = "닫기", color = Gray600, hasBorder = false,
                onClick = { onClose() }
            )
            TextField(value = "${getData(ACCESS_TOKEN)}", onValueChange = {})
        }
    }
}