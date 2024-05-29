package com.mimo.android.views

import androidx.navigation.NavType
import androidx.navigation.navArgument

interface Destination {
    val route : String
}

object MyHouseScreenDestination: Destination {
    override val route = "MyHouseScreenDestination"
}

object MyHouseDetailScreenDestination: Destination {
    override val route = "MyHouseDetailScreenDestination"
    const val houseIdTypeArg = "houseId"
    val routeWithArgs = "$route/{$houseIdTypeArg}"
    val arguments = listOf(
        navArgument(houseIdTypeArg) { type = NavType.StringType }
    )
}

object MyProfileScreenDestination: Destination {
    override val route = "MyProfileScreenDestination"
}

object SleepScreenDestination: Destination {
    override val route = "SleepScreenDestination"
}

object CreateHouseConfirmScreenDestination: Destination {
    override val route = "CreateHouseConfirmScreen"
}

object ChangeHouseNicknameScreenDestination: Destination {
    override val route = "ChangeHouseNicknameScreenDestination"
    const val houseIdTypeArg = "houseId"
    val routeWithArgs = "${route}/{$houseIdTypeArg}"
    val arguments = listOf(
        navArgument(houseIdTypeArg) { type = NavType.StringType }
    )
}

object MyHouseHubListScreenDestination: Destination {
    override val route = "MyHouseHubListScreen"
    const val houseIdTypeArg = "houseId"
    val routeWithArgs = "$route/{$houseIdTypeArg}"
    val arguments = listOf(
        navArgument(houseIdTypeArg) { type = NavType.StringType }
    )
}

object MyHouseSimpleDeviceListDestination: Destination {
    override val route = "MyHouseSimpleDeviceListDestination"
    const val hubIdTypeArg = "hubId"
    val routeWithArgs = "$route/{$hubIdTypeArg}"
    val arguments = listOf(
        navArgument(hubIdTypeArg) { type = NavType.StringType }
    )
}

object MyHouseCurtainScreenDestination: Destination {
    override val route = "MyHouseCurtainScreenDestination"
    const val deviceIdTypeArg = "curtainId"
    val routeWithArgs = "$route/{$deviceIdTypeArg}"
    val arguments = listOf(
        navArgument(deviceIdTypeArg) { type = NavType.StringType }
    )
}

object MyHouseLampScreenDestination: Destination {
    override val route = "MyHouseLampScreenDestination"
    const val deviceIdTypeArg = "lampId"
    val routeWithArgs = "$route/{$deviceIdTypeArg}"
    val arguments = listOf(
        navArgument(deviceIdTypeArg) { type = NavType.StringType }
    )
}

object MyHouseLightScreenDestination: Destination {
    override val route = "MyHouseLightScreenDestination"
    const val deviceIdTypeArg = "lightId"
    val routeWithArgs = "$route/{$deviceIdTypeArg}"
    val arguments = listOf(
        navArgument(deviceIdTypeArg) { type = NavType.StringType }
    )
}

object MyHouseWindowScreenDestination: Destination {
    override val route = "MyHouseWindowScreenDestination"
    const val deviceIdTypeArg = "windowId"
    val routeWithArgs = "$route/{$deviceIdTypeArg}"
    val arguments = listOf(
        navArgument(deviceIdTypeArg) { type = NavType.StringType }
    )
}


fun isShowNavigation(currentRoute: String?): Boolean{
    if (currentRoute == null) {
        return true
    }

    if (currentRoute.contains("ChangeHouseNicknameScreenDestination")) {
        return false
    }

    if (currentRoute.contains("CreateHouseConfirmScreen")) {
        return false
    }

    return true
}