package kr.bodywell.android.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
   companion object {
      const val DATABASE_NAME = "app.db"
      const val DATABASE_VERSION = 5
      const val USER = "user"
      const val TOKEN = "token"
      const val MEDICINE = "medicine"
      const val MEDICINE_TIME = "medicineTime"
      const val MEDICINE_INTAKE = "medicineIntake"
      const val NOTE = "note"
      const val IMAGE = "image"
      const val USER_ID = "userId"
      const val TYPE_ADMIN = "ADMIN"
      const val TYPE_USER = "USER"
      const val CREATED_AT = "createdAt"
   }

   override fun onCreate(db: SQLiteDatabase) {
      val user = "create table $USER(id integer primary key autoincrement, type text, idToken text, accessToken text, username text, email text, role text, uid text);"
      db.execSQL(user)

      val token = "create table $TOKEN(id integer primary key autoincrement, $USER_ID integer, access text, refresh text, accessCreated text, refreshCreated text);"
      db.execSQL(token)

      val medicine = "create table $MEDICINE(id integer primary key autoincrement, $USER_ID integer, uid text, alarmId integer);"
      db.execSQL(medicine)

      val medicineTime = "create table $MEDICINE_TIME(id integer primary key autoincrement, $USER_ID integer, uid text, medicineUid text);"
      db.execSQL(medicineTime)

      val medicineIntake = "create table $MEDICINE_INTAKE(id integer primary key autoincrement, $USER_ID integer, uid text, medicineUid text, medicineTimeUid text, intakeAt text);"
      db.execSQL(medicineIntake)

      val note = "create table $NOTE(id integer primary key autoincrement, $USER_ID integer, title text, content text, status integer, $CREATED_AT text);"
      db.execSQL(note)

      val image = "create table $IMAGE(id integer primary key autoincrement, $USER_ID integer, uid text, type text, dataName text, imageName text, $CREATED_AT text);"
      db.execSQL(image)
   }

   override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
      db.execSQL("drop table if exists $USER")
      db.execSQL("drop table if exists $TOKEN")
      db.execSQL("drop table if exists $MEDICINE")
      db.execSQL("drop table if exists $MEDICINE_TIME")
      db.execSQL("drop table if exists $MEDICINE_INTAKE")
      db.execSQL("drop table if exists $NOTE")
      db.execSQL("drop table if exists $IMAGE")
      onCreate(db)
   }
}