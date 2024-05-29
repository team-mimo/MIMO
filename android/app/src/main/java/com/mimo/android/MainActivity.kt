package com.mimo.android

import android.Manifest
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import com.journeyapps.barcodescanner.ScanContract
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import com.mimo.android.apis.createMimoApiService
import com.mimo.android.viewmodels.AuthViewModel
import com.mimo.android.viewmodels.FirstSettingFunnelsViewModel
import com.mimo.android.viewmodels.QrCodeViewModel
import com.mimo.android.viewmodels.UserLocation
import com.mimo.android.services.health.*
import com.mimo.android.services.gogglelocation.*
import com.mimo.android.services.kakao.initializeKakaoSdk
import com.mimo.android.services.qrcode.*
import com.mimo.android.utils.backpresshandler.initializeWhenTwiceBackPressExitApp
import com.mimo.android.utils.os.printKeyHash
import com.mimo.android.utils.preferences.createSharedPreferences
import com.mimo.android.utils.showToast
import com.mimo.android.viewmodels.*

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    // health-connect
    private lateinit var healthConnectManager: HealthConnectManager
    private lateinit var healthConnectPermissionRequest: ActivityResultLauncher<Set<String>>

    private val authViewModel = AuthViewModel()
    private val firstSettingFunnelsViewModel = FirstSettingFunnelsViewModel()
    private val qrCodeViewModel = QrCodeViewModel()
    private val myHouseViewModel = MyHouseViewModel()
    private val myHouseDetailViewModel = MyHouseDetailViewModel()
    private val myHouseHubListViewModel = MyHouseHubListViewModel()
    private val myProfileViewModel = MyProfileViewModel()
    private val myHouseCurtainViewModel = MyHouseCurtainViewModel()
    private val myHouseLampViewModel = MyHouseLampViewModel()
    private val myHouseLightViewModel = MyHouseLightViewModel()
    private val myHouseWindowViewModel = MyHouseWindowViewModel()
    private val sleepViewModel = SleepViewModel()

    // 초기세팅용 QR code Scanner
    private val barCodeLauncherFirstSetting = registerForActivityResult(ScanContract()) {
            result ->
        if (result.contents == null) {
            qrCodeViewModel.removeQrCode()
            showToast("취소")
            return@registerForActivityResult
        }
        showToast("허브를 찾고 있어요")
        qrCodeViewModel.initRegisterFirstSetting(qrCode = result.contents)
        firstSettingFunnelsViewModel.updateCurrentStep(stepId = R.string.fsfunnel_waiting)
    }
    private val qRRequestPermissionLauncherFirstSetting = createQRRequestPermissionLauncher(
        barCodeLauncher = barCodeLauncherFirstSetting
    )

    // 허브를 집에 등록하는 QR code Scanner
    private val barCodeLauncherHubToHouse = registerForActivityResult(ScanContract()) {
            result ->
        if (result.contents == null) {
            qrCodeViewModel.removeQrCode()
            showToast("취소")
            return@registerForActivityResult
        }
        qrCodeViewModel.registerHubToHouse(qrCode = result.contents)
    }
    private val qRRequestPermissionLauncherHubToHouse = createQRRequestPermissionLauncher(
        barCodeLauncher = barCodeLauncherHubToHouse
    )

    // 기기를 허브에 등록하는 QR code Scanner
    private val barCodeLauncherMachineToHub = registerForActivityResult(ScanContract()) {
            result ->
        if (result.contents == null) {
            qrCodeViewModel.removeQrCode()
            showToast("취소")
            return@registerForActivityResult
        }
        // TODO...
    }
    private val qRRequestPermissionLauncherMachineToHub = createQRRequestPermissionLauncher(
        barCodeLauncher = barCodeLauncherMachineToHub
    )

    init {
        instance = this
    }

    companion object {
        lateinit var instance: MainActivity
        fun getMainActivityContext(): Context {
            return instance.applicationContext
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        printKeyHash(this)
        initializeWhenTwiceBackPressExitApp(this) // 안드로이드OS 뒤로가기 연속 2번 누르면 앱을 종료시키는 핸들러 추가
        initializeKakaoSdk(this) // kakao sdk 초기화
        createSharedPreferences() // 스토리지 초기화
        createMimoApiService() // mimo api 초기화

        // health-connect 권한 요청
        healthConnectManager = (application as BaseApplication).healthConnectManager
        if (checkAvailability()) {
            healthConnectPermissionRequest = createHealthConnectPermissionRequest(
                healthConnectManager = healthConnectManager,
                context = this
            )
            checkHealthConnectPermission(
                showInfo = false,
                healthConnectManager = healthConnectManager,
                context = this,
                healthConnectPermissionRequest = healthConnectPermissionRequest
            )
        }

        // 위치 권한 요청
        RequestPermissionsUtil(this).requestLocation()

        // 포그라운드 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }

        // TODO: 개발 중에는 잠시 주석
        // Notification Permission
        if (!isSleepNotificationPermissionGranted()) {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }

        authViewModel.checkAlreadyLoggedIn(
            firstSettingFunnelsViewModel = firstSettingFunnelsViewModel
        )

        setContent {
            MimoApp(
                context = this,
                isActiveSleepForegroundService = isActiveSleepForegroundService,
                authViewModel = authViewModel,
                healthConnectManager = healthConnectManager,
                qrCodeViewModel = qrCodeViewModel,
                firstSettingFunnelsViewModel = firstSettingFunnelsViewModel,
                myHouseViewModel = myHouseViewModel,
                myHouseDetailViewModel = myHouseDetailViewModel,
                myHouseHubListViewModel = myHouseHubListViewModel,
                myProfileViewModel = myProfileViewModel,
                myHouseCurtainViewModel = myHouseCurtainViewModel,
                myHouseLampViewModel = myHouseLampViewModel,
                myHouseLightViewModel = myHouseLightViewModel,
                myHouseWindowViewModel = myHouseWindowViewModel,
                sleepViewModel = sleepViewModel,
                launchGoogleLocationAndAddress = { cb: (userLocation: UserLocation?) -> Unit -> launchGoogleLocationAndAddress(cb = cb) },
                onStartSleepForegroundService = ::handleStartSleepForegroundService,
                onStopSleepForegroundService = ::handleStopSleepForegroundService,
                checkCameraPermissionFirstSetting = { checkCameraPermission(
                    context = this,
                    barCodeLauncher = barCodeLauncherFirstSetting,
                    qRRequestPermissionLauncher = qRRequestPermissionLauncherFirstSetting
                ) },
                checkCameraPermissionHubToHouse = { checkCameraPermission(
                    context = this,
                    barCodeLauncher = barCodeLauncherHubToHouse,
                    qRRequestPermissionLauncher = qRRequestPermissionLauncherHubToHouse
                ) },
                checkCameraPermissionMachineToHub = { checkCameraPermission(
                    context = this,
                    barCodeLauncher = barCodeLauncherMachineToHub,
                    qRRequestPermissionLauncher = qRRequestPermissionLauncherMachineToHub
                ) },
            )
        }
    }

    private var isActiveSleepForegroundService by mutableStateOf(false)
//    // private var job: Job? = null
//    private var timerTask: TimerTask? = null

    private fun handleStartSleepForegroundService(){
        if (isActiveSleepForegroundService) {
            return
        }

        Intent(getMainActivityContext(), SleepForegroundService::class.java).also {
            it.action = SleepForegroundService.Actions.START.toString()
            startService(it)

//            //job = createJob()
//            timerTask = Task()
//            Timer().scheduleAtFixedRate(timerTask, 1000, FIFTEEN_MINUTES)
            isActiveSleepForegroundService = true
        }
    }
    private fun handleStopSleepForegroundService(){
        if (!isActiveSleepForegroundService) {
            return
        }

        Intent(applicationContext, SleepForegroundService::class.java).also {
            it.action = SleepForegroundService.Actions.STOP.toString()
            startService(it)
//            //job?.cancel()
//            timerTask?.cancel()
//            timerTask = null
            isActiveSleepForegroundService = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "App destroy")
        handleStopSleepForegroundService()
    }

//    private fun createJob(): Job{
//        return scope.launch {
//            while (true) {
//                //readLastSleepStage()
//                //readSleepSession(4, 28) // 4월 28일의 기록
//                readSteps()
//                delay(15 * 60 * 1000L) // 15분
//            }
//        }
//    }

//    inner class Task: TimerTask() {
//        override fun run() {
//            lifecycleScope.launch {
//                //readFifteenMinutesAgoSleepStage()
//                readLastSleepStage()
//                if (timerTask == null) {
//                    this.cancel()
//                    return@launch
//                }
//            }
//        }
//
//        override fun cancel(): Boolean {
//            return super.cancel()
//        }
//    }

//    private suspend fun readFifteenMinutesAgoSleepStage(){
//        val now = Instant.now()
//        val fifteenMinutesAgo = now.minus(15, ChronoUnit.MINUTES)
//        val lastSleepStage = healthConnectManager.readLastSleepStage(fifteenMinutesAgo, now)
//        if (lastSleepStage == null) {
//            Log.d(TAG, "MIMO가 감지 중 @@ ${dateFormatter.format(fifteenMinutesAgo)} ~ ${dateFormatter.format(now)} @@ 수면기록이 감지되지 않음")
//            return
//        }
//        Log.d(TAG, "MIMO가 감지 중 @@ ${getCurrentTime()} @@ ${dateFormatter.format(lastSleepStage.startTime)} ~ ${dateFormatter.format(lastSleepStage.endTime)} @@ ${meanStage(lastSleepStage.stage)}")
//    }
//
//    private suspend fun readSleepSession(
//        month: Int,
//        dayOfMonth: Int,
//    ){
//        val startTime = ZonedDateTime.of(2024, month, dayOfMonth, 0, 0, 0, 0, ZoneId.of("Asia/Seoul"))
//        val endTime = ZonedDateTime.of(2024, month, dayOfMonth, 23, 59, 59, 0, ZoneId.of("Asia/Seoul"))
//
//        val sleepSessionRecord = healthConnectManager.readSleepSessionRecordList(startTime.toInstant(), endTime.toInstant())
//
//        if (sleepSessionRecord == null) {
//            Log.d(TAG, "MIMO가 감지 중")
//            Log.d(TAG, "${dateFormatter.format(startTime)} ~ ${dateFormatter.format(endTime)} 까지 수면기록 없음")
//            return
//        }
//        sleepSessionRecord.forEachIndexed() { sessionIndex, session ->
//            val koreanStartTime = dateFormatter.format(session.startTime)
//            val koreanEndTime = dateFormatter.format(session.endTime)
//            Log.d(TAG, "@@@@@@@ 상세 수면 기록 @@@@@@@")
//            Log.d(TAG, "수면 ${sessionIndex + 1} 전체 : $koreanStartTime ~ $koreanEndTime")
//            session.stages.forEach() { stage ->
//                Log.d(TAG, "${dateFormatter.format(stage.startTime)} ~ ${dateFormatter.format(stage.endTime)} @@ ${meanStage(stage.stage)}")
//            }
//        }
//    }
//
//    private suspend fun readLastSleepStage(){
//        val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
//        val now = Instant.now()
//        val lastSleepStage = healthConnectManager.readLastSleepStage(startOfDay.toInstant(), now)
//        if (lastSleepStage == null) {
//            Log.d(TAG, "MIMO가 감지 중 @@ ${getCurrentTime()} @@ 수면기록이 감지되지 않음")
//            postSleepData(
//                accessToken = getData(ACCESS_TOKEN) ?: "",
//                postSleepDataRequest = PostSleepDataRequest(
//                    sleepLevel = -1
//                )
//            )
//            return
//        }
//        Log.d(TAG, "MIMO가 감지 중 @@ ${getCurrentTime()} @@ ${dateFormatter.format(lastSleepStage.startTime)} ~ ${dateFormatter.format(lastSleepStage.endTime)} @@ ${meanStage(lastSleepStage.stage)}")
//        postSleepData(
//            accessToken = getData(ACCESS_TOKEN) ?: "",
//            postSleepDataRequest = PostSleepDataRequest(
//                sleepLevel = lastSleepStage.stage
//            )
//        )
//    }
//
//    private suspend fun readSteps(){
//        val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
//        val now = Instant.now()
//        val step = healthConnectManager.readSteps(startOfDay.toInstant(), now)
//        Log.d(TAG, "MIMO가 감지 중 @@ ${getCurrentTime()} @@ ${step}")
//    }
//
//    private fun getCurrentTime(): String{
//        val zoneId = ZoneId.of("Asia/Seoul") // 한국 시간대 (KST)
//        val currentTimeKST = ZonedDateTime.now(zoneId) // 현재 한국 시간
//
//        // 월, 일, 시, 분, 초 추출
//        val month = currentTimeKST.monthValue
//        val day = currentTimeKST.dayOfMonth
//        val hour = currentTimeKST.hour
//        val minute = currentTimeKST.minute
//        val second = currentTimeKST.second
//
//        // 형식 지정
//        val formatter = DateTimeFormatter.ofPattern("M월 d일 H시 m분 s초")
//
//        // 포맷에 따라 날짜 및 시간을 문자열로 변환하여 반환
//        return currentTimeKST.format(formatter)
//    }
}

//const val FIFTEEN_MINUTES = 15 * 60 * 1000L
//
