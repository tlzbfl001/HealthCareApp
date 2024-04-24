package kr.bodywell.android.util

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class MyApp : Application() {
   companion object {
      lateinit var prefs: PreferenceUtil
   }

   override fun onCreate() {
      super.onCreate()

      // KaKao SDK  초기화
      KakaoSdk.init(this, kr.bodywell.android.BuildConfig.KAKAO_NATIVE_APP_KEY)

      prefs = PreferenceUtil(applicationContext)
   }
}