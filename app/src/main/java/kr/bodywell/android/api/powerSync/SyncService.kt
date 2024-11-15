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
import kr.bodywell.android.model.Activities
import kr.bodywell.android.model.Body
import kr.bodywell.android.model.Food
import kr.bodywell.android.model.Goal
import kr.bodywell.android.model.Item
import kr.bodywell.android.model.Medicine
import kr.bodywell.android.model.MedicineIntake
import kr.bodywell.android.model.MedicineTime
import kr.bodywell.android.model.Sleep
import kr.bodywell.android.model.Water
import kr.bodywell.android.model.Workout
import kr.bodywell.android.util.CustomUtil
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
							Log.d(TAG, "${entry.op}\ntable: ${entry.table}\nid: ${entry.id}\ndata: ${entry.opData}")
							update(entry)
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
		return database.getAll(sql = "SELECT * FROM(SELECT * FROM foods ORDER BY created_at DESC) GROUP BY name", mapper = { cursor ->
			Food(
				id = cursor.getString(0)!!,
				name = cursor.getString(1)!!,
				calorie = cursor.getDouble(2)!!.toInt(),
				carbohydrate = cursor.getDouble(3)!!,
				protein = cursor.getDouble(4)!!,
				fat = cursor.getDouble(5)!!,
				volume = cursor.getDouble(8)!!.toInt(),
				volumeUnit = cursor.getString(9)!!,
				createdAt = cursor.getString(11)!!
			)}
		)
	}

	suspend fun getAllFoodOrder(): List<Food> {
		return database.getAll(sql = "SELECT * FROM(SELECT * FROM(SELECT * FROM foods ORDER BY created_at DESC) GROUP BY name)ORDER BY created_at DESC", mapper = { cursor ->
			Food(
				id = cursor.getString(0)!!,
				name = cursor.getString(1)!!,
				calorie = cursor.getDouble(2)!!.toInt(),
				carbohydrate = cursor.getDouble(3)!!,
				protein = cursor.getDouble(4)!!,
				fat = cursor.getDouble(5)!!,
				volume = cursor.getDouble(8)!!.toInt(),
				volumeUnit = cursor.getString(9)!!,
				createdAt = cursor.getString(11)!!
			)
		})
	}

	suspend fun getDiet(data1: String, data2: String): Food {
		return database.getOptional(sql = "SELECT id, name FROM diets WHERE name = '$data1' AND strftime('%Y-%m-%d', date) = '$data2'", mapper = { cursor ->
			Food(
				id = cursor.getString(0)!!,
				name = cursor.getString(1)!!
			)}
		) ?: Food()
	}

	suspend fun getAllDiet(mealTime: String, date: String): List<Food> {
		return database.getAll(sql = "SELECT * FROM(SELECT * FROM diets WHERE meal_time = '$mealTime' AND strftime('%Y-%m-%d', date) = '$date' " +
			"ORDER BY created_at DESC) GROUP BY name", mapper = { cursor ->
			Food(
				id = cursor.getString(0)!!,
				mealTime = cursor.getString(1)!!,
				name = cursor.getString(2)!!,
				calorie = if(cursor.getString(3) == null) 0 else cursor.getDouble(3)!!.toInt(),
				carbohydrate = if(cursor.getDouble(4) == null) 0.0 else cursor.getDouble(4)!!,
				protein = if(cursor.getDouble(5) == null) 0.0 else cursor.getDouble(5)!!,
				fat = if(cursor.getDouble(6) == null) 0.0 else cursor.getDouble(6)!!,
				quantity = if(cursor.getString(7) == null) 0 else cursor.getDouble(7)!!.toInt(),
				volume = if(cursor.getString(9) == null) 0 else cursor.getDouble(9)!!.toInt(),
				volumeUnit = cursor.getString(10)!!,
				createdAt = cursor.getString(13)!!
			)}
		)
	}

	suspend fun getWater(data: String): Water {
		return database.getOptional(sql = "SELECT * FROM water WHERE date = '$data' order by created_at desc limit 1", mapper = { cursor ->
			Water(
				id = cursor.getString(0)!!,
				mL = cursor.getDouble(1)!!.toInt(),
				count = cursor.getDouble(2)!!.toInt(),
				date = cursor.getString(3)!!,
				createdAt = cursor.getString(4)!!
			)}
		) ?: Water()
	}

	suspend fun getAllActivity(): List<Activities> {
		return database.getAll(sql = "SELECT * FROM(SELECT id, name, created_at FROM activities ORDER BY created_at DESC) GROUP BY name", mapper = { cursor ->
			Activities(
				id = cursor.getString(0)!!,
				name = cursor.getString(1)!!,
				createdAt = cursor.getString(2)!!
			)}
		)
	}

	suspend fun getAllActivityOrder(): List<Activities> {
		return database.getAll(sql = "SELECT * FROM(SELECT * FROM(SELECT id, name, created_at FROM activities ORDER BY created_at DESC) GROUP BY name)ORDER BY created_at DESC", mapper = { cursor ->
			Activities(
				id = cursor.getString(0)!!,
				name = cursor.getString(1)!!,
				createdAt = cursor.getString(2)!!
			)}
		)
	}

	suspend fun getActivity(data: String): Activities {
		return database.getOptional(sql = "SELECT id, name, created_at FROM activities where id = '$data'", mapper = { cursor ->
			Activities(
				id = cursor.getString(0)!!,
				name = cursor.getString(1)!!,
				createdAt = cursor.getString(2)!!
			)}
		) ?: Activities()
	}

	suspend fun getAllWorkout(data: String): List<Workout> {
		return database.getAll(sql = "SELECT * FROM workouts WHERE strftime('%Y-%m-%d', date) = '$data'", mapper = { cursor ->
			Workout(
				id = cursor.getString(0)!!,
				name = cursor.getString(1)!!,
				calorie = cursor.getDouble(2)!!.toInt(),
				intensity = cursor.getString(3)!!,
				time = cursor.getDouble(4)!!.toInt(),
				date = cursor.getString(5)!!,
				createdAt = cursor.getString(6)!!,
				updatedAt = cursor.getString(7)!!,
				activityId = cursor.getString(9)!!
			)}
		)
	}

	suspend fun getBody(data: String): Body {
		return database.getOptional(sql = "SELECT * FROM body_measurements WHERE strftime('%Y-%m-%d', time) = '$data' ORDER BY created_at DESC LIMIT 1", mapper = { cursor ->
			Body(
				id = cursor.getString(0)!!,
				height = if(cursor.getDouble(1) == null) 0.0 else cursor.getDouble(1)!!,
				weight = if(cursor.getDouble(2) == null) 0.0 else cursor.getDouble(2)!!,
				bodyMassIndex = if(cursor.getDouble(3) == null) 0.0 else cursor.getDouble(3)!!,
				bodyFatPercentage = if(cursor.getDouble(4) == null) 0.0 else cursor.getDouble(4)!!,
				skeletalMuscleMass = if(cursor.getDouble(5) == null) 0.0 else cursor.getDouble(5)!!,
				basalMetabolicRate = if(cursor.getDouble(6) == null) 0.0 else cursor.getDouble(6)!!,
				workoutIntensity = if(cursor.getString(7) == null) 0 else cursor.getString(7)!!.toInt(),
				time = cursor.getString(8)!!,
				createdAt = cursor.getString(9)!!,
				updatedAt = cursor.getString(10)!!
			)}
		) ?: Body()
	}

	suspend fun getSleep(data: String): Sleep {
		return database.getOptional(sql = "SELECT * FROM sleep WHERE strftime('%Y-%m-%d', starts) = '$data' ORDER BY created_at DESC LIMIT 1", mapper = { cursor ->
			Sleep(
				id = cursor.getString(0)!!,
				starts = cursor.getString(1)!!,
				ends = cursor.getString(2)!!,
				createdAt = cursor.getString(3)!!,
				updatedAt = cursor.getString(4)!!
			)}
		) ?: Sleep()
	}

	suspend fun getMedicine(column: String, data: String): Medicine {
		return database.getOptional(sql = "SELECT * FROM medicines WHERE $column = '$data'", mapper = { cursor ->
			Medicine(
				id = cursor.getString(0)!!,
				category = cursor.getString(1)!!,
				name = cursor.getString(2)!!,
				amount = if(cursor.getDouble(3) == null) 0 else cursor.getDouble(3)!!.toInt(),
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
				amount = if(cursor.getDouble(3) == null) 0 else cursor.getDouble(3)!!.toInt(),
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
				time = cursor.getString(1)!!
			)}
		)
	}

	suspend fun getMedicineIntake(data1: String, data2: String): MedicineIntake {
		return database.getOptional(sql = "SELECT id, category, name, amount, unit, intaked_at FROM medicine_intakes WHERE intaked_at = '$data1' " +
			"AND medicine_time_id = '$data2'", mapper = { cursor ->
			MedicineIntake(
				id = cursor.getString(0)!!,
				category = cursor.getString(1)!!,
				name = cursor.getString(2)!!,
				amount = cursor.getDouble(3)!!.toInt(),
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

	suspend fun getMedicine2(column: String): Item {
		return database.getOptional(sql = "SELECT", mapper = { cursor ->
			Item(
				string1 = cursor.getString(0)!!,
				string2 = cursor.getString(1)!!
			)}
		) ?: Item()
	}

	suspend fun getGoal(date: String): Goal {
		return database.getOptional(sql = "SELECT * FROM goals WHERE strftime('%Y-%m-%d', date) = '$date'", mapper = { cursor ->
			Goal(
				id = cursor.getString(0)!!,
				weight = if(cursor.getDouble(1) == null) 0.0 else cursor.getDouble(1)!!,
				kcalOfDiet = if(cursor.getDouble(2) == null) 0 else cursor.getDouble(2)!!.toInt(),
				kcalOfWorkout = if(cursor.getDouble(3) == null) 0 else cursor.getDouble(3)!!.toInt(),
				waterAmountOfCup = if(cursor.getDouble(4) == null) 0 else cursor.getDouble(4)!!.toInt(),
				waterIntake = if(cursor.getDouble(5) == null) 0 else cursor.getDouble(5)!!.toInt(),
				sleep = if(cursor.getDouble(6) == null) 0 else cursor.getDouble(6)!!.toInt(),
				medicineIntake = if(cursor.getDouble(7) == null) 0 else cursor.getDouble(7)!!.toInt(),
				date = cursor.getString(8)!!
			)}
		) ?: Goal()
	}

	suspend fun getCount(): Int {
		return database.getOptional(sql = "SELECT count(*) FROM medicines", mapper = { cursor ->
			cursor.getString(0)!!.toInt()
		}) ?: 0
	}

	suspend fun getData(table: String, column1: String, column2: String, data: String): Item {
		return database.getOptional(sql = "SELECT id, $column1 FROM $table WHERE $column2 = '$data'", mapper = { cursor ->
			Item(
				string1 = cursor.getString(0)!!,
				string2 = cursor.getString(1)!!
			)}
		) ?: Item()
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
					"volume, volume_unit, date, created_at, updated_at, food_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				parameters = listOf(data.id, data.mealTime, data.name, "${data.calorie}", "${data.carbohydrate}", "${data.protein}", "${data.fat}",
					"1", "개", "${data.volume}", data.volumeUnit, data.date, data.createdAt, data.updatedAt, data.foodId)
			)
		}
	}

	suspend fun insertWater(data: Water) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO water(id, mL, count, date, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)",
				parameters = listOf(data.id, "${data.mL}", "${data.count}", data.date, data.createdAt, data.updatedAt)
			)
		}
	}

	suspend fun insertActivity(data: Activities) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO activities(id, name, created_at, updated_at) VALUES (?, ?, ?, ?)",
				parameters = listOf(data.id, data.name, data.createdAt, data.updatedAt)
			)
		}
	}

	suspend fun insertWorkout(data: Workout) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO workouts(id, name, calorie, intensity, time, date, created_at, updated_at, activity_id) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
				parameters = listOf(data.id, data.name, "${data.calorie}", data.intensity, "${data.time}", data.date, data.createdAt, data.updatedAt, data.activityId)
			)
		}
	}

	suspend fun insertBody(data: Body) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO body_measurements(id, height, weight, body_mass_index, body_fat_percentage, skeletal_muscle_mass, " +
					"basal_metabolic_rate, workout_intensity, time, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				parameters = listOf(data.id, "${data.height}", "${data.weight}", "${data.bodyMassIndex}", "${data.bodyFatPercentage}", "${data.skeletalMuscleMass}",
					"${data.basalMetabolicRate}", "${data.workoutIntensity}", data.time, data.createdAt, data.updatedAt)
			)
		}
	}

	suspend fun insertSleep(data: Sleep) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO sleep(id, starts, ends, created_at, updated_at) VALUES (?, ?, ?, ?, ?)",
				parameters = listOf(data.id, data.starts, data.ends, data.createdAt, data.updatedAt)
			)
		}
	}

	suspend fun insertMedicine(data: Medicine) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO medicines(id, category, name, amount, unit, starts, ends, created_at, updated_at) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
				parameters = listOf(data.id, data.category, data.name, "${data.amount}", data.unit, data.starts, data.ends, data.createdAt, data.updatedAt)
			)
		}
	}

	suspend fun insertMedicineTime(data: MedicineTime) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO medicine_times(id, time, created_at, updated_at, medicine_id) VALUES (uuid(), ?, ?, ?, ?)",
				parameters = listOf(data.time, data.createdAt, data.updatedAt, data.medicineId)
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
			sql = "UPDATE diets SET name = ?, quantity = ?, date = ? WHERE id = ?",
			parameters = listOf(data.name, "${data.quantity}", data.date, data.id)
		)
	}

	suspend fun updateWater(data: Water) {
		database.execute(
			sql = "UPDATE water SET mL = ?, count = ?, date = ? WHERE id = ?",
			parameters = listOf("${data.mL}", "${data.count}", data.date, data.id)
		)
	}

	suspend fun updateWorkout(data: Workout) {
		database.execute(
			sql = "UPDATE workouts SET calorie = ?, intensity = ?, time = ? WHERE id = ?",
			parameters = listOf("${data.calorie}", data.intensity, "${data.time}", data.id)
		)
	}

	suspend fun updateBody(data: Body) {
		database.execute(
			sql = "UPDATE body_measurements SET height = ?, weight = ?, body_mass_index = ?, body_fat_percentage = ?, skeletal_muscle_mass = ?, " +
				"basal_metabolic_rate = ?, workout_intensity = ? WHERE id = ?",
			parameters = listOf("${data.height}", "${data.weight}", "${data.bodyMassIndex}", "${data.bodyFatPercentage}", "${data.skeletalMuscleMass}",
				"${data.basalMetabolicRate}", "${data.workoutIntensity}", data.id)
		)
	}

	suspend fun updateSleep(data: Sleep) {
		database.execute(
			sql = "UPDATE sleep SET starts = ?, ends = ? WHERE id = ?",
			parameters = listOf(data.starts, data.ends, data.id)
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

	suspend fun deleteDuplicates(table: String, column: String, data1: String, data2: String) {
		database.writeTransaction {
			database.execute(sql = "DELETE FROM $table WHERE $column = '$data1' and id <> '$data2'")
		}
	}
}