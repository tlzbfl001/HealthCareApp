package kr.bodywell.android.database

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import kr.bodywell.android.database.DBHelper.Companion.TABLE_BODY
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DAILY_EXERCISE
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DAILY_FOOD
import kr.bodywell.android.database.DBHelper.Companion.TABLE_GOAL
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DRUG
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DRUG_CHECK
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DRUG_TIME
import kr.bodywell.android.database.DBHelper.Companion.TABLE_EXERCISE
import kr.bodywell.android.database.DBHelper.Companion.TABLE_FOOD
import kr.bodywell.android.database.DBHelper.Companion.TABLE_IMAGE
import kr.bodywell.android.database.DBHelper.Companion.TABLE_NOTE
import kr.bodywell.android.database.DBHelper.Companion.TABLE_SLEEP
import kr.bodywell.android.database.DBHelper.Companion.TABLE_TOKEN
import kr.bodywell.android.database.DBHelper.Companion.TABLE_UNUSED
import kr.bodywell.android.database.DBHelper.Companion.TABLE_USER
import kr.bodywell.android.database.DBHelper.Companion.TABLE_WATER
import kr.bodywell.android.model.Body
import kr.bodywell.android.model.Goal
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
import kr.bodywell.android.model.Unused
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
      var count = 0
      val sql = "select count(id) from $TABLE_USER"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         count = cursor.getInt(0)
      }
      cursor.close()
      return count
   }

   fun getUser(type: String, email: String) : User {
      val db = dbHelper!!.readableDatabase
      val values = User()
      val sql = "select id, type, email, idToken, regDate from $TABLE_USER where type = '$type' and email = '$email'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id=cursor.getInt(0)
         values.type=cursor.getString(1)
         values.email = cursor.getString(2)
         values.idToken = cursor.getString(3)
         values.regDate = cursor.getString(4)
      }
      cursor.close()
      return values
   }

   fun getUser() : User {
      val db = dbHelper!!.readableDatabase
      val values = User()
      val sql = "select * from $TABLE_USER where id = ${MyApp.prefs.getId()}"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id=cursor.getInt(0)
         values.type=cursor.getString(1)
         values.email = cursor.getString(2)
         values.idToken = cursor.getString(3)
         values.userUid=cursor.getString(4)
         values.deviceUid=cursor.getString(5)
         values.name = cursor.getString(6)
         values.gender = cursor.getString(7)
         values.birthday = cursor.getString(8)
         values.image = cursor.getString(9)
         values.height = cursor.getDouble(10)
         values.weight = cursor.getDouble(11)
         values.weightGoal = cursor.getDouble(12)
         values.kcalGoal = cursor.getInt(13)
         values.waterGoal = cursor.getInt(14)
         values.waterUnit = cursor.getInt(15)
         values.regDate = cursor.getString(16)
      }
      cursor.close()
      return values
   }

   fun getToken() : Token {
      val db = dbHelper!!.readableDatabase
      val values = Token()
      val sql = "select * from $TABLE_TOKEN where userId = ${MyApp.prefs.getId()}"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id=cursor.getInt(0)
         values.userId=cursor.getInt(1)
         values.access=cursor.getString(2)
         values.refresh = cursor.getString(3)
         values.accessRegDate = cursor.getString(4)
         values.refreshRegDate = cursor.getString(5)
      }
      cursor.close()
      return values
   }

   fun getFood(column: String, data: Int) : Food {
      val db = dbHelper!!.readableDatabase
      val values = Food()
      val sql = "select * from $TABLE_FOOD where userId = ${MyApp.prefs.getId()} and $column = $data"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id=cursor.getInt(0)
         values.name=cursor.getString(4)
         values.unit=cursor.getString(5)
         values.amount= cursor.getInt(6)
         values.kcal= cursor.getInt(7)
         values.carbohydrate= cursor.getDouble(8)
         values.protein= cursor.getDouble(9)
         values.fat= cursor.getDouble(10)
         values.salt= cursor.getDouble(11)
         values.sugar= cursor.getDouble(12)
         values.useCount= cursor.getInt(13)
         values.useDate = cursor.getString(14)
      }
      cursor.close()
      return values
   }

   fun getFood(column: String, data: String) : Food {
      val db = dbHelper!!.readableDatabase
      val values = Food()
      val sql = "select * from $TABLE_FOOD where userId = ${MyApp.prefs.getId()} and $column = '$data'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id=cursor.getInt(0)
         values.uid=cursor.getString(3)
         values.name=cursor.getString(4)
         values.unit=cursor.getString(5)
         values.amount= cursor.getInt(6)
         values.kcal= cursor.getInt(7)
         values.carbohydrate= cursor.getDouble(8)
         values.protein= cursor.getDouble(9)
         values.fat= cursor.getDouble(10)
         values.salt= cursor.getDouble(11)
         values.sugar= cursor.getDouble(12)
         values.useCount= cursor.getInt(13)
         values.useDate = cursor.getString(14)
      }
      cursor.close()
      return values
   }

   fun getFoodUid() : Food {
      val db = dbHelper!!.readableDatabase
      val values = Food()
      val sql = "select * from $TABLE_FOOD where userId = ${MyApp.prefs.getId()} and uid is '' limit 1"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id=cursor.getInt(0)
         values.uid=cursor.getString(3)
         values.name=cursor.getString(4)
         values.unit=cursor.getString(5)
         values.amount= cursor.getInt(6)
         values.kcal= cursor.getInt(7)
         values.carbohydrate= cursor.getDouble(8)
         values.protein= cursor.getDouble(9)
         values.fat= cursor.getDouble(10)
         values.salt= cursor.getDouble(11)
         values.sugar= cursor.getDouble(12)
         values.useCount= cursor.getInt(13)
         values.useDate = cursor.getString(14)
      }
      cursor.close()
      return values
   }

   fun getFoodUpdated() : Food {
      val db = dbHelper!!.readableDatabase
      val values = Food()
      val sql = "select * from $TABLE_FOOD where userId = ${MyApp.prefs.getId()} and isUpdated = 1 limit 1"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id=cursor.getInt(0)
         values.uid=cursor.getString(3)
         values.name=cursor.getString(4)
         values.unit=cursor.getString(5)
         values.amount= cursor.getInt(6)
         values.kcal= cursor.getInt(7)
         values.carbohydrate= cursor.getDouble(8)
         values.protein= cursor.getDouble(9)
         values.fat= cursor.getDouble(10)
         values.salt= cursor.getDouble(11)
         values.sugar= cursor.getDouble(12)
         values.useCount= cursor.getInt(13)
         values.useDate = cursor.getString(14)
      }
      cursor.close()
      return values
   }

   fun getSearchFood(column: String) : ArrayList<Food> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Food>()
      val sql = "select * from $TABLE_FOOD where userId = ${MyApp.prefs.getId()} group by name order by $column desc"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Food()
         values.id=cursor.getInt(0)
         values.basic=cursor.getInt(2)
         values.uid=cursor.getString(3)
         values.name=cursor.getString(4)
         values.unit=cursor.getString(5)
         values.amount= cursor.getInt(6)
         values.kcal= cursor.getInt(7)
         values.carbohydrate= cursor.getDouble(8)
         values.protein= cursor.getDouble(9)
         values.fat= cursor.getDouble(10)
         values.salt= cursor.getDouble(11)
         values.sugar= cursor.getDouble(12)
         values.useCount= cursor.getInt(13)
         values.useDate = cursor.getString(14)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getDailyFood(column: String, id: Int) : Food {
      val db = dbHelper!!.readableDatabase
      val values = Food()
      val sql = "select * from $TABLE_DAILY_FOOD where userId = ${MyApp.prefs.getId()} and $column = $id"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id=cursor.getInt(0)
         values.type = cursor.getString(3)
         values.name=cursor.getString(4)
         values.unit=cursor.getString(5)
         values.amount= cursor.getInt(6)
         values.kcal= cursor.getInt(7)
         values.carbohydrate= cursor.getDouble(8)
         values.protein= cursor.getDouble(9)
         values.fat= cursor.getDouble(10)
         values.salt= cursor.getDouble(11)
         values.sugar= cursor.getDouble(12)
         values.count= cursor.getInt(13)
         values.regDate = cursor.getString(14)
      }
      cursor.close()
      return values
   }

   fun getDailyFood(type: String, date: String) : ArrayList<Food> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<Food> = ArrayList()
      val sql = "select * from $TABLE_DAILY_FOOD where userId = ${MyApp.prefs.getId()} and type = '$type' and regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Food()
         values.id=cursor.getInt(0)
         values.uid = cursor.getString(2)
         values.type = cursor.getString(3)
         values.name=cursor.getString(4)
         values.unit=cursor.getString(5)
         values.amount= cursor.getInt(6)
         values.kcal= cursor.getInt(7)
         values.carbohydrate= cursor.getDouble(8)
         values.protein= cursor.getDouble(9)
         values.fat= cursor.getDouble(10)
         values.salt= cursor.getDouble(11)
         values.sugar= cursor.getDouble(12)
         values.count= cursor.getInt(13)
         values.regDate = cursor.getString(14)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getDailyFood(type: String, name: String, date: String) : Food {
      val db = dbHelper!!.readableDatabase
      val values = Food()
      val sql = "select * from $TABLE_DAILY_FOOD where userId = ${MyApp.prefs.getId()} and type = '$type' and name = '$name' and regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id=cursor.getInt(0)
         values.type = cursor.getString(3)
         values.name=cursor.getString(4)
         values.unit=cursor.getString(5)
         values.amount= cursor.getInt(6)
         values.kcal= cursor.getInt(7)
         values.carbohydrate= cursor.getDouble(8)
         values.protein= cursor.getDouble(9)
         values.fat= cursor.getDouble(10)
         values.salt= cursor.getDouble(11)
         values.sugar= cursor.getDouble(12)
         values.count= cursor.getInt(13)
         values.regDate = cursor.getString(14)
      }
      cursor.close()
      return values
   }

   fun getDailyFoodUid() : Food {
      val db = dbHelper!!.readableDatabase
      val values = Food()
      val sql = "select * from $TABLE_DAILY_FOOD where userId = ${MyApp.prefs.getId()} and uid is '' limit 1"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id=cursor.getInt(0)
         values.type = cursor.getString(3)
         values.name=cursor.getString(4)
         values.unit=cursor.getString(5)
         values.amount= cursor.getInt(6)
         values.kcal= cursor.getInt(7)
         values.carbohydrate= cursor.getDouble(8)
         values.protein= cursor.getDouble(9)
         values.fat= cursor.getDouble(10)
         values.salt= cursor.getDouble(11)
         values.sugar= cursor.getDouble(12)
         values.count= cursor.getInt(13)
         values.regDate = cursor.getString(14)
      }
      cursor.close()
      return values
   }

   fun getDailyFoodUpdated() : Food {
      val db = dbHelper!!.readableDatabase
      val values = Food()
      val sql = "select * from $TABLE_DAILY_FOOD where userId = ${MyApp.prefs.getId()} and isUpdated = 1 limit 1"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id=cursor.getInt(0)
         values.uid = cursor.getString(2)
         values.type = cursor.getString(3)
         values.name=cursor.getString(4)
         values.unit=cursor.getString(5)
         values.amount= cursor.getInt(6)
         values.kcal= cursor.getInt(7)
         values.carbohydrate= cursor.getDouble(8)
         values.protein= cursor.getDouble(9)
         values.fat= cursor.getDouble(10)
         values.salt= cursor.getDouble(11)
         values.sugar= cursor.getDouble(12)
         values.count= cursor.getInt(13)
         values.regDate = cursor.getString(14)
      }
      cursor.close()
      return values
   }

   fun getWater(start: String, end: String) : ArrayList<Water> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Water>()
      val sql = "select * from $TABLE_WATER where userId = ${MyApp.prefs.getId()} and regDate BETWEEN '$start' and '$end' order by regDate"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Water()
         values.id = cursor.getInt(0)
         values.count=cursor.getInt(3)
         values.volume=cursor.getInt(4)
         values.regDate = cursor.getString(5)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getWater(date: String) : Water {
      val db = dbHelper!!.readableDatabase
      val values = Water()
      val sql = "select * from $TABLE_WATER where userId = ${MyApp.prefs.getId()} and regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.uid=cursor.getString(2)
         values.count=cursor.getInt(3)
         values.volume=cursor.getInt(4)
         values.regDate = cursor.getString(5)
         values.isUpdated = cursor.getInt(6)
      }
      cursor.close()
      return values
   }

   fun getWaterUid() : Water {
      val db = dbHelper!!.readableDatabase
      val values = Water()
      val sql = "select * from $TABLE_WATER where userId = ${MyApp.prefs.getId()} and uid is '' limit 1"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.uid=cursor.getString(2)
         values.count=cursor.getInt(3)
         values.volume=cursor.getInt(4)
         values.regDate = cursor.getString(5)
      }
      cursor.close()
      return values
   }

   fun getWaterUpdated() : Water {
      val db = dbHelper!!.readableDatabase
      val values = Water()
      val sql = "select * from $TABLE_WATER where userId = ${MyApp.prefs.getId()} and isUpdated = 1 limit 1"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.uid=cursor.getString(2)
         values.count=cursor.getInt(3)
         values.volume=cursor.getInt(4)
         values.regDate = cursor.getString(5)
      }
      cursor.close()
      return values
   }

   fun getExercise(id: Int): Exercise {
      val db = dbHelper!!.readableDatabase
      val values = Exercise()
      val sql = "select * from $TABLE_EXERCISE where userId = ${MyApp.prefs.getId()} and id = $id"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.uid=cursor.getString(3)
         values.name = cursor.getString(4)
         values.intensity =cursor.getString(5)
         values.workoutTime = cursor.getInt(6)
         values.kcal = cursor.getInt(7)
         values.useCount = cursor.getInt(8)
         values.useDate = cursor.getString(9)
      }
      cursor.close()
      return values
   }

   fun getExercise(column: String, data: String) : Exercise {
      val db = dbHelper!!.readableDatabase
      val values = Exercise()
      val sql = "select * from $TABLE_EXERCISE where userId = ${MyApp.prefs.getId()} and $column = '$data'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.uid=cursor.getString(3)
         values.name = cursor.getString(4)
         values.intensity =cursor.getString(5)
         values.workoutTime = cursor.getInt(6)
         values.kcal = cursor.getInt(7)
         values.useCount = cursor.getInt(8)
         values.useDate = cursor.getString(9)
      }
      cursor.close()
      return values
   }

   fun getExerciseUid(): Exercise {
      val db = dbHelper!!.readableDatabase
      val values = Exercise()
      val sql = "select * from $TABLE_EXERCISE where userId = ${MyApp.prefs.getId()} and uid is '' limit 1"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.uid=cursor.getString(3)
         values.name = cursor.getString(4)
         values.intensity =cursor.getString(5)
         values.workoutTime = cursor.getInt(6)
         values.kcal = cursor.getInt(7)
         values.useCount = cursor.getInt(8)
         values.useDate = cursor.getString(9)
      }
      cursor.close()
      return values
   }

   fun getExerciseUpdated(): Exercise {
      val db = dbHelper!!.readableDatabase
      val values = Exercise()
      val sql = "select * from $TABLE_EXERCISE where userId = ${MyApp.prefs.getId()} and isUpdated = 1 limit 1"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.uid=cursor.getString(3)
         values.name = cursor.getString(4)
         values.intensity =cursor.getString(5)
         values.workoutTime = cursor.getInt(6)
         values.kcal = cursor.getInt(7)
         values.useCount = cursor.getInt(8)
         values.useDate = cursor.getString(9)
      }
      cursor.close()
      return values
   }

   fun getDailyExercise(column: String, id: Int): Exercise {
      val db = dbHelper!!.readableDatabase
      val values = Exercise()
      val sql = "select * from $TABLE_DAILY_EXERCISE where userId = ${MyApp.prefs.getId()} and $column=$id"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.name = cursor.getString(3)
         values.intensity =cursor.getString(4)
         values.workoutTime = cursor.getInt(5)
         values.kcal = cursor.getInt(6)
         values.regDate = cursor.getString(7)
      }
      cursor.close()
      return values
   }

   fun getDailyExercise(column: String, data: String): ArrayList<Exercise> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Exercise>()
      val sql = "select * from $TABLE_DAILY_EXERCISE where userId = ${MyApp.prefs.getId()} and $column='$data'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Exercise()
         values.id = cursor.getInt(0)
         values.uid=cursor.getString(2)
         values.name = cursor.getString(3)
         values.intensity =cursor.getString(4)
         values.workoutTime = cursor.getInt(5)
         values.kcal = cursor.getInt(6)
         values.regDate = cursor.getString(7)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getDailyExerciseUid(): Exercise {
      val db = dbHelper!!.readableDatabase
      val values = Exercise()
      val sql = "select * from $TABLE_DAILY_EXERCISE where userId = ${MyApp.prefs.getId()} and uid is '' limit 1"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.uid=cursor.getString(2)
         values.name = cursor.getString(3)
         values.intensity =cursor.getString(4)
         values.workoutTime = cursor.getInt(5)
         values.kcal = cursor.getInt(6)
         values.regDate = cursor.getString(7)
      }
      cursor.close()
      return values
   }

   fun getDailyExerciseUpdated(): Exercise {
      val db = dbHelper!!.readableDatabase
      val values = Exercise()
      val sql = "select * from $TABLE_DAILY_EXERCISE where userId = ${MyApp.prefs.getId()} and isUpdated = 1 limit 1"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.uid=cursor.getString(2)
         values.name = cursor.getString(3)
         values.intensity =cursor.getString(4)
         values.workoutTime = cursor.getInt(5)
         values.kcal = cursor.getInt(6)
         values.regDate = cursor.getString(7)
      }
      cursor.close()
      return values
   }

   fun getSearchExercise(column: String) : ArrayList<Exercise> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Exercise>()
      val sql = "select id, basic, uid, name from $TABLE_EXERCISE where userId = ${MyApp.prefs.getId()} group by name order by $column desc"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Exercise()
         values.id = cursor.getInt(0)
         values.basic = cursor.getInt(1)
         values.uid = cursor.getString(2)
         values.name = cursor.getString(3)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getBody(date: String) : Body {
      val db = dbHelper!!.readableDatabase
      val values = Body()
      val sql = "select * from $TABLE_BODY where userId = ${MyApp.prefs.getId()} and regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.uid=cursor.getString(2)
         values.height = cursor.getDouble(3)
         values.weight = cursor.getDouble(4)
         values.intensity = cursor.getInt(5)
         values.fat = cursor.getDouble(6)
         values.muscle = cursor.getDouble(7)
         values.bmi = cursor.getDouble(8)
         values.bmr = cursor.getDouble(9)
         values.regDate = cursor.getString(10)
      }
      cursor.close()
      return values
   }

   fun getBody(start: String, end: String) : ArrayList<Body> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Body>()
      val sql = "select weight, fat, bmi, regDate from $TABLE_BODY where userId = ${MyApp.prefs.getId()} and regDate BETWEEN '$start' and '$end' order by regDate"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Body()
         values.weight = cursor.getDouble(0)
         values.fat = cursor.getDouble(1)
         values.bmi = cursor.getDouble(2)
         values.regDate = cursor.getString(3)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getBodyUid() : Body {
      val db = dbHelper!!.readableDatabase
      val values = Body()
      val sql = "select * from $TABLE_BODY where userId = ${MyApp.prefs.getId()} and uid is '' limit 1"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.uid=cursor.getString(2)
         values.height = cursor.getDouble(3)
         values.weight = cursor.getDouble(4)
         values.intensity = cursor.getInt(5)
         values.fat = cursor.getDouble(6)
         values.muscle = cursor.getDouble(7)
         values.bmi = cursor.getDouble(8)
         values.bmr = cursor.getDouble(9)
         values.regDate = cursor.getString(10)
      }
      cursor.close()
      return values
   }

   fun getBodyUpdated() : Body {
      val db = dbHelper!!.readableDatabase
      val values = Body()
      val sql = "select * from $TABLE_BODY where userId = ${MyApp.prefs.getId()} and isUpdated = 1 limit 1"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.uid=cursor.getString(2)
         values.height = cursor.getDouble(3)
         values.weight = cursor.getDouble(4)
         values.intensity = cursor.getInt(5)
         values.fat = cursor.getDouble(6)
         values.muscle = cursor.getDouble(7)
         values.bmi = cursor.getDouble(8)
         values.bmr = cursor.getDouble(9)
         values.regDate = cursor.getString(10)
      }
      cursor.close()
      return values
   }

   fun getDrug(date: String) : ArrayList<Drug> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Drug>()
      val sql = "select * from $TABLE_DRUG where userId = ${MyApp.prefs.getId()} and '$date' BETWEEN startDate and endDate"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Drug()
         values.id = cursor.getInt(0)
         values.uid = cursor.getString(2)
         values.type = cursor.getString(3)
         values.name = cursor.getString(4)
         values.amount = cursor.getInt(5)
         values.unit = cursor.getString(6)
         values.count = cursor.getInt(7)
         values.startDate = cursor.getString(8)
         values.endDate = cursor.getString(9)
         values.isSet = cursor.getInt(10)
         values.regDate = cursor.getString(11)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getDrug(id: Int) : Drug {
      val db = dbHelper!!.readableDatabase
      val values = Drug()
      val sql = "select * from $TABLE_DRUG where userId = ${MyApp.prefs.getId()} and id = $id"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.uid = cursor.getString(2)
         values.type = cursor.getString(3)
         values.name = cursor.getString(4)
         values.amount = cursor.getInt(5)
         values.unit = cursor.getString(6)
         values.count = cursor.getInt(7)
         values.startDate = cursor.getString(8)
         values.endDate = cursor.getString(9)
         values.isSet = cursor.getInt(10)
         values.regDate = cursor.getString(11)
      }
      cursor.close()
      return values
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
      val values = Drug()
      val sql = "select id from $TABLE_DRUG where userId = ${MyApp.prefs.getId()} and regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
      }
      cursor.close()
      return values
   }

   fun getDrugDaily(date: String): ArrayList<Drug> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<Drug> = ArrayList()
      val sql = "select * from $TABLE_DRUG where userId = ${MyApp.prefs.getId()} and date('$date') between date(startDate) and date(endDate)"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Drug()
         values.id = cursor.getInt(0)
         values.type = cursor.getString(3)
         values.name = cursor.getString(4)
         values.amount = cursor.getInt(5)
         values.unit = cursor.getString(6)
         values.count = cursor.getInt(7)
         values.startDate = cursor.getString(8)
         values.endDate = cursor.getString(9)
         values.isSet = cursor.getInt(10)
         values.regDate = cursor.getString(11)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getDrugUid() : Drug {
      val db = dbHelper!!.readableDatabase
      val values = Drug()
      val sql = "select * from $TABLE_DRUG where userId = ${MyApp.prefs.getId()} and uid is '' limit 1"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.type = cursor.getString(3)
         values.name = cursor.getString(4)
         values.amount = cursor.getInt(5)
         values.unit = cursor.getString(6)
         values.count = cursor.getInt(7)
         values.startDate = cursor.getString(8)
         values.endDate = cursor.getString(9)
         values.isSet = cursor.getInt(10)
         values.regDate = cursor.getString(11)
      }
      cursor.close()
      return values
   }

   fun getDrugTime(id: Int) : ArrayList<DrugTime> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<DrugTime> = ArrayList()
      val sql = "select * from $TABLE_DRUG_TIME where userId = ${MyApp.prefs.getId()} and drugId = $id order by time"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = DrugTime()
         values.id = cursor.getInt(0)
         values.drugId = cursor.getInt(2)
         values.uid = cursor.getString(3)
         values.time = cursor.getString(4)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getDrugTimeUid(data: Int) : DrugTime {
      val db = dbHelper!!.readableDatabase
      val values = DrugTime()
      val sql = "select uid from $TABLE_DRUG_TIME where userId = ${MyApp.prefs.getId()} and id = $data"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.uid = cursor.getString(0)
      }
      cursor.close()
      return values
   }

   fun getDrugTimeUid() : DrugTime {
      val db = dbHelper!!.readableDatabase
      val values = DrugTime()
      val sql = "select * from $TABLE_DRUG_TIME where userId = ${MyApp.prefs.getId()} and uid is '' limit 1"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.drugId = cursor.getInt(2)
         values.uid = cursor.getString(3)
         values.time = cursor.getString(4)
      }
      cursor.close()
      return values
   }

   fun getDrugCheck(drugTimeId: Int) : ArrayList<String> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<String> = ArrayList()
      val sql = "select uid from $TABLE_DRUG_CHECK where userId = ${MyApp.prefs.getId()} and drugTimeId = $drugTimeId"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         list.add(cursor.getString(0))
      }
      cursor.close()
      return list
   }

   fun getDrugCheckCount(date: String) : Int {
      val db = dbHelper!!.readableDatabase
      var count = 0
      val sql = "select count(id) from $TABLE_DRUG_CHECK where userId = ${MyApp.prefs.getId()} and regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         count = cursor.getInt(0)
      }
      cursor.close()
      return count
   }

   fun getDrugCheckCount(drugTimeId: Int, date: String) : Int {
      val db = dbHelper!!.readableDatabase
      var count = 0
      val sql = "select count(id) from $TABLE_DRUG_CHECK where userId=${MyApp.prefs.getId()} and drugTimeId=$drugTimeId and regDate='$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         count = cursor.getInt(0)
      }
      cursor.close()
      return count
   }

   fun getDrugCheckUid() : DrugCheck {
      val db = dbHelper!!.readableDatabase
      val values = DrugCheck()
      val sql = "select * from $TABLE_DRUG_CHECK where userId = ${MyApp.prefs.getId()} and uid is '' limit 1"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.drugId = cursor.getInt(2)
         values.drugTimeId = cursor.getInt(3)
         values.uid = cursor.getString(4)
         values.regDate = cursor.getString(5)
      }
      cursor.close()
      return values
   }

   fun getSleep(date: String) : Sleep {
      val db = dbHelper!!.readableDatabase
      val values = Sleep()
      val sql = "select * from $TABLE_SLEEP where userId = ${MyApp.prefs.getId()} and regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.uid = cursor.getString(2)
         values.startTime = cursor.getString(3)
         values.endTime = cursor.getString(4)
         values.total = cursor.getInt(5)
         values.regDate = cursor.getString(6)
      }
      cursor.close()
      return values
   }

   fun getSleepUid() : Sleep {
      val db = dbHelper!!.readableDatabase
      val values = Sleep()
      val sql = "select id, startTime, endTime from $TABLE_SLEEP where userId = ${MyApp.prefs.getId()} and uid is '' limit 1"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.startTime = cursor.getString(1)
         values.endTime = cursor.getString(2)
      }
      cursor.close()
      return values
   }

   fun getSleepUpdated() : Sleep {
      val db = dbHelper!!.readableDatabase
      val values = Sleep()
      val sql = "select id, uid, startTime, endTime from $TABLE_SLEEP where userId = ${MyApp.prefs.getId()} and isUpdated = 1 limit 1"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.uid = cursor.getString(1)
         values.startTime = cursor.getString(2)
         values.endTime = cursor.getString(3)
      }
      cursor.close()
      return values
   }

   fun getExerciseRanking(date: String) : ArrayList<Item> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Item>()
      val sql = "select count(name) as ranking, name from $TABLE_DAILY_EXERCISE where userId = ${MyApp.prefs.getId()} and regDate = '$date' " +
         "group by name order by ranking desc limit 4"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Item()
         values.string1=cursor.getString(0)
         values.string2=cursor.getString(1)
         list.add(values)
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
         val values = Item()
         values.string1=cursor.getString(0)
         values.string2=cursor.getString(1)
         list.add(values)
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
         val values = Item()
         values.int1=cursor.getInt(0)
         values.int2=cursor.getInt(1)
         list.add(values)
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
         val values = Item()
         values.int1=cursor.getInt(0)
         values.int2=cursor.getInt(1)
         list.add(values)
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
      val values = Note()
      val sql = "select * from $TABLE_NOTE where userId = ${MyApp.prefs.getId()} and regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.title = cursor.getString(2)
         values.content = cursor.getString(3)
         values.status = cursor.getInt(4)
         values.regDate = cursor.getString(5)
      }
      cursor.close()
      return values
   }

   fun getGoal(id: Int) : Goal {
      val db = dbHelper!!.readableDatabase
      val values = Goal()
      val sql = "select * from $TABLE_GOAL where userId = ${MyApp.prefs.getId()} and id = $id"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.uid = cursor.getString(2)
         values.food = cursor.getInt(3)
         values.waterVolume = cursor.getInt(4)
         values.water = cursor.getInt(5)
         values.exercise = cursor.getInt(6)
         values.body = cursor.getDouble(7)
         values.sleep = cursor.getInt(8)
         values.drug = cursor.getInt(9)
         values.regDate = cursor.getString(10)
      }
      cursor.close()
      return values
   }

   fun getGoal(date: String) : Goal {
      val db = dbHelper!!.readableDatabase
      val values = Goal()
      val sql = "select * from $TABLE_GOAL where userId = ${MyApp.prefs.getId()} and regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.uid = cursor.getString(2)
         values.food = cursor.getInt(3)
         values.waterVolume = cursor.getInt(4)
         values.water = cursor.getInt(5)
         values.exercise = cursor.getInt(6)
         values.body = cursor.getDouble(7)
         values.sleep = cursor.getInt(8)
         values.drug = cursor.getInt(9)
         values.regDate = cursor.getString(10)
      }
      cursor.close()
      return values
   }

   fun getGoalUid() : Goal {
      val db = dbHelper!!.readableDatabase
      val values = Goal()
      val sql = "select * from $TABLE_GOAL where userId = ${MyApp.prefs.getId()} and uid is '' limit 1"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.uid = cursor.getString(2)
         values.food = cursor.getInt(3)
         values.waterVolume = cursor.getInt(4)
         values.water = cursor.getInt(5)
         values.exercise = cursor.getInt(6)
         values.body = cursor.getDouble(7)
         values.sleep = cursor.getInt(8)
         values.drug = cursor.getInt(9)
         values.regDate = cursor.getString(10)
      }
      cursor.close()
      return values
   }

   fun getGoalUpdated() : Goal {
      val db = dbHelper!!.readableDatabase
      val values = Goal()
      val sql = "select * from $TABLE_GOAL where userId = ${MyApp.prefs.getId()} and isUpdated = 1 limit 1"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.uid = cursor.getString(2)
         values.food = cursor.getInt(3)
         values.waterVolume = cursor.getInt(4)
         values.water = cursor.getInt(5)
         values.exercise = cursor.getInt(6)
         values.body = cursor.getDouble(7)
         values.sleep = cursor.getInt(8)
         values.drug = cursor.getInt(9)
         values.regDate = cursor.getString(10)
      }
      cursor.close()
      return values
   }

   fun getImage(type: String, date: String) : ArrayList<Image> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<Image> = ArrayList()
      val sql = "select * from $TABLE_IMAGE where userId = ${MyApp.prefs.getId()} and type = '$type' and regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Image()
         values.id = cursor.getInt(0)
         values.type = cursor.getString(2)
         values.dataId = cursor.getInt(3)
         values.imageUri = cursor.getString(4)
         values.regDate = cursor.getString(5)
         list.add(values)
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
         val values = Image()
         values.id = cursor.getInt(0)
         values.type = cursor.getString(2)
         values.dataId = cursor.getInt(3)
         values.imageUri = cursor.getString(4)
         values.regDate = cursor.getString(5)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getUnused() : Unused {
      val db = dbHelper!!.readableDatabase
      val values = Unused()
      val sql = "select * from $TABLE_UNUSED where userId = ${MyApp.prefs.getId()} limit 1"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.type = cursor.getString(2)
         values.value = cursor.getString(3)
      }
      cursor.close()
      return values
   }

   fun insertUser(data: User) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("type", data.type)
      values.put("email", data.email)
      values.put("idToken", data.idToken)
      values.put("userUid", data.userUid)
      values.put("deviceUid", data.deviceUid)
      values.put("name", data.name)
      values.put("gender", data.gender)
      values.put("birthday", data.birthday)
      values.put("image", data.image)
      values.put("height", data.height)
      values.put("weight", data.weight)
      values.put("weightGoal", data.weightGoal)
      values.put("kcalGoal", data.kcalGoal)
      values.put("waterGoal", data.waterGoal)
      values.put("waterUnit", data.waterUnit)
      values.put("regDate", data.regDate)
      db!!.insert(TABLE_USER, null, values)
   }

   fun insertToken(data: Token) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("userId", MyApp.prefs.getId())
      values.put("access", data.access)
      values.put("refresh", data.refresh)
      values.put("accessRegDate", data.accessRegDate)
      values.put("refreshRegDate", data.refreshRegDate)
      db!!.insert(TABLE_TOKEN, null, values)
   }

   fun insertFood(data: Food) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("userId", MyApp.prefs.getId())
      values.put("basic", data.basic)
      values.put("uid", data.uid)
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
      values.put("isUpdated", data.isUpdated)
      db!!.insert(TABLE_FOOD, null, values)
   }

   fun insertDailyFood(data: Food) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("userId", MyApp.prefs.getId())
      values.put("uid", data.uid)
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
      values.put("isUpdated", data.isUpdated)
      db!!.insert(TABLE_DAILY_FOOD, null, values)
   }

   fun insertWater(data: Water) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("userId", MyApp.prefs.getId())
      values.put("uid", data.uid)
      values.put("count", data.count)
      values.put("volume", data.volume)
      values.put("regDate", data.regDate)
      db!!.insert(TABLE_WATER, null, values)
   }

   fun insertExercise(data: Exercise) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("userId", MyApp.prefs.getId())
      values.put("basic", data.basic)
      values.put("uid", data.uid)
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
      values.put("uid", data.uid)
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
      values.put("uid", data.uid)
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
      values.put("uid", data.uid)
      values.put("startTime", data.startTime)
      values.put("endTime", data.endTime)
      values.put("total", data.total)
      values.put("regDate", data.regDate)
      db!!.insert(TABLE_SLEEP, null, values)
   }

   fun insertDrug(data: Drug) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("userId", MyApp.prefs.getId())
      values.put("uid", data.uid)
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
      values.put("drugId", data.drugId)
      values.put("uid", data.uid)
      values.put("time", data.time)
      db!!.insert(TABLE_DRUG_TIME, null, values)
   }

   fun insertDrugCheck(data: DrugCheck) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("userId", MyApp.prefs.getId())
      values.put("drugId", data.drugId)
      values.put("drugTimeId", data.drugTimeId)
      values.put("uid", data.uid)
      values.put("regDate", data.regDate)
      db!!.insert(TABLE_DRUG_CHECK, null, values)
   }

   fun insertGoal(data: Goal) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("userId", MyApp.prefs.getId())
      values.put("uid", data.uid)
      values.put("food", data.food)
      values.put("waterVolume", data.waterVolume)
      values.put("water", data.water)
      values.put("exercise", data.exercise)
      values.put("body", data.body)
      values.put("sleep", data.sleep)
      values.put("drug", data.drug)
      values.put("regDate", data.regDate)
      db!!.insert(TABLE_GOAL, null, values)
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

   fun insertImage(data: Image) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("userId", MyApp.prefs.getId())
      values.put("type", data.type)
      values.put("dataId", data.dataId)
      values.put("imageUri", data.imageUri)
      values.put("regDate", data.regDate)
      db!!.insert(TABLE_IMAGE, null, values)
   }

   fun insertUnused(data: Unused) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("userId", MyApp.prefs.getId())
      values.put("type", data.type)
      values.put("value", data.value)
      db!!.insert(TABLE_UNUSED, null, values)
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

   fun updateStr(table: String, column1: String, data: String, column2: String, id: Int){
      val db = dbHelper!!.writableDatabase
      val sql = "update $table set $column1='$data' where userId = ${MyApp.prefs.getId()} and $column2=$id"
      db.execSQL(sql)
      db.close()
   }

   fun updateInt(table: String, column1: String, data: Int, column2: String, id: Int){
      val db = dbHelper!!.writableDatabase
      val sql = "update $table set $column1=$data where userId = ${MyApp.prefs.getId()} and $column2=$id"
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
      val sql = "update $TABLE_USER set idToken='${data.idToken}', userUid='${data.userUid}', deviceUid='${data.deviceUid}', regDate='${data.regDate}' " +
         "where type='${data.type}' and email='${data.email}'"
      db.execSQL(sql)
      db.close()
   }

   fun updateToken(data: Token){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_TOKEN set access='${data.access}', refresh='${data.refresh}', accessRegDate='${data.accessRegDate}', refreshRegDate='${data.refreshRegDate}' " +
         "where userId=${MyApp.prefs.getId()}"
      db.execSQL(sql)
      db.close()
   }

   fun updateAccess(data: Token){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_TOKEN set access='${data.access}', accessRegDate='${data.accessRegDate}' where userId=${MyApp.prefs.getId()}"
      db.execSQL(sql)
      db.close()
   }

   fun updateFood(data: Food){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_FOOD set name='${data.name}', unit='${data.unit}', amount=${data.amount}, kcal=${data.kcal}, carbohydrate=${data.carbohydrate}, " +
         "protein=${data.protein}, fat=${data.fat}, salt=${data.salt}, sugar=${data.sugar}, isUpdated=1 where id=${data.id}"
      db.execSQL(sql)
      db.close()
   }

   fun updateDailyFood(data: Food){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_DAILY_FOOD set amount=${data.amount}, kcal=${data.kcal}, carbohydrate=${data.carbohydrate}, protein=${data.protein}, " +
         "fat=${data.fat}, salt=${data.salt}, sugar=${data.sugar}, count=${data.count}, isUpdated=1 where id=${data.id}"
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
         "bmi=${data.bmi}, bmr=${data.bmr}, isUpdated=1 where userId = ${MyApp.prefs.getId()} and id=${data.id}"
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

   fun updateSleep(data: Sleep){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_SLEEP set startTime='${data.startTime}', endTime='${data.endTime}', total=${data.total}, isUpdated=1 " +
         "where userId=${MyApp.prefs.getId()} and regDate='${data.regDate}'"
      db.execSQL(sql)
      db.close()
   }

   fun updateNote(data: Note){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_NOTE set title='${data.title}', content='${data.content}', status=${data.status} where userId = ${MyApp.prefs.getId()} and regDate='${data.regDate}'"
      db.execSQL(sql)
      db.close()
   }

   fun deleteItem(table: String, column: String, data: Int): Int {
      val db = dbHelper!!.writableDatabase
      val result = db.delete(table, "userId=${MyApp.prefs.getId()} and $column=$data", null)
      db.close()
      return result
   }

   fun deleteItem(table: String, column: String, data: String): Int {
      val db = dbHelper!!.writableDatabase
      val result = db.delete(table, "userId=${MyApp.prefs.getId()} and $column='$data'", null)
      db.close()
      return result
   }

   fun deleteItem(table: String, column1: String, int: Int, column2: String, str: String): Int {
      val db = dbHelper!!.writableDatabase
      val result = db.delete(table, "userId=${MyApp.prefs.getId()} and $column1=$int and $column2='$str'", null)
      db.close()
      return result
   }

   fun deleteTable(table: String, column: String): Int {
      val db = dbHelper!!.writableDatabase
      val result = db.delete(table, "$column=${MyApp.prefs.getId()}", null)
      db.close()
      return result
   }
}
