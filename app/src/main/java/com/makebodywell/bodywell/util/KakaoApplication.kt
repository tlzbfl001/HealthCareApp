package com.makebodywell.bodywell.util

import android.app.Application
import com.makebodywell.bodywell.R
import com.kakao.sdk.common.KakaoSdk

class KakaoApplication : Application() {
   override fun onCreate() {
      super.onCreate()

      // KaKao SDK  초기화
      KakaoSdk.init(this, getString(R.string.nativeAppKey))
   }
}