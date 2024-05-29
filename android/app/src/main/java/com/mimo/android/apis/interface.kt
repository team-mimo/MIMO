package com.mimo.android.apis

import com.mimo.android.apis.controls.ControlsApiService
import com.mimo.android.apis.devices.curtain.CurtainApiService
import com.mimo.android.apis.devices.lamp.LampApiService
import com.mimo.android.apis.devices.light.LightApiService
import com.mimo.android.apis.devices.window.WindowApiService
import com.mimo.android.apis.houses.HousesApiService
import com.mimo.android.apis.hubs.HubsApiService
import com.mimo.android.apis.sleeps.SleepsApiService
import com.mimo.android.apis.users.UsersApiService

interface MimoApiService: UsersApiService, HousesApiService, HubsApiService, SleepsApiService, ControlsApiService, CurtainApiService, LampApiService, LightApiService, WindowApiService