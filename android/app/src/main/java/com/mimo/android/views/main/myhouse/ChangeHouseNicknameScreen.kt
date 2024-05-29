package com.mimo.android.views.main.myhouse

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import com.mimo.android.components.*
import com.mimo.android.components.base.Size
import com.mimo.android.views.MyHouseScreenDestination
import com.mimo.android.viewmodels.MyHouseViewModel

@Composable
fun ChangeHouseNicknameScreen(
    navController: NavHostController? = null,
    houseId: Long,
    myHouseViewModel: MyHouseViewModel
){
    val myHouseUiState by myHouseViewModel.uiState.collectAsState()
    val house = myHouseViewModel.queryHouse(myHouseUiState, houseId)
    var inputText by remember { mutableStateOf("") }

    fun handleChange(newText: String){
        inputText = newText
    }

    fun handleComplete(){
        if (inputText.isEmpty()) {
            return
        }
        myHouseViewModel.fetchChangeHouseNickname(
            house = house!!,
            newNickname = inputText,
            cb = {
                navController?.navigate(MyHouseScreenDestination.route) {
                    popUpTo(0)
                }
            }
        )
    }

    fun handlePrev(){
        navController?.navigateUp()
    }

    BackHandler {
        handlePrev()
    }

    Column {
        Icon(imageVector = Icons.Filled.ArrowBack, onClick = ::handlePrev)
        Spacer(modifier = Modifier.padding(14.dp))

        HeadingLarge(text = "집의 별칭을 지어주세요", fontSize = Size.lg)

        Spacer(modifier = Modifier.padding(16.dp))

        FunnelInput(
            text = inputText,
            onChange = { newText ->  handleChange(newText) },
            onClear = { handleChange("") },
            placeholder = house!!.nickname,
            description = "장소 별칭",
        )
        Spacer(modifier = Modifier.padding(16.dp))

        Button(text = "변경하기", onClick = ::handleComplete)
    }
}