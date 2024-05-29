package com.mimo.android

import androidx.compose.material3.*
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.FabPosition
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mimo.android.components.BackgroundImage
import com.mimo.android.components.navigation.getColor
import com.mimo.android.components.navigation.myicons.MyIconPack
import com.mimo.android.components.navigation.myicons.myiconpack.MoonStarsFillIcon185549
import com.mimo.android.viewmodels.AuthViewModel
import com.mimo.android.viewmodels.FirstSettingFunnelsViewModel
import com.mimo.android.viewmodels.QrCodeViewModel
import com.mimo.android.viewmodels.UserLocation
import com.mimo.android.services.health.HealthConnectManager
import com.mimo.android.views.*
import com.mimo.android.views.firstsettingfunnels.*
import com.mimo.android.views.login.LoginScreen
import com.mimo.android.ui.theme.Teal900
import com.mimo.android.viewmodels.MyHouseCurtainViewModel
import com.mimo.android.viewmodels.MyHouseDetailViewModel
import com.mimo.android.viewmodels.MyHouseHubListViewModel
import com.mimo.android.viewmodels.MyHouseLampViewModel
import com.mimo.android.viewmodels.MyHouseLightViewModel
import com.mimo.android.viewmodels.MyHouseViewModel
import com.mimo.android.viewmodels.MyHouseWindowViewModel
import com.mimo.android.viewmodels.MyProfileViewModel
import com.mimo.android.viewmodels.SleepViewModel

private const val TAG = "MimoApp"

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MimoApp(
    context: Context,
    isActiveSleepForegroundService: Boolean,
    authViewModel: AuthViewModel,
    qrCodeViewModel: QrCodeViewModel,
    firstSettingFunnelsViewModel: FirstSettingFunnelsViewModel,
    myHouseViewModel: MyHouseViewModel,
    myHouseDetailViewModel: MyHouseDetailViewModel,
    myHouseHubListViewModel: MyHouseHubListViewModel,
    myProfileViewModel: MyProfileViewModel,
    healthConnectManager: HealthConnectManager,
    launchGoogleLocationAndAddress: (cb: (userLocation: UserLocation?) -> Unit) -> Unit,
    onStartSleepForegroundService: (() -> Unit)? = null,
    onStopSleepForegroundService: (() -> Unit)? = null,
    checkCameraPermissionFirstSetting: () -> Unit,
    checkCameraPermissionHubToHouse: () -> Unit,
    checkCameraPermissionMachineToHub: () -> Unit,
    myHouseCurtainViewModel: MyHouseCurtainViewModel,
    myHouseLampViewModel: MyHouseLampViewModel,
    myHouseLightViewModel: MyHouseLightViewModel,
    myHouseWindowViewModel: MyHouseWindowViewModel,
    sleepViewModel: SleepViewModel
    ){

    MaterialTheme {
        val scaffoldState = rememberScaffoldState()
        val navController = rememberNavController()
        val scope = rememberCoroutineScope()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val availability by healthConnectManager.availability

        val authUiState by authViewModel.uiState.collectAsState()
        val firstSettingFunnelsUiState by firstSettingFunnelsViewModel.uiState.collectAsState()
        val sleepUiState by sleepViewModel.uiState.collectAsState()

        val activeSleep = currentRoute?.contains("Sleep") ?: false
        androidx.compose.material.Scaffold(
            bottomBar = {
                if (authUiState.user != null && firstSettingFunnelsUiState.currentStepId == null && sleepUiState.wakeupTime == null) {
                    com.mimo.android.components.navigation.Navigation(
                        navController = navController,
                        currentRoute = currentRoute
                    )
                }
            },
            floatingActionButton = {
                if (authUiState.user != null && firstSettingFunnelsUiState.currentStepId == null && isShowNavigation(currentRoute) && sleepUiState.wakeupTime == null) {
                    androidx.compose.material.FloatingActionButton(
                        onClick = { /*TODO*/ },
                        contentColor = getColor(activeSleep),
                        backgroundColor = Teal900,
                        modifier = Modifier
                            .width(80.dp)
                            .height(80.dp)
                    ) {
                        androidx.compose.material.Icon(
                            imageVector = MyIconPack.MoonStarsFillIcon185549,
                            contentDescription = null,
                            modifier = Modifier
                                .height(45.dp)
                                .width(45.dp)
                                .clickable {
                                    navController.navigate(SleepScreenDestination.route) {
                                        popUpTo(0)
                                    }
                                }
                        )
                    }
                }
            },
            scaffoldState = scaffoldState,
            isFloatingActionButtonDocked = true,
            floatingActionButtonPosition = FabPosition.Center
        ) {
            BackgroundImage {
                Box(modifier = Modifier.padding(16.dp)) {
                    if (firstSettingFunnelsUiState.currentStepId != null) {
                        FirstSettingFunnelsRoot(
                            qrCodeViewModel = qrCodeViewModel,
                            firstSettingFunnelsViewModel = firstSettingFunnelsViewModel,
                            checkCameraPermission = checkCameraPermissionFirstSetting,
                            launchGoogleLocationAndAddress = launchGoogleLocationAndAddress,
                            context = context
                        )
                        return@BackgroundImage
                    }

                    if (authUiState.accessToken == null) {
                        LoginScreen(
                            authViewModel = authViewModel,
                            firstSettingFunnelsViewModel = firstSettingFunnelsViewModel
                        )
                        return@BackgroundImage
                    }

                    if (authUiState.user != null) {
                        Router(
                            navController = navController,
                            authViewModel = authViewModel,
                            isActiveSleepForegroundService = isActiveSleepForegroundService,
                            healthConnectManager = healthConnectManager,
                            onStartSleepForegroundService = onStartSleepForegroundService,
                            onStopSleepForegroundService = onStopSleepForegroundService,
                            myHouseViewModel = myHouseViewModel,
                            myHouseDetailViewModel = myHouseDetailViewModel,
                            myHouseHubListViewModel = myHouseHubListViewModel,
                            myProfileViewModel = myProfileViewModel,
                            qrCodeViewModel = qrCodeViewModel,
                            checkCameraPermissionHubToHouse = checkCameraPermissionHubToHouse,
                            checkCameraPermissionMachineToHub = checkCameraPermissionMachineToHub,
                            launchGoogleLocationAndAddress = launchGoogleLocationAndAddress,
                            myHouseCurtainViewModel = myHouseCurtainViewModel,
                            myHouseLampViewModel = myHouseLampViewModel,
                            myHouseLightViewModel = myHouseLightViewModel,
                            myHouseWindowViewModel = myHouseWindowViewModel,
                            sleepViewModel = sleepViewModel
                        )
                    }
                }
            }
        }
    }
}