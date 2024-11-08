package kr.bodywell.android.api.powerSync

import android.content.Context
import android.util.Log
import com.powersync.DatabaseDriverFactory
import com.powersync.PowerSyncDatabase
import com.powersync.connectors.PowerSyncBackendConnector
import com.powersync.connectors.PowerSyncCredentials
import com.powersync.db.crud.CrudEntry
import com.powersync.db.crud.UpdateType
import kotlinx.coroutines.runBlocking
import kr.bodywell.android.api.powerSync.SyncController.delete
import kr.bodywell.android.api.powerSync.SyncController.upsert
import kr.bodywell.android.api.powerSync.SyncController.update
import kr.bodywell.android.model.Food
import kr.bodywell.android.model.Goal
import kr.bodywell.android.model.Medicine
import kr.bodywell.android.model.MedicineIntake
import kr.bodywell.android.model.MedicineTime
import kr.bodywell.android.model.Water
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.CustomUtil.getToken
import kr.bodywell.android.util.CustomUtil.getUser

class SyncService(context: Context, driverFactory: DatabaseDriverFactory) {
	private val _context = context
	private val database = PowerSyncDatabase(driverFactory, SyncSchema)
	private val db: PowerSyncDatabase
		get() = database

	init {
		runBlocking {
			db.connect(MyConnector())
		}
	}

	inner class MyConnector : PowerSyncBackendConnector() {
		private val powerSyncEndpoint = "https://6711c44725a60ad3b2df2ae6.powersync.journeyapps.com"

		override suspend fun fetchCredentials(): PowerSyncCredentials {
			return PowerSyncCredentials(
				powerSyncEndpoint,
				getToken.access,
				getUser.uid
			)
		}

		override suspend fun uploadData(database: PowerSyncDatabase) {
			val transaction = database.getNextCrudTransaction() ?: return
			var lastEntry: CrudEntry? = null

			try {
				for (entry in transaction.crud) {
					lastEntry = entry

					when (entry.op) {
						UpdateType.PUT -> {
							Log.d(TAG, "${entry.op}\nid: ${entry.id}\ntable: ${entry.table}\ndata: ${entry.opData}")
							upsert(entry)
						}

						UpdateType.PATCH -> {
							Log.d(TAG, "${entry.op}\ntable: ${entry.table}\nid: ${entry.id}/data: ${entry.opData}")
							update(entry.table, entry.id, entry.opData)
						}

						UpdateType.DELETE -> {
							Log.d(TAG, "${entry.op}\ntable: ${entry.table}\nid: ${entry.id}")
							delete(entry.table, entry.id)
						}
					}
				}

				transaction.complete(null)
			}catch(e: Exception) {
				println("Data upload error - retrying last entry: ${lastEntry!!}, $e")
				throw e
			}
		}
	}

	suspend fun getAllFood(): List<Food> {
		return database.getAll("SELECT * FROM foods", mapper = { cursor ->
			Food(
				id = cursor.getString(0)!!,
				name = cursor.getString(1)!!,
				calorie = cursor.getString(2)!!.toInt(),
				carbohydrate = cursor.getDouble(3)!!,
				protein = cursor.getDouble(4)!!,
				fat = cursor.getDouble(5)!!,
				volume = cursor.getString(8)!!.toInt(),
				volumeUnit = cursor.getString(9)!!
			)}
		)
	}

	suspend fun getFood(data: String): Food {
		return database.get("SELECT * FROM foods where name='$data'", mapper = { cursor ->
			Food(
				id = cursor.getString(0)!!,
				name = cursor.getString(1)!!,
				calorie = cursor.getString(2)!!.toInt(),
				carbohydrate = cursor.getDouble(3)!!,
				protein = cursor.getDouble(4)!!,
				fat = cursor.getDouble(5)!!,
				volume = cursor.getString(8)!!.toInt(),
				volumeUnit = cursor.getString(9)!!
			)}
		)
	}

	suspend fun getFoodOrder(): List<Food> {
		return database.getAll(sql = "SELECT * FROM foods ORDER BY created_at DESC", mapper = { cursor ->
			Food(
				id = cursor.getString(0)!!,
				name = cursor.getString(1)!!,
				calorie = cursor.getString(2)!!.toInt(),
				carbohydrate = cursor.getDouble(3)!!,
				protein = cursor.getDouble(4)!!,
				fat = cursor.getDouble(5)!!,
				volume = cursor.getString(8)!!.toInt(),
				volumeUnit = cursor.getString(9)!!
			)
		})
	}

