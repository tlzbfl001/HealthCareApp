package kr.bodywell.android.database

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import kr.bodywell.android.database.DBHelper.Companion.MEDICINE
import kr.bodywell.android.database.DBHelper.Companion.MEDICINE_TIME
import kr.bodywell.android.database.DBHelper.Companion.TOKEN
import kr.bodywell.android.database.DBHelper.Companion.UPDATE_TIME
import kr.bodywell.android.database.DBHelper.Companion.USER
import kr.bodywell.android.database.DBHelper.Companion.USER_ID
import kr.bodywell.android.model.MedicineList
import kr.bodywell.android.model.MedicineItem
import kr.bodywell.android.model.MedicineTime
import kr.bodywell.android.model.Token
import kr.bodywell.android.model.User
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
      val value = User()
      val sql = "select id, type, email from $USER where type = '$type' and email = '$email'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         value.id=cursor.getInt(0)
         value.type=cursor.getString(1)
         value.email = cursor.getString(2)
      }
      cursor.close()
      db.close()
      return value
   }

   fun getUser() : User {
      val db = dbHelper!!.readableDatabase
      val value = User()
      val sql = "select * from $USER where id = ${MyApp.prefs.getUserId()}"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         value.id=cursor.getInt(0)
         value.type=cursor.getString(1)
         value.idToken = cursor.getString(2)
         value.accessToken = cursor.getString(3)
         value.username = cursor.getString(4)
         value.email = cursor.getString(5)
         value.role = cursor.getString(6)
         value.uid = cursor.getString(7)
      }
      cursor.close()
      db.close()
      return value
   }

   fun getToken() : Token {
      val db = dbHelper!!.readableDatabase
      val value = Token()
      val sql = "select * from $TOKEN where $USER_ID = ${MyApp.prefs.getUserId()}"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         value.id=cursor.getInt(0)
         value.userId=cursor.getInt(1)
         value.access=cursor.getString(2)
         value.refresh = cursor.getString(3)
         value.accessCreated = cursor.getString(4)
         value.refreshCreated = cursor.getString(5)
      }
      cursor.close()
      db.close()
      return value
   }

   fun getMedicine(data: String) : MedicineItem {
      val db = dbHelper!!.readableDatabase
      val value = MedicineItem()
      val sql = "SELECT * FROM $MEDICINE WHERE $USER_ID = ${MyApp.prefs.getUserId()} AND medicineId = '$data'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         value.id = cursor.getInt(0)
         value.medicineId = cursor.getString(2)
         value.name = cursor.getString(3)
         value.amount = cursor.getInt(4)
         value.unit = cursor.getString(5)
         value.starts = cursor.getString(6)
         value.ends = cursor.getString(7)
         value.isSet = cursor.getInt(8)
      }
      cursor.close()
      db.close()
      return value
   }

   fun getMedicines() : ArrayList<MedicineList> {
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
      db.close()
      return list
   }

   fun getMedicines(data: String) : ArrayList<MedicineItem> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<MedicineItem>()
      val sql = "select * from $MEDICINE where $USER_ID = ${MyApp.prefs.getUserId()} AND strftime('%Y-%m-%d', starts) >= '$data'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val value = MedicineItem()
         value.id = cursor.getInt(0)
         value.medicineId = cursor.getString(2)
         value.name = cursor.getString(3)
         value.amount = cursor.getInt(4)
         value.unit = cursor.getString(5)
         value.starts = cursor.getString(6)
         value.ends = cursor.getString(7)
         value.isSet = cursor.getInt(8)
         list.add(value)
      }
      cursor.close()
      db.close()
      return list
   }

   fun getMedicineTime(data: Int) : ArrayList<MedicineTime> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<MedicineTime>()
      val sql = "select time from $MEDICINE_TIME where $USER_ID = ${MyApp.prefs.getUserId()} AND medicineId = $data"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         list.add(MedicineTime(time = cursor.getString(0)))
      }
      cursor.close()
      db.close()
      return list
   }

   fun getUpdateTime(data: String) : String {
      val db = dbHelper!!.readableDatabase
      var value = ""
      val sql = "select $data from $UPDATE_TIME where $USER_ID = ${MyApp.prefs.getUserId()}"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         value = cursor.getString(0)
      }
      cursor.close()
      db.close()
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
      db.close()
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
      db.close()
   }

   fun insertMedicine(data: MedicineItem) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put(USER_ID, MyApp.prefs.getUserId())
      values.put("medicineId", data.medicineId)
      values.put("name", data.name)
      values.put("amount", data.amount)
      values.put("unit", data.unit)
      values.put("starts", data.starts)
      values.put("ends", data.ends)
      values.put("isSet", data.isSet)
      db!!.insert(MEDICINE, null, values)
      db.close()
   }

   fun insertMedicineTime(data: MedicineTime) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put(USER_ID, MyApp.prefs.getUserId())
      values.put("medicineId", data.userId)
      values.put("time", data.time)
      db!!.insert(MEDICINE_TIME, null, values)
      db.close()
   }

   fun insertUpdateTime(data: String) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put(USER_ID, MyApp.prefs.getUserId())
      values.put("medicine", data)
      values.put("file", data)
      db!!.insert(UPDATE_TIME, null, values)
      db.close()
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

   fun updateMedicine(data: MedicineItem){
      val db = dbHelper!!.writableDatabase
      val sql = "update $MEDICINE set name='${data.name}', amount=${data.amount}, unit='${data.unit}', starts='${data.starts}', ends='${data.ends}', " +
         "isSet=${data.isSet} where $USER_ID=${MyApp.prefs.getUserId()} AND id = ${data.id}"
      db.execSQL(sql)
      db.close()
   }

   fun updateAlarmSet(data: Int){
      val db = dbHelper!!.writableDatabase
      val sql = "update $MEDICINE set isSet=$data where $USER_ID=${MyApp.prefs.getUserId()}"
      db.execSQL(sql)
      db.close()
   }

   fun updateTime1(data: String){
      val db = dbHelper!!.writableDatabase
      val sql = "update $UPDATE_TIME set medicine='$data' where $USER_ID=${MyApp.prefs.getUserId()}"
      db.execSQL(sql)
      db.close()
   }

   fun updateTime2(data: String){
      val db = dbHelper!!.writableDatabase
      val sql = "update $UPDATE_TIME set file='$data' where $USER_ID=${MyApp.prefs.getUserId()}"
      db.execSQL(sql)
      db.close()
   }

   fun deleteTable(table: String, column: String): Int {
      val db = dbHelper!!.writableDatabase
      val result = db.delete(table, "$column=${MyApp.prefs.getUserId()}", null)
      db.close()
      return result
   }

   fun deleteMedicine(data: Int): Int {
      val db = dbHelper!!.writableDatabase
      val result = db.delete(MEDICINE, "$USER_ID=${MyApp.prefs.getUserId()} AND id = $data", null)
      db.close()
      return result
   }

   fun deleteMedicineTime(data: Int): Int {
      val db = dbHelper!!.writableDatabase
      val result = db.delete(MEDICINE_TIME, "$USER_ID=${MyApp.prefs.getUserId()} AND medicineId = $data", null)
      db.close()
      return result
   }
}
