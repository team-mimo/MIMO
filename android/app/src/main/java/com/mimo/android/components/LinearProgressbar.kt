package com.mimo.android.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mimo.android.ui.theme.Teal400
import com.mimo.android.ui.theme.Teal900

@Composable
fun LinearProgressbar(){
    Column {
        Spacer(modifier = Modifier.padding(4.dp))
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth().height(2.dp),
            color = Teal900,
            trackColor = Teal400
        )
        Spacer(modifier = Modifier.padding(8.dp))
    }
}

@Composable
fun LinearProgressbar2(){
    Column {
        Spacer(modifier = Modifier.padding(10.dp))
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth().height(2.dp),
            color = Teal900,
            trackColor = Teal400
        )
        Spacer(modifier = Modifier.padding(3.dp))
    }
}