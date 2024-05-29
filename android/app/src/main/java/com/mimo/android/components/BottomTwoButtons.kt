package com.mimo.android.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.mimo.android.ui.theme.Gray600

@Composable
fun BottomTowButtons(){
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Button(
                text = "닫기", color = Gray600, hasBorder = false,
                onClick = {}
            )
        }
        item {
            Button(text = "변경할게요", onClick = {})
        }
    }
}