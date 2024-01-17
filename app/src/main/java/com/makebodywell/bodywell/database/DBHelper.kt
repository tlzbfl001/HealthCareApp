package com.makebodywell.bodywell.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
   companion object {
      const val DATABASE_NAME = "app.db"
      const val DATABASE_VERSION = 4
      const val TABLE_USER = "user"
      const val TABLE_FOOD = "food"
      const val TABLE_WATER = "water"
      const val TABLE_EXERCISE = "exercise"
      const val TABLE_EXERCISE_CATEGORY = "exerciseCategory"
      const val TABLE_EXERCISE_PART = "exercisePart"
      const val TABLE_EXERCISE_ITEM = "exerciseItem"
      const val TABLE_EXERCISE_DELETE = "exerciseDelete"
      const val TABLE_BODY = "body"
      const val TABLE_DRUG = "drug"
      const val TABLE_DRUG_DATE = "drugDate"
      const val TABLE_DRUG_TIME = "drugTime"
      const val TABLE_DRUG_CHECK = "drugCheck"
      const val TABLE_NOTE = "note"
      const val TABLE_DAILY_DATA = "dailyData"
      const val TABLE_IMAGE = "image"
   }

   override fun onCreate(db: SQLiteDatabase) {
      val user = "create table $TABLE_USER(id integer primary key autoincrement, type text, idToken text, accessToken text, email text, name text, " +
              "nickname text, gender text, birthYear text, birthDay text, profileImage text, regDate text);"
      db.execSQL(user)

      val food = "create table $TABLE_FOOD(id integer primary key autoincrement, name text, unit integer, amount integer, kcal integer, carbohydrate real, protein real, " +
              "fat real, salt real, sugar real, type integer, regDate text);"
      db.execSQL(food)

      val water = "create table $TABLE_WATER(id integer primary key autoincrement, water integer, volume integer, regDate text);"
      db.execSQL(water)

      val exercise = "create table $TABLE_EXERCISE(id integer primary key autoincrement, category text, name text, workoutTime text, distance real, calories integer, regDate text);"
      db.execSQL(exercise)

      val exerciseCategory = "create table $TABLE_EXERCISE_CATEGORY(id integer primary key autoincrement, name text, dataId integer);"
      db.execSQL(exerciseCategory)

      val exercisePart = "create table $TABLE_EXERCISE_PART(id integer primary key autoincrement, name text, dataId integer);"
      db.execSQL(exercisePart)

      val exerciseItem = "create table $TABLE_EXERCISE_ITEM(id integer primary key autoincrement, type text, name text);"
      db.execSQL(exerciseItem)

      val exerciseDelete = "create table $TABLE_EXERCISE_DELETE(id integer primary key autoincrement, type text, name text);"
      db.execSQL(exerciseDelete)

      val body = "create table $TABLE_BODY(id integer primary key autoincrement, height real, weight real, age integer, " +
              "gender text, exerciseLevel integer, fat real, muscle real, bmi real, bmr real, regDate text);"
      db.execSQL(body)

      val drug = "create table $TABLE_DRUG(id integer primary key autoincrement, type text, name text, amount text, unit text, period text," +
              "startDate text, endDate text, isSet integer, regDate text);"
      db.execSQL(drug)

      val drugDate = "create table $TABLE_DRUG_DATE(id integer primary key autoincrement, date text, drugId integer);"
      db.execSQL(drugDate)

      val drugTime = "create table $TABLE_DRUG_TIME(id integer primary key autoincrement, hour integer, minute integer, drugId integer);"
      db.execSQL(drugTime)

      val drugCheck = "create table $TABLE_DRUG_CHECK(id integer primary key autoincrement, checked integer, " +
              "drugTimeId integer, regDate text);"
      db.execSQL(drugCheck)

      val note = "create table $TABLE_NOTE(id integer primary key autoincrement, title text, content integer, regDate text);"
      db.execSQL(note)

      val dailyData = "create table $TABLE_DAILY_DATA(id integer primary key autoincrement, foodGoal integer, waterGoal integer, " +
              "exerciseGoal integer, bodyGoal real, sleepGoal integer, drugGoal integer, regDate text);"
      db.execSQL(dailyData)

      val image = "create table $TABLE_IMAGE(id integer primary key autoincrement, imageUri text, type text, regDate text);"
      db.execSQL(image)
   }

   override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
      db.execSQL("drop table if exists $TABLE_USER")
      db.execSQL("drop table if exists $TABLE_FOOD")
      db.execSQL("drop table if exists $TABLE_WATER")
      db.execSQL("drop table if exists $TABLE_EXERCISE")
      db.execSQL("drop table if exists $TABLE_EXERCISE_CATEGORY")
      db.execSQL("drop table if exists $TABLE_EXERCISE_PART")
      db.execSQL("drop table if exists $TABLE_EXERCISE_ITEM")
      db.execSQL("drop table if exists $TABLE_EXERCISE_DELETE")
      db.execSQL("drop table if exists $TABLE_BODY")
      db.execSQL("drop table if exists $TABLE_DRUG")
      db.execSQL("drop table if exists $TABLE_DRUG_DATE")
      db.execSQL("drop table if exists $TABLE_DRUG_TIME")
      db.execSQL("drop table if exists $TABLE_DRUG_CHECK")
      db.execSQL("drop table if exists $TABLE_NOTE")
      db.execSQL("drop table if exists $TABLE_DAILY_DATA")
      db.execSQL("drop table if exists $TABLE_IMAGE")
      onCreate(db)
   }
}