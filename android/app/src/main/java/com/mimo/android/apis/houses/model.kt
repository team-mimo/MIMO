package com.mimo.android.apis.houses

data class PostRegisterHouseRequest(
    val address: String,
    val nickname: String
)

data class PostRegisterHouseResponse(
    val houseId: Long
)

data class PostAutoRegisterHubToHouseRequest(
    val serialNumber: String
)

data class PostAutoRegisterHubToHouseResponse(
    val houseId: Long?,
    val address: String?
)

data class PutChangeHouseNicknameRequest(
    val nickname: String
)

data class GetDeviceListByHouseIdResponse(
    val houseId: Long,
    val nickname: String,
    val address: String,
    val isHome: Boolean,
    val devices: List<Device>
)

data class House(
    val houseId: Long,
    val nickname: String,
    val address: String,
    val isHome: Boolean,
    val devices: List<String>
)

data class Device(
    val userId: Long,
    val hubId: Long,
    val deviceId: Long,
    val type: String,
    val nickname: String,
    val isAccessible: Boolean,
    val color: String? = "FFFFFF",
    val curColor: Long? = null,
    val openDegree: Long? = null
)