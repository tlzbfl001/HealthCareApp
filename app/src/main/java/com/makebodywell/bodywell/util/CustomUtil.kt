package com.makebodywell.bodywell.util

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.model.Item
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

      fun getFoodKcal(context: Context, date:String) : Item {
         val dataManager = DataManager(context)
         dataManager.open()

         val text = Item()

         var sum = 0
         val getFood1 = dataManager.getFood(1, date)
         val getFood2 = dataManager.getFood(2, date)
         val getFood3 = dataManager.getFood(3, date)
         val getFood4 = dataManager.getFood(4, date)

         for(i in 0 until getFood1.size) {
            sum += getFood1[i].kcal!!.toInt() * getFood1[i].amount
            text.int1 = text.int1.plus(getFood1[i].kcal!!.toInt() * getFood1[i].amount)
         }
         for(i in 0 until getFood2.size) {
            sum += getFood2[i].kcal!!.toInt() * getFood2[i].amount
            text.int2 = text.int2.plus(getFood2[i].kcal!!.toInt() * getFood2[i].amount)
         }
         for(i in 0 until getFood3.size) {
            sum += getFood3[i].kcal!!.toInt() * getFood3[i].amount
            text.int3 = text.int3.plus(getFood3[i].kcal!!.toInt() * getFood3[i].amount)
         }
         for(i in 0 until getFood4.size) {
            sum += getFood4[i].kcal!!.toInt() * getFood4[i].amount
            text.int4 = text.int4.plus(getFood4[i].kcal!!.toInt() * getFood4[i].amount)
         }

         text.int5 = sum

         return text
      }

      fun getNutrition(context: Context, date:String) : Food {
         val dataManager = DataManager(context)
         dataManager.open()

         val getFood1 = dataManager.getFood(1, date)
         val getFood2 = dataManager.getFood(2, date)
         val getFood3 = dataManager.getFood(3, date)
         val getFood4 = dataManager.getFood(4, date)

         var carbohydrate = 0.0
         var protein = 0.0
         var fat = 0.0
         var sugar = 0.0

         for(i in 0 until getFood1.size) {
            carbohydrate += getFood1[i].carbohydrate!!.toDouble() * getFood1[i].amount
            protein += getFood1[i].protein!!.toDouble() * getFood1[i].amount
            fat += getFood1[i].fat!!.toDouble() * getFood1[i].amount
            sugar += getFood1[i].sugar!!.toDouble() * getFood1[i].amount
         }
         for(i in 0 until getFood2.size) {
            carbohydrate += getFood2[i].carbohydrate!!.toDouble() * getFood2[i].amount
            protein += getFood2[i].protein!!.toDouble() * getFood2[i].amount
            fat += getFood2[i].fat!!.toDouble() * getFood2[i].amount
            sugar += getFood2[i].sugar!!.toDouble() * getFood2[i].amount
         }
         for(i in 0 until getFood3.size) {
            carbohydrate += getFood3[i].carbohydrate!!.toDouble() * getFood3[i].amount
            protein += getFood3[i].protein!!.toDouble() * getFood3[i].amount
            fat += getFood3[i].fat!!.toDouble() * getFood3[i].amount
            sugar += getFood3[i].sugar!!.toDouble() * getFood3[i].amount
         }
         for(i in 0 until getFood4.size) {
            carbohydrate += getFood4[i].carbohydrate!!.toDouble() * getFood4[i].amount
            protein += getFood4[i].protein!!.toDouble() * getFood4[i].amount
            fat += getFood4[i].fat!!.toDouble() * getFood4[i].amount
            sugar += getFood4[i].sugar!!.toDouble() * getFood4[i].amount
         }

         return Food(unit = (carbohydrate+protein+fat+sugar).toString(), carbohydrate = carbohydrate.toString(),
            protein = protein.toString(), fat = fat.toString(), sugar = sugar.toString())
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