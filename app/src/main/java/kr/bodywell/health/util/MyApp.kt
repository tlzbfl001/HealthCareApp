package kr.bodywell.health.util

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import kr.bodywell.health.BuildConfig

class MyApp : Application() {
   companion object {
      lateinit var prefs: PreferenceUtil
   }

   override fun onCreate() {
      super.onCreate()

      // KaKao SDK  초기화
      KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)

      prefs = PreferenceUtil(applicationContext)
   }
}