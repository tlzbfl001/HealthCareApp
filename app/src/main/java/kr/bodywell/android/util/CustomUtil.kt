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
import androidx.fragment.app.FragmentManager
import com.github.f4b6a3.uuid.UuidCreator
import com.powersync.DatabaseDriverFactory
import kr.bodywell.android.R
import kr.bodywell.android.api.powerSync.SyncService
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.Constant.BREAKFAST
import kr.bodywell.android.model.Constant.DINNER
import kr.bodywell.android.model.Constant.LUNCH
import kr.bodywell.android.model.Constant.SNACK
import kr.bodywell.android.model.FileItem
import kr.bodywell.android.model.Food
import kr.bodywell.android.model.Item
import kr.bodywell.android.model.MedicineTime
import kr.bodywell.android.model.Token
import kr.bodywell.android.model.User
import kr.bodywell.android.service.AlarmReceiver
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
import kotlin.math.floor
import kotlin.math.sqrt

object CustomUtil {
   const val TAG = "logTAG"
   lateinit var powerSync: SyncService
   var getUser = User()
   var getToken = Token()
   var drugTimeList = ArrayList<MedicineTime>()
   var layoutType = 1

   fun replaceFragment1(fragmentManager: FragmentManager, fragment: Fragment?) {
      fragmentManager.beginTransaction().apply {
         setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.enter_to_right)
         replace(R.id.mainFrame, fragment!!)
         commit()
      }
   }

   fun replaceFragment2(fragmentManager: FragmentManager, fragment: Fragment?, bundle: Bundle?) {
      fragmentManager.beginTransaction().apply {
         fragment?.arguments = bundle
         setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.enter_to_right)
         replace(R.id.mainFrame, fragment!!)
         commit()
      }
   }

   fun replaceFragment3(fragmentManager: FragmentManager, fragment: Fragment?) {
      fragmentManager.beginTransaction().apply {
         replace(R.id.mainFrame, fragment!!)
         commit()
      }
   }

   fun replaceFragment4(fragmentManager: FragmentManager, fragment: Fragment?, bundle: Bundle?) {
      fragmentManager.beginTransaction().apply {
         fragment?.arguments = bundle
         replace(R.id.mainFrame, fragment!!)
         commit()
      }
   }

   fun dateToIso(date: LocalDate): String {
      return date.atStartOfDay().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)
   }

   fun dateTimeToIso1(date: Calendar): String {
      val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
      sdf.timeZone = TimeZone.getTimeZone(ZoneId.systemDefault())
      return sdf.format(date.time)
   }

   fun dateTimeToIso2(): String {
      val format = LocalDateTime.now().atZone(ZoneId.of(ZoneId.systemDefault().toString())).toInstant().toString()
      return format.replace("T", " ")
   }

   fun isoToDateTime(data: String): LocalDateTime {
      val parse = Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse(data)).atZone(ZoneId.of(ZoneId.systemDefault().toString()))
      return LocalDateTime.from(parse)
   }

   fun getUUID(): String {
      return UuidCreator.getTimeOrderedEpoch().toString()
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

   fun filterText(text: String): Boolean { // 텍스트 필터링
      val pattern = "^[0-9a-zA-Zㄱ-ㅎ가-힣 ]*\$" // 한글, 영문, 숫자 패턴
      val compile = Pattern.compile(pattern)
      val match = compile.matcher(text)
      return match.find()
   }

   fun saveFile(context: Context, bitmap: Bitmap): String {
      val imageName = SimpleDateFormat("yyMMddhhmmSSS").format(Date()) + ".jpg" // 이미지 파일명을 현재시간으로 설정
      val file = File(context.filesDir, imageName) // 파일 경로 설정

      val maxBytes = 921600
      val currentWidth = bitmap.width
      val currentHeight = bitmap.height
      val currentPixels = currentWidth * currentHeight

      try {
         file.createNewFile() // 새 파일 생성
         val fos = FileOutputStream(file) // 출력 스트림 열기

         if(currentPixels <= maxBytes) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
         }else {
            val scaleFactor = sqrt(maxBytes / currentPixels.toDouble())
            val newWidthPx = floor(currentWidth * scaleFactor).toInt()
            val newHeightPx = floor(currentHeight * scaleFactor).toInt()
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidthPx, newHeightPx, true)
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
         }

         fos.close() // 출력 스트림 닫기

         return imageName
      }catch(e: Exception) {
         e.printStackTrace()
      }

      return ""
   }

   fun getRotatedBitmap(context: Context, data: Uri): Bitmap? {
      val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(data))
      var degree = 0F

      // ExifInterface 정보 가져오기
      val inputStream = context.contentResolver.openInputStream(data)
      val exif: ExifInterface? = try {
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

   suspend fun resetAlarm(context: Context) {
      val alarmReceiver = AlarmReceiver()
      val dataManager = DataManager(context)
      dataManager.open()

      val driverFactory = DatabaseDriverFactory(context)
      powerSync = SyncService(context, driverFactory)

      val getMedicineId = powerSync.getMedicineIds(LocalDate.now().toString())

      for(i in getMedicineId.indices) {
         val getMedicine = powerSync.getMedicine(getMedicineId[i])
         val split = getMedicine.category.split("/", limit=3)

         if(split[2] == "1") {
            val timeList = ArrayList<MedicineTime>()
            val getMedicineTime = powerSync.getAllMedicineTime(getMedicineId[i])
            for(element in getMedicineTime) timeList.add(MedicineTime(time = element.time))

            val getId = dataManager.getMedicine(getMedicineId[i]) // 알람ID 가져오기
            if(timeList.isNotEmpty() && getId != 0) {
               val message = split[0] + " " + getMedicine.amount + getMedicine.unit
               alarmReceiver.setAlarm(context, getId, getMedicine.starts, getMedicine.ends, timeList, message)
            }
         }
      }
   }

   suspend fun getFoodCalories(date:String) : Item {
      var sum = 0
      val item = Item()
      val getDiet1 = powerSync.getDiets(BREAKFAST, date)
      val getDiet2 = powerSync.getDiets(LUNCH, date)
      val getDiet3 = powerSync.getDiets(DINNER, date)
      val getDiet4 = powerSync.getDiets(SNACK, date)

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
      val getDiet1 = powerSync.getDiets(BREAKFAST, date)
      val getDiet2 = powerSync.getDiets(LUNCH, date)
      val getDiet3 = powerSync.getDiets(DINNER, date)
      val getDiet4 = powerSync.getDiets(SNACK, date)
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

      return Food(carbohydrate = carbohydrate, protein = protein, fat = fat, volumeUnit = (carbohydrate + protein + fat).toString())
   }

   suspend fun getDietFiles(date:String) : ArrayList<FileItem> {
      val files = ArrayList<FileItem>()
      val getDiets = powerSync.getDietIds(date)

      for(i in getDiets.indices) {
         val getFiles = powerSync.getFiles("diet_id", getDiets[i])
         for(j in getFiles.indices) files.add(FileItem(name = getFiles[j].name))
      }

      return files
   }

   suspend fun getExerciseCalories(date:String) : Int {
      var sum = 0
      val getWorkouts = powerSync.getWorkouts(date)
      for(element in getWorkouts) sum += element.calorie
      return sum
   }

   fun setDrugTimeList(data: String) {
      drugTimeList.add(MedicineTime(time = data))
   }
}