package com.makebodywell.bodywell.database

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_BODY
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DAILY_DATA
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DRUG
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
import com.makebodywell.bodywell.model.Exercise
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.model.Image
import com.makebodywell.bodywell.model.Text
import com.makebodywell.bodywell.model.User
import com.makebodywell.bodywell.model.Water

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

   fun getFood(type: String, date: String) : ArrayList<Food> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<Food> = ArrayList()
      val sql = "select * from $TABLE_FOOD where type = '$type' and regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         var data = Food()
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
         data.type = cursor.getString(10)
         data.regDate = cursor.getString(11)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getImage(type: String, date: String) : ArrayList<Image> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<Image> = ArrayList()
      val sql = "select * from $TABLE_IMAGE where type = '$type' and regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         var data = Image()
         data.id=cursor.getInt(0)
         data.imageUri=cursor.getString(1)
         data.type=cursor.getString(2)
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

   fun getExercise(date: String) : ArrayList<Exercise> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<Exercise> = ArrayList()
      val sql = "select * from $TABLE_EXERCISE where regDate = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         var data = Exercise()
         data.id=cursor.getInt(0)
         data.category=cursor.getString(1)
         data.name=cursor.getString(2)
         data.workoutTime= cursor.getString(3)
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

   fun getBodyDaily(date: String) : Body {
      val db = dbHelper!!.readableDatabase
      val data = Body()
      val sql = "select weight, regDate from body where regDate=strftime('%Y-%m-%d',datetime('$date', '-1 day'))"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data.weight = cursor.getDouble(0)
         data.regDate = cursor.getString(1)
      }
      cursor.close()
      return data
   }

   fun getBodyWeekly(start: String, end: String) : ArrayList<Body> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Body>()
      val sql = "select weight, regDate from body where regDate BETWEEN '$start' and '$end'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         var data = Body()
         data.weight = cursor.getDouble(0)
         data.regDate = cursor.getString(1)
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
         var data = Drug()
         data.id = cursor.getInt(0)
         data.type = cursor.getString(1)
         data.name = cursor.getString(2)
         data.amount = cursor.getString(3)
         data.unit = cursor.getString(4)
         data.period = cursor.getString(5)
         data.startDate = cursor.getString(6)
         data.endDate = cursor.getString(7)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getDrugId() : Drug {
      val db = dbHelper!!.readableDatabase
      val data = Drug()
      val sql = "select id from $TABLE_DRUG order by id desc limit 1"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data.id = cursor.getInt(0)
      }
      cursor.close()
      return data
   }

   fun getDrugDate(id: Int) : ArrayList<Drug> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<Drug> = ArrayList()
      val sql = "select * from $TABLE_DRUG_DATE where dataId = $id"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         var data = Drug()
         data.name=cursor.getString(1)
         data.id = cursor.getInt(2)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getDrugTime(id: Int) : ArrayList<Drug> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<Drug> = ArrayList()
      val sql = "select time, checked, dataId from $TABLE_DRUG_TIME where dataId = $id"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         var data = Drug()
         data.name = cursor.getString(0)
         data.count = cursor.getInt(1)
         data.id = cursor.getInt(2)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getDrugCheckCount(id: Int) : Drug {
      val db = dbHelper!!.readableDatabase
      val data = Drug()
      val sql = "select count(id) from $TABLE_DRUG_TIME where checked = 1 and dataId = $id"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         data.count = cursor.getInt(0)
      }
      cursor.close()
      return data
   }

   fun getDrugDaily(date: String): ArrayList<Drug> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<Drug> = ArrayList()
      val sql = "select id, name, amount, unit from $TABLE_DRUG where date('$date') between date(startDate) and date(endDate)"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         var data = Drug()
         data.id = cursor.getInt(0)
         data.name = cursor.getString(1)
         data.amount = cursor.getString(2)
         data.unit = cursor.getString(3)
         list.add(data)
      }
      cursor.close()
      return list
   }

   fun getNote(date: String) : Text {
      val db = dbHelper!!.readableDatabase
      val data = Text()
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
      db!!.insert(TABLE_DRUG, null, values)
   }

   fun insertDrugDate(date: String, dataId: Int) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("date", date)
      values.put("dataId", dataId)
      db!!.insert(TABLE_DRUG_DATE, null, values)
   }

   fun insertDrugTime(time: String, dataId: Int) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put("time", time)
      values.put("dataId", dataId)
      db!!.insert(TABLE_DRUG_TIME, null, values)
   }

   fun insertNote(data: Text) {
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
      val sql = "update $TABLE_FOOD set amount='${data.amount}' where id='${data.id}'"
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

   fun updateDrugGoal(data: DailyData){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_DAILY_DATA set drugGoal=${data.drugGoal} where regDate='${data.regDate}'"
      db.execSQL(sql)
      db.close()
   }

   fun updateDrugTime(data: Drug){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_DRUG_TIME set checked=${data.count} where time='${data.name}' and dataId=${data.id}"
      db.execSQL(sql)
      db.close()
   }

   fun updateNote(data: Text){
      val db = dbHelper!!.writableDatabase
      val sql = "update $TABLE_NOTE set title='${data.string1}', content='${data.string2}' where regDate='${data.string3}'"
      db.execSQL(sql)
      db.close()
   }

   fun deleteFood(id: Int): Boolean {
      val db = dbHelper!!.writableDatabase
      val success = db!!.delete("$TABLE_FOOD","id=$id",null)
      db.close()
      return (Integer.parseInt("$success") != -1)
   }

   fun deleteImage(id: Int): Boolean {
      val db = dbHelper!!.writableDatabase
      val success = db!!.delete("$TABLE_IMAGE","id=$id",null)
      db.close()
      return (Integer.parseInt("$success") != -1)
   }

   fun deleteExercise(id: Int): Boolean {
      val db = dbHelper!!.writableDatabase
      val success = db!!.delete("$TABLE_EXERCISE","id=$id",null)
      db.close()
      return (Integer.parseInt("$success") != -1)
   }

   fun deleteExerciseItem(type: String, name: String?): Boolean {
      val db = dbHelper!!.writableDatabase
      val success = db!!.delete("$TABLE_EXERCISE_ITEM","type='$type' and name='$name'",null)
      db.close()
      return (Integer.parseInt("$success") != -1)
   }

   fun deleteDrug(id: Int): Boolean {
      val db = dbHelper!!.writableDatabase
      val success = db!!.delete("$TABLE_DRUG","id=$id",null)
      db.close()
      return (Integer.parseInt("$success") != -1)
   }

   fun deleteDrugDate(id: Int): Boolean {
      val db = dbHelper!!.writableDatabase
      val success = db!!.delete("$TABLE_DRUG_DATE","dataId=$id",null)
      db.close()
      return (Integer.parseInt("$success") != -1)
   }

   fun deleteDrugTime(id: Int): Boolean {
      val db = dbHelper!!.writableDatabase
      val success = db!!.delete("$TABLE_DRUG_TIME","dataId=$id",null)
      db.close()
      return (Integer.parseInt("$success") != -1)
   }
}