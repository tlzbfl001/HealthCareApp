package kr.bodywell.android.util

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import kr.bodywell.android.R

class MyApp : Application() {
   companion object {
      lateinit var prefs: PreferenceUtil
   }

   override fun onCreate() {
      super.onCreate()

      // KaKao SDK  초기화
      KakaoSdk.init(this, resources.getString(R.string.kakaoNativeAppKey))

      prefs = PreferenceUtil(applicationContext)
   }
}