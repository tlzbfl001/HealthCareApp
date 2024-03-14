package com.makebodywell.bodywell.util

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.apollographql.apollo3.ApolloClient
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.model.DrugTime
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.model.Item
import com.makebodywell.bodywell.view.home.MainActivity

class CustomUtil {
   companion object {
      const val TAG = "testTag"
      val apolloClient = ApolloClient.Builder().serverUrl("https://api.bodywell.dev/graphql").build()
      var drugTimeList = ArrayList<DrugTime>()

      fun replaceFragment1(activity: Activity, fragment: Fragment?) {
         (activity as MainActivity).supportFragmentManager.beginTransaction().apply {
            replace(R.id.mainFrame, fragment!!)
            commit()
         }
      }

      fun replaceFragment2(activity: Activity, fragment: Fragment?, bundle: Bundle?) {
         (activity as MainActivity).supportFragmentManager.beginTransaction().apply {
            fragment?.arguments = bundle
            replace(R.id.mainFrame, fragment!!)
            commit()
         }
      }

      fun networkStatusCheck(context: Context): Boolean {
         val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
         val networkCapabilities = connectivityManager.activeNetwork ?: return false
         val activeNetwork = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
         val result = when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
         }

         return result
      }

      fun hideKeyboard(activity: Activity) {
         if(activity.currentFocus != null) {
            val inputManager: InputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
         }
      }

      fun getFoodCalories(context: Context, date:String) : Item {
         val dataManager = DataManager(context)
         dataManager.open()

         val item = Item()

         var sum = 0
         val getFood1 = dataManager.getDailyFood(1, date)
         val getFood2 = dataManager.getDailyFood(2, date)
         val getFood3 = dataManager.getDailyFood(3, date)
         val getFood4 = dataManager.getDailyFood(4, date)

         for(i in 0 until getFood1.size) {
            sum += getFood1[i].kcal * getFood1[i].count
            item.int1 = item.int1.plus(getFood1[i].kcal * getFood1[i].count)
         }
         for(i in 0 until getFood2.size) {
            sum += getFood2[i].kcal * getFood2[i].count
            item.int2 = item.int2.plus(getFood2[i].kcal * getFood2[i].count)
         }
         for(i in 0 until getFood3.size) {
            sum += getFood3[i].kcal * getFood3[i].count
            item.int3 = item.int3.plus(getFood3[i].kcal * getFood3[i].count)
         }
         for(i in 0 until getFood4.size) {
            sum += getFood4[i].kcal * getFood4[i].count
            item.int4 = item.int4.plus(getFood4[i].kcal * getFood4[i].count)
         }

         item.int5 = sum

         return item
      }

      fun getNutrition(context: Context, date:String) : Food {
         val dataManager = DataManager(context)
         dataManager.open()

         val getFood1 = dataManager.getDailyFood(1, date)
         val getFood2 = dataManager.getDailyFood(2, date)
         val getFood3 = dataManager.getDailyFood(3, date)
         val getFood4 = dataManager.getDailyFood(4, date)

         var carbohydrate = 0.0
         var protein = 0.0
         var fat = 0.0
         var sugar = 0.0

         for(i in 0 until getFood1.size) {
            carbohydrate += getFood1[i].carbohydrate * getFood1[i].count
            protein += getFood1[i].protein * getFood1[i].count
            fat += getFood1[i].fat * getFood1[i].count
            sugar += getFood1[i].sugar * getFood1[i].count
         }
         for(i in 0 until getFood2.size) {
            carbohydrate += getFood2[i].carbohydrate * getFood2[i].count
            protein += getFood2[i].protein * getFood2[i].count
            fat += getFood2[i].fat * getFood2[i].count
            sugar += getFood2[i].sugar * getFood2[i].count
         }
         for(i in 0 until getFood3.size) {
            carbohydrate += getFood3[i].carbohydrate * getFood3[i].count
            protein += getFood3[i].protein * getFood3[i].count
            fat += getFood3[i].fat * getFood3[i].count
            sugar += getFood3[i].sugar * getFood3[i].count
         }
         for(i in 0 until getFood4.size) {
            carbohydrate += getFood4[i].carbohydrate * getFood4[i].count
            protein += getFood4[i].protein * getFood4[i].count
            fat += getFood4[i].fat * getFood4[i].count
            sugar += getFood4[i].sugar * getFood4[i].count
         }

         return Food(carbohydrate = carbohydrate, protein = protein, fat = fat, sugar = sugar, salt = carbohydrate+protein+fat+sugar)
      }

      fun getExerciseCalories(context: Context, date:String) : Int {
         val dataManager = DataManager(context)
         dataManager.open()

         var sum = 0
         val getExercise = dataManager.getDailyExercise(date)

         if(getExercise.size > 0) {
            for(i in 0 until getExercise.size) {
               sum += getExercise[i].kcal
            }
         }

         return sum
      }

      fun setDrugTimeList(h: Int, m: Int) {
         drugTimeList.add(DrugTime(hour = h, minute = m))
      }
   }
}