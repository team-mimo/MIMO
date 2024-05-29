package com.mimo.android.views.firstsettingfunnels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.mimo.android.viewmodels.FirstSettingFunnelsViewModel
import com.mimo.android.MainActivity
import com.mimo.android.viewmodels.QrCodeViewModel
import com.mimo.android.R
import com.mimo.android.viewmodels.UserLocation
import com.mimo.android.services.gogglelocation.RequestPermissionsUtil

private const val TAG = "FUNNEL_ROOT"

@Composable
fun FirstSettingFunnelsRoot(
    qrCodeViewModel: QrCodeViewModel,
    firstSettingFunnelsViewModel: FirstSettingFunnelsViewModel,
    checkCameraPermission: () -> Unit,
    launchGoogleLocationAndAddress: (cb: (userLocation: UserLocation?) -> Unit) -> Unit,
    context: Context
){
    FunnelMatcher(
            qrCodeViewModel = qrCodeViewModel,
            firstSettingFunnelsViewModel = firstSettingFunnelsViewModel,
            checkCameraPermission = checkCameraPermission,
            launchGoogleLocationAndAddress = launchGoogleLocationAndAddress,
            context = context
    )
}

@Composable
fun FunnelMatcher(
    qrCodeViewModel: QrCodeViewModel,
    firstSettingFunnelsViewModel: FirstSettingFunnelsViewModel,
    checkCameraPermission: () -> Unit,
    launchGoogleLocationAndAddress: (cb: (userLocation: UserLocation?) -> Unit) -> Unit,
    context: Context
){
    val firstSettingFunnelsUiState by firstSettingFunnelsViewModel.uiState.collectAsState()
    val qrCodeUiState by qrCodeViewModel.uiState.collectAsState()

    if (firstSettingFunnelsUiState.currentStepId == R.string.fsfunnel_start) {
        Start(
            checkCameraPermission = checkCameraPermission
        )
        return
    }

    if (firstSettingFunnelsUiState.currentStepId == R.string.fsfunnel_waiting) {
        Waiting(
            goNext = {
                val qrCode = qrCodeUiState.qrCode
                if (qrCode == null) {
                    Log.e(TAG, "QR CODE 없음...")
                    Toast.makeText(
                        context,
                        "다시 시도해주세요",
                        Toast.LENGTH_SHORT
                    ).show()
                    firstSettingFunnelsViewModel.updateCurrentStep(stepId = R.string.fsfunnel_start)
                    return@Waiting
                }

                firstSettingFunnelsViewModel.setHubAndRedirect(
                    qrCode = qrCode,
                    launchGoogleLocationAndAddress = launchGoogleLocationAndAddress
                )
            }
        )
        return
    }

    if (firstSettingFunnelsUiState.currentStepId == R.string.fsfunnel_directregister) {
        DirectRegister(
            hub = firstSettingFunnelsUiState.hub,
            goNext = {
                firstSettingFunnelsViewModel.redirectToMain()
            },
            redirectAfterCatchError = {
                Toast.makeText(
                    context,
                    "다시 시도해주세요",
                    Toast.LENGTH_SHORT
                ).show()
                firstSettingFunnelsViewModel.updateCurrentStep(stepId = R.string.fsfunnel_start)
            }
        )
        return
    }

    if (firstSettingFunnelsUiState.currentStepId == R.string.fsfunnel_confirmregister) {
        val userLocation = firstSettingFunnelsUiState.userLocation
        val userAddress = userLocation?.address

        // 위치권한이 없거나 모종의 이유로 위치를 받아올 수 없었다면...
        if (userAddress == null) {
            Toast.makeText(
                context,
                "앱의 위치권한을 켜주세요",
                Toast.LENGTH_SHORT
            ).show()
            // 처음 화면으로 그냥 이동시키고
            firstSettingFunnelsViewModel.updateCurrentStep(stepId = R.string.fsfunnel_start)
            // 다시 권한 묻기
            RequestPermissionsUtil(context).requestLocation()
            return
        }

        ConfirmRegister(
            location = userAddress,
            onConfirm = {
                val qrCode = qrCodeUiState.qrCode
                Log.i(TAG, "$qrCode")
                if (qrCode == null) {
                    Toast.makeText(
                        MainActivity.getMainActivityContext(),
                        "다시 시도해주세요",
                        Toast.LENGTH_SHORT
                    ).show()
                    firstSettingFunnelsViewModel.updateCurrentStep(stepId = R.string.fsfunnel_start)
                    return@ConfirmRegister
                }
                firstSettingFunnelsViewModel.registerNewHubAndRedirectToMain(qrCode = qrCode)
            }
        )
        return
    }
}