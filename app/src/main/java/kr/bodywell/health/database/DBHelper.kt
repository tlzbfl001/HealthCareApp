package kr.bodywell.health.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
   companion object {
      const val DATABASE_NAME = "app.db"
      const val DATABASE_VERSION = 8
      const val USER = "user"
      const val TOKEN = "token"
      const val MEDICINE = "medicine"
      const val MEDICINE_TIME = "medicineTime"
      const val USER_ID = "userId"
   }

   override fun onCreate(db: SQLiteDatabase) {
      val user = "create table $USER(id integer primary key autoincrement, type text, idToken text, accessToken text, username text, email text, role text, uid text);"
      db.execSQL(user)

      val token = "create table $TOKEN(id integer primary key autoincrement, $USER_ID integer, access text, refresh text, accessCreated text, refreshCreated text);"
      db.execSQL(token)

      val medicine = "create table $MEDICINE(id integer primary key autoincrement, $USER_ID integer, medicineId text, name text, " +
         "amount integer, unit text, starts text, ends text, isSet integer);"
      db.execSQL(medicine)

      val medicineTime = "create table $MEDICINE_TIME(id integer primary key autoincrement, $USER_ID integer, medicineId integer, time text);"
      db.execSQL(medicineTime)
   }

   override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
      db.execSQL("drop table if exists $USER")
      db.execSQL("drop table if exists $TOKEN")
      db.execSQL("drop table if exists $MEDICINE")
      db.execSQL("drop table if exists $MEDICINE_TIME")
      onCreate(db)
   }
}