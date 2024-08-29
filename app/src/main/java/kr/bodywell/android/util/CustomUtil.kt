package kr.bodywell.android.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import kr.bodywell.android.R
import kr.bodywell.android.database.DBHelper.Companion.CREATED_AT
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.Constant
import kr.bodywell.android.model.DrugTime
import kr.bodywell.android.model.Food
import kr.bodywell.android.model.Item
import kr.bodywell.android.service.AlarmReceiver
import kr.bodywell.android.view.home.MainActivity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

object CustomUtil {
   const val TAG = "logTAG"
   var layoutType = 1
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

   fun dateToIso(date: String): String {
      return LocalDate.parse(date).atStartOfDay().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)
   }

   fun dateTimeToIso(date: LocalDateTime): String {
      return date.atZone(ZoneId.of("Asia/Seoul")).toInstant().toString()
   }

   fun isoToDateTime(date: String): LocalDateTime {
      return LocalDateTime.from(Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse(date)).atZone(ZoneId.of("Asia/Seoul")))
   }

   fun setStatusBar(context: Activity, mainLayout: ConstraintLayout) {
      context.window?.apply {
         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         mainLayout.setPadding(0, statusBarHeight, 0, 0)
      }
   }

   fun hideKeyboard(activity: Activity) {
      if(activity.currentFocus != null) {
         val inputManager: InputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
         inputManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
      }
   }

   fun filterText(text: String): Boolean {
      val pattern = "^[0-9a-zA-Zㄱ-ㅎ가-힣 ]*\$" // 한글, 영문, 숫자 패턴
      val compile = Pattern.compile(pattern)
      val match = compile.matcher(text)
      return match.find()
   }

   fun networkStatusCheck(context: Context): Boolean {
      val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
      val networkCapabilities = connectivityManager.activeNetwork ?: return false
      val activeNetwork = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
      return when {
         activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
         activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
         activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
         else -> false
      }
   }

   // 이미지를 저장하는 메서드
   fun saveImage(context: Context, bitmap: Bitmap): String {
      var result = ""
      val uploadBitmap: Bitmap
      val imageName = getNowTime() + ".png" // 이미지 파일명을 현재 시간으로 설정
      val file = File(context.filesDir, imageName) // 파일 경로 설정

      try{
         file.createNewFile() // 새 파일 생성
         val fos = FileOutputStream(file)

         if(bitmap.width > 300) {
            uploadBitmap = Bitmap.createScaledBitmap(bitmap, 300, bitmap.height*300/bitmap.width, true)
            uploadBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
         }

         fos.close() // 출력 스트림 닫기
         result = imageName
      } catch (e: java.lang.Exception) {
         e.printStackTrace()
      }

      return result
   }

   // 현재 시간을 문자열 형태로 반환하는 메서드
   private fun getNowTime(): String? {
      val now = System.currentTimeMillis()
      val mDate = Date(now)
      val simpleDate = SimpleDateFormat("yyMMddhhmmSSS", Locale.KOREA)
      return simpleDate.format(mDate)
   }

   fun deleteFile(context: Context, data: String): Boolean {
      return File(context.filesDir, data).delete()
   }

   fun resetAlarm(context: Context) {
      val alarmReceiver = AlarmReceiver()
      val dataManager = DataManager(context)
      dataManager.open()

      val getData = dataManager.getDrugDate(LocalDate.now().toString())

      for(i in 0 until getData.size) {
         val getDrug = dataManager.getDrugData(getData[i])

         if(getDrug.isSet == 1) {
            val timeList = ArrayList<DrugTime>()
            val getDrugTime = dataManager.getDrugTime(getData[i])

            for(j in 0 until getDrugTime.size) {
               timeList.add(DrugTime(time = getDrugTime[j].time))
            }

            val message = getDrug.name + " " + getDrug.amount + getDrug.unit
            alarmReceiver.setAlarm(context, getData[i], getDrug.startDate, getDrug.endDate, timeList, message)
         }
      }
   }

   fun getFoodCalories(context: Context, date:String) : Item {
      val dataManager = DataManager(context)
      dataManager.open()

      var sum = 0
      val item = Item()
      val getDailyFood1 = dataManager.getDailyFood(Constant.BREAKFAST.name, date)
      val getDailyFood2 = dataManager.getDailyFood(Constant.LUNCH.name, date)
      val getDailyFood3 = dataManager.getDailyFood(Constant.DINNER.name, date)
      val getDailyFood4 = dataManager.getDailyFood(Constant.SNACK.name, date)

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

      val getFood1 = dataManager.getDailyFood(Constant.BREAKFAST.name, date)
      val getFood2 = dataManager.getDailyFood(Constant.LUNCH.name, date)
      val getFood3 = dataManager.getDailyFood(Constant.DINNER.name, date)
      val getFood4 = dataManager.getDailyFood(Constant.SNACK.name, date)

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