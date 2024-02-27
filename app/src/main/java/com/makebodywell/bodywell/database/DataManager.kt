package com.makebodywell.bodywell.database

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_BODY
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DAILY_DATA
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DRUG
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DRUG_CHECK
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DRUG_TIME
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_EXERCISE
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_FOOD
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_IMAGE
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_NOTE
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_SLEEP
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_TOKEN
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_USER
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_WATER
import com.makebodywell.bodywell.model.Body
import com.makebodywell.bodywell.model.DailyData
import com.makebodywell.bodywell.model.Drug
import com.makebodywell.bodywell.model.DrugCheck
import com.makebodywell.bodywell.model.DrugTime
import com.makebodywell.bodywell.model.Exercise
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.model.Image
import com.makebodywell.bodywell.model.Item
import com.makebodywell.bodywell.model.Note
import com.makebodywell.bodywell.model.Sleep
import com.makebodywell.bodywell.model.Token
import com.makebodywell.bodywell.model.User
import com.makebodywell.bodywell.model.Water
import com.makebodywell.bodywell.util.MyApp


class DataManager(private var context: Context?) {
   private var dbHelper: DBHelper? = null

   @Throws(SQLException::class)
   fun open(): DataManager {
      dbHelper = DBHelper(context)
      return this
   }