	suspend fun getDiets(mealTime: String, date: String): List<Food> {
		return database.getAll(sql = "SELECT * FROM diets WHERE meal_time = '$mealTime' AND strftime('%Y-%m-%d', date) = '$date'", mapper = { cursor ->
			Food(
				id = cursor.getString(0)!!,
				mealTime = cursor.getString(1)!!,
				name = cursor.getString(2)!!,
				calorie = if(cursor.getString(3) == null) 0 else cursor.getString(3)!!.toInt(),
				carbohydrate = if(cursor.getDouble(4) == null) 0.0 else cursor.getDouble(4)!!,
				protein = if(cursor.getDouble(5) == null) 0.0 else cursor.getDouble(5)!!,
				fat = if(cursor.getDouble(6) == null) 0.0 else cursor.getDouble(6)!!,
				volume = if(cursor.getString(9) == null) 0 else cursor.getString(9)!!.toInt(),
				volumeUnit = cursor.getString(10)!!
			)}
		)
	}

	suspend fun getWater(data: String): Water {
		return database.getOptional(sql = "SELECT * FROM water WHERE date = '$data'", mapper = { cursor ->
			Water(
				id = cursor.getString(0)!!,
				mL = cursor.getString(1)!!.toInt(),
				count = cursor.getString(2)!!.toInt(),
				date = cursor.getString(3)!!
			)}
		) ?: Water()
	}

	suspend fun getMedicine(column: String, data: String): Medicine {
		return database.getOptional(sql = "SELECT * FROM medicines WHERE $column = '$data'", mapper = { cursor ->
			Medicine(
				id = cursor.getString(0)!!,
				category = cursor.getString(1)!!,
				name = cursor.getString(2)!!,
				amount = if(cursor.getString(3) == null) 0 else cursor.getString(3)!!.toInt(),
				unit = cursor.getString(4)!!,
				starts = cursor.getString(5)!!,
				ends = cursor.getString(6)!!
			)}
		) ?: Medicine()
	}

	suspend fun getMedicine(data: String): List<Medicine> {
		return database.getAll(sql = "SELECT * FROM medicines WHERE '$data' BETWEEN substr(starts, 1, 10) AND substr(ends, 1, 10)", mapper = { cursor ->
			Medicine(
				id = cursor.getString(0)!!,
				category = cursor.getString(1)!!,
				name = cursor.getString(2)!!,
				amount = if(cursor.getString(3) == null) 0 else cursor.getString(3)!!.toInt(),
				unit = cursor.getString(4)!!,
				starts = cursor.getString(5)!!,
				ends = cursor.getString(6)!!
			)}
		)
	}

	suspend fun getMedicineTime(column: String, data: String): List<MedicineTime> {
		return database.getAll(sql = "SELECT * FROM medicine_times WHERE $column = '$data'", mapper = { cursor ->
			MedicineTime(
				id = cursor.getString(0)!!,
				time = cursor.getString(1)!!,
				medicineId = cursor.getString(5)!!
			)}
		)
	}

	suspend fun getMedicineIntake(data1: String, data2: String): MedicineIntake {
		return database.getOptional(sql = "SELECT id, category, name, amount, unit, intaked_at FROM medicine_intakes " +
			"WHERE intaked_at = '$data1' AND medicine_time_id = '$data2'", mapper = { cursor ->
			MedicineIntake(
				id = cursor.getString(0)!!,
				category = cursor.getString(1)!!,
				name = cursor.getString(2)!!,
				amount = cursor.getString(3)!!.toInt(),
				unit = cursor.getString(4)!!,
				intakeAt = cursor.getString(5)!!,
				medicineTimeId = cursor.getString(6)!!
			)
		}) ?: MedicineIntake()
	}

	suspend fun getMedicineIntakeCount(date: String): Int {
		return database.getOptional(sql = "SELECT count(*) FROM medicine_intakes WHERE intaked_at = '$date'", mapper = { cursor ->
			cursor.getString(0)!!.toInt()
		}) ?: 0
	}

	suspend fun getCount(): Int {
		return database.getOptional(sql = "SELECT count(*) FROM medicines", mapper = { cursor ->
			cursor.getString(0)!!.toInt()
		}) ?: 0
	}

	suspend fun getGoal(date: String): Goal {
		return database.getOptional(sql = "SELECT * FROM goals WHERE strftime('%Y-%m-%d', date) = '$date'", mapper = { cursor ->
			Goal(
				id = cursor.getString(0)!!,
				weight = if(cursor.getDouble(1) == null) 0.0 else cursor.getDouble(1)!!,
				kcalOfDiet = if(cursor.getDouble(2) == null) 0 else cursor.getString(2)!!.toInt(),
				kcalOfWorkout = if(cursor.getDouble(3) == null) 0 else cursor.getString(3)!!.toInt(),
				waterAmountOfCup = if(cursor.getDouble(4) == null) 0 else cursor.getString(4)!!.toInt(),
				waterIntake = if(cursor.getDouble(5) == null) 0 else cursor.getString(5)!!.toInt(),
				sleep = if(cursor.getDouble(6) == null) 0 else cursor.getString(6)!!.toInt(),
				medicineIntake = if(cursor.getDouble(7) == null) 0 else cursor.getString(7)!!.toInt(),
				date = cursor.getString(8)!!
			)}
		) ?: Goal()
	}

