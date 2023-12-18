package com.makebodywell.bodywell.util

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.view.init.InputActivity
import com.makebodywell.bodywell.view.init.MainActivity

class CustomUtil {
   companion object {
      const val TAG = "testTag"

      fun replaceInputFragment(activity: FragmentActivity, fragment: Fragment?) {
         (activity as InputActivity).supportFragmentManager.beginTransaction().apply {
            replace(R.id.inputFrame, fragment!!)
            commit()
         }
      }

      fun replaceFragment1(activity: Activity, fragment: Fragment?) {
         (activity as MainActivity).supportFragmentManager.beginTransaction().apply {
            replace(R.id.mainFrame, fragment!!)
            commit()
         }
      }

      fun replaceFragment2(activity: Activity, fragment: Fragment?, bundle: Bundle?) {
         (activity as MainActivity).supportFragmentManager.beginTransaction().apply {
            fragment?.arguments = bundle
            add(R.id.mainFrame, fragment!!)
            commit()
         }
      }

      fun getFoodIntake(context: Context, date:String) : Int {
         val dataManager = DataManager(context)
         dataManager.open()

         var sum = 0
         val getBreakfast = dataManager.getFood("breakfast", date)
         val getLunch = dataManager.getFood("lunch", date)
         val getDinner = dataManager.getFood("dinner", date)
         val getSnack = dataManager.getFood("snack", date)

         if(getBreakfast.size > 0) {
            for(i in 0 until getBreakfast.size) {
               sum += getBreakfast[i].kcal!!.toInt() * getBreakfast[i].amount
            }
         }
         if(getLunch.size > 0) {
            for(i in 0 until getLunch.size) {
               sum += getLunch[i].kcal!!.toInt() * getLunch[i].amount
            }
         }
         if(getDinner.size > 0) {
            for(i in 0 until getDinner.size) {
               sum += getDinner[i].kcal!!.toInt() * getDinner[i].amount
            }
         }
         if(getSnack.size > 0) {
            for(i in 0 until getSnack.size) {
               sum += getSnack[i].kcal!!.toInt() * getSnack[i].amount
            }
         }

         return sum
      }

      fun getExerciseCalories(context: Context, date:String) : Int {
         val dataManager = DataManager(context)
         dataManager.open()

         var sum = 0
         val getExercise = dataManager.getExercise(date)

         if(getExercise.size > 0) {
            for(i in 0 until getExercise.size) {
               sum += getExercise[i].calories!!
            }
         }

         return sum
      }
   }
}