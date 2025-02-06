package kr.bodywell.health.util

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import kr.bodywell.health.R
import kr.bodywell.health.api.powerSync.SyncService
import kr.bodywell.health.database.DataManager

class MyApp : Application() {
   companion object {
      lateinit var prefs: PreferenceUtil
      lateinit var dataManager: DataManager
      lateinit var powerSync: SyncService
   }

   override fun onCreate() {
      super.onCreate()

      KakaoSdk.init(this, resources.getString(R.string.kakaoNativeAppKey)) // KaKao SDK 초기화

      prefs = PreferenceUtil(applicationContext)

      dataManager = DataManager(applicationContext)
      dataManager.open()
   }
}