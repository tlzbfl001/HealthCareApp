package kr.bodywell.android.database

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.util.Log
import kr.bodywell.android.database.DBHelper.Companion.CREATED_AT
import kr.bodywell.android.database.DBHelper.Companion.IS_UPDATED
import kr.bodywell.android.database.DBHelper.Companion.BODY
import kr.bodywell.android.database.DBHelper.Companion.DAILY_EXERCISE
import kr.bodywell.android.database.DBHelper.Companion.DAILY_FOOD
import kr.bodywell.android.database.DBHelper.Companion.GOAL
import kr.bodywell.android.database.DBHelper.Companion.DRUG
import kr.bodywell.android.database.DBHelper.Companion.DRUG_CHECK
import kr.bodywell.android.database.DBHelper.Companion.DRUG_TIME
import kr.bodywell.android.database.DBHelper.Companion.EXERCISE
import kr.bodywell.android.database.DBHelper.Companion.FOOD
import kr.bodywell.android.database.DBHelper.Companion.IMAGE
import kr.bodywell.android.database.DBHelper.Companion.NOTE
import kr.bodywell.android.database.DBHelper.Companion.SLEEP
import kr.bodywell.android.database.DBHelper.Companion.SYNC_TIME
import kr.bodywell.android.database.DBHelper.Companion.TOKEN
import kr.bodywell.android.database.DBHelper.Companion.UNUSED
import kr.bodywell.android.database.DBHelper.Companion.USER
import kr.bodywell.android.database.DBHelper.Companion.WATER
import kr.bodywell.android.database.DBHelper.Companion.USER_ID
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
import kr.bodywell.android.util.CustomUtil
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
      val sql = "select count(id) from $USER"
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
      val sql = "select id, type, email, idToken, $CREATED_AT from $USER where type = '$type' and email = '$email'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id=cursor.getInt(0)
         values.type=cursor.getString(1)
         values.email = cursor.getString(2)
         values.idToken = cursor.getString(3)
         values.createdAt = cursor.getString(4)
      }
      cursor.close()
      db.close()
      return values
   }

   fun getUser() : User {
      val db = dbHelper!!.readableDatabase
      val values = User()
      val sql = "select * from $USER where id = ${MyApp.prefs.getUserId()}"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id=cursor.getInt(0)
         values.type=cursor.getString(1)
         values.email = cursor.getString(2)
         values.idToken = cursor.getString(3)
         values.accessToken = cursor.getString(4)
         values.uid = cursor.getString(5)
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
         values.createdAt = cursor.getString(16)
      }
      cursor.close()
      return values
   }

   fun getUserUpdated() : User {
      val db = dbHelper!!.readableDatabase
      val values = User()
      val sql = "select id, name, gender, birthday, image, height, weight, $CREATED_AT from $USER where id = ${MyApp.prefs.getUserId()} and $IS_UPDATED = 1"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id=cursor.getInt(0)
         values.name = cursor.getString(1)
         values.gender = cursor.getString(2)
         values.birthday = cursor.getString(3)
         values.image = cursor.getString(4)
         values.height = cursor.getDouble(5)
         values.weight = cursor.getDouble(6)
         values.createdAt = cursor.getString(7)
      }
      cursor.close()
      return values
   }

   fun getToken() : Token {
      val db = dbHelper!!.readableDatabase
      val values = Token()
      val sql = "select * from $TOKEN where $USER_ID = ${MyApp.prefs.getUserId()}"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id=cursor.getInt(0)
         values.userId=cursor.getInt(1)
         values.access=cursor.getString(2)
         values.refresh = cursor.getString(3)
         values.accessCreated = cursor.getString(4)
         values.refreshCreated = cursor.getString(5)
      }
      cursor.close()
      return values
   }

   fun getData(table: String, column: String, data: String) : Food {
      val db = dbHelper!!.readableDatabase
      val values = Food()
      val sql = "select id, uid from $table where $USER_ID = ${MyApp.prefs.getUserId()} and $column = '$data'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.uid = cursor.getString(1)
      }
      cursor.close()
      return values
   }

   fun getData(table: String, data: Int) : Food {
      val db = dbHelper!!.readableDatabase
      val values = Food()
      val sql = "select id, uid from $table where $USER_ID = ${MyApp.prefs.getUserId()} and id = $data"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.uid = cursor.getString(1)
      }
      cursor.close()
      return values
   }

   fun getFood(column: String, data: Int) : Food {
      val db = dbHelper!!.readableDatabase
      val values = Food()
      val sql = "select * from $FOOD where $USER_ID = ${MyApp.prefs.getUserId()} and $column = $data"
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
      val sql = "select * from $FOOD where $USER_ID = ${MyApp.prefs.getUserId()} and $column = '$data'"
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

   fun getFoodUid() : ArrayList<Food> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Food>()
      val sql = "select * from $FOOD where $USER_ID = ${MyApp.prefs.getUserId()} and uid is '' limit 3"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Food()
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
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getFoodUpdated() : ArrayList<Food> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Food>()
      val sql = "select * from $FOOD where $USER_ID = ${MyApp.prefs.getUserId()} and $IS_UPDATED = 1 and uid <> '' limit 3"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Food()
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
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getSearchFood(column: String) : ArrayList<Food> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Food>()
      val sql = "select * from $FOOD where $USER_ID = ${MyApp.prefs.getUserId()} group by name order by $column desc"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Food()
         values.id=cursor.getInt(0)
         values.admin=cursor.getInt(2)
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
      val sql = "select * from $DAILY_FOOD where $USER_ID = ${MyApp.prefs.getUserId()} and $column = $id"
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
         values.createdAt = cursor.getString(14)
      }
      cursor.close()
      return values
   }

   fun getDailyFood(type: String, date: String) : ArrayList<Food> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<Food> = ArrayList()
      val sql = "select * from $DAILY_FOOD where $USER_ID = ${MyApp.prefs.getUserId()} and type = '$type' and $CREATED_AT = '$date'"
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
         values.createdAt = cursor.getString(14)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getDailyFood(type: String, name: String, date: String) : Food {
      val db = dbHelper!!.readableDatabase
      val values = Food()
      val sql = "select * from $DAILY_FOOD where $USER_ID = ${MyApp.prefs.getUserId()} and type = '$type' and name = '$name' and $CREATED_AT = '$date'"
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
         values.createdAt = cursor.getString(14)
      }
      cursor.close()
      return values
   }

   fun getDailyFoodUid() : ArrayList<Food> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Food>()
      val sql = "select * from $DAILY_FOOD where $USER_ID = ${MyApp.prefs.getUserId()} and uid is '' limit 3"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Food()
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
         values.createdAt = cursor.getString(14)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getDailyFoodUpdated() : ArrayList<Food> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Food>()
      val sql = "select * from $DAILY_FOOD where $USER_ID = ${MyApp.prefs.getUserId()} and $IS_UPDATED = 1 and uid <> '' limit 3"
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
         values.createdAt = cursor.getString(14)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getWater(start: String, end: String) : ArrayList<Water> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Water>()
      val sql = "select * from $WATER where $USER_ID = ${MyApp.prefs.getUserId()} and $CREATED_AT BETWEEN '$start' and '$end' order by $CREATED_AT"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Water()
         values.id = cursor.getInt(0)
         values.count=cursor.getInt(3)
         values.volume=cursor.getInt(4)
         values.createdAt = cursor.getString(5)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getWater(date: String) : Water {
      val db = dbHelper!!.readableDatabase
      val values = Water()
      val sql = "select * from $WATER where $USER_ID = ${MyApp.prefs.getUserId()} and $CREATED_AT = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.uid=cursor.getString(2)
         values.count=cursor.getInt(3)
         values.volume=cursor.getInt(4)
         values.createdAt = cursor.getString(5)
         values.isUpdated = cursor.getInt(6)
      }
      cursor.close()
      return values
   }

   fun getWaterUid() : ArrayList<Water> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Water>()
      val sql = "select * from $WATER where $USER_ID = ${MyApp.prefs.getUserId()} and uid is '' limit 3"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Water()
         values.id = cursor.getInt(0)
         values.uid=cursor.getString(2)
         values.count=cursor.getInt(3)
         values.volume=cursor.getInt(4)
         values.createdAt = cursor.getString(5)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getWaterUpdated() : ArrayList<Water> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Water>()
      val sql = "select * from $WATER where $USER_ID = ${MyApp.prefs.getUserId()} and $IS_UPDATED = 1 and uid <> '' limit 3"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Water()
         values.id = cursor.getInt(0)
         values.uid=cursor.getString(2)
         values.count=cursor.getInt(3)
         values.volume=cursor.getInt(4)
         values.createdAt = cursor.getString(5)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getExercise(id: Int): Exercise {
      val db = dbHelper!!.readableDatabase
      val values = Exercise()
      val sql = "select * from $EXERCISE where $USER_ID = ${MyApp.prefs.getUserId()} and id = $id"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.uid=cursor.getString(3)
         values.name = cursor.getString(4)
         values.useCount = cursor.getInt(5)
         values.useDate = cursor.getString(6)
      }
      cursor.close()
      return values
   }

   fun getExercise(column: String, data: String) : Exercise {
      val db = dbHelper!!.readableDatabase
      val values = Exercise()
      val sql = "select * from $EXERCISE where $USER_ID = ${MyApp.prefs.getUserId()} and $column = '$data'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.uid=cursor.getString(3)
         values.name = cursor.getString(4)
         values.useCount = cursor.getInt(5)
         values.useDate = cursor.getString(6)
      }
      cursor.close()
      return values
   }

   fun getExerciseUid(): ArrayList<Exercise> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Exercise>()
      val sql = "select * from $EXERCISE where $USER_ID = ${MyApp.prefs.getUserId()} and uid is '' limit 3"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Exercise()
         values.id = cursor.getInt(0)
         values.uid=cursor.getString(3)
         values.name = cursor.getString(4)
         values.useCount = cursor.getInt(5)
         values.useDate = cursor.getString(6)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getExerciseUpdated(): ArrayList<Exercise> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Exercise>()
      val sql = "select * from $EXERCISE where $USER_ID = ${MyApp.prefs.getUserId()} and $IS_UPDATED = 1 and uid <> '' limit 3"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Exercise()
         values.id = cursor.getInt(0)
         values.uid=cursor.getString(3)
         values.name = cursor.getString(4)
         values.useCount = cursor.getInt(5)
         values.useDate = cursor.getString(6)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getDailyExercise(column: String, id: Int): Exercise {
      val db = dbHelper!!.readableDatabase
      val values = Exercise()
      val sql = "select * from $DAILY_EXERCISE where $USER_ID = ${MyApp.prefs.getUserId()} and $column=$id"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.name = cursor.getString(3)
         values.intensity =cursor.getString(4)
         values.workoutTime = cursor.getInt(5)
         values.kcal = cursor.getInt(6)
         values.createdAt = cursor.getString(7)
      }
      cursor.close()
      return values
   }

   fun getDailyExercise(column: String, data: String): ArrayList<Exercise> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Exercise>()
      val sql = "select * from $DAILY_EXERCISE where $USER_ID = ${MyApp.prefs.getUserId()} and $column='$data'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Exercise()
         values.id = cursor.getInt(0)
         values.uid=cursor.getString(2)
         values.name = cursor.getString(3)
         values.intensity =cursor.getString(4)
         values.workoutTime = cursor.getInt(5)
         values.kcal = cursor.getInt(6)
         values.createdAt = cursor.getString(7)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getDailyExerciseUid(): ArrayList<Exercise> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Exercise>()
      val sql = "select * from $DAILY_EXERCISE where $USER_ID = ${MyApp.prefs.getUserId()} and uid is '' limit 3"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Exercise()
         values.id = cursor.getInt(0)
         values.uid=cursor.getString(2)
         values.name = cursor.getString(3)
         values.intensity =cursor.getString(4)
         values.workoutTime = cursor.getInt(5)
         values.kcal = cursor.getInt(6)
         values.createdAt = cursor.getString(7)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getDailyExerciseUpdated(): ArrayList<Exercise> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Exercise>()
      val sql = "select * from $DAILY_EXERCISE where $USER_ID = ${MyApp.prefs.getUserId()} and $IS_UPDATED = 1 and uid <> '' limit 3"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Exercise()
         values.id = cursor.getInt(0)
         values.uid=cursor.getString(2)
         values.name = cursor.getString(3)
         values.intensity =cursor.getString(4)
         values.workoutTime = cursor.getInt(5)
         values.kcal = cursor.getInt(6)
         values.createdAt = cursor.getString(7)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getSearchExercise(column: String) : ArrayList<Exercise> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Exercise>()
      val sql = "select id, admin, uid, name from $EXERCISE where $USER_ID = ${MyApp.prefs.getUserId()} group by name order by $column desc"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Exercise()
         values.id = cursor.getInt(0)
         values.admin = cursor.getInt(1)
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
      val sql = "select * from $BODY where $USER_ID = ${MyApp.prefs.getUserId()} and $CREATED_AT = '$date'"
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
         values.createdAt = cursor.getString(10)
      }
      cursor.close()
      return values
   }

   fun getBody(start: String, end: String) : ArrayList<Body> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Body>()
      val sql = "select weight, fat, bmi, $CREATED_AT from $BODY where $USER_ID = ${MyApp.prefs.getUserId()} and $CREATED_AT BETWEEN '$start' and '$end' order by $CREATED_AT"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Body()
         values.weight = cursor.getDouble(0)
         values.fat = cursor.getDouble(1)
         values.bmi = cursor.getDouble(2)
         values.createdAt = cursor.getString(3)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getBodyUid() : ArrayList<Body> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Body>()
      val sql = "select * from $BODY where $USER_ID = ${MyApp.prefs.getUserId()} and uid is '' limit 3"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Body()
         values.id = cursor.getInt(0)
         values.uid=cursor.getString(2)
         values.height = if(cursor.getDouble(3) == 0.0) null else cursor.getDouble(3)
         values.weight = if(cursor.getDouble(4) == 0.0) null else cursor.getDouble(4)
         values.intensity = cursor.getInt(5)
         values.fat = if(cursor.getDouble(6) == 0.0) null else cursor.getDouble(6)
         values.muscle = if(cursor.getDouble(7) == 0.0) null else cursor.getDouble(7)
         values.bmi = if(cursor.getDouble(8) == 0.0) null else cursor.getDouble(8)
         values.bmr = if(cursor.getDouble(9) == 0.0) null else cursor.getDouble(9)
         values.createdAt = cursor.getString(10)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getBodyUpdated() : ArrayList<Body> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Body>()
      val sql = "select * from $BODY where $USER_ID = ${MyApp.prefs.getUserId()} and $IS_UPDATED = 1 and uid <> '' limit 3"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Body()
         values.id = cursor.getInt(0)
         values.uid=cursor.getString(2)
         values.height = if(cursor.getDouble(3) == 0.0) null else cursor.getDouble(3)
         values.weight = if(cursor.getDouble(4) == 0.0) null else cursor.getDouble(4)
         values.intensity = cursor.getInt(5)
         values.fat = if(cursor.getDouble(6) == 0.0) null else cursor.getDouble(6)
         values.muscle = if(cursor.getDouble(7) == 0.0) null else cursor.getDouble(7)
         values.bmi = if(cursor.getDouble(8) == 0.0) null else cursor.getDouble(8)
         values.bmr = if(cursor.getDouble(9) == 0.0) null else cursor.getDouble(9)
         values.createdAt = cursor.getString(10)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getSleep(date: String) : Sleep {
      val db = dbHelper!!.readableDatabase
      val values = Sleep()
      val sql = "select * from $SLEEP where $USER_ID = ${MyApp.prefs.getUserId()} and substr(startTime,1,10) = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.uid = cursor.getString(2)
         values.startTime = cursor.getString(3)
         values.endTime = cursor.getString(4)
      }
      cursor.close()
      return values
   }

   fun getSleepUid() : ArrayList<Sleep> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Sleep>()
      val sql = "select id, startTime, endTime from $SLEEP where $USER_ID = ${MyApp.prefs.getUserId()} and uid is '' limit 3"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Sleep()
         values.id = cursor.getInt(0)
         values.startTime = cursor.getString(1)
         values.endTime = cursor.getString(2)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getSleepUpdated() : ArrayList<Sleep> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Sleep>()
      val sql = "select id, uid, startTime, endTime from $SLEEP where $USER_ID = ${MyApp.prefs.getUserId()} and $IS_UPDATED = 1 and uid <> '' limit 3"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Sleep()
         values.id = cursor.getInt(0)
         values.uid = cursor.getString(1)
         values.startTime = cursor.getString(2)
         values.endTime = cursor.getString(3)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getDrug(date: String) : ArrayList<Drug> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Drug>()
      val sql = "select * from $DRUG where $USER_ID = ${MyApp.prefs.getUserId()} and '$date' BETWEEN startDate and endDate"
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
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getDrug(id: Int) : Drug {
      val db = dbHelper!!.readableDatabase
      val values = Drug()
      val sql = "select * from $DRUG where $USER_ID = ${MyApp.prefs.getUserId()} and id = $id"
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
      }
      cursor.close()
      return values
   }

   fun getDrugId() : ArrayList<Int> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Int>()
      val sql = "select id from $DRUG where $USER_ID = ${MyApp.prefs.getUserId()}"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         list.add(cursor.getInt(0))
      }
      cursor.close()
      return list
   }

   fun getDrugUid(table: String, column1: String, column2: String, data: Int) : ArrayList<DrugCheck> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<DrugCheck>()
      val sql = "select $column1, uid from $table where $USER_ID = ${MyApp.prefs.getUserId()} and $column2 = $data"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = DrugCheck()
         values.drugTimeId = cursor.getInt(0)
         values.uid = cursor.getString(1)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getDrugUid() : ArrayList<Drug> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Drug>()
      val sql = "select * from $DRUG where $USER_ID = ${MyApp.prefs.getUserId()} and uid is '' limit 3"
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
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getDrugUpdated() : ArrayList<Drug> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Drug>()
      val sql = "select * from $DRUG where $USER_ID = ${MyApp.prefs.getUserId()} and $IS_UPDATED = 1 and uid <> '' limit 3"
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
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getDrugTime(data: Int) : ArrayList<DrugTime> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<DrugTime>()
      val sql = "select id, uid, time from $DRUG_TIME where $USER_ID = ${MyApp.prefs.getUserId()} and drugId = $data order by time"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = DrugTime()
         values.id = cursor.getInt(0)
         values.uid = cursor.getString(1)
         values.time = cursor.getString(2)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getDrugTimeData(data: Int) : ArrayList<DrugTime> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<DrugTime>()
      val sql = "select id, time from $DRUG_TIME where $USER_ID = ${MyApp.prefs.getUserId()} and uid is '' and drugId = $data"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = DrugTime()
         values.id = cursor.getInt(0)
         values.time = cursor.getString(1)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getDrugCheck(data: Int) : ArrayList<DrugCheck> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<DrugCheck>()
      val sql = "select id, uid from $DRUG_CHECK where $USER_ID = ${MyApp.prefs.getUserId()} and drugTimeId = $data"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = DrugCheck()
         values.id = cursor.getInt(0)
         values.uid = cursor.getString(1)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getDrugCheck(data: String) : ArrayList<DrugCheck> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<DrugCheck>()
      val sql = "select drugId, drugTimeId from $DRUG_CHECK where $USER_ID = ${MyApp.prefs.getUserId()} and $CREATED_AT > '$data'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = DrugCheck()
         values.drugId = cursor.getInt(0)
         values.drugTimeId = cursor.getInt(1)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getDrugCheckCount(data: String) : Int {
      val db = dbHelper!!.readableDatabase
      var count = 0
      val sql = "select count(id) from $DRUG_CHECK where $USER_ID = ${MyApp.prefs.getUserId()} and substr(createdAt,1,10) = '$data'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         count = cursor.getInt(0)
      }
      cursor.close()
      return count
   }

   fun getDrugCheck(drugTimeId: Int, data: String) : DrugCheck {
      val db = dbHelper!!.readableDatabase
      val values = DrugCheck()
      val sql = "select id, uid, $CREATED_AT from $DRUG_CHECK where $USER_ID=${MyApp.prefs.getUserId()} and drugTimeId=$drugTimeId and $CREATED_AT='$data'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.uid = cursor.getString(1)
         values.createdAt = cursor.getString(2)
      }
      cursor.close()
      return values
   }

   fun getDrugCheckUid() : ArrayList<DrugCheck> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<DrugCheck>()
      val sql = "select * from $DRUG_CHECK where $USER_ID = ${MyApp.prefs.getUserId()} and uid is '' limit 3"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = DrugCheck()
         values.id = cursor.getInt(0)
         values.uid = cursor.getString(2)
         values.drugId = cursor.getInt(3)
         values.drugTimeId = cursor.getInt(4)
         values.time = cursor.getString(5)
         values.createdAt = cursor.getString(6)
         values.checkedAt = cursor.getString(7)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getDrugCheck(drugTimeId: Int, time: String, date: String) : Drug {
      val db = dbHelper!!.readableDatabase
      val values = Drug()
      val sql = "select id from $DRUG_CHECK where $USER_ID = ${MyApp.prefs.getUserId()} and drugTimeId=$drugTimeId and time = '$time' and substr($CREATED_AT,1,10)='$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
      }
      cursor.close()
      return values
   }

   fun getExerciseRanking(date: String) : ArrayList<Item> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Item>()
      val sql = "select count(name) as ranking, name from $DAILY_EXERCISE where $USER_ID = ${MyApp.prefs.getUserId()} and $CREATED_AT = '$date' " +
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
      val sql = "select count(name) as ranking, name from $DAILY_EXERCISE where $USER_ID = ${MyApp.prefs.getUserId()} and $CREATED_AT BETWEEN '$start' and '$end' " +
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
      val sql = "select count(drugId) as ranking, drugId from $DRUG_CHECK where $USER_ID = ${MyApp.prefs.getUserId()} and $CREATED_AT = '$date' " +
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
      val sql = "select count(drugId) as ranking, drugId from $DRUG_CHECK where $USER_ID = ${MyApp.prefs.getUserId()} and $CREATED_AT BETWEEN '$start' and '$end' " +
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
      val sql = "select distinct substr(createdAt,1,10) from $table where $USER_ID = ${MyApp.prefs.getUserId()} and $CREATED_AT BETWEEN '$start' and '$end' order by $CREATED_AT"
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
      val sql = "select * from $NOTE where $USER_ID = ${MyApp.prefs.getUserId()} and $CREATED_AT = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.title = cursor.getString(2)
         values.content = cursor.getString(3)
         values.status = cursor.getInt(4)
         values.createdAt = cursor.getString(5)
      }
      cursor.close()
      return values
   }

   fun getGoal(id: Int) : Goal {
      val db = dbHelper!!.readableDatabase
      val values = Goal()
      val sql = "select * from $GOAL where $USER_ID = ${MyApp.prefs.getUserId()} and id = $id"
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
         values.createdAt = cursor.getString(10)
      }
      cursor.close()
      return values
   }

   fun getGoal(date: String) : Goal {
      val db = dbHelper!!.readableDatabase
      val values = Goal()
      val sql = "select * from $GOAL where $USER_ID = ${MyApp.prefs.getUserId()} and $CREATED_AT = '$date'"
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
         values.createdAt = cursor.getString(10)
      }
      cursor.close()
      return values
   }

   fun getGoalUid() : ArrayList<Goal> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Goal>()
      val sql = "select * from $GOAL where $USER_ID = ${MyApp.prefs.getUserId()} and uid is '' limit 3"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Goal()
         values.id = cursor.getInt(0)
         values.uid = cursor.getString(2)
         values.food = cursor.getInt(3)
         values.waterVolume = cursor.getInt(4)
         values.water = cursor.getInt(5)
         values.exercise = cursor.getInt(6)
         values.body = cursor.getDouble(7)
         values.sleep = cursor.getInt(8)
         values.drug = cursor.getInt(9)
         values.createdAt = cursor.getString(10)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getGoalUpdated() : ArrayList<Goal> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Goal>()
      val sql = "select * from $GOAL where $USER_ID = ${MyApp.prefs.getUserId()} and $IS_UPDATED = 1 and uid <> '' limit 3"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Goal()
         values.id = cursor.getInt(0)
         values.uid = cursor.getString(2)
         values.food = cursor.getInt(3)
         values.waterVolume = cursor.getInt(4)
         values.water = cursor.getInt(5)
         values.exercise = cursor.getInt(6)
         values.body = cursor.getDouble(7)
         values.sleep = cursor.getInt(8)
         values.drug = cursor.getInt(9)
         values.createdAt = cursor.getString(10)
         values.isUpdated = cursor.getInt(11)
         list.add(values)
      }
      cursor.close()
      return list

   }

   fun getImage(type: String, date: String) : ArrayList<Image> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<Image> = ArrayList()
      val sql = "select * from $IMAGE where $USER_ID = ${MyApp.prefs.getUserId()} and type = '$type' and $CREATED_AT = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Image()
         values.id = cursor.getInt(0)
         values.type = cursor.getString(2)
         values.dataId = cursor.getInt(3)
         values.imageUri = cursor.getString(4)
         values.createdAt = cursor.getString(5)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getImage(id: Int) : ArrayList<Image> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<Image> = ArrayList()
      val sql = "select * from $IMAGE where $USER_ID = ${MyApp.prefs.getUserId()} and dataId = $id"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Image()
         values.id = cursor.getInt(0)
         values.type = cursor.getString(2)
         values.dataId = cursor.getInt(3)
         values.imageUri = cursor.getString(4)
         values.createdAt = cursor.getString(5)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getUnused() : ArrayList<Unused> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<Unused> = ArrayList()
      val sql = "select * from $UNUSED where $USER_ID = ${MyApp.prefs.getUserId()}"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Unused()
         values.id = cursor.getInt(0)
         values.type = cursor.getString(2)
         values.value = cursor.getString(3)
         values.drugUid = cursor.getString(4)
         values.drugTimeUid = cursor.getString(5)
         values.createdAt = cursor.getString(6)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getUnusedTime() : ArrayList<Unused> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<Unused> = ArrayList()
      val sql = "select * from $UNUSED where $USER_ID = ${MyApp.prefs.getUserId()} and type = 'drugTime'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Unused()
         values.id = cursor.getInt(0)
         values.type = cursor.getString(2)
         values.value = cursor.getString(3)
         values.drugUid = cursor.getString(4)
         values.drugTimeUid = cursor.getString(5)
         values.createdAt = cursor.getString(6)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getSynced() : String {
      val db = dbHelper!!.readableDatabase
      var value = ""
      val sql = "select syncedAt from $SYNC_TIME where $USER_ID = ${MyApp.prefs.getUserId()}"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         value = cursor.getString(0)
      }
      cursor.close()
      return value
   }

   fun insertUser(data: User) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("type", data.type)
      values.put("email", data.email)
      values.put("idToken", data.idToken)
      values.put("accessToken", data.accessToken)
      values.put("uid", data.uid)
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
      values.put(CREATED_AT, data.createdAt)
      values.put(IS_UPDATED, data.isUpdated)
      db!!.insert(USER, null, values)
   }

   fun insertToken(data: Token) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put(USER_ID, MyApp.prefs.getUserId())
      values.put("access", data.access)
      values.put("refresh", data.refresh)
      values.put("accessCreated", data.accessCreated)
      values.put("refreshCreated", data.refreshCreated)
      db!!.insert(TOKEN, null, values)
   }

   fun insertFood(data: Food) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put(USER_ID, MyApp.prefs.getUserId())
      values.put("admin", data.admin)
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
      db!!.insert(FOOD, null, values)
   }

   fun insertDailyFood(data: Food) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put(USER_ID, MyApp.prefs.getUserId())
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
      values.put(CREATED_AT, data.createdAt)
      db!!.insert(DAILY_FOOD, null, values)
   }

   fun insertWater(data: Water) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put(USER_ID, MyApp.prefs.getUserId())
      values.put("uid", data.uid)
      values.put("count", data.count)
      values.put("volume", data.volume)
      values.put(CREATED_AT, data.createdAt)
      db!!.insert(WATER, null, values)
   }

   fun insertExercise(data: Exercise) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put(USER_ID, MyApp.prefs.getUserId())
      values.put("admin", data.admin)
      values.put("uid", data.uid)
      values.put("name", data.name)
      values.put("useCount", data.useCount)
      values.put("useDate", data.useDate)
      db.insert(EXERCISE, null, values)
   }

   fun insertDailyExercise(data: Exercise) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put(USER_ID, MyApp.prefs.getUserId())
      values.put("uid", data.uid)
      values.put("name", data.name)
      values.put("intensity", data.intensity)
      values.put("workoutTime", data.workoutTime)
      values.put("kcal", data.kcal)
      values.put(CREATED_AT, data.createdAt)
      db.insert(DAILY_EXERCISE, null, values)
   }

   fun insertBody(data: Body) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put(USER_ID, MyApp.prefs.getUserId())
      values.put("uid", data.uid)
      values.put("height", data.height)
      values.put("weight", data.weight)
      values.put("intensity", data.intensity)
      values.put("fat", data.fat)
      values.put("muscle", data.muscle)
      values.put("bmi", data.bmi)
      values.put("bmr", data.bmr)
      values.put(CREATED_AT, data.createdAt)
      db!!.insert(BODY, null, values)
   }

   fun insertSleep(data: Sleep) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put(USER_ID, MyApp.prefs.getUserId())
      values.put("uid", data.uid)
      values.put("startTime", data.startTime)
      values.put("endTime", data.endTime)
      db!!.insert(SLEEP, null, values)
   }

   fun insertDrug(data: Drug) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put(USER_ID, MyApp.prefs.getUserId())
      values.put("uid", data.uid)
      values.put("type", data.type)
      values.put("name", data.name)
      values.put("amount", data.amount)
      values.put("unit", data.unit)
      values.put("count", data.count)
      values.put("startDate", data.startDate)
      values.put("endDate", data.endDate)
      values.put("isSet", 1)
      db!!.insert(DRUG, null, values)
   }

   fun insertDrugTime(data: DrugTime) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put(USER_ID, MyApp.prefs.getUserId())
      values.put("uid", data.uid)
      values.put("drugId", data.drugId)
      values.put("time", data.time)
      db!!.insert(DRUG_TIME, null, values)
   }

   fun insertDrugCheck(data: DrugCheck) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put(USER_ID, MyApp.prefs.getUserId())
      values.put("uid", data.uid)
      values.put("drugId", data.drugId)
      values.put("drugTimeId", data.drugTimeId)
      values.put("time", data.time)
      values.put(CREATED_AT, data.createdAt)
      values.put("checkedAt", data.checkedAt)
      db!!.insert(DRUG_CHECK, null, values)
   }

   fun insertGoal(data: Goal) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put(USER_ID, MyApp.prefs.getUserId())
      values.put("uid", data.uid)
      values.put("food", data.food)
      values.put("waterVolume", data.waterVolume)
      values.put("water", data.water)
      values.put("exercise", data.exercise)
      values.put("body", data.body)
      values.put("sleep", data.sleep)
      values.put("drug", data.drug)
      values.put(CREATED_AT, data.createdAt)
      db!!.insert(GOAL, null, values)
   }

   fun insertNote(data: Note) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put(USER_ID, MyApp.prefs.getUserId())
      values.put("title", data.title)
      values.put("content", data.content)
      values.put("status", data.status)
      values.put(CREATED_AT, data.createdAt)
      db!!.insert(NOTE, null, values)
   }

   fun insertImage(data: Image) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put(USER_ID, MyApp.prefs.getUserId())
      values.put("type", data.type)
      values.put("dataId", data.dataId)
      values.put("imageUri", data.imageUri)
      values.put(CREATED_AT, data.createdAt)
      db!!.insert(IMAGE, null, values)
   }

   fun insertUnused(data: Unused) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put(USER_ID, MyApp.prefs.getUserId())
      values.put("type", data.type)
      values.put("value", data.value)
      values.put("drugUid", data.drugUid)
      values.put("drugTimeUid", data.drugTimeUid)
      values.put(CREATED_AT, data.createdAt)
      db!!.insert(UNUSED, null, values)
   }

   fun insertSync(data: String) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put(USER_ID, MyApp.prefs.getUserId())
      values.put("syncedAt", data)
      db!!.insert(SYNC_TIME, null, values)
   }

   fun updateUserStr(column: String, data: String){
      val db = dbHelper!!.writableDatabase
      val sql = "update $USER set $column='$data' where id = ${MyApp.prefs.getUserId()}"
      db.execSQL(sql)
      db.close()
   }

   fun updateUserInt(column: String, data: Int){
      val db = dbHelper!!.writableDatabase
      val sql = "update $USER set $column=$data where id = ${MyApp.prefs.getUserId()}"
      db.execSQL(sql)
      db.close()
   }

   fun updateUserDouble(column: String, data: Double){
      val db = dbHelper!!.writableDatabase
      val sql = "update $USER set $column=$data where id = ${MyApp.prefs.getUserId()}"
      db.execSQL(sql)
      db.close()
   }

   fun updateStr(table: String, column1: String, data: String, column2: String, id: Int){
      val db = dbHelper!!.writableDatabase
      val sql = "update $table set $column1='$data' where $USER_ID = ${MyApp.prefs.getUserId()} and $column2=$id"
      db.execSQL(sql)
      db.close()
   }

   fun updateInt(table: String, column1: String, data: Int, column2: String, id: Int){
      val db = dbHelper!!.writableDatabase
      val sql = "update $table set $column1=$data where $USER_ID = ${MyApp.prefs.getUserId()} and $column2=$id"
      db.execSQL(sql)
      db.close()
   }

   fun updateInt(table: String, column: String, data: Int, regDate: String){
      val db = dbHelper!!.writableDatabase
      val sql = "update $table set $column=$data where $USER_ID=${MyApp.prefs.getUserId()} and $CREATED_AT='$regDate'"
      db.execSQL(sql)
      db.close()
   }

   fun updateDouble(table: String, column: String, data: Double, regDate: String){
      val db = dbHelper!!.writableDatabase
      val sql = "update $table set $column=$data where $USER_ID=${MyApp.prefs.getUserId()} and $CREATED_AT='$regDate'"
      db.execSQL(sql)
      db.close()
   }

   fun updateSync(data: String){
      val db = dbHelper!!.writableDatabase
      val sql = "update $SYNC_TIME set syncedAt=$data where $USER_ID = ${MyApp.prefs.getUserId()}"
      db.execSQL(sql)
      db.close()
   }

   fun updateUser(data: User){
      val db = dbHelper!!.writableDatabase
      val sql = "update $USER set idToken='${data.idToken}', accessToken='${data.accessToken}', $CREATED_AT='${data.createdAt}' where type='${data.type}' and email='${data.email}'"
      db.execSQL(sql)
      db.close()
   }

   fun updateUser2(data: User){
      val db = dbHelper!!.writableDatabase
      val sql = "update $USER set idToken='${data.idToken}', accessToken='${data.accessToken}', name='${data.name}', gender='${data.gender}', birthday='${data.birthday}', " +
         "image='${data.image}', height='${data.height}', weight='${data.weight}', $CREATED_AT='${data.createdAt}' where type='${data.type}' and email='${data.email}'"
      db.execSQL(sql)
      db.close()
   }

   fun updateProfile(data: User){
      val db = dbHelper!!.writableDatabase
      val sql = "update $USER set name='${data.name}', gender='${data.gender}', birthday='${data.birthday}', height=${data.height}, weight=${data.weight}, " +
         "$IS_UPDATED=${data.isUpdated} where id=${MyApp.prefs.getUserId()}"
      db.execSQL(sql)
      db.close()
   }

   fun updateToken(data: Token){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TOKEN set access='${data.access}', refresh='${data.refresh}', accessCreated='${data.accessCreated}', refreshCreated='${data.refreshCreated}' " +
         "where $USER_ID=${MyApp.prefs.getUserId()}"
      db.execSQL(sql)
      db.close()
   }

   fun updateAccess(data: Token){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TOKEN set access='${data.access}', accessCreated='${data.accessCreated}' where $USER_ID=${MyApp.prefs.getUserId()}"
      db.execSQL(sql)
      db.close()
   }

   fun updateFood(data: Food){
      val db = dbHelper!!.writableDatabase
      val sql = "update $FOOD set unit='${data.unit}', amount=${data.amount}, kcal=${data.kcal}, carbohydrate=${data.carbohydrate}, " +
         "protein=${data.protein}, fat=${data.fat}, salt=${data.salt}, sugar=${data.sugar}, $IS_UPDATED=1 where id=${data.id}"
      db.execSQL(sql)
      db.close()
   }

   fun updateDailyFood(data: Food){
      val db = dbHelper!!.writableDatabase
      val sql = "update $DAILY_FOOD set amount=${data.amount}, kcal=${data.kcal}, carbohydrate=${data.carbohydrate}, protein=${data.protein}, " +
         "fat=${data.fat}, salt=${data.salt}, sugar=${data.sugar}, count=${data.count}, $IS_UPDATED=${data.isUpdated} where id=${data.id}"
      db.execSQL(sql)
      db.close()
   }

   fun updateExercise(table: String, data: Exercise){
      val db = dbHelper!!.writableDatabase
      val sql = "update $table set name='${data.name}', intensity='${data.intensity}', workoutTime=${data.workoutTime}, kcal=${data.kcal}, isUpdated=${data.isUpdated}, " +
         "$CREATED_AT='${data.createdAt}' where id=${data.id}"
      db.execSQL(sql)
      db.close()
   }

   fun updateBody(data: Body){
      val db = dbHelper!!.writableDatabase
      val sql = "update $BODY set height=${data.height}, weight=${data.weight}, intensity=${data.intensity}, fat=${data.fat}, muscle=${data.muscle}, " +
         "bmi=${data.bmi}, bmr=${data.bmr}, $IS_UPDATED=${data.isUpdated} where $USER_ID = ${MyApp.prefs.getUserId()} and id=${data.id}"
      db.execSQL(sql)
      db.close()
   }

   fun updateDrug(data: Drug) {
      val db = dbHelper!!.writableDatabase
      val sql = "update $DRUG set type='${data.type}', name='${data.name}', amount=${data.amount}, unit='${data.unit}', count=${data.count}, " +
         "startDate='${data.startDate}', endDate='${data.endDate}', isSet=1, $IS_UPDATED=${data.isUpdated} where $USER_ID = ${MyApp.prefs.getUserId()} and id=${data.id}"
      db.execSQL(sql)
      db.close()
   }

   fun updateSleep(data: Sleep){
      val db = dbHelper!!.writableDatabase
      val sql = "update $SLEEP set startTime='${data.startTime}', endTime='${data.endTime}', $IS_UPDATED=${data.isUpdated} " +
         "where $USER_ID=${MyApp.prefs.getUserId()} and substr(startTime,1,10)='${data.startTime.substring(0, 10)}'"
      db.execSQL(sql)
      db.close()
   }

   fun updateGoal(data: Goal){
      val db = dbHelper!!.writableDatabase
      val sql = "update $GOAL set uid='${data.uid}', food=${data.food}, waterVolume=${data.waterVolume}, water=${data.water}, exercise=${data.exercise}, body=${data.body}, " +
         "sleep=${data.sleep}, drug=${data.drug} where $USER_ID = ${MyApp.prefs.getUserId()} and id=${data.id}"
      db.execSQL(sql)
      db.close()
   }

   fun updateNote(data: Note){
      val db = dbHelper!!.writableDatabase
      val sql = "update $NOTE set title='${data.title}', content='${data.content}', status=${data.status} where userId = ${MyApp.prefs.getUserId()} and $CREATED_AT='${data.createdAt}'"
      db.execSQL(sql)
      db.close()
   }

   fun deleteTable(table: String, column: String): Int {
      val db = dbHelper!!.writableDatabase
      val result = db.delete(table, "$column=${MyApp.prefs.getUserId()}", null)
      db.close()
      return result
   }

   fun deleteItem(table: String, column: String, data: Int): Int {
      val db = dbHelper!!.writableDatabase
      val result = db.delete(table, "$USER_ID=${MyApp.prefs.getUserId()} and $column=$data", null)
      db.close()
      return result
   }

   fun deleteItem(table: String, column: String, data: String): Int {
      val db = dbHelper!!.writableDatabase
      val result = db.delete(table, "$USER_ID=${MyApp.prefs.getUserId()} and $column='$data'", null)
      db.close()
      return result
   }

   fun deleteItem(table: String, column1: String, int: Int, column2: String, str: String): Int {
      val db = dbHelper!!.writableDatabase
      val result = db.delete(table, "$USER_ID=${MyApp.prefs.getUserId()} and $column1=$int and $column2='$str'", null)
      db.close()
      return result
   }
}
