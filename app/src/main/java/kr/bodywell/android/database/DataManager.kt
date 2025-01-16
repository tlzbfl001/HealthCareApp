package kr.bodywell.android.database

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.util.Log
import kr.bodywell.android.database.DBHelper.Companion.MEDICINE
import kr.bodywell.android.database.DBHelper.Companion.TOKEN
import kr.bodywell.android.database.DBHelper.Companion.UPDATED_AT
import kr.bodywell.android.database.DBHelper.Companion.USER
import kr.bodywell.android.database.DBHelper.Companion.USER_ID
import kr.bodywell.android.model.Medicine
import kr.bodywell.android.model.MedicineList
import kr.bodywell.android.model.Token
import kr.bodywell.android.model.User
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.MyApp

class DataManager(private var context: Context?) {
   private var dbHelper: DBHelper? = null

   @Throws(SQLException::class)
   fun open(): DataManager {
      dbHelper = DBHelper(context)
      return this
   }

   fun getUser(type: String, email: String) : User {
      val db = dbHelper!!.readableDatabase
      val values = User()
      val sql = "select id, type, email from $USER where type = '$type' and email = '$email'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id=cursor.getInt(0)
         values.type=cursor.getString(1)
         values.email = cursor.getString(2)
      }
      cursor.close()
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
         values.idToken = cursor.getString(2)
         values.accessToken = cursor.getString(3)
         values.username = cursor.getString(4)
         values.email = cursor.getString(5)
         values.role = cursor.getString(6)
         values.uid = cursor.getString(7)
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

   fun getMedicine(data: String) : Int {
      val db = dbHelper!!.readableDatabase
      var value = 0
      val sql = "SELECT id, medicineId FROM $MEDICINE WHERE $USER_ID = ${MyApp.prefs.getUserId()} AND medicineId = '$data'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         value = cursor.getInt(0)
      }
      cursor.close()
      return value
   }

   fun getAllMedicine() : ArrayList<MedicineList> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<MedicineList>()
      val sql = "select id, medicineId from $MEDICINE where $USER_ID = ${MyApp.prefs.getUserId()}"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val value = MedicineList()
         value.id = cursor.getInt(0)
         value.medicineId = cursor.getString(1)
         list.add(value)
      }
      cursor.close()
      return list
   }

   fun getUpdatedAt(data: String) : String {
      val db = dbHelper!!.readableDatabase
      var value = ""
      val sql = "select $data from $UPDATED_AT where $USER_ID = ${MyApp.prefs.getUserId()}"
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
      values.put("idToken", data.idToken)
      values.put("accessToken", data.accessToken)
      values.put("username", data.username)
      values.put("email", data.email)
      values.put("role", data.role)
      values.put("uid", data.uid)
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

   fun insertMedicine(medicineId: String) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put(USER_ID, MyApp.prefs.getUserId())
      values.put("medicineId", medicineId)
      db!!.insert(MEDICINE, null, values)
   }

   fun insertUpdatedAt(data: String) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put(USER_ID, MyApp.prefs.getUserId())
      values.put("medicine", data)
      values.put("file", data)
      db!!.insert(UPDATED_AT, null, values)
   }

   fun updateUser(data: User){
      val db = dbHelper!!.writableDatabase
      val sql = "update $USER set idToken='${data.idToken}', accessToken='${data.accessToken}', username='${data.username}', role='${data.role}', uid='${data.uid}' " +
         "where type='${data.type}' and email='${data.email}'"
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

   fun updateMedicineTime(data: String){
      val db = dbHelper!!.writableDatabase
      val sql = "update $UPDATED_AT set medicine='$data' where $USER_ID=${MyApp.prefs.getUserId()}"
      db.execSQL(sql)
      db.close()
   }

   fun updateFileTime(data: String){
      val db = dbHelper!!.writableDatabase
      val sql = "update $UPDATED_AT set file='$data' where $USER_ID=${MyApp.prefs.getUserId()}"
      db.execSQL(sql)
      db.close()
   }

   fun deleteTable(table: String, column: String): Int {
      val db = dbHelper!!.writableDatabase
      val result = db.delete(table, "$column=${MyApp.prefs.getUserId()}", null)
      db.close()
      return result
   }

   fun deleteMedicine(data: String): Int {
      val db = dbHelper!!.writableDatabase
      val result = db.delete(MEDICINE, "$USER_ID=${MyApp.prefs.getUserId()} and medicineId='$data'", null)
      db.close()
      return result
   }
}
