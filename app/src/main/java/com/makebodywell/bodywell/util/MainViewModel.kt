package com.makebodywell.bodywell.util

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.model.Token
import com.makebodywell.bodywell.model.User

class MainViewModel(application: Application) : AndroidViewModel(application) {
   private val context = application.applicationContext
   private var dataManager: DataManager? = null
   private var user = User()
   private var token = Token()
   private var accessCheck = false
   private var loginCheck = false

   init {
      dataManager = DataManager(context)
      dataManager!!.open()
   }
}