package kr.bodywell.android.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.powersync.DatabaseDriverFactory
import kr.bodywell.android.R
import kr.bodywell.android.api.powerSync.SyncService
import kr.bodywell.android.model.Constant
import kr.bodywell.android.model.Food
import kr.bodywell.android.model.Item
import kr.bodywell.android.model.MedicineTime
import kr.bodywell.android.model.Token
import kr.bodywell.android.model.User
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
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import java.util.regex.Pattern

object CustomUtil {
   const val TAG = "logTAG"
   lateinit var powerSync: SyncService
   var getUser = User()
   var getToken = Token()
   var drugTimeList = ArrayList<MedicineTime>()
   var layoutType = 1

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

   fun dateToIso(date: LocalDate): String {
      return date.atStartOfDay().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)
   }

   fun dateTimeToIso(date: LocalDateTime): String {
      return date.atZone(ZoneId.of("Asia/Seoul")).toInstant().toString()
   }

   fun dateTimeToIso2(date: Calendar): String {
      val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
      sdf.timeZone = TimeZone.getTimeZone("Asia/Seoul")
      return sdf.format(date.time)
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

   fun networkStatus(context: Context): Boolean {
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

   fun filterText(text: String): Boolean {
      val pattern = "^[0-9a-zA-Zㄱ-ㅎ가-힣 ]*\$" // 한글, 영문, 숫자 패턴
      val compile = Pattern.compile(pattern)
      val match = compile.matcher(text)
      return match.find()
   }

   fun getRotatedBitmap(context: Context, data: Uri): Bitmap? {
      val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(data))	// 사진 가져오기
      var degree = 0F

      // 이미지 회전정보 가져오기
      val inputStream = context.contentResolver.openInputStream(data) // 스트림을 추출
      val exif: ExifInterface? = try { // ExifInterface 정보를 읽어옴
         ExifInterface(inputStream!!)
      }catch(e: Exception) {
         e.printStackTrace()
         return null
      }
      inputStream.close()

      // 회전된 각도 알아내기
      val orientation = exif?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
      if (orientation != -1) {
         when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90F
            ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180F
            ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270F
         }
      }

      if (bitmap == null) return null
      if (degree == 0F) return bitmap
      val m = Matrix()
      m.setRotate(degree, bitmap.width.toFloat() / 2, bitmap.height.toFloat() / 2)
      return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, m, true)
   }

   fun saveImage(context: Context, bitmap: Bitmap): String {
      val uploadBitmap: Bitmap
      val imageName = SimpleDateFormat("yyMMddhhmmSSS").format(Date()) + ".png" // 이미지 파일명을 현재 시간으로 설정
      val file = File(context.filesDir, imageName) // 파일 경로 설정

      try{
         file.createNewFile() // 새 파일 생성
         val fos = FileOutputStream(file)

         if(bitmap.width > 800) {
            uploadBitmap = Bitmap.createScaledBitmap(bitmap, 800, bitmap.height*800/bitmap.width, true)
            uploadBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos)
         }else {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos)
         }

         fos.close() // 출력 스트림 닫기
         return imageName
      }catch (e: Exception) {
         e.printStackTrace()
      }

      return ""
   }

   suspend fun resetAlarm(context: Context) {
      val alarmReceiver = AlarmReceiver()

      val driverFactory = DatabaseDriverFactory(context)
      powerSync = SyncService(context,driverFactory)

      val getMedicineId = powerSync.getAllMedicineDate(LocalDate.now().toString())

      for(i in getMedicineId.indices) {
         val getMedicine = powerSync.getMedicineData(getMedicineId[i])
         val split = getMedicine.name.split("/", limit=4)

         if(split[3] == "1") {
            val timeList = ArrayList<MedicineTime>()
            val getDrugTime = powerSync.getAllMedicineTime("medicine_id", getMedicineId[i])
            for(element in getDrugTime) timeList.add(MedicineTime(time = element.time))

            val message = split[1] + " " + getMedicine.amount + getMedicine.unit
            alarmReceiver.setAlarm(context, getMedicine.category.toInt(), getMedicine.starts, getMedicine.ends, timeList, message)
         }
      }
   }

   suspend fun getFoodCalories(date:String) : Item {
      var sum = 0
      val item = Item()
      val getDiet1 = powerSync.getAllDiet(Constant.BREAKFAST.name, date)
      val getDiet2 = powerSync.getAllDiet(Constant.LUNCH.name, date)
      val getDiet3 = powerSync.getAllDiet(Constant.DINNER.name, date)
      val getDiet4 = powerSync.getAllDiet(Constant.SNACK.name, date)

      for(i in getDiet1.indices) {
         sum += getDiet1[i].calorie * getDiet1[i].quantity
         item.int1 += getDiet1[i].calorie * getDiet1[i].quantity
      }
      for(i in getDiet2.indices) {
         sum += getDiet2[i].calorie * getDiet2[i].quantity
         item.int2 += getDiet2[i].calorie * getDiet2[i].quantity
      }
      for(i in getDiet3.indices) {
         sum += getDiet3[i].calorie * getDiet3[i].quantity
         item.int3 += getDiet3[i].calorie * getDiet3[i].quantity
      }
      for(i in getDiet4.indices) {
         sum += getDiet4[i].calorie * getDiet4[i].quantity
         item.int4 += getDiet4[i].calorie * getDiet4[i].quantity
      }

      item.int5 = sum

      return item
   }

   suspend fun getNutrition(date:String) : Food {
      val getDiet1 = powerSync.getAllDiet(Constant.BREAKFAST.name, date)
      val getDiet2 = powerSync.getAllDiet(Constant.LUNCH.name, date)
      val getDiet3 = powerSync.getAllDiet(Constant.DINNER.name, date)
      val getDiet4 = powerSync.getAllDiet(Constant.SNACK.name, date)

      var carbohydrate = 0.0
      var protein = 0.0
      var fat = 0.0

      for(i in getDiet1.indices) {
         carbohydrate += getDiet1[i].carbohydrate * getDiet1[i].quantity
         protein += getDiet1[i].protein * getDiet1[i].quantity
         fat += getDiet1[i].fat * getDiet1[i].quantity
      }
      for(i in getDiet2.indices) {
         carbohydrate += getDiet2[i].carbohydrate * getDiet2[i].quantity
         protein += getDiet2[i].protein * getDiet2[i].quantity
         fat += getDiet2[i].fat * getDiet2[i].quantity
      }
      for(i in getDiet3.indices) {
         carbohydrate += getDiet3[i].carbohydrate * getDiet3[i].quantity
         protein += getDiet3[i].protein * getDiet3[i].quantity
         fat += getDiet3[i].fat * getDiet3[i].quantity
      }
      for(i in getDiet4.indices) {
         carbohydrate += getDiet4[i].carbohydrate * getDiet4[i].quantity
         protein += getDiet4[i].protein * getDiet4[i].quantity
         fat += getDiet4[i].fat * getDiet4[i].quantity
      }

      return Food(carbohydrate = carbohydrate, protein = protein, fat = fat, volumeUnit = (carbohydrate+protein+fat).toString())
   }

   suspend fun getExerciseCalories(date:String) : Int {
      var sum = 0
      val getExercise = powerSync.getAllWorkout(date)

      for(element in getExercise) {
         sum += element.calorie
      }

      return sum
   }

   fun setDrugTimeList(data: String) {
      drugTimeList.add(MedicineTime(time = data))
   }
}