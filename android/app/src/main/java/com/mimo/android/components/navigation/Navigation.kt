package com.mimo.android.components.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import com.mimo.android.components.navigation.myicons.MyIconPack
import com.mimo.android.components.navigation.myicons.myiconpack.MoonStarsFillIcon185549
import com.mimo.android.views.MyHouseScreenDestination
import com.mimo.android.views.MyProfileScreenDestination
import com.mimo.android.views.isShowNavigation
import com.mimo.android.ui.theme.*

val activeColor: Color = Teal100
val inactiveColor: Color = Teal500
fun getColor(condition: Boolean): Color{
    if (condition) {
        return activeColor
    }
    return inactiveColor
}

@Composable
fun Navigation(
    navController: NavHostController? = null,
    currentRoute: String? = null
){
    val activeMyHouse = currentRoute?.contains("House") ?: false
    val activeMyProfile = currentRoute?.contains("Profile") ?: false

    fun navigateToMyHome(){
        navController?.navigate(MyHouseScreenDestination.route) {
            popUpTo(0)
        }
    }

    fun navigateToMyProfile(){
        navController?.navigate(MyProfileScreenDestination.route) {
            popUpTo(0)
        }
    }

    Box {
        if (!isShowNavigation(currentRoute)) {
            return@Box
        }

        BottomAppBar(
            cutoutShape = CircleShape,
            backgroundColor = Teal900,
            elevation = 3.dp
        ) {
            BottomNavigationItem(
                selected = false,
                onClick = { /*TODO*/ },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = "MyHome",
                        modifier = Modifier
                            .height(20.dp)
                            .width(20.dp).clickable { navigateToMyHome() },
                        tint = getColor(activeMyHouse)
                    )
                },
                label = {
                    Text(text = "우리 집", modifier = Modifier.padding(top = 14.dp), color = getColor(activeMyHouse))
                },
                alwaysShowLabel = true,
                enabled = true
            )

            BottomNavigationItem(
                selected = false,
                onClick = { /*TODO*/ },
                icon = {},
                enabled = false
            )

            BottomNavigationItem(
                selected = false,
                onClick = { /*TODO*/ },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "MyProfile",
                        modifier = Modifier
                            .height(20.dp)
                            .width(20.dp).clickable { navigateToMyProfile() },
                        tint = getColor(activeMyProfile)
                    )
                },
                label = {
                    Text(text = "내 정보", modifier = Modifier.padding(top = 14.dp), color = getColor(activeMyProfile))
                },
                alwaysShowLabel = true,
                enabled = true
            )
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Preview
@Composable
private fun NavigationPreview(){
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        bottomBar = { Navigation() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /*TODO*/ },
                contentColor = Teal100,
                backgroundColor = Teal900,
                modifier = Modifier
                    .width(80.dp)
                    .height(80.dp)
            ) {
                Icon(
                    imageVector = MyIconPack.MoonStarsFillIcon185549,
                    contentDescription = null,
                    modifier = Modifier
                        .height(45.dp)
                        .width(45.dp)
                )
            }
        },
        scaffoldState = scaffoldState,
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center
    ){}
}