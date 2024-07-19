package kr.bodywell.test.util

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import kr.bodywell.test.BuildConfig

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