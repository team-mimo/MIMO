package com.mimo.android.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Modal(
    onClose: (() -> Unit)? = null,
    children: @Composable () -> Unit
){
    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = { onClose?.invoke() },
    ) {
        children()
    }
}