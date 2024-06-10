package kr.bodywell.android.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
   companion object {
      const val DATABASE_NAME = "app.db"
      const val DATABASE_VERSION = 1
      const val TABLE_USER = "user"
      const val TABLE_TOKEN = "token"
      const val TABLE_FOOD = "food"
      const val TABLE_DAILY_FOOD = "dailyFood"
      const val TABLE_WATER = "water"
      const val TABLE_EXERCISE = "exercise"
      const val TABLE_DAILY_EXERCISE = "dailyExercise"
      const val TABLE_BODY = "body"
      const val TABLE_SLEEP = "sleep"
      const val TABLE_DRUG = "drug"
      const val TABLE_DRUG_TIME = "drugTime"
      const val TABLE_DRUG_CHECK = "drugCheck"
      const val TABLE_NOTE = "note"
      const val TABLE_GOAL = "goal"
      const val TABLE_IMAGE = "image"
      const val TABLE_UNUSED = "unused"
   }

   override fun onCreate(db: SQLiteDatabase) {
      val user = "create table $TABLE_USER(id integer primary key autoincrement, type text, email text, idToken text, userUid text, deviceUid text, name text, " +
         "gender text, birthday text, image text, height real, weight real, weightGoal real, kcalGoal real, waterGoal integer, waterUnit integer, regDate text);"
      db.execSQL(user)

      val token = "create table $TABLE_TOKEN(id integer primary key autoincrement, userId integer, access text, refresh text, accessRegDate text, refreshRegDate text);"
      db.execSQL(token)

      val food = "create table $TABLE_FOOD(id integer primary key autoincrement, userId integer, basic integer, uid text, name text, unit text, amount integer, kcal integer, " +
         "carbohydrate real, protein real, fat real, salt real, sugar real, useCount integer, useDate text, isUpdated integer);"
      db.execSQL(food)

      val dailyFood = "create table $TABLE_DAILY_FOOD(id integer primary key autoincrement, userId integer, uid text, type text, name text, unit text, amount integer, " +
         "kcal integer, carbohydrate real, protein real, fat real, salt real, sugar real, count integer, regDate text, isUpdated integer);"
      db.execSQL(dailyFood)

      val water = "create table $TABLE_WATER(id integer primary key autoincrement, userId integer, uid text, count integer, ml integer, regDate text, isUpdated integer);"
      db.execSQL(water)

      val exercise = "create table $TABLE_EXERCISE(id integer primary key autoincrement, userId integer, basic integer, uid text, name text, intensity text, " +
         "workoutTime integer, kcal integer, useCount integer, useDate text, isUpdated integer);"
      db.execSQL(exercise)

      val dailyExercise = "create table $TABLE_DAILY_EXERCISE(id integer primary key autoincrement, userId integer, uid text, name text, intensity text, " +
         "workoutTime integer, kcal integer, regDate text, isUpdated integer);"
      db.execSQL(dailyExercise)

      val body = "create table $TABLE_BODY(id integer primary key autoincrement, userId integer, uid text, height real, weight real, intensity integer, fat real, " +
         "muscle real, bmi real, bmr real, regDate text, isUpdated integer);"
      db.execSQL(body)

      val sleep = "create table $TABLE_SLEEP(id integer primary key autoincrement, userId integer, uid text, startTime text, endTime text, total integer, " +
         "regDate text, isUpdated integer);"
      db.execSQL(sleep)

      val drug = "create table $TABLE_DRUG(id integer primary key autoincrement, userId integer, uid text, type text, name text, amount integer, unit text, " +
         "count integer, startDate text, endDate text, isSet integer, regDate text);"
      db.execSQL(drug)

      val drugTime = "create table $TABLE_DRUG_TIME(id integer primary key autoincrement, userId integer, drugId integer, uid text, time text);"
      db.execSQL(drugTime)

      val drugCheck = "create table $TABLE_DRUG_CHECK(id integer primary key autoincrement, userId integer, drugId integer, drugTimeId integer, uid text, regDate text);"
      db.execSQL(drugCheck)

      val note = "create table $TABLE_NOTE(id integer primary key autoincrement, userId integer, title text, content integer, status integer, regDate text);"
      db.execSQL(note)

      val goal = "create table $TABLE_GOAL(id integer primary key autoincrement, userId integer, uid text, foodGoal integer, waterGoal integer, " +
         "exerciseGoal integer, bodyGoal real, sleepGoal integer, drugGoal integer, regDate text, isUpdated integer);"
      db.execSQL(goal)

      val image = "create table $TABLE_IMAGE(id integer primary key autoincrement, userId integer, type text, dataId integer, imageUri text, regDate text);"
      db.execSQL(image)

      val unused = "create TABLE $TABLE_UNUSED(id integer primary key autoincrement, userId integer, type text, value text);"
      db.execSQL(unused)
   }

   override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
      db.execSQL("drop table if exists $TABLE_USER")
      db.execSQL("drop table if exists $TABLE_TOKEN")
      db.execSQL("drop table if exists $TABLE_FOOD")
      db.execSQL("drop table if exists $TABLE_DAILY_FOOD")
      db.execSQL("drop table if exists $TABLE_WATER")
      db.execSQL("drop table if exists $TABLE_EXERCISE")
      db.execSQL("drop table if exists $TABLE_DAILY_EXERCISE")
      db.execSQL("drop table if exists $TABLE_BODY")
      db.execSQL("drop table if exists $TABLE_DRUG")
      db.execSQL("drop table if exists $TABLE_DRUG_TIME")
      db.execSQL("drop table if exists $TABLE_DRUG_CHECK")
      db.execSQL("drop table if exists $TABLE_NOTE")
      db.execSQL("drop table if exists $TABLE_SLEEP")
      db.execSQL("drop table if exists $TABLE_GOAL")
      db.execSQL("drop table if exists $TABLE_IMAGE")
      db.execSQL("drop table if exists $TABLE_UNUSED")
      onCreate(db)
   }
}