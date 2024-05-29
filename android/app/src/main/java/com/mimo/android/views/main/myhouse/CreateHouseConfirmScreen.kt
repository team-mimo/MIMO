package com.mimo.android.views.main.myhouse

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mimo.android.MainActivity
import com.mimo.android.components.Button
import com.mimo.android.components.HeadingLarge
import com.mimo.android.components.HeadingSmall
import com.mimo.android.components.Icon
import com.mimo.android.components.Text
import com.mimo.android.components.base.Size
import com.mimo.android.ui.theme.Teal100
import com.mimo.android.viewmodels.MyHouseViewModel
import com.mimo.android.viewmodels.UserLocation

@Composable
fun CreateHouseConfirmScreen(
    navController: NavHostController? = null,
    launchGoogleLocationAndAddress: ((cb: (userLocation: UserLocation?) -> Unit) -> Unit)? = null,
    myHouseViewModel: MyHouseViewModel? = null
){
    var address by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        launchGoogleLocationAndAddress?.invoke { userLocation ->
            address = userLocation?.address
        }
    }

    fun handleGoPrev(){
        navController?.navigateUp()
    }

    fun handleFetchCreateNewHouse(){
        if (address == null) {
            return
        }

        myHouseViewModel?.fetchCreateNewHouse(
            address = address!!,
            onSuccessCallback = {
                Toast.makeText(
                    MainActivity.getMainActivityContext(),
                    "새로운 집을 추가했어요",
                    Toast.LENGTH_SHORT
                ).show()
                handleGoPrev()
            },
            onFailureCallback = {
                Toast.makeText(
                    MainActivity.getMainActivityContext(),
                    "다시 시도해주세요",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    BackHandler {
        handleGoPrev()
    }

    Column {
        Icon(imageVector = Icons.Filled.ArrowBack, onClick = ::handleGoPrev)
        Spacer(modifier = Modifier.padding(14.dp))

        if (address == null) {
            Text(text = "현재 위치를 불러오고 있어요...", color = Teal100)
            return@Column
        } else {
            HeadingLarge(text = "현재 위치로 집을 추가할까요?", fontSize = Size.lg)
            Spacer(modifier = Modifier.padding(8.dp))
            HeadingSmall(text = address!!, color = Teal100, fontSize = Size.sm)
            Spacer(modifier = Modifier.padding(16.dp))
            Button(text = "추가하기", onClick = ::handleFetchCreateNewHouse)
        }
    }
}

@Preview
@Composable
fun CreateHouseConfirmScreenPreview(){
    CreateHouseConfirmScreen()
}