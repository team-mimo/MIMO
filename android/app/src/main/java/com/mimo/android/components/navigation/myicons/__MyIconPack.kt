package com.mimo.android.components.navigation.myicons

import androidx.compose.ui.graphics.vector.ImageVector
import com.mimo.android.components.navigation.myicons.myiconpack.MoonIcon224573
import com.mimo.android.components.navigation.myicons.myiconpack.MoonStarsFillIcon185549
import com.mimo.android.components.navigation.myicons.myiconpack.MoonStarsIcon184818
import com.mimo.android.components.navigation.myicons.myiconpack.WeatherMoonStarNightIcon259075
import com.mimo.android.components.navigation.myicons.myiconpack.`Wondicon-ui-free-sleep111270`
import kotlin.collections.List as ____KtList

public object MyIconPack

private var __AllIcons: ____KtList<ImageVector>? = null

public val MyIconPack.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= listOf(MoonStarsFillIcon185549, MoonStarsIcon184818, WeatherMoonStarNightIcon259075,
        MoonIcon224573, `Wondicon-ui-free-sleep111270`)
    return __AllIcons!!
  }
