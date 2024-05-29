package com.mimo.android.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mimo.android.components.base.Size
import com.mimo.android.components.base.TextSizeMatcher
import com.mimo.android.ui.theme.Gray400
import com.mimo.android.ui.theme.Teal400
import com.mimo.android.ui.theme.Teal500
import kotlinx.coroutines.delay

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FunnelInput(
    text: String = "",
    onChange: ((newText: String) -> Unit)? = null,
    onClear: (() -> Unit)? = null,
    placeholder: String = "placeholder",
    description: String? = null,
){

    val keyboard = LocalSoftwareKeyboardController.current

    val handleChange: (newText: String) -> Unit = {
        newText -> onChange?.invoke(newText)
    }

    val handleClear: () -> Unit = {
        onClear?.invoke()
    }

    LaunchedEffect(key1 = Unit) {
        delay(100)
        keyboard?.show()
    }

    _FunnelInput(
        text = text,
        onChange = handleChange,
        onClear = handleClear,
        placeholder = placeholder,
        description = description
    )
}

@Composable
fun _FunnelInput(
    text: String,
    onChange: (newText: String) -> Unit,
    onClear: () -> Unit,
    placeholder: String,
    description: String?,
){
    val focusRequester = remember { FocusRequester() }

    Column {
        if (text.isNotEmpty() && description != null) {
            Text(text = description, color = Teal500, fontSize = Size.xs)
        } else {
            Spacer(modifier = Modifier.padding(9.dp))
        }

        BasicTextField(
            value = text,
            onValueChange = onChange,
            textStyle = TextStyle(
                color = Color.White,
                fontSize = TextSizeMatcher.getOrElse(Size.xl) { 28.sp },
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            singleLine = true,
            cursorBrush = SolidColor(Teal400),
            decorationBox = { innerTextField ->
                if (text.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = Gray400,
                        fontSize = Size.xl,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    innerTextField()

                    if (text.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Clear, color = Teal400,
                            onClick = onClear
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.padding(1.dp))
        HorizontalDivider(opacity = 1.0f)
    }

    SideEffect {
        focusRequester.requestFocus()
    }
}