	suspend fun getData(table: String, column: String, data: String): Food {
		return database.getOptional(sql = "SELECT id, name FROM $table WHERE $column = '$data' limit 1", mapper = { cursor ->
			Food(
				id = cursor.getString(0)!!,
				name = cursor.getString(1)!!
			)}
		) ?: Food()
	}

	suspend fun insertFood(data: Food) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO foods(id, name, calorie, carbohydrate, protein, fat, quantity, quantity_unit, volume, volume_unit, " +
					"created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				parameters = listOf(data.id, data.name, "${data.calorie}", "${data.carbohydrate}", "${data.protein}", "${data.fat}", "1", "개",
					"${data.volume}", data.volumeUnit, data.createdAt, data.updatedAt)
			)
		}
	}

	suspend fun insertDiet(data: Food) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO diets(id, meal_time, name, calorie, carbohydrate, protein, fat, quantity, quantity_unit, " +
					"volume, volume_unit, date) VALUES (uuid(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				parameters = listOf(data.mealTime, data.name, "${data.calorie}", "${data.carbohydrate}", "${data.protein}", "${data.fat}",
					"1", "개", "${data.volume}", data.volumeUnit, data.date)
			)
		}
	}

	suspend fun insertWater(data: Water) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO water(id, mL, count, date) VALUES (uuid(), ?, ?, ?)",
				parameters = listOf("${data.mL}", "${data.count}", data.date)
			)
		}
	}

	suspend fun insertMedicine(data: Medicine) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO medicines(id, category, name, amount, unit, starts, ends) VALUES (uuid(), ?, ?, ?, ?, ?, ?)",
				parameters = listOf(data.category, data.name, "${data.amount}", data.unit, data.starts, data.ends)
			)
		}
	}

	suspend fun insertMedicineTime(data: MedicineTime) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO medicine_times(id, time, medicine_id) VALUES (uuid(), ?, ?)",
				parameters = listOf(data.time, data.medicineId)
			)
		}
	}

	suspend fun insertMedicineIntake(data: MedicineIntake) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO medicine_intakes(id, intaked_at, medicine_time_id, source_id) VALUES (uuid(), ?, ?, ?)",
				parameters = listOf(data.intakeAt, data.medicineTimeId, data.sourceId)
			)
		}
	}

	suspend fun insertGoal(data: Goal) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO goals(id, weight, kcal_of_diet, kcal_of_workout, water_amount_of_cup, water_intake, " +
					"sleep, medicine_intake, date) VALUES (uuid(), ?, ?, ?, ?, ?, ?, ?, ?)",
				parameters = listOf("${data.weight}", "${data.kcalOfDiet}", "${data.kcalOfWorkout}", "${data.waterAmountOfCup}",
					"${data.waterIntake}", "${data.sleep}", "${data.medicineIntake}", data.date)
			)
		}
	}

	suspend fun updateFood(data: Food) {
		database.execute(
			sql = "UPDATE foods SET calorie = ?, carbohydrate = ?, protein = ?, fat = ?, quantity = ?, volume = ?, volume_unit = ? WHERE id = ?",
			parameters = listOf("${data.calorie}", "${data.carbohydrate}", "${data.protein}", "${data.fat}", "${data.quantity}", "${data.volume}", data.volumeUnit, data.id)
		)
	}

	suspend fun updateDiet(data: Food) {
		database.execute(
			sql = "UPDATE diets SET calorie = ?, carbohydrate = ?, protein = ?, fat = ?, quantity = ?, volume = ? WHERE id = ?",
			parameters = listOf("${data.calorie}", "${data.carbohydrate}", "${data.protein}", "${data.fat}", "${data.quantity}","${data.volume}", data.id)
		)
	}

	suspend fun updateWater(data: Water) {
		database.execute(
			sql = "UPDATE water SET mL = ?, count = ?, date = ? WHERE id = ?",
			parameters = listOf("${data.mL}", "${data.count}", data.date, data.id)
		)
	}

	suspend fun updateMedicine(data: Medicine) {
		database.execute(
			sql = "UPDATE medicines SET category = ?, name = ?, amount = ?, unit = ?, starts = ?, ends = ? WHERE id = ?",
			parameters = listOf(data.category, data.name, "${data.amount}", data.unit, data.starts, data.ends, data.id)
		)
	}

	suspend fun updateStr(table: String, column: String, data: String, id: String) {
		database.execute(sql = "UPDATE $table SET $column = '$data' WHERE id = '$id'")
	}

	suspend fun updateInt(table: String, column: String, data: Int, id: String) {
		database.execute(sql = "UPDATE $table SET $column = $data WHERE id = '$id'")
	}

	suspend fun deleteItem(table: String, column: String, data: String) {
		database.writeTransaction {
			database.execute(sql = "DELETE FROM $table WHERE $column = '$data'")
		}
	}
}