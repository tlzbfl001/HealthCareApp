package com.makebodywell.bodywell.util

import android.app.Application
import com.makebodywell.bodywell.R
import com.kakao.sdk.common.KakaoSdk
import com.makebodywell.bodywell.BuildConfig

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