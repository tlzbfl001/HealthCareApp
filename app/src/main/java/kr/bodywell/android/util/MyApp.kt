package kr.bodywell.android.util

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.powersync.DatabaseDriverFactory
import kr.bodywell.android.R
import kr.bodywell.android.api.PowerSync
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.Token
import kr.bodywell.android.model.User
import kr.bodywell.android.util.CustomUtil.TAG

class MyApp : Application() {
   companion object {
      lateinit var prefs: PreferenceUtil
      lateinit var powerSync: PowerSync
      lateinit var getUser: User
      lateinit var getToken: Token
   }

   override fun onCreate() {
      super.onCreate()

      // KaKao SDK 초기화
      KakaoSdk.init(this, resources.getString(R.string.kakaoNativeAppKey))

      prefs = PreferenceUtil(applicationContext)

      val dataManager = DataManager(applicationContext)
      dataManager.open()
      getUser = dataManager.getUser()
      getToken = dataManager.getToken()
      Log.d(TAG, "userId: ${getUser.uid}")
      Log.d(TAG, "access: ${getToken.access}")

      val driverFactory = DatabaseDriverFactory(applicationContext)
      powerSync = PowerSync(driverFactory)
   }
}