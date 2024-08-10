package kr.bodywell.android.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
   companion object {
      const val DATABASE_NAME = "app.db"
      const val DATABASE_VERSION = 6
      const val USER = "user"
      const val TOKEN = "token"
      const val FOOD = "food"
      const val DAILY_FOOD = "dailyFood"
      const val WATER = "water"
      const val EXERCISE = "exercise"
      const val DAILY_EXERCISE = "dailyExercise"
      const val BODY = "body"
      const val SLEEP = "sleep"
      const val DRUG = "drug"
      const val DRUG_TIME = "drugTime"
      const val DRUG_CHECK = "drugCheck"
      const val NOTE = "note"
      const val GOAL = "goal"
      const val IMAGE = "image"
      const val UNUSED = "unused"
      const val SYNC_TIME = "syncTime"
      const val USER_ID = "userId"
      const val CREATED_AT = "createdAt"
      const val IS_UPDATED = "isUpdated"
   }

   override fun onCreate(db: SQLiteDatabase) {
      val user = "create table $USER(id integer primary key autoincrement, type text, email text, idToken text, accessToken text, uid text, name text, gender text, " +
         "birthday text, image text, height real, weight real, weightGoal real, kcalGoal real, waterGoal integer, waterUnit integer, $CREATED_AT text, $IS_UPDATED integer);"
      db.execSQL(user)

      val token = "create table $TOKEN(id integer primary key autoincrement, $USER_ID integer, access text, refresh text, accessCreated text, refreshCreated text);"
      db.execSQL(token)

      val food = "create table $FOOD(id integer primary key autoincrement, $USER_ID integer, admin integer, uid text, name text, unit text, amount integer, kcal integer, " +
         "carbohydrate real, protein real, fat real, salt real, sugar real, useCount integer, useDate text, $IS_UPDATED integer);"
      db.execSQL(food)

      val dailyFood = "create table $DAILY_FOOD(id integer primary key autoincrement, $USER_ID integer, uid text, type text, name text, unit text, amount integer, " +
         "kcal integer, carbohydrate real, protein real, fat real, salt real, sugar real, count integer, $CREATED_AT text, $IS_UPDATED integer);"
      db.execSQL(dailyFood)

      val water = "create table $WATER(id integer primary key autoincrement, $USER_ID integer, uid text, count integer, volume integer, $CREATED_AT text, $IS_UPDATED integer);"
      db.execSQL(water)

      val exercise = "create table $EXERCISE(id integer primary key autoincrement, $USER_ID integer, admin integer, uid text, name text, " +
         "useCount integer, useDate text, $IS_UPDATED integer);"
      db.execSQL(exercise)

      val dailyExercise = "create table $DAILY_EXERCISE(id integer primary key autoincrement, $USER_ID integer, uid text, name text, intensity text, " +
         "workoutTime integer, kcal integer, $CREATED_AT text, $IS_UPDATED integer);"
      db.execSQL(dailyExercise)

      val body = "create table $BODY(id integer primary key autoincrement, $USER_ID integer, uid text, height real, weight real, intensity integer, fat real, " +
         "muscle real, bmi real, bmr real, $CREATED_AT text, $IS_UPDATED integer);"
      db.execSQL(body)

      val sleep = "create table $SLEEP(id integer primary key autoincrement, $USER_ID integer, uid text, startTime text, endTime text, $IS_UPDATED integer);"
      db.execSQL(sleep)

      val drug = "create table $DRUG(id integer primary key autoincrement, $USER_ID integer, uid text, type text, name text, amount integer, unit text, " +
         "count integer, startDate text, endDate text, isSet integer, $IS_UPDATED integer);"
      db.execSQL(drug)

      val drugTime = "create table $DRUG_TIME(id integer primary key autoincrement, $USER_ID integer, uid text, drugId integer, time text);"
      db.execSQL(drugTime)

      val drugCheck = "create table $DRUG_CHECK(id integer primary key autoincrement, $USER_ID integer, uid text, drugId integer, drugTimeId integer, " +
         "time text, $CREATED_AT text, checkedAt text);"
      db.execSQL(drugCheck)

      val note = "create table $NOTE(id integer primary key autoincrement, $USER_ID integer, title text, content integer, status integer, $CREATED_AT text);"
      db.execSQL(note)

      val goal = "create table $GOAL(id integer primary key autoincrement, $USER_ID integer, uid text, food integer, waterVolume integer, water integer, " +
         "exercise integer, body real, sleep integer, drug integer, $CREATED_AT text, $IS_UPDATED integer);"
      db.execSQL(goal)

      val image = "create table $IMAGE(id integer primary key autoincrement, $USER_ID integer, type text, dataId integer, imageUri text, $CREATED_AT text);"
      db.execSQL(image)

      val unused = "create TABLE $UNUSED(id integer primary key autoincrement, $USER_ID integer, type text, value text, drugUid text, drugTimeUid text, $CREATED_AT text);"
      db.execSQL(unused)

      val syncTime = "create TABLE $SYNC_TIME(id integer primary key autoincrement, $USER_ID integer, syncedAt text);"
      db.execSQL(syncTime)
   }

   override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
      db.execSQL("drop table if exists $USER")
      db.execSQL("drop table if exists $TOKEN")
      db.execSQL("drop table if exists $FOOD")
      db.execSQL("drop table if exists $DAILY_FOOD")
      db.execSQL("drop table if exists $WATER")
      db.execSQL("drop table if exists $EXERCISE")
      db.execSQL("drop table if exists $DAILY_EXERCISE")
      db.execSQL("drop table if exists $BODY")
      db.execSQL("drop table if exists $DRUG")
      db.execSQL("drop table if exists $DRUG_TIME")
      db.execSQL("drop table if exists $DRUG_CHECK")
      db.execSQL("drop table if exists $NOTE")
      db.execSQL("drop table if exists $SLEEP")
      db.execSQL("drop table if exists $GOAL")
      db.execSQL("drop table if exists $IMAGE")
      db.execSQL("drop table if exists $UNUSED")
      db.execSQL("drop table if exists $SYNC_TIME")
      onCreate(db)
   }
}