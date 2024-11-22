package kr.bodywell.android.database

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import kr.bodywell.android.database.DBHelper.Companion.CREATED_AT
import kr.bodywell.android.database.DBHelper.Companion.IMAGE
import kr.bodywell.android.database.DBHelper.Companion.MEDICINE
import kr.bodywell.android.database.DBHelper.Companion.MEDICINE_INTAKE
import kr.bodywell.android.database.DBHelper.Companion.MEDICINE_TIME
import kr.bodywell.android.database.DBHelper.Companion.NOTE
import kr.bodywell.android.database.DBHelper.Companion.TOKEN
import kr.bodywell.android.database.DBHelper.Companion.USER
import kr.bodywell.android.database.DBHelper.Companion.USER_ID
import kr.bodywell.android.model.Image
import kr.bodywell.android.model.Item
import kr.bodywell.android.model.Note
import kr.bodywell.android.model.Token
import kr.bodywell.android.model.User
import kr.bodywell.android.model.MedicineIntake
import kr.bodywell.android.model.MedicineTime
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
      val sql = "select id, type, email, idToken from $USER where type = '$type' and email = '$email'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id=cursor.getInt(0)
         values.type=cursor.getString(1)
         values.email = cursor.getString(2)
         values.idToken = cursor.getString(3)
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

   /*fun getDailyExercise(column: String, data: String): ArrayList<Exercise> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Exercise>()
      val sql = "select * from $DAILY_EXERCISE where $USER_ID = ${MyApp.prefs.getUserId()} and $column='$data'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Exercise()
         values.id = cursor.getInt(0)
         values.uid=cursor.getString(2)
         values.name = cursor.getString(3)
         values.intensity =cursor.getString(4)
         values.workoutTime = cursor.getInt(5)
         values.kcal = cursor.getInt(6)
         values.createdAt = cursor.getString(7)
         list.add(values)
      }
      cursor.close()
      return list
   }*/

   fun getMedicineRanking(data: String) : ArrayList<Item> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Item>()
      val sql = "select count(medicineUid) as ranking, medicineUid from $MEDICINE_INTAKE where $USER_ID = ${MyApp.prefs.getUserId()} and intakeAt = '$data' " +
         "group by medicineUid order by ranking desc limit 4"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Item()
         values.int1=cursor.getInt(0)
         values.string1=cursor.getString(1)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getMedicineRanking(start: String, end: String) : ArrayList<Item> {
      val db = dbHelper!!.readableDatabase
      val list = ArrayList<Item>()
      val sql = "select count(medicineUid) as ranking, medicineUid from $MEDICINE_INTAKE where $USER_ID = ${MyApp.prefs.getUserId()} " +
         "and intakeAt BETWEEN '$start' and '$end' group by medicineUid order by ranking desc limit 4"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Item()
         values.int1=cursor.getInt(0)
         values.string1=cursor.getString(1)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getNote(date: String) : Note {
      val db = dbHelper!!.readableDatabase
      val values = Note()
      val sql = "select * from $NOTE where $USER_ID = ${MyApp.prefs.getUserId()} and $CREATED_AT = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.id = cursor.getInt(0)
         values.title = cursor.getString(2)
         values.content = cursor.getString(3)
         values.status = cursor.getInt(4)
         values.createdAt = cursor.getString(5)
      }
      cursor.close()
      return values
   }

   fun getImage(type: String, date: String) : ArrayList<Image> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<Image> = ArrayList()
      val sql = "select * from $IMAGE where $USER_ID = ${MyApp.prefs.getUserId()} and type = '$type' and $CREATED_AT = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Image()
         values.id = cursor.getInt(0)
         values.type = cursor.getString(3)
         values.dataName = cursor.getString(4)
         values.imageName = cursor.getString(5)
         values.createdAt = cursor.getString(6)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getImage(type: String, name: String, date: String) : ArrayList<Image> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<Image> = ArrayList()
      val sql = "select * from $IMAGE where $USER_ID = ${MyApp.prefs.getUserId()} and type = '$type' and dataName = '$name' and $CREATED_AT = '$date'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Image()
         values.id = cursor.getInt(0)
         values.type = cursor.getString(3)
         values.dataName = cursor.getString(4)
         values.imageName = cursor.getString(5)
         values.createdAt = cursor.getString(6)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getImageUid() : ArrayList<Image> {
      val db = dbHelper!!.readableDatabase
      val list: ArrayList<Image> = ArrayList()
      val sql = "select id, imageName from $IMAGE where $USER_ID = ${MyApp.prefs.getUserId()} and uid is '' limit 5"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         val values = Image()
         values.id = cursor.getInt(0)
         values.imageName = cursor.getString(1)
         list.add(values)
      }
      cursor.close()
      return list
   }

   fun getAlarmId() : Int {
      val db = dbHelper!!.readableDatabase
      var value = 0
      val sql = "select alarmId from $MEDICINE order by alarmId desc limit 1"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         value = cursor.getInt(0)
      }
      cursor.close()
      return value
   }

   fun getMedicineTime(data: String) : MedicineTime {
      val db = dbHelper!!.readableDatabase
      val values = MedicineTime()
      val sql = "select medicineUid from $MEDICINE_TIME where uid = '$data'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.medicineId = cursor.getString(0)
      }
      cursor.close()
      return values
   }

   fun getMedicineIntake(data: String) : MedicineIntake {
      val db = dbHelper!!.readableDatabase
      val values = MedicineIntake()
      val sql = "select medicineUid, medicineTimeUid from $MEDICINE_INTAKE where uid = '$data'"
      val cursor = db!!.rawQuery(sql, null)
      while(cursor.moveToNext()) {
         values.medicineId = cursor.getString(0)
         values.medicineTimeId = cursor.getString(1)
      }
      cursor.close()
      return values
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

   fun insertMedicine(uid: String, alarmId: Int) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put(USER_ID, MyApp.prefs.getUserId())
      values.put("uid", uid)
      values.put("alarmId", alarmId)
      db!!.insert(MEDICINE, null, values)
   }

   fun insertMedicineTime(data: MedicineTime) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put(USER_ID, MyApp.prefs.getUserId())
      values.put("uid", data.id)
      values.put("medicineUid", data.medicineId)
      db!!.insert(MEDICINE_TIME, null, values)
   }

   fun insertMedicineIntake(data: MedicineIntake) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put(USER_ID, MyApp.prefs.getUserId())
      values.put("uid", data.id)
      values.put("medicineUid", data.medicineId)
      values.put("medicineTimeUid", data.medicineTimeId)
      values.put("intakeAt", data.intakeAt)
      db!!.insert(MEDICINE_INTAKE, null, values)
   }

   fun insertNote(data: Note) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put(USER_ID, MyApp.prefs.getUserId())
      values.put("title", data.title)
      values.put("content", data.content)
      values.put("status", data.status)
      values.put(CREATED_AT, data.createdAt)
      db!!.insert(NOTE, null, values)
   }

   fun insertImage(data: Image) {
      val db = dbHelper!!.writableDatabase
      val values = ContentValues()
      values.put(USER_ID, MyApp.prefs.getUserId())
      values.put("uid", data.uid)
      values.put("type", data.type)
      values.put("dataName", data.dataName)
      values.put("imageName", data.imageName)
      values.put(CREATED_AT, data.createdAt)
      db!!.insert(IMAGE, null, values)
   }

   fun updateUser(data: User){
      val db = dbHelper!!.writableDatabase
      val sql = "update $USER set idToken='${data.idToken}', accessToken='${data.accessToken}' where type='${data.type}' and email='${data.email}'"
      db.execSQL(sql)
      db.close()
   }

   fun updateUser2(data: User){
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

   fun updateNote(data: Note){
      val db = dbHelper!!.writableDatabase
      val sql = "update $NOTE set title='${data.title}', content='${data.content}', status=${data.status} where userId = ${MyApp.prefs.getUserId()} and $CREATED_AT='${data.createdAt}'"
      db.execSQL(sql)
      db.close()
   }

   fun deleteTable(table: String, column: String): Int {
      val db = dbHelper!!.writableDatabase
      val result = db.delete(table, "$column=${MyApp.prefs.getUserId()}", null)
      db.close()
      return result
   }

   fun deleteItem(table: String, column: String, data: Int): Int {
      val db = dbHelper!!.writableDatabase
      val result = db.delete(table, "$USER_ID=${MyApp.prefs.getUserId()} and $column=$data", null)
      db.close()
      return result
   }

   fun deleteItem(table: String, column: String, data: String): Int {
      val db = dbHelper!!.writableDatabase
      val result = db.delete(table, "$USER_ID=${MyApp.prefs.getUserId()} and $column='$data'", null)
      db.close()
      return result
   }

   fun deleteImage(type: String, name: String, createdAt: String): Int {
      val db = dbHelper!!.writableDatabase
      val result = db.delete(IMAGE, "$USER_ID=${MyApp.prefs.getUserId()} and type='$type' and dataName='$name' and createdAt='$createdAt'", null)
      db.close()
      return result
   }
}
