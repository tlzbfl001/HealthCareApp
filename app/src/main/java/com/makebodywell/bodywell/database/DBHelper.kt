package com.makebodywell.bodywell.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
   companion object {
      var DATABASE_VERSION = 1
      const val DATABASE_NAME = "app.db"
      const val TABLE_USER = "user"
      const val TABLE_TOKEN = "token"
      const val TABLE_FOOD = "food"
      const val TABLE_WATER = "water"
      const val TABLE_EXERCISE = "exercise"
      const val TABLE_BODY = "body"
      const val TABLE_DRUG = "drug"
      const val TABLE_DRUG_TIME = "drugTime"
      const val TABLE_DRUG_CHECK = "drugCheck"
      const val TABLE_NOTE = "note"
      const val TABLE_SLEEP = "sleep"
      const val TABLE_DAILY_DATA = "dailyData"
      const val TABLE_IMAGE = "image"
   }

   override fun onCreate(db: SQLiteDatabase) {
      val user = "create table $TABLE_USER(id integer primary key autoincrement, type text, idToken text, userId text, deviceId text," +
         "healthId text, activityId text, bodyMeasurementId text, email text, name text, nickname text, gender text, birthday text, profileImage text," +
         "height real, weight real, weightGoal real, kcalGoal real, waterGoal integer, waterUnit integer, regDate text);"
      db.execSQL(user)

      val token = "create table $TABLE_TOKEN(id integer primary key autoincrement, userId integer, accessToken text, refreshToken text, accessTokenRegDate text, " +
         "refreshTokenRegDate text);"
      db.execSQL(token)

      val food = "create table $TABLE_FOOD(id integer primary key autoincrement, userId integer, name text, unit text, amount integer, count integer, kcal integer," +
              "carbohydrate real, protein real, fat real, salt real, sugar real, type integer, regDate text);"
      db.execSQL(food)

      val water = "create table $TABLE_WATER(id integer primary key autoincrement, userId integer, water integer, volume integer, regDate text);"
      db.execSQL(water)

      val exercise = "create table $TABLE_EXERCISE(id integer primary key autoincrement, userId integer, name text, intensity text, workoutTime text, calories integer, regDate text);"
      db.execSQL(exercise)

      val body = "create table $TABLE_BODY(id integer primary key autoincrement, userId integer, height real, weight real, age integer, " +
              "gender text, exerciseLevel integer, fat real, muscle real, bmi real, bmr real, regDate text);"
      db.execSQL(body)

      val drug = "create table $TABLE_DRUG(id integer primary key autoincrement, userId integer, type text, name text, amount text, unit text, count integer," +
              "startDate text, endDate text, isSet integer, regDate text);"
      db.execSQL(drug)

      val drugTime = "create table $TABLE_DRUG_TIME(id integer primary key autoincrement, userId integer, hour integer, minute integer, drugId integer);"
      db.execSQL(drugTime)

      val drugCheck = "create table $TABLE_DRUG_CHECK(id integer primary key autoincrement, userId integer, checked integer, drugTimeId integer, regDate text);"
      db.execSQL(drugCheck)

      val note = "create table $TABLE_NOTE(id integer primary key autoincrement, userId integer, title text, content integer, status integer, regDate text);"
      db.execSQL(note)

      val sleep = "create table $TABLE_SLEEP(id integer primary key autoincrement, userId integer, bedTime integer, wakeTime integer, sleepTime integer, regDate text);"
      db.execSQL(sleep)

      val dailyData = "create table $TABLE_DAILY_DATA(id integer primary key autoincrement, userId integer, foodGoal integer, waterGoal integer, exerciseGoal integer," +
              "bodyGoal real, sleepGoal integer, drugGoal integer, regDate text);"
      db.execSQL(dailyData)

      val image = "create table $TABLE_IMAGE(id integer primary key autoincrement, userId integer, imageUri text, type integer, dataId integer, regDate text);"
      db.execSQL(image)
   }

   override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
      db.execSQL("drop table if exists $TABLE_USER")
      db.execSQL("drop table if exists $TABLE_TOKEN")
      db.execSQL("drop table if exists $TABLE_FOOD")
      db.execSQL("drop table if exists $TABLE_WATER")
      db.execSQL("drop table if exists $TABLE_EXERCISE")
      db.execSQL("drop table if exists $TABLE_BODY")
      db.execSQL("drop table if exists $TABLE_DRUG")
      db.execSQL("drop table if exists $TABLE_DRUG_TIME")
      db.execSQL("drop table if exists $TABLE_DRUG_CHECK")
      db.execSQL("drop table if exists $TABLE_NOTE")
      db.execSQL("drop table if exists $TABLE_SLEEP")
      db.execSQL("drop table if exists $TABLE_DAILY_DATA")
      db.execSQL("drop table if exists $TABLE_IMAGE")
      onCreate(db)
   }
}