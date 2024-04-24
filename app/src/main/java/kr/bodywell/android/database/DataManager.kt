package kr.bodywell.android.database

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import kr.bodywell.android.database.DBHelper.Companion.TABLE_BODY
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DAILY_EXERCISE
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DAILY_FOOD
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DAILY_GOAL
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DRUG
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DRUG_CHECK
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DRUG_TIME
import kr.bodywell.android.database.DBHelper.Companion.TABLE_EXERCISE
import kr.bodywell.android.database.DBHelper.Companion.TABLE_FOOD
import kr.bodywell.android.database.DBHelper.Companion.TABLE_IMAGE
import kr.bodywell.android.database.DBHelper.Companion.TABLE_NOTE
import kr.bodywell.android.database.DBHelper.Companion.TABLE_SLEEP
import kr.bodywell.android.database.DBHelper.Companion.TABLE_TOKEN
import kr.bodywell.android.database.DBHelper.Companion.TABLE_USER
import kr.bodywell.android.database.DBHelper.Companion.TABLE_WATER
import kr.bodywell.android.model.Body
import kr.bodywell.android.model.DailyGoal
import kr.bodywell.android.model.Drug
import kr.bodywell.android.model.DrugCheck
import kr.bodywell.android.model.DrugTime
import kr.bodywell.android.model.Exercise
import kr.bodywell.android.model.Food
import kr.bodywell.android.model.Image
import kr.bodywell.android.model.Item
import kr.bodywell.android.model.Note
import kr.bodywell.android.model.Sleep
import kr.bodywell.android.model.Token
import kr.bodywell.android.model.User
import kr.bodywell.android.model.Water
import kr.bodywell.android.util.MyApp

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

   fun getUserById() : Int {
      val db = dbHelper!!.readableDatabase
      var data = 0
      val sql = "select count(id) from $TABLE_USER where id = ${MyApp.prefs.getId()}"
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
         data.uid=cursor.getString(1)
         data.deviceUid=cursor.getString(2)
         data.bodyUid=cursor.getString(3)
         data.type=cursor.getString(4)
         data.email = cursor.getString(5)
         data.name = cursor.getString(6)
         data.gender = cursor.getString(7)
         data.birthday = cursor.getString(8)
         data.image = cursor.getString(9)
         data.height = cursor.getDouble(10).toString()
         data.weight = cursor.getDouble(11).toString()
         data.weightGoal = cursor.getDouble(12).toString()
         data.kcalGoal = cursor.getInt(13).toString()
         data.waterGoal = cursor.getInt(14).toString()
         data.waterUnit = cursor.getInt(15).toString()
         data.regDate = cursor.getString(16)
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
         data.uid=cursor.getString(1)
         data.deviceUid=cursor.getString(2)
         data.bodyUid=cursor.getString(3)
         data.type=cursor.getString(4)
         data.email = cursor.getString(5)
         data.name = cursor.getString(6)
         data.gender = cursor.getString(7)
         data.birthday = cursor.getString(8)
         data.image = cursor.getString(9)
         data.height = cursor.getDouble(10).toString()
         data.weight = cursor.getDouble(11).toString()
         data.weightGoal = cursor.getDouble(12).toString()
         data.kcalGoal = cursor.getInt(13).toString()
         data.waterGoal = cursor.getInt(14).toString()
         data.waterUnit = cursor.getInt(15).toString()
         data.regDate = cursor.getString(16)
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
         data.kcal= cursor.getInt(5)
         data.carbohydrate= cursor.getDouble(6)
         data.protein= cursor.getDouble(7)
         data.fat= cursor.getDouble(8)
         data.salt= cursor.getDouble(9)
         data.sugar= cursor.getDouble(10)
         data.useCount= cursor.getInt(11)
         data.useDate = cursor.getString(12)
      }
      cursor.close()
      return data
   }

   fun getFood(name: String) : Food {
      val db = dbHelper!!.readableDatabase
      val data = Food()
      val sql = "select * from $TABLE_FOOD where userId = ${MyApp.prefs.getId()} and name = '$name'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data.id=cursor.getInt(0)
         data.name=cursor.getString(2)
         data.unit=cursor.getString(3)
         data.amount= cursor.getInt(4)
         data.kcal= cursor.getInt(5)
         data.carbohydrate= cursor.getDouble(6)
         data.protein= cursor.getDouble(7)
         data.fat= cursor.getDouble(8)
         data.salt= cursor.getDouble(9)
         data.sugar= cursor.getDouble(10)
         data.useCount= cursor.getInt(11)
         data.useDate = cursor.getString(12)
      }
      cursor.close()
      return data
   }

   fun getSearchFood(column: String) : ArrayList<Food> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Food>()
      val sql = "select * from $TABLE_FOOD where userId = ${MyApp.prefs.getId()} group by name order by $column desc"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Food()
         data.id=cursor.getInt(0)
         data.name=cursor.getString(2)
         data.unit=cursor.getString(3)
         data.amount= cursor.getInt(4)
         data.kcal= cursor.getInt(5)
         data.carbohydrate= cursor.getDouble(6)
         data.protein= cursor.getDouble(7)
         data.fat= cursor.getDouble(8)
         data.salt= cursor.getDouble(9)
         data.sugar= cursor.getDouble(10)
         data.useCount= cursor.getInt(11)
         data.useDate = cursor.getString(12)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getDailyFood(id: Int) : Food {
      val db = dbHelper!!.readableDatabase
      val data = Food()
      val sql = "select * from $TABLE_DAILY_FOOD where userId = ${MyApp.prefs.getId()} and id = $id"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data.id=cursor.getInt(0)
         data.type = cursor.getInt(2)
         data.name=cursor.getString(3)
         data.unit=cursor.getString(4)
         data.amount= cursor.getInt(5)
         data.kcal= cursor.getInt(6)
         data.carbohydrate= cursor.getDouble(7)
         data.protein= cursor.getDouble(8)
         data.fat= cursor.getDouble(9)
         data.salt= cursor.getDouble(10)
         data.sugar= cursor.getDouble(11)
         data.count= cursor.getInt(12)
         data.regDate = cursor.getString(13)
      }
      cursor.close()
      return data
   }

   fun getDailyFood(type: Int, date: String) : ArrayList<Food> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<Food> = ArrayList()
      val sql = "select * from $TABLE_DAILY_FOOD where userId = ${MyApp.prefs.getId()} and type = $type and regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Food()
         data.id=cursor.getInt(0)
         data.type = cursor.getInt(2)
         data.name=cursor.getString(3)
         data.unit=cursor.getString(4)
         data.amount= cursor.getInt(5)
         data.kcal= cursor.getInt(6)
         data.carbohydrate= cursor.getDouble(7)
         data.protein= cursor.getDouble(8)
         data.fat= cursor.getDouble(9)
         data.salt= cursor.getDouble(10)
         data.sugar= cursor.getDouble(11)
         data.count= cursor.getInt(12)
         data.regDate = cursor.getString(13)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getDailyFood(type: Int, name: String, date: String) : Food {
      val db = dbHelper!!.readableDatabase
      val data = Food()
      val sql = "select * from $TABLE_DAILY_FOOD where userId = ${MyApp.prefs.getId()} and type = $type and name = '$name' and regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data.id=cursor.getInt(0)
         data.type = cursor.getInt(2)
         data.name=cursor.getString(3)
         data.unit=cursor.getString(4)
         data.amount= cursor.getInt(5)
         data.kcal= cursor.getInt(6)
         data.carbohydrate= cursor.getDouble(7)
         data.protein= cursor.getDouble(8)
         data.fat= cursor.getDouble(9)
         data.salt= cursor.getDouble(10)
         data.sugar= cursor.getDouble(11)
         data.count= cursor.getInt(12)
         data.regDate = cursor.getString(13)
      }
      cursor.close()
      return data
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
      val sql = "select * from $TABLE_WATER where userId = ${MyApp.prefs.getId()} and regDate BETWEEN '$start' and '$end' order by regDate"
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

   fun getExercise(id: Int): Exercise {
      val db = dbHelper!!.readableDatabase
      val data = Exercise()
      val sql = "select * from $TABLE_EXERCISE where userId = ${MyApp.prefs.getId()} and id = $id"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data.id=cursor.getInt(0)
         data.name=cursor.getString(2)
         data.intensity =cursor.getString(3)
         data.workoutTime = cursor.getInt(4)
         data.kcal = cursor.getInt(5)
         data.useCount=cursor.getInt(6)
         data.useDate= cursor.getString(7)
      }
      cursor.close()
      return data
   }

   fun getExercise(column: String, data: String) : Exercise {
      val db = dbHelper!!.readableDatabase
      val exercise = Exercise()
      val sql = "select * from $TABLE_EXERCISE where userId = ${MyApp.prefs.getId()} and $column = '$data'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         exercise.id=cursor.getInt(0)
         exercise.name=cursor.getString(2)
         exercise.intensity =cursor.getString(3)
         exercise.workoutTime = cursor.getInt(4)
         exercise.kcal = cursor.getInt(5)
         exercise.useCount=cursor.getInt(6)
         exercise.useDate= cursor.getString(7)
      }
      cursor.close()
      return exercise
   }

   fun getDailyExercise(id: Int): Exercise {
      val db = dbHelper!!.readableDatabase
      val data = Exercise()
      val sql = "select * from $TABLE_DAILY_EXERCISE where userId = ${MyApp.prefs.getId()} and id=$id"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data.id = cursor.getInt(0)
         data.name = cursor.getString(2)
         data.intensity =cursor.getString(3)
         data.workoutTime = cursor.getInt(4)
         data.kcal = cursor.getInt(5)
         data.regDate = cursor.getString(6)
      }
      cursor.close()
      return data
   }

   fun getDailyExercise(regDate: String): ArrayList<Exercise> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Exercise>()
      val sql = "select * from $TABLE_DAILY_EXERCISE where userId = ${MyApp.prefs.getId()} and regDate='$regDate'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Exercise()
         data.id = cursor.getInt(0)
         data.name = cursor.getString(2)
         data.intensity =cursor.getString(3)
         data.workoutTime = cursor.getInt(4)
         data.kcal = cursor.getInt(5)
         data.regDate = cursor.getString(6)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getSearchExercise(column: String) : ArrayList<Exercise> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Exercise>()
      val sql = "select * from $TABLE_EXERCISE where userId = ${MyApp.prefs.getId()} group by name order by $column desc"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Exercise()
         data.id=cursor.getInt(0)
         data.name=cursor.getString(2)
         data.useCount= cursor.getInt(3)
         data.useDate= cursor.getString(4)
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
         data.intensity = cursor.getInt(4)
         data.fat = cursor.getDouble(5)
         data.muscle = cursor.getDouble(6)
         data.bmi = cursor.getDouble(7)
         data.bmr = cursor.getDouble(8)
         data.regDate = cursor.getString(9)
      }
      cursor.close()
      return data
   }

   fun getBody(start: String, end: String) : ArrayList<Body> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Body>()
      val sql = "select weight, fat, bmi, regDate from $TABLE_BODY where userId = ${MyApp.prefs.getId()} and regDate BETWEEN '$start' and '$end' order by regDate"
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
         data.amount = cursor.getInt(4)
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

   fun getDrug(id: Int) : Drug {
      val db = dbHelper!!.readableDatabase
      val data = Drug()
      val sql = "select * from $TABLE_DRUG where userId = ${MyApp.prefs.getId()} and id = $id"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data.id = cursor.getInt(0)
         data.type = cursor.getString(2)
         data.name = cursor.getString(3)
         data.amount = cursor.getInt(4)
         data.unit = cursor.getString(5)
         data.count = cursor.getInt(6)
         data.startDate = cursor.getString(7)
         data.endDate = cursor.getString(8)
         data.isSet = cursor.getInt(9)
         data.regDate = cursor.getString(10)
      }
      cursor.close()
      return data
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
         data.amount = cursor.getInt(4)
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

   fun getDrugCheckCount(date: String) : Int {
      val db = dbHelper!!.readableDatabase
      var data = 0
      val sql = "select count(id) from $TABLE_DRUG_CHECK where userId = ${MyApp.prefs.getId()} and regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data = cursor.getInt(0)
      }
      cursor.close()
      return data
   }

   fun getDrugCheckCount(drugTimeId: Int, date: String) : Int {
      val db = dbHelper!!.readableDatabase
      var data = 0
      val sql = "select count(id) from $TABLE_DRUG_CHECK where userId=${MyApp.prefs.getId()} and drugTimeId=$drugTimeId and regDate='$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data = cursor.getInt(0)
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

   fun getDailyGoal(date: String) : DailyGoal {
      val db = dbHelper!!.readableDatabase
      val data = DailyGoal()
      val sql = "select * from $TABLE_DAILY_GOAL where userId = ${MyApp.prefs.getId()} and regDate = '$date'"
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

   fun getExerciseRanking(date: String) : ArrayList<Item> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Item>()
      val sql = "select count(name) as ranking, name from $TABLE_DAILY_EXERCISE where userId = ${MyApp.prefs.getId()} and regDate = '$date' " +
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

   fun getExerciseRanking(start: String, end: String) : ArrayList<Item> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Item>()
      val sql = "select count(name) as ranking, name from $TABLE_DAILY_EXERCISE where userId = ${MyApp.prefs.getId()} and regDate BETWEEN '$start' and '$end' " +
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

   fun getDrugRanking(date: String) : ArrayList<Item> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Item>()
      val sql = "select count(drugId) as ranking, drugId from $TABLE_DRUG_CHECK where userId = ${MyApp.prefs.getId()} and regDate = '$date' " +
         "group by drugId order by ranking desc limit 4"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Item()
         data.int1=cursor.getInt(0)
         data.int2=cursor.getInt(1)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getDrugRanking(start: String, end: String) : ArrayList<Item> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Item>()
      val sql = "select count(drugId) as ranking, drugId from $TABLE_DRUG_CHECK where userId = ${MyApp.prefs.getId()} and regDate BETWEEN '$start' and '$end' " +
         "group by drugId order by ranking desc limit 4"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Item()
         data.int1=cursor.getInt(0)
         data.int2=cursor.getInt(1)
         list.add(data)
      }
      cursor.close()
      return list
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

   fun insertUser(data: User) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("type", data.type)
      values.put("uid", data.uid)
      values.put("email", data.email)
      values.put("name", data.name)
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
      values.put("kcal", data.kcal)
      values.put("carbohydrate", data.carbohydrate)
      values.put("protein", data.protein)
      values.put("fat", data.fat)
      values.put("salt", data.salt)
      values.put("sugar", data.sugar)
      values.put("useCount", data.useCount)
      values.put("useDate", data.useDate)
      db!!.insert(TABLE_FOOD, null, values)
   }

   fun insertDailyFood(data: Food) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("userId", MyApp.prefs.getId())
      values.put("type", data.type)
      values.put("name", data.name)
      values.put("unit", data.unit)
      values.put("amount", data.amount)
      values.put("kcal", data.kcal)
      values.put("carbohydrate", data.carbohydrate)
      values.put("protein", data.protein)
      values.put("fat", data.fat)
      values.put("salt", data.salt)
      values.put("sugar", data.sugar)
      values.put("count", data.count)
      values.put("regDate", data.regDate)
      db!!.insert(TABLE_DAILY_FOOD, null, values)
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
      values.put("kcal", data.kcal)
      values.put("useCount", data.useCount)
      values.put("useDate", data.useDate)
      db.insert(TABLE_EXERCISE, null, values)
   }

   fun insertDailyExercise(data: Exercise) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("userId", MyApp.prefs.getId())
      values.put("name", data.name)
      values.put("intensity", data.intensity)
      values.put("workoutTime", data.workoutTime)
      values.put("kcal", data.kcal)
      values.put("regDate", data.regDate)
      db.insert(TABLE_DAILY_EXERCISE, null, values)
   }

   fun insertBody(data: Body) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("userId", MyApp.prefs.getId())
      values.put("height", data.height)
      values.put("weight", data.weight)
      values.put("intensity", data.intensity)
      values.put("fat", data.fat)
      values.put("muscle", data.muscle)
      values.put("bmi", data.bmi)
      values.put("bmr", data.bmr)
      values.put("regDate", data.regDate)
      db!!.insert(TABLE_BODY, null, values)
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

   fun insertDrugCheck(data: DrugCheck) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("userId", MyApp.prefs.getId())
      values.put("drugId", data.drugId)
      values.put("drugTimeId", data.drugTimeId)
      values.put("regDate", data.regDate)
      db!!.insert(TABLE_DRUG_CHECK, null, values)
   }

   fun insertDailyGoal(data: DailyGoal) {
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
      db!!.insert(TABLE_DAILY_GOAL, null, values)
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

   fun updateIntByDate(table: String, column: String, data: Int, regDate: String){
      val db = dbHelper!!.writableDatabase
      val sql = "update $table set $column=$data where userId=${MyApp.prefs.getId()} and regDate='$regDate'"
      db.execSQL(sql)
      db.close()
   }

   fun updateDoubleByDate(table: String, column: String, data: Double, regDate: String){
      val db = dbHelper!!.writableDatabase
      val sql = "update $table set $column=$data where userId=${MyApp.prefs.getId()} and regDate='$regDate'"
      db.execSQL(sql)
      db.close()
   }

   fun updateUser(data: User){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_USER set uid='${data.uid}', name='${data.name}', regDate='${data.regDate}' where type='${data.type}' and email='${data.email}'"
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

   fun updateFood(data: Food){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_FOOD set name='${data.name}', unit='${data.unit}', amount=${data.amount}, kcal=${data.kcal}, carbohydrate=${data.carbohydrate}, " +
         "protein=${data.protein}, fat=${data.fat}, salt=${data.salt}, sugar=${data.sugar} where id=${data.id}"
      db.execSQL(sql)
      db.close()
   }

   fun updateDailyFood(data: Food){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_DAILY_FOOD set unit='${data.unit}', amount=${data.amount}, kcal=${data.kcal}, carbohydrate=${data.carbohydrate}, " +
         "protein=${data.protein}, fat=${data.fat}, salt=${data.salt}, sugar=${data.sugar}, count=${data.count} where id=${data.id}"
      db.execSQL(sql)
      db.close()
   }

   fun updateExercise(data: Exercise){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_EXERCISE set name='${data.name}', intensity='${data.intensity}', workoutTime=${data.workoutTime}, kcal=${data.kcal} where id=${data.id}"
      db.execSQL(sql)
      db.close()
   }

   fun updateBody(data: Body){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_BODY set height=${data.height}, weight=${data.weight}, intensity=${data.intensity}, fat=${data.fat}, muscle=${data.muscle}, " +
         "bmi=${data.bmi}, bmr=${data.bmr} where userId = ${MyApp.prefs.getId()} and id=${data.id}"
      db.execSQL(sql)
      db.close()
   }

   fun updateDrug(data: Drug) {
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_DRUG set type='${data.type}', name='${data.name}', amount=${data.amount}, unit='${data.unit}', count=${data.count}, " +
         "startDate='${data.startDate}', endDate='${data.endDate}', isSet=1 where userId = ${MyApp.prefs.getId()} and id=${data.id}"
      db.execSQL(sql)
      db.close()
   }

   fun updateNote(data: Note){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_NOTE set title='${data.title}', content='${data.content}', status=${data.status} where userId = ${MyApp.prefs.getId()} and regDate='${data.regDate}'"
      db.execSQL(sql)
      db.close()
   }

   fun updateSleep(data: Sleep){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_SLEEP set bedTime=${data.bedTime}, wakeTime=${data.wakeTime}, sleepTime=${data.sleepTime} where userId=${MyApp.prefs.getId()} and regDate='${data.regDate}'"
      db.execSQL(sql)
      db.close()
   }

   fun deleteItem(table: String, column: String, data: Int) {
      val db = dbHelper!!.writableDatabase
      db.delete(table, "userId=${MyApp.prefs.getId()} and $column=$data", null)
      db.close()
   }

   fun deleteItem(table: String, column: String, data: String) {
      val db = dbHelper!!.writableDatabase
      db.delete(table, "userId=${MyApp.prefs.getId()} and $column='$data'", null)
      db.close()
   }

   fun deleteItem(table: String, column1: String, int: Int, column2: String, str: String) {
      val db = dbHelper!!.writableDatabase
      db.delete(table, "userId=${MyApp.prefs.getId()} and $column1=$int and $column2='$str'", null)
      db.close()
   }

   fun deleteAll(table: String, column: String) {
      val db = dbHelper!!.writableDatabase
      db.delete(table, "$column=${MyApp.prefs.getId()}", null)
      db.close()
   }
}
