package com.mimo.android.views

import com.mimo.android.services.health.HealthConnectManager
import com.mimo.android.views.main.myprofile.MyProfileScreen
import com.mimo.android.views.main.sleep.SleepScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mimo.android.views.main.myhouse.MyHouseHubListScreen
import com.mimo.android.views.main.myhouse.*
import com.mimo.android.views.main.myhouse.detaildevices.*
import com.mimo.android.utils.alertError
import com.mimo.android.viewmodels.*

@Composable
fun Router(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    isActiveSleepForegroundService: Boolean,
    healthConnectManager: HealthConnectManager,
    onStartSleepForegroundService: (() -> Unit)? = null,
    onStopSleepForegroundService: (() -> Unit)? = null,
    myHouseViewModel: MyHouseViewModel,
    myHouseDetailViewModel: MyHouseDetailViewModel,
    myHouseHubListViewModel: MyHouseHubListViewModel,
    myProfileViewModel: MyProfileViewModel,
    qrCodeViewModel: QrCodeViewModel,
    checkCameraPermissionHubToHouse: () -> Unit,
    checkCameraPermissionMachineToHub: () -> Unit,
    launchGoogleLocationAndAddress: (cb: (userLocation: UserLocation?) -> Unit) -> Unit,
    myHouseCurtainViewModel: MyHouseCurtainViewModel,
    myHouseLampViewModel: MyHouseLampViewModel,
    myHouseLightViewModel: MyHouseLightViewModel,
    myHouseWindowViewModel: MyHouseWindowViewModel,
    sleepViewModel: SleepViewModel
){
    val myHouseUiState by myHouseViewModel.uiState.collectAsState()
    val myHouseDetailUiState by myHouseDetailViewModel.uiState.collectAsState()
    val myHouseHubListUiState by myHouseHubListViewModel.uiState.collectAsState()

    NavHost(navController = navController, startDestination = SleepScreenDestination.route) {
        composable(MyHouseScreenDestination.route) {
            MyHouseScreen(
                navController = navController,
                myHouseViewModel = myHouseViewModel,
                qrCodeViewModel = qrCodeViewModel,
                checkCameraPermissionHubToHouse = checkCameraPermissionHubToHouse,
                checkCameraPermissionMachineToHub = checkCameraPermissionMachineToHub
            )
            return@composable
        }

        composable(SleepScreenDestination.route) {
            SleepScreen(
                navController = navController,
                isActiveSleepForegroundService = isActiveSleepForegroundService,
                onStartSleepForegroundService = onStartSleepForegroundService,
                onStopSleepForegroundService = onStopSleepForegroundService,
                sleepViewModel = sleepViewModel,
                myHouseViewModel = myHouseViewModel
            )
            return@composable
        }

        composable(MyProfileScreenDestination.route) {
            MyProfileScreen(
                navController = navController,
                myProfileViewModel = myProfileViewModel,
                authViewModel = authViewModel,
                healthConnectManager = healthConnectManager
            )
            return@composable
        }

        composable(route = CreateHouseConfirmScreenDestination.route){
            CreateHouseConfirmScreen(
                navController = navController,
                launchGoogleLocationAndAddress = launchGoogleLocationAndAddress,
                myHouseViewModel = myHouseViewModel
            )
            return@composable
        }

        composable(
            route = ChangeHouseNicknameScreenDestination.routeWithArgs,
            arguments = ChangeHouseNicknameScreenDestination.arguments
        ) { backStackEntry ->
            val houseId = backStackEntry.arguments?.getString(ChangeHouseNicknameScreenDestination.houseIdTypeArg)
            ChangeHouseNicknameScreen(
                navController = navController,
                houseId = houseId!!.toLong(),
                myHouseViewModel = myHouseViewModel)
        }

        composable(
            route = MyHouseDetailScreenDestination.routeWithArgs,
            arguments = MyHouseDetailScreenDestination.arguments
        ){ backStackEntry ->
            val houseId = backStackEntry.arguments?.getString(MyHouseDetailScreenDestination.houseIdTypeArg)
            val house = myHouseViewModel.queryHouse(myHouseUiState, houseId!!.toLong())
            if (house == null) {
                navController.navigate(MyHouseScreenDestination.route) {
                    popUpTo(0)
                }
                return@composable
            }
            MyHouseDetailScreen(
                navController = navController,
                myHouseDetailViewModel = myHouseDetailViewModel,
                house = house,
                qrCodeViewModel = qrCodeViewModel,
                checkCameraPermissionHubToHouse = checkCameraPermissionHubToHouse,
                checkCameraPermissionMachineToHub = checkCameraPermissionMachineToHub,
                myHouseCurtainViewModel = myHouseCurtainViewModel,
                myHouseLampViewModel = myHouseLampViewModel,
                myHouseLightViewModel = myHouseLightViewModel,
                myHouseWindowViewModel = myHouseWindowViewModel
            )
            return@composable
        }

        composable(
            route = MyHouseHubListScreenDestination.routeWithArgs,
            arguments = MyHouseHubListScreenDestination.arguments
        ){ backStackEntry ->
            val houseId = backStackEntry.arguments?.getString(MyHouseHubListScreenDestination.houseIdTypeArg)
            val house = myHouseViewModel.queryHouse(myHouseUiState, houseId!!.toLong())
            if (house == null) {
                alertError()
                return@composable
            }
            MyHouseHubListScreen(
                navController = navController,
                house = house,
                myHouseHubListViewModel = myHouseHubListViewModel,
            )
            return@composable
        }

        composable(
            route = MyHouseSimpleDeviceListDestination.routeWithArgs,
            arguments = MyHouseSimpleDeviceListDestination.arguments
        ) { backStackEntry ->
            val hubId = backStackEntry.arguments?.getString(MyHouseSimpleDeviceListDestination.hubIdTypeArg)
            val hub = myHouseHubListViewModel.queryHub(
                hubId = hubId!!.toLong(),
                myHouseHubListUiState = myHouseHubListUiState
            )
            if (hub == null) {
                alertError()
                return@composable
            }
            MyHouseSimpleDeviceListScreen(navController = navController, hub = hub)
            return@composable
        }

        composable(
            route = MyHouseCurtainScreenDestination.routeWithArgs,
            arguments = MyHouseCurtainScreenDestination.arguments
        ) { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString(MyHouseCurtainScreenDestination.deviceIdTypeArg)
            val device = myHouseDetailViewModel.queryDevice(
                deviceId = deviceId!!.toLong(),
                deviceType = DeviceType.CURTAIN,
                myHouseDetailUiState = myHouseDetailUiState
            )
            if (device == null) {
                alertError()
                return@composable
            }
            MyHouseCurtainScreen(
                navController = navController,
                device = device,
                myHouseCurtainViewModel = myHouseCurtainViewModel
            )
            return@composable
        }

        composable(
            route = MyHouseLampScreenDestination.routeWithArgs,
            arguments = MyHouseLampScreenDestination.arguments
        ) { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString(MyHouseLampScreenDestination.deviceIdTypeArg)
            val device = myHouseDetailViewModel.queryDevice(
                deviceId = deviceId!!.toLong(),
                deviceType = DeviceType.LAMP,
                myHouseDetailUiState = myHouseDetailUiState
            )
            if (device == null) {
                alertError()
                return@composable
            }
            MyHouseLampScreen(
                navController = navController,
                device = device,
                myHouseLampViewModel = myHouseLampViewModel
            )
            return@composable
        }

        composable(
            route = MyHouseLightScreenDestination.routeWithArgs,
            arguments = MyHouseLightScreenDestination.arguments
        ) { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString(MyHouseLightScreenDestination.deviceIdTypeArg)
            val device = myHouseDetailViewModel.queryDevice(
                deviceId = deviceId!!.toLong(),
                deviceType = DeviceType.LIGHT,
                myHouseDetailUiState = myHouseDetailUiState
            )
            if (device == null) {
                alertError()
                return@composable
            }
            MyHouseLightScreen(
                navController = navController,
                device = device,
                myHouseLightViewModel = myHouseLightViewModel
            )
            return@composable
        }

        composable(
            route = MyHouseWindowScreenDestination.routeWithArgs,
            arguments = MyHouseWindowScreenDestination.arguments
        ) { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString(MyHouseWindowScreenDestination.deviceIdTypeArg)
            val device = myHouseDetailViewModel.queryDevice(
                deviceId = deviceId!!.toLong(),
                deviceType = DeviceType.WINDOW,
                myHouseDetailUiState = myHouseDetailUiState
            )
            if (device == null) {
                alertError()
                return@composable
            }
            MyHouseWindowScreen(
                navController = navController,
                device = device,
                myHouseWindowViewModel = myHouseWindowViewModel
            )
            return@composable
        }
    }
}