   fun getUserCount() : Int {
      val db = dbHelper!!.readableDatabase
      var data = 0
      val sql = "select count(id) from $TABLE_USER"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data = cursor.getInt(0)
      }
      cursor.close()
      return data
   }

   fun getUser(type: String, email: String) : User {
      val db = dbHelper!!.readableDatabase
      val data = User()
      val sql = "select * from $TABLE_USER where type = '$type' and email = '$email'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data.id=cursor.getInt(0)
         data.type=cursor.getString(1)
         data.idToken=cursor.getString(2)
         data.userId=cursor.getString(3)
         data.deviceId=cursor.getString(4)
         data.healthId=cursor.getString(5)
         data.activityId=cursor.getString(6)
         data.bodyMeasurementId=cursor.getString(7)
         data.email = cursor.getString(8)
         data.name = cursor.getString(9)
         data.nickname = cursor.getString(10)
         data.gender = cursor.getString(11)
         data.birthday = cursor.getString(12)
         data.profileImage = cursor.getString(13)
         data.height = cursor.getDouble(14).toString()
         data.weight = cursor.getDouble(15).toString()
         data.weightGoal = cursor.getDouble(16).toString()
         data.kcalGoal = cursor.getInt(17).toString()
         data.waterGoal = cursor.getInt(18).toString()
         data.waterUnit = cursor.getInt(19).toString()
         data.regDate = cursor.getString(20)
      }
      cursor.close()
      return data
   }

   fun getUser() : User {
      val db = dbHelper!!.readableDatabase
      val data = User()
      val sql = "select * from $TABLE_USER where id = ${MyApp.prefs.getId()}"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data.id=cursor.getInt(0)
         data.type=cursor.getString(1)
         data.idToken=cursor.getString(2)
         data.userId=cursor.getString(3)
         data.deviceId=cursor.getString(4)
         data.healthId=cursor.getString(5)
         data.activityId=cursor.getString(6)
         data.bodyMeasurementId=cursor.getString(7)
         data.email = cursor.getString(8)
         data.name = cursor.getString(9)
         data.nickname = cursor.getString(10)
         data.gender = cursor.getString(11)
         data.birthday = cursor.getString(12)
         data.profileImage = cursor.getString(13)
         data.height = cursor.getDouble(14).toString()
         data.weight = cursor.getDouble(15).toString()
         data.weightGoal = cursor.getDouble(16).toString()
         data.kcalGoal = cursor.getInt(17).toString()
         data.waterGoal = cursor.getInt(18).toString()
         data.waterUnit = cursor.getInt(19).toString()
         data.regDate = cursor.getString(20)
      }
      cursor.close()
      return data
   }

   fun getToken() : Token {
      val db = dbHelper!!.readableDatabase
      val data = Token()
      val sql = "select * from $TABLE_TOKEN where userId = ${MyApp.prefs.getId()}"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data.id=cursor.getInt(0)
         data.userId=cursor.getInt(1)
         data.accessToken=cursor.getString(2)
         data.refreshToken = cursor.getString(3)
         data.accessTokenRegDate = cursor.getString(4)
         data.refreshTokenRegDate = cursor.getString(5)
      }
      cursor.close()
      return data
   }

   fun getFood(id: Int) : Food {
      val db = dbHelper!!.readableDatabase
      val data = Food()
      val sql = "select * from $TABLE_FOOD where id = $id"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data.id=cursor.getInt(0)
         data.name=cursor.getString(2)
         data.unit=cursor.getString(3)
         data.amount= cursor.getInt(4)
         data.count= cursor.getInt(5)
         data.kcal= cursor.getInt(6)
         data.carbohydrate= cursor.getDouble(7)
         data.protein= cursor.getDouble(8)
         data.fat= cursor.getDouble(9)
         data.salt= cursor.getDouble(10)
         data.sugar= cursor.getDouble(11)
         data.type = cursor.getInt(12)
         data.regDate = cursor.getString(13)
      }
      cursor.close()
      return data
   }

   fun getFood(type: Int, date: String) : ArrayList<Food> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<Food> = ArrayList()
      val sql = "select * from $TABLE_FOOD where userId = ${MyApp.prefs.getId()} and type = $type and regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Food()
         data.id=cursor.getInt(0)
         data.name=cursor.getString(2)
         data.unit=cursor.getString(3)
         data.amount= cursor.getInt(4)
         data.count= cursor.getInt(5)
         data.kcal= cursor.getInt(6)
         data.carbohydrate= cursor.getDouble(7)
         data.protein= cursor.getDouble(8)
         data.fat= cursor.getDouble(9)
         data.salt= cursor.getDouble(10)
         data.sugar= cursor.getDouble(11)
         data.type = cursor.getInt(12)
         data.regDate = cursor.getString(13)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getImage(type: Int, date: String) : ArrayList<Image> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<Image> = ArrayList()
      val sql = "select * from $TABLE_IMAGE where userId = ${MyApp.prefs.getId()} and type = $type and regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Image()
         data.id = cursor.getInt(0)
         data.imageUri = cursor.getString(2)
         data.type = cursor.getInt(3)
         data.dataId = cursor.getInt(4)
         data.regDate = cursor.getString(5)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getImage(id: Int) : ArrayList<Image> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<Image> = ArrayList()
      val sql = "select * from $TABLE_IMAGE where userId = ${MyApp.prefs.getId()} and dataId = $id"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Image()
         data.id = cursor.getInt(0)
         data.imageUri = cursor.getString(2)
         data.type = cursor.getInt(3)
         data.dataId = cursor.getInt(4)
         data.regDate = cursor.getString(5)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getWater(start: String, end: String) : ArrayList<Water> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Water>()
      val sql = "select * from $TABLE_WATER where userId = ${MyApp.prefs.getId()} and regDate BETWEEN '$start' and '$end'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Water()
         data.id = cursor.getInt(0)
         data.water=cursor.getInt(2)
         data.volume=cursor.getInt(3)
         data.regDate = cursor.getString(4)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getWater(date: String) : Water {
      val db = dbHelper!!.readableDatabase
      val data = Water()
      val sql = "select * from $TABLE_WATER where userId = ${MyApp.prefs.getId()} and regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data.id=cursor.getInt(0)
         data.water=cursor.getInt(2)
         data.volume=cursor.getInt(3)
         data.regDate = cursor.getString(4)
      }
      cursor.close()
      return data
   }

   fun getExercise(date: String) : ArrayList<Exercise> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Exercise>()
      val sql = "select * from $TABLE_EXERCISE where userId = ${MyApp.prefs.getId()} and regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Exercise()
         data.id=cursor.getInt(0)
         data.name=cursor.getString(2)
         data.intensity=cursor.getString(3)
         data.workoutTime= cursor.getInt(4)
         data.calories= cursor.getInt(5)
         data.regDate= cursor.getString(6)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getExercise(id: Int): Exercise {
      val db = dbHelper!!.readableDatabase
      val data = Exercise()
      val sql = "select * from $TABLE_EXERCISE where userId = ${MyApp.prefs.getId()} and id = '$id'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data.id=cursor.getInt(0)
         data.name=cursor.getString(2)
         data.intensity=cursor.getString(3)
         data.workoutTime= cursor.getInt(4)
         data.calories= cursor.getInt(5)
         data.regDate= cursor.getString(6)
      }
      cursor.close()
      return data
   }

   fun getRanking(table: String, date: String) : ArrayList<Item> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Item>()
      val sql = "select count(name) as ranking, name from $table where userId = ${MyApp.prefs.getId()} and regDate = '$date' " +
         "group by name order by ranking desc limit 4"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Item()
         data.string1=cursor.getString(0)
         data.string2=cursor.getString(1)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getRanking(table: String, start: String, end: String) : ArrayList<Item> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Item>()
      val sql = "select count(name) as ranking, name from $table where userId = ${MyApp.prefs.getId()} and regDate BETWEEN '$start' and '$end' " +
         "group by name order by ranking desc limit 4"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Item()
         data.string1=cursor.getString(0)
         data.string2=cursor.getString(1)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getBody(date: String) : Body {
      val db = dbHelper!!.readableDatabase
      val data = Body()
      val sql = "select * from $TABLE_BODY where userId = ${MyApp.prefs.getId()} and regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data.id = cursor.getInt(0)
         data.height = cursor.getDouble(2)
         data.weight = cursor.getDouble(3)
         data.age = cursor.getInt(4)
         data.gender = cursor.getString(5)
         data.exerciseLevel = cursor.getInt(6)
         data.fat = cursor.getDouble(7)
         data.muscle = cursor.getDouble(8)
         data.bmi = cursor.getDouble(9)
         data.bmr = cursor.getDouble(10)
         data.regDate = cursor.getString(11)
      }
      cursor.close()
      return data
   }

   fun getBody(start: String, end: String) : ArrayList<Body> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Body>()
      val sql = "select weight, fat, bmi, regDate from $TABLE_BODY where userId = ${MyApp.prefs.getId()} and regDate BETWEEN '$start' and '$end'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Body()
         data.weight = cursor.getDouble(0)
         data.fat = cursor.getDouble(1)
         data.bmi = cursor.getDouble(2)
         data.regDate = cursor.getString(3)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getDrug(date: String) : ArrayList<Drug> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Drug>()
      val sql = "select * from $TABLE_DRUG where userId = ${MyApp.prefs.getId()} and '$date' BETWEEN startDate and endDate"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Drug()
         data.id = cursor.getInt(0)
         data.type = cursor.getString(2)
         data.name = cursor.getString(3)
         data.amount = cursor.getString(4)
         data.unit = cursor.getString(5)
         data.count = cursor.getInt(6)
         data.startDate = cursor.getString(7)
         data.endDate = cursor.getString(8)
         data.isSet = cursor.getInt(9)
         data.regDate = cursor.getString(10)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getDrugId() : ArrayList<Int> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Int>()
      val sql = "select id from $TABLE_DRUG where userId = ${MyApp.prefs.getId()}"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         list.add(cursor.getInt(0))
      }
      cursor.close()
      return list
   }

   fun getDrugId(date: String) : Drug {
      val db = dbHelper!!.readableDatabase
      val data = Drug()
      val sql = "select id from $TABLE_DRUG where userId = ${MyApp.prefs.getId()} and regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data.id = cursor.getInt(0)
      }
      cursor.close()
      return data
   }

   fun getDrugTime(id: Int) : ArrayList<DrugTime> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<DrugTime> = ArrayList()
      val sql = "select * from $TABLE_DRUG_TIME where userId = ${MyApp.prefs.getId()} and drugId = $id order by hour, minute"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = DrugTime()
         data.id = cursor.getInt(0)
         data.hour = cursor.getInt(2)
         data.minute = cursor.getInt(3)
         data.drugId = cursor.getInt(4)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getDrugCheck(dataId: Int, date: String) : DrugCheck {
      val db = dbHelper!!.readableDatabase
      val data = DrugCheck()
      val sql = "select checked, drugTimeId, regDate from $TABLE_DRUG_CHECK where userId=${MyApp.prefs.getId()} and drugTimeId=$dataId and regDate='$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data.checked = cursor.getInt(0)
         data.drugTimeId = cursor.getInt(1)
         data.regDate = cursor.getString(2)
      }
      cursor.close()
      return data
   }

   fun getDrugCheckCount(date: String) : Int {
      val db = dbHelper!!.readableDatabase
      var data = 0
      val sql = "select count(id) from $TABLE_DRUG_CHECK where userId = ${MyApp.prefs.getId()} and checked = 1 and regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data = cursor.getInt(0)
      }
      cursor.close()
      return data
   }

   fun getDrugDaily(date: String): ArrayList<Drug> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<Drug> = ArrayList()
      val sql = "select * from $TABLE_DRUG where userId = ${MyApp.prefs.getId()} and date('$date') between date(startDate) and date(endDate)"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Drug()
         data.id = cursor.getInt(0)
         data.type = cursor.getString(2)
         data.name = cursor.getString(3)
         data.amount = cursor.getString(4)
         data.unit = cursor.getString(5)
         data.count = cursor.getInt(6)
         data.startDate = cursor.getString(7)
         data.endDate = cursor.getString(8)
         data.isSet = cursor.getInt(9)
         data.regDate = cursor.getString(10)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getNote(date: String) : Note {
      val db = dbHelper!!.readableDatabase
      val data = Note()
      val sql = "select * from $TABLE_NOTE where userId = ${MyApp.prefs.getId()} and regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data.id = cursor.getInt(0)
         data.title = cursor.getString(2)
         data.content = cursor.getString(3)
         data.status = cursor.getInt(4)
         data.regDate = cursor.getString(5)
      }
      cursor.close()
      return data
   }

   fun getSleep(date: String) : Sleep {
      val db = dbHelper!!.readableDatabase
      val data = Sleep()
      val sql = "select * from $TABLE_SLEEP where userId = ${MyApp.prefs.getId()} and regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data.id = cursor.getInt(0)
         data.bedTime = cursor.getInt(2)
         data.wakeTime = cursor.getInt(3)
         data.sleepTime = cursor.getInt(4)
         data.regDate = cursor.getString(5)
      }
      cursor.close()
      return data
   }

   fun getDailyData(date: String) : DailyData {
      val db = dbHelper!!.readableDatabase
      val data = DailyData()
      val sql = "select * from $TABLE_DAILY_DATA where userId = ${MyApp.prefs.getId()} and regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data.foodGoal = cursor.getInt(2)
         data.waterGoal = cursor.getInt(3)
         data.exerciseGoal = cursor.getInt(4)
         data.bodyGoal = cursor.getDouble(5)
         data.sleepGoal = cursor.getInt(6)
         data.drugGoal = cursor.getInt(7)
         data.regDate = cursor.getString(8)
      }
      cursor.close()
      return data
   }

   fun getDates(table: String, start: String, end: String) : ArrayList<String> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<String>()
      val sql = "select distinct regDate from $table where userId = ${MyApp.prefs.getId()} and regDate BETWEEN '$start' and '$end' order by regDate"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         list.add(cursor.getString(0))
      }
      cursor.close()
      return list
   }

   fun insertUser(data: User) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("type", data.type)
      values.put("idToken", data.idToken)
      values.put("email", data.email)
      values.put("name", data.name)
      values.put("nickname", data.nickname)
      values.put("gender", data.gender)
      values.put("birthday", data.birthday)
      values.put("profileImage", data.profileImage)
      values.put("regDate", data.regDate)
      db!!.insert(TABLE_USER, null, values)
   }

   fun insertToken(data: Token) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("userId", MyApp.prefs.getId())
      values.put("accessToken", data.accessToken)
      values.put("refreshToken", data.refreshToken)
      values.put("accessTokenRegDate", data.accessTokenRegDate)
      values.put("refreshTokenRegDate", data.refreshTokenRegDate)
      db!!.insert(TABLE_TOKEN, null, values)
   }

   fun insertFood(data: Food) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("userId", MyApp.prefs.getId())
      values.put("name", data.name)
      values.put("unit", data.unit)
      values.put("amount", data.amount)
      values.put("count", data.count)
      values.put("kcal", data.kcal)
      values.put("carbohydrate", data.carbohydrate)
      values.put("protein", data.protein)
      values.put("fat", data.fat)
      values.put("salt", data.salt)
      values.put("sugar", data.sugar)
      values.put("type", data.type)
      values.put("regDate", data.regDate)
      db!!.insert(TABLE_FOOD, null, values)
   }

   fun insertImage(data: Image) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("userId", MyApp.prefs.getId())
      values.put("imageUri", data.imageUri)
      values.put("type", data.type)
      values.put("dataId", data.dataId)
      values.put("regDate", data.regDate)
      db!!.insert(TABLE_IMAGE, null, values)
   }

   fun insertWater(data: Water) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("userId", MyApp.prefs.getId())
      values.put("water", data.water)
      values.put("volume", data.volume)
      values.put("regDate", data.regDate)
      db!!.insert(TABLE_WATER, null, values)
   }

   fun insertExercise(data: Exercise) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("userId", MyApp.prefs.getId())
      values.put("name", data.name)
      values.put("intensity", data.intensity)
      values.put("workoutTime", data.workoutTime)
      values.put("calories", data.calories)
      values.put("regDate", data.regDate)
      db.insert(TABLE_EXERCISE, null, values)
   }

   fun insertBody(data: Body) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("userId", MyApp.prefs.getId())
      values.put("height", data.height)
      values.put("weight", data.weight)
      values.put("age", data.age)
      values.put("gender", data.gender)
      values.put("exerciseLevel", data.exerciseLevel)
      values.put("fat", data.fat)
      values.put("muscle", data.muscle)
      values.put("bmi", data.bmi)
      values.put("bmr", data.bmr)
      values.put("regDate", data.regDate)
      db!!.insert(TABLE_BODY, null, values)
   }

   fun insertDrug(data: Drug) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("userId", MyApp.prefs.getId())
      values.put("type", data.type)
      values.put("name", data.name)
      values.put("amount", data.amount)
      values.put("unit", data.unit)
      values.put("count", data.count)
      values.put("startDate", data.startDate)
      values.put("endDate", data.endDate)
      values.put("isSet", data.isSet)
      values.put("regDate", data.regDate)
      db!!.insert(TABLE_DRUG, null, values)
   }

   fun insertDrugTime(data: DrugTime) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("userId", MyApp.prefs.getId())
      values.put("hour", data.hour)
      values.put("minute", data.minute)
      values.put("drugId", data.drugId)
      db!!.insert(TABLE_DRUG_TIME, null, values)
   }

   fun insertDrugCheck(checked: Int, drugTimeId: Int, regDate: String) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("userId", MyApp.prefs.getId())
      values.put("checked", checked)
      values.put("drugTimeId", drugTimeId)
      values.put("regDate", regDate)
      db!!.insert(TABLE_DRUG_CHECK, null, values)
   }

   fun insertNote(data: Note) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("userId", MyApp.prefs.getId())
      values.put("title", data.title)
      values.put("content", data.content)
      values.put("status", data.status)
      values.put("regDate", data.regDate)
      db!!.insert(TABLE_NOTE, null, values)
   }

   fun insertSleep(data: Sleep) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("userId", MyApp.prefs.getId())
      values.put("bedTime", data.bedTime)
      values.put("wakeTime", data.wakeTime)
      values.put("sleepTime", data.sleepTime)
      values.put("regDate", data.regDate)
      db!!.insert(TABLE_SLEEP, null, values)
   }

   fun insertDailyData(data: DailyData) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("userId", MyApp.prefs.getId())
      values.put("foodGoal", data.foodGoal)
      values.put("waterGoal", data.waterGoal)
      values.put("exerciseGoal", data.exerciseGoal)
      values.put("bodyGoal", data.bodyGoal)
      values.put("sleepGoal", data.sleepGoal)
      values.put("drugGoal", data.drugGoal)
      values.put("regDate", data.regDate)
      db!!.insert(TABLE_DAILY_DATA, null, values)
   }

   fun updateUserStr(table: String, column: String, data: String){
      val db = dbHelper!!.writableDatabase
      val sql = "update $table set $column='$data' where id = ${MyApp.prefs.getId()}"
      db.execSQL(sql)
      db.close()
   }

   fun updateUserInt(table: String, column: String, data: Int){
      val db = dbHelper!!.writableDatabase
      val sql = "update $table set $column=$data where id = ${MyApp.prefs.getId()}"
      db.execSQL(sql)
      db.close()
   }

   fun updateUserDouble(table: String, column: String, data: Double){
      val db = dbHelper!!.writableDatabase
      val sql = "update $table set $column=$data where id = ${MyApp.prefs.getId()}"
      db.execSQL(sql)
      db.close()
   }

   fun updateStr(table: String, column: String, data: String, id: Int){
      val db = dbHelper!!.writableDatabase
      val sql = "update $table set $column='$data' where userId = ${MyApp.prefs.getId()} and id=$id"
      db.execSQL(sql)
      db.close()
   }

   fun updateInt(table: String, column: String, data: Int, id: Int){
      val db = dbHelper!!.writableDatabase
      val sql = "update $table set $column=$data where userId = ${MyApp.prefs.getId()} and id=$id"
      db.execSQL(sql)
      db.close()
   }

   fun updateDouble(table: String, column: String, data: Double, id: Int){
      val db = dbHelper!!.writableDatabase
      val sql = "update $table set $column=$data where userId = ${MyApp.prefs.getId()} and id=$id"
      db.execSQL(sql)
      db.close()
   }

   fun updateUser(data: User){
      val db = dbHelper!!.writableDatabase
      val sql = "update user set userId='${data.userId}', deviceId='${data.deviceId}', healthId='${data.healthId}', " +
         "activityId='${data.activityId}', bodyMeasurementId='${data.bodyMeasurementId}' where id=${data.id}"
      db.execSQL(sql)
      db.close()
   }

   fun updateToken(data: Token){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_TOKEN set accessToken='${data.accessToken}', refreshToken='${data.refreshToken}', accessTokenRegDate='${data.accessTokenRegDate}', " +
         "refreshTokenRegDate='${data.refreshTokenRegDate}' where userId=${MyApp.prefs.getId()}"
      db.execSQL(sql)
      db.close()
   }

   fun updateAccessToken(data: Token){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_TOKEN set accessToken='${data.accessToken}', accessTokenRegDate='${data.accessTokenRegDate}' where userId=${MyApp.prefs.getId()}"
      db.execSQL(sql)
      db.close()
   }

   fun updateWater(data: Water){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_WATER set water=${data.water}, volume=${data.volume} where userId = ${MyApp.prefs.getId()} and regDate='${data.regDate}'"
      db.execSQL(sql)
      db.close()
   }

   fun updateBody(data: Body){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_BODY set height=${data.height}, weight=${data.weight}, age=${data.age}, gender='${data.gender}', " +
         "exerciseLevel=${data.exerciseLevel}, fat=${data.fat}, muscle=${data.muscle}, bmi=${data.bmi}, bmr=${data.bmr} " +
         "where userId = ${MyApp.prefs.getId()} and id=${data.id}"
      db.execSQL(sql)
      db.close()
   }

   fun updateDrugCheck(data: DrugCheck){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_DRUG_CHECK set checked=${data.checked} " +
         "where userId = ${MyApp.prefs.getId()} and drugTimeId=${data.drugTimeId} and regDate='${data.regDate}'"
      db.execSQL(sql)
      db.close()
   }

   fun updateDrugSet(isSet: Int){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_DRUG set userId = ${MyApp.prefs.getId()} and isSet = $isSet"
      db.execSQL(sql)
      db.close()
   }

   fun updateNote(data: Note){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_NOTE set title='${data.title}', content='${data.content}', status=${data.status} " +
         "where userId = ${MyApp.prefs.getId()} and regDate='${data.regDate}'"
      db.execSQL(sql)
      db.close()
   }

   fun updateSleep(data: Sleep){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_SLEEP set bedTime=${data.bedTime}, wakeTime=${data.wakeTime}, sleepTime=${data.sleepTime} " +
         "where userId=${MyApp.prefs.getId()} and regDate='${data.regDate}'"
      db.execSQL(sql)
      db.close()
   }

   fun updateGoal(column: String, goal: Int, regDate: String){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_DAILY_DATA set $column=$goal where userId=${MyApp.prefs.getId()} and regDate='$regDate'"
      db.execSQL(sql)
      db.close()
   }

   fun updateBodyGoal(data: DailyData){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_DAILY_DATA set bodyGoal=${data.bodyGoal} where userId=${MyApp.prefs.getId()} and regDate='${data.regDate}'"
      db.execSQL(sql)
      db.close()
   }

   fun deleteAll(table: String, column: String) {
      val db = dbHelper!!.writableDatabase
      val delete = "delete from $table where $column=${MyApp.prefs.getId()}"
      db.execSQL(delete)
      db.close()
   }

   fun deleteItem(table: String, column: String, id: Int) {
      val db = dbHelper!!.writableDatabase
      val delete = "delete from $table where userId=${MyApp.prefs.getId()} and $column=$id"
      db.execSQL(delete)
      db.close()
   }
}
