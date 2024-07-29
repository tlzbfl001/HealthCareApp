package kr.bodywell.android.util

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import kr.bodywell.android.R
import kr.bodywell.android.database.DBHelper.Companion.CREATED_AT
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.DrugTime
import kr.bodywell.android.model.Food
import kr.bodywell.android.model.Item
import kr.bodywell.android.view.home.MainActivity
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

class CustomUtil {
   companion object {
      const val TAG = "logTAG"
      val isoFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
      var dataType = 1
      var drugTimeList = ArrayList<DrugTime>()

      fun replaceFragment1(activity: Activity, fragment: Fragment?) {
         (activity as MainActivity).supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.enter_to_right)
            replace(R.id.mainFrame, fragment!!)
            commit()
         }
      }

      fun replaceFragment2(activity: Activity, fragment: Fragment?, bundle: Bundle?) {
         (activity as MainActivity).supportFragmentManager.beginTransaction().apply {
            fragment?.arguments = bundle
            setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.enter_to_right)
            replace(R.id.mainFrame, fragment!!)
            commit()
         }
      }

      fun replaceFragment3(activity: Activity, fragment: Fragment?) {
         (activity as MainActivity).supportFragmentManager.beginTransaction().apply {
            replace(R.id.mainFrame, fragment!!)
            commit()
         }
      }

      fun replaceFragment4(activity: Activity, fragment: Fragment?, bundle: Bundle?) {
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

      fun dateToIso(date: String): String {
         return LocalDate.parse(date).atStartOfDay().format(isoFormatter)
      }

      fun dateTimeToIso(date: LocalDateTime): String {
         return date.atZone(ZoneId.of("Asia/Seoul")).toInstant().toString()
      }

      fun isoToDateTime(date: String): LocalDateTime {
         return LocalDateTime.parse(date, isoFormatter)
      }

      fun filterText(text: String): Boolean {
         val pattern = "^[ㄱ-ㅣ가-힣a-zA-Z0-9]+\$" // 한글, 영문, 숫자 패턴
         val compile = Pattern.compile(pattern)
         val match = compile.matcher(text)
         return match.find()
      }

      fun getFoodCalories(context: Context, date:String) : Item {
         val dataManager = DataManager(context)
         dataManager.open()

         var sum = 0
         val item = Item()
         val getDailyFood1 = dataManager.getDailyFood("BREAKFAST", date)
         val getDailyFood2 = dataManager.getDailyFood("LUNCH", date)
         val getDailyFood3 = dataManager.getDailyFood("DINNER", date)
         val getDailyFood4 = dataManager.getDailyFood("SNACK", date)

         for(i in 0 until getDailyFood1.size) {
            sum += getDailyFood1[i].kcal * getDailyFood1[i].count
            item.int1 += getDailyFood1[i].kcal * getDailyFood1[i].count
         }
         for(i in 0 until getDailyFood2.size) {
            sum += getDailyFood2[i].kcal * getDailyFood2[i].count
            item.int2 += getDailyFood2[i].kcal * getDailyFood2[i].count
         }
         for(i in 0 until getDailyFood3.size) {
            sum += getDailyFood3[i].kcal * getDailyFood3[i].count
            item.int3 += getDailyFood3[i].kcal * getDailyFood3[i].count
         }
         for(i in 0 until getDailyFood4.size) {
            sum += getDailyFood4[i].kcal * getDailyFood4[i].count
            item.int4 += getDailyFood4[i].kcal * getDailyFood4[i].count
         }

         item.int5 = sum

         return item
      }

      fun getNutrition(context: Context, date:String) : Food {
         val dataManager = DataManager(context)
         dataManager.open()

         val getFood1 = dataManager.getDailyFood("BREAKFAST", date)
         val getFood2 = dataManager.getDailyFood("LUNCH", date)
         val getFood3 = dataManager.getDailyFood("DINNER", date)
         val getFood4 = dataManager.getDailyFood("SNACK", date)

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
         val getExercise = dataManager.getDailyExercise(CREATED_AT, date)

         if(getExercise.size > 0) {
            for(i in 0 until getExercise.size) {
               sum += getExercise[i].kcal
            }
         }

         return sum
      }

      fun setDrugTimeList(data: String) {
         drugTimeList.add(DrugTime(time = data))
      }
   }
}