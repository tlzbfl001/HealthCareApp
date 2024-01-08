package com.makebodywell.bodywell.database

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_BODY
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DAILY_DATA
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DRUG
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DRUG_CHECK
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DRUG_DATE
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DRUG_TIME
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_EXERCISE
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_EXERCISE_DELETE
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_EXERCISE_ITEM
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_FOOD
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_IMAGE
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_NOTE
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_USER
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_WATER
import com.makebodywell.bodywell.model.Body
import com.makebodywell.bodywell.model.DailyData
import com.makebodywell.bodywell.model.Drug
import com.makebodywell.bodywell.model.DrugCheck
import com.makebodywell.bodywell.model.DrugDate
import com.makebodywell.bodywell.model.DrugList
import com.makebodywell.bodywell.model.DrugTime
import com.makebodywell.bodywell.model.Exercise
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.model.Image
import com.makebodywell.bodywell.model.Item
import com.makebodywell.bodywell.model.User
import com.makebodywell.bodywell.model.Water
import org.w3c.dom.Text

class DataManager(private var context: Context?) {
   private var dbHelper: DBHelper? = null

   @Throws(SQLException::class)
   fun open(): DataManager {
      dbHelper = DBHelper(context)
      return this
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
         data.accessToken = cursor.getString(3)
         data.email = cursor.getString(4)
         data.name = cursor.getString(5)
         data.nickname = cursor.getString(6)
         data.gender = cursor.getString(7)
         data.birthYear = cursor.getString(8)
         data.birthDay = cursor.getString(9)
         data.profileImage = cursor.getString(10)
         data.regDate = cursor.getString(11)
      }
      cursor.close()
      return data
   }

   fun getFood(type: Int, date: String) : ArrayList<Food> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<Food> = ArrayList()
      val sql = "select * from $TABLE_FOOD where type = $type and regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Food()
         data.id=cursor.getInt(0)
         data.name=cursor.getString(1)
         data.unit=cursor.getString(2)
         data.amount= cursor.getInt(3)
         data.kcal= cursor.getInt(4).toString()
         data.carbohydrate= cursor.getDouble(5).toString()
         data.protein= cursor.getDouble(6).toString()
         data.fat= cursor.getDouble(7).toString()
         data.salt= cursor.getDouble(8).toString()
         data.sugar= cursor.getDouble(9).toString()
         data.type = cursor.getInt(10)
         data.regDate = cursor.getString(11)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getFoodDates() : ArrayList<Food> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Food>()
      val sql = "select distinct regDate from $TABLE_FOOD"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Food()
         data.regDate = cursor.getString(0)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getFoodDates(start: String, end: String) : ArrayList<Food> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Food>()
      val sql = "select distinct regDate from $TABLE_FOOD where regDate BETWEEN '$start' and '$end'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Food()
         data.regDate = cursor.getString(0)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getImage(type: Int, date: String) : ArrayList<Image> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<Image> = ArrayList()
      val sql = "select * from $TABLE_IMAGE where type = $type and regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Image()
         data.id=cursor.getInt(0)
         data.imageUri=cursor.getString(1)
         data.type=cursor.getString(2)
         data.regDate = cursor.getString(3)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getWater() : ArrayList<Water> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<Water> = ArrayList()
      val sql = "select * from $TABLE_WATER"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Water()
         data.id=cursor.getInt(0)
         data.water=cursor.getInt(1)
         data.volume=cursor.getInt(2)
         data.regDate = cursor.getString(3)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getWater(start: String, end: String) : ArrayList<Water> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Water>()
      val sql = "select * from $TABLE_WATER where regDate BETWEEN '$start' and '$end'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Water()
         data.id=cursor.getInt(0)
         data.water=cursor.getInt(1)
         data.volume=cursor.getInt(2)
         data.regDate = cursor.getString(3)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getWater(date: String) : Water {
      val db = dbHelper!!.readableDatabase
      val data = Water()
      val sql = "select * from $TABLE_WATER where regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data.id=cursor.getInt(0)
         data.water=cursor.getInt(1)
         data.volume=cursor.getInt(2)
         data.regDate = cursor.getString(3)
      }
      cursor.close()
      return data
   }

   fun getExerciseDates() : ArrayList<Exercise> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Exercise>()
      val sql = "select distinct regDate from $TABLE_EXERCISE"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Exercise()
         data.regDate = cursor.getString(0)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getExercise() : ArrayList<Exercise> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<Exercise> = ArrayList()
      val sql = "select * from $TABLE_EXERCISE"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Exercise()
         data.id=cursor.getInt(0)
         data.category=cursor.getString(1)
         data.name=cursor.getString(2)
         data.workoutTime= cursor.getInt(3)
         data.distance= cursor.getDouble(4)
         data.calories= cursor.getInt(5)
         data.regDate= cursor.getString(6)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getExercise(start: String, end: String) : ArrayList<Exercise> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Exercise>()
      val sql = "select * from $TABLE_EXERCISE where regDate BETWEEN '$start' and '$end'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Exercise()
         data.id=cursor.getInt(0)
         data.category=cursor.getString(1)
         data.name=cursor.getString(2)
         data.workoutTime= cursor.getInt(3)
         data.distance= cursor.getDouble(4)
         data.calories= cursor.getInt(5)
         data.regDate= cursor.getString(6)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getExercise(date: String) : ArrayList<Exercise> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Exercise>()
      val sql = "select * from $TABLE_EXERCISE where regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Exercise()
         data.id=cursor.getInt(0)
         data.category=cursor.getString(1)
         data.name=cursor.getString(2)
         data.workoutTime= cursor.getInt(3)
         data.distance= cursor.getDouble(4)
         data.calories= cursor.getInt(5)
         data.regDate= cursor.getString(6)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getExerciseItem(type: String?) : ArrayList<String> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<String> = ArrayList()
      val sql = "select name from $TABLE_EXERCISE_ITEM where type='$type'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         list.add(cursor.getString(0))
      }
      cursor.close()
      return list
   }

   fun getExerciseDelete(type: String?) : ArrayList<String> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<String> = ArrayList()
      val sql = "select name from $TABLE_EXERCISE_DELETE where type='$type'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         list.add(cursor.getString(0))
      }
      cursor.close()
      return list
   }

   fun getBody(date: String) : Body {
      val db = dbHelper!!.readableDatabase
      val data = Body()
      val sql = "select * from $TABLE_BODY where regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data.id = cursor.getInt(0)
         data.height = cursor.getDouble(1)
         data.weight = cursor.getDouble(2)
         data.age = cursor.getInt(3)
         data.gender = cursor.getString(4)
         data.exerciseLevel = cursor.getInt(5)
         data.fat = cursor.getDouble(6)
         data.muscle = cursor.getDouble(7)
         data.bmi = cursor.getDouble(8)
         data.bmr = cursor.getDouble(9)
         data.regDate = cursor.getString(10)
      }
      cursor.close()
      return data
   }

   fun getBody() : ArrayList<Body> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Body>()
      val sql = "select weight, fat, bmi, regDate from $TABLE_BODY"
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

   fun getBody(start: String, end: String) : ArrayList<Body> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Body>()
      val sql = "select weight, fat, bmi, regDate from $TABLE_BODY where regDate BETWEEN '$start' and '$end'"
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

   fun getDrug() : ArrayList<Drug> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Drug>()
      val sql = "select * from $TABLE_DRUG"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Drug()
         data.id = cursor.getInt(0)
         data.type = cursor.getString(1)
         data.name = cursor.getString(2)
         data.amount = cursor.getString(3)
         data.unit = cursor.getString(4)
         data.period = cursor.getString(5)
         data.startDate = cursor.getString(6)
         data.endDate = cursor.getString(7)
         data.isSet = cursor.getInt(8)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getDrugId(date: String) : Drug {
      val db = dbHelper!!.readableDatabase
      val data = Drug()
      val sql = "select id from $TABLE_DRUG where regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data.id = cursor.getInt(0)
      }
      cursor.close()
      return data
   }

   fun getDrugDate(id: Int) : ArrayList<DrugDate> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<DrugDate> = ArrayList()
      val sql = "select * from $TABLE_DRUG_DATE where drugId = $id"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = DrugDate()
         data.id = cursor.getInt(0)
         data.date=cursor.getString(1)
         data.drugId=cursor.getInt(2)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getDrugTime(id: Int) : ArrayList<DrugTime> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<DrugTime> = ArrayList()
      val sql = "select * from $TABLE_DRUG_TIME where drugId = $id order by hour, minute"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = DrugTime()
         data.id = cursor.getInt(0)
         data.hour = cursor.getInt(1)
         data.minute = cursor.getInt(2)
         data.drugId = cursor.getInt(3)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getDrugCheck(id: Int, date: String) : DrugCheck {
      val db = dbHelper!!.readableDatabase
      val data = DrugCheck()
      val sql = "select checked, drugTimeId, regDate from $TABLE_DRUG_CHECK where drugTimeId = $id and regDate = '$date'"
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
      val sql = "select count(id) from $TABLE_DRUG_CHECK where checked = 1 and regDate = '$date'"
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
      val sql = "select * from $TABLE_DRUG where date('$date') between date(startDate) and date(endDate)"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val data = Drug()
         data.id = cursor.getInt(0)
         data.type = cursor.getString(1)
         data.name = cursor.getString(2)
         data.amount = cursor.getString(3)
         data.unit = cursor.getString(4)
         data.period = cursor.getString(5)
         data.startDate = cursor.getString(6)
         data.endDate = cursor.getString(7)
         data.isSet = cursor.getInt(8)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getNote(date: String) : Item {
      val db = dbHelper!!.readableDatabase
      val data = Item()
      val sql = "select * from $TABLE_NOTE where regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data.int1 = cursor.getInt(0)
         data.string1 = cursor.getString(1)
         data.string2 = cursor.getString(2)
         data.string3 = cursor.getString(3)
      }
      cursor.close()
      return data
   }

   fun getDailyData(date: String) : DailyData {
      val db = dbHelper!!.readableDatabase
      val data = DailyData()
      val sql = "select * from $TABLE_DAILY_DATA where regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data.foodGoal = cursor.getInt(1)
         data.waterGoal = cursor.getInt(2)
         data.exerciseGoal = cursor.getInt(3)
         data.bodyGoal = cursor.getDouble(4)
         data.sleepGoal = cursor.getInt(5)
         data.drugGoal = cursor.getInt(6)
         data.regDate = cursor.getString(7)
      }
      cursor.close()
      return data
   }

   fun insertUser(data: User) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("type", data.type)
      values.put("idToken", data.idToken)
      values.put("accessToken", data.accessToken)
      values.put("email", data.email)
      values.put("name", data.name)
      values.put("nickname", data.nickname)
      values.put("gender", data.gender)
      values.put("birthYear", data.birthYear)
      values.put("birthDay", data.birthDay)
      values.put("profileImage", data.profileImage)
      values.put("regDate", data.regDate)
      db!!.insert(TABLE_USER, null, values)
   }

   fun insertFood(data: Food?) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("name", data?.name)
      values.put("unit", data?.unit)
      values.put("amount", data?.amount)
      values.put("kcal", data?.kcal)
      values.put("carbohydrate", data?.carbohydrate)
      values.put("protein", data?.protein)
      values.put("fat", data?.fat)
      values.put("salt", data?.salt)
      values.put("sugar", data?.sugar)
      values.put("type", data?.type)
      values.put("regDate", data?.regDate)
      db!!.insert(TABLE_FOOD, null, values)
   }

   fun insertImage(data: Image?) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("imageUri", data?.imageUri)
      values.put("type", data?.type)
      values.put("regDate", data?.regDate)
      db!!.insert(TABLE_IMAGE, null, values)
   }

   fun insertWater(data: Water) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("water", data.water)
      values.put("volume", data.volume)
      values.put("regDate", data.regDate)
      db!!.insert(TABLE_WATER, null, values)
   }

   fun insertExercise(data: Exercise) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("category", data.category)
      values.put("name", data.name)
      values.put("workoutTime", data.workoutTime)
      values.put("distance", data.distance)
      values.put("calories", data.calories)
      values.put("regDate", data.regDate)
      db.insert(TABLE_EXERCISE, null, values)
   }

   fun insertExerciseItem(type: String, name: String) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("type", type)
      values.put("name", name)
      db!!.insert(TABLE_EXERCISE_ITEM, null, values)
      db.close()
   }

   fun insertExerciseDelete(type: String, name: String) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("type", type)
      values.put("name", name)
      db!!.insert(TABLE_EXERCISE_DELETE, null, values)
   }

   fun insertBody(data: Body) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
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
      values.put("type", data.type)
      values.put("name", data.name)
      values.put("amount", data.amount)
      values.put("unit", data.unit)
      values.put("period", data.period)
      values.put("startDate", data.startDate)
      values.put("endDate", data.endDate)
      values.put("regDate", data.regDate)
      db!!.insert(TABLE_DRUG, null, values)
   }

   fun insertDrugTime(data: DrugTime) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("hour", data.hour)
      values.put("minute", data.minute)
      values.put("drugId", data.drugId)
      db!!.insert(TABLE_DRUG_TIME, null, values)
   }

   fun insertDrugDate(data: DrugDate) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("date", data.date)
      values.put("drugId", data.drugId)
      db!!.insert(TABLE_DRUG_DATE, null, values)
   }

   fun insertDrugCheck(checked: Int, drugTimeId: Int, regDate: String) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("checked", checked)
      values.put("drugTimeId", drugTimeId)
      values.put("regDate", regDate)
      db!!.insert(TABLE_DRUG_CHECK, null, values)
   }

   fun insertNote(data: Item) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("title", data.string1)
      values.put("content", data.string2)
      values.put("regDate", data.string3)
      db!!.insert(TABLE_NOTE, null, values)
   }

   fun insertDailyData(data: DailyData) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("foodGoal", data.foodGoal)
      values.put("waterGoal", data.waterGoal)
      values.put("exerciseGoal", data.exerciseGoal)
      values.put("bodyGoal", data.bodyGoal)
      values.put("sleepGoal", data.sleepGoal)
      values.put("drugGoal", data.drugGoal)
      values.put("regDate", data.regDate)
      db!!.insert(TABLE_DAILY_DATA, null, values)
   }

   fun updateFoodAmount(data: Food){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_FOOD set amount=${data.amount} where id='${data.id}'"
      db.execSQL(sql)
      db.close()
   }

   fun updateWater(data: Water){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_WATER set water=${data.water}, volume=${data.volume} where regDate='${data.regDate}'"
      db.execSQL(sql)
      db.close()
   }

   fun updateFoodGoal(data: DailyData){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_DAILY_DATA set foodGoal=${data.foodGoal} where regDate='${data.regDate}'"
      db.execSQL(sql)
      db.close()
   }

   fun updateWaterGoal(data: DailyData) {
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_DAILY_DATA set waterGoal=${data.waterGoal} where regDate='${data.regDate}'"
      db.execSQL(sql)
      db.close()
   }

   fun updateExerciseGoal(data: DailyData) {
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_DAILY_DATA set exerciseGoal=${data.exerciseGoal} where regDate='${data.regDate}'"
      db.execSQL(sql)
      db.close()
   }

   fun updateExercise(data: Exercise) {
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_EXERCISE set workoutTime='${data.workoutTime}', calories=${data.calories} where id=${data.id}"
      db.execSQL(sql)
      db.close()
   }

   fun updateBody(data: Body){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_BODY set height=${data.height}, weight=${data.weight}, age=${data.age}, gender='${data.gender}', " +
              "exerciseLevel=${data.exerciseLevel}, fat=${data.fat}, muscle=${data.muscle}, bmi=${data.bmi}, bmr=${data.bmr} where id=${data.id}"
      db.execSQL(sql)
      db.close()
   }

   fun updateBodyGoal(data: DailyData){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_DAILY_DATA set bodyGoal=${data.bodyGoal} where regDate='${data.regDate}'"
      db.execSQL(sql)
      db.close()
   }

   fun updateDrugSet(isSet: Int){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_DRUG set isSet = $isSet"
      db.execSQL(sql)
      db.close()
   }

   fun updateDrugGoal(data: DailyData){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_DAILY_DATA set drugGoal=${data.drugGoal} where regDate='${data.regDate}'"
      db.execSQL(sql)
      db.close()
   }

   fun updateDrugCheck(data: DrugCheck){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_DRUG_CHECK set checked=${data.checked} where drugTimeId=${data.drugTimeId} and regDate='${data.regDate}'"
      db.execSQL(sql)
      db.close()
   }

   fun updateNote(data: Item){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_NOTE set title='${data.string1}', content='${data.string2}' where regDate='${data.string3}'"
      db.execSQL(sql)
      db.close()
   }

   fun deleteFood(id: Int): Boolean {
      val db = dbHelper!!.writableDatabase
      val success = db!!.delete(TABLE_FOOD,"id=$id",null)
      db.close()
      return (Integer.parseInt("$success") != -1)
   }

   fun deleteImage(id: Int): Boolean {
      val db = dbHelper!!.writableDatabase
      val success = db!!.delete(TABLE_IMAGE,"id=$id",null)
      db.close()
      return (Integer.parseInt("$success") != -1)
   }

   fun deleteExercise(id: Int): Boolean {
      val db = dbHelper!!.writableDatabase
      val success = db!!.delete(TABLE_EXERCISE,"id=$id",null)
      db.close()
      return (Integer.parseInt("$success") != -1)
   }

   fun deleteExerciseItem(type: String, name: String?): Boolean {
      val db = dbHelper!!.writableDatabase
      val success = db!!.delete(TABLE_EXERCISE_ITEM,"type='$type' and name='$name'",null)
      db.close()
      return (Integer.parseInt("$success") != -1)
   }

   fun deleteDrug(id: Int): Boolean {
      val db = dbHelper!!.writableDatabase
      val success = db!!.delete(TABLE_DRUG,"id=$id",null)
      db.close()
      return (Integer.parseInt("$success") != -1)
   }

   fun deleteDrugDate(id: Int): Boolean {
      val db = dbHelper!!.writableDatabase
      val success = db!!.delete(TABLE_DRUG_DATE,"drugId=$id",null)
      db.close()
      return (Integer.parseInt("$success") != -1)
   }

   fun deleteDrugTime(id: Int): Boolean {
      val db = dbHelper!!.writableDatabase
      val success = db!!.delete(TABLE_DRUG_TIME,"drugId=$id",null)
      db.close()
      return (Integer.parseInt("$success") != -1)
   }

   fun deleteDrugCheck(id: Int): Boolean {
      val db = dbHelper!!.writableDatabase
      val success = db!!.delete(TABLE_DRUG_CHECK,"drugTimeId=$id",null)
      db.close()
      return (Integer.parseInt("$success") != -1)
   }

   fun deleteBody(): Boolean {
      val db = dbHelper!!.writableDatabase
      val success = db!!.delete(TABLE_BODY,"",null)
      db.close()
      return (Integer.parseInt("$success") != -1)
   }

   fun deleteFood(): Boolean {
      val db = dbHelper!!.writableDatabase
      val success = db!!.delete(TABLE_FOOD,"",null)
      db.close()
      return (Integer.parseInt("$success") != -1)
   }

   fun deleteWater(): Boolean {
      val db = dbHelper!!.writableDatabase
      val success = db!!.delete(TABLE_WATER,"",null)
      db.close()
      return (Integer.parseInt("$success") != -1)
   }

   fun deleteExercise(): Boolean {
      val db = dbHelper!!.writableDatabase
      val success = db!!.delete(TABLE_EXERCISE,"",null)
      db.close()
      return (Integer.parseInt("$success") != -1)
   }
}