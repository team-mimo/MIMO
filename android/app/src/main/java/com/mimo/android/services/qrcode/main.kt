package com.mimo.android.services.qrcode

import androidx.activity.result.ActivityResultLauncher
import com.journeyapps.barcodescanner.ScanOptions

fun showCamera(barCodeLauncher: ActivityResultLauncher<ScanOptions>){
    val options = ScanOptions()
    options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
    options.setPrompt("QR코드를 스캔해주세요")
    options.setCameraId(0)
    options.setBeepEnabled(false)
    options.setOrientationLocked(true)
    barCodeLauncher.launch(options)
}