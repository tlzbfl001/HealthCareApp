package kr.bodywell.android.api.powerSync

import android.content.Context
import android.util.Log
import com.powersync.DatabaseDriverFactory
import com.powersync.PowerSyncDatabase
import com.powersync.connectors.PowerSyncBackendConnector
import com.powersync.connectors.PowerSyncCredentials
import com.powersync.db.crud.CrudEntry
import com.powersync.db.crud.UpdateType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kr.bodywell.android.api.powerSync.SyncController.delete
import kr.bodywell.android.api.powerSync.SyncController.upsert
import kr.bodywell.android.api.powerSync.SyncController.update
import kr.bodywell.android.model.ActivityData
import kr.bodywell.android.model.Body
import kr.bodywell.android.model.Constant.ACTIVITIES
import kr.bodywell.android.model.Constant.ACTIVITY_USAGES
import kr.bodywell.android.model.Constant.BODY_MEASUREMENTS
import kr.bodywell.android.model.Constant.DIETS
import kr.bodywell.android.model.Constant.FILES
import kr.bodywell.android.model.Constant.FOODS
import kr.bodywell.android.model.Constant.FOOD_USAGES
import kr.bodywell.android.model.Constant.GOALS
import kr.bodywell.android.model.Constant.MEDICINES
import kr.bodywell.android.model.Constant.MEDICINE_INTAKES
import kr.bodywell.android.model.Constant.MEDICINE_TIMES
import kr.bodywell.android.model.Constant.NOTES
import kr.bodywell.android.model.Constant.PROFILES
import kr.bodywell.android.model.Constant.SLEEP
import kr.bodywell.android.model.Constant.WATER
import kr.bodywell.android.model.Constant.WORKOUTS
import kr.bodywell.android.model.FileItem
import kr.bodywell.android.model.Food
import kr.bodywell.android.model.Goal
import kr.bodywell.android.model.Medicine
import kr.bodywell.android.model.MedicineIntake
import kr.bodywell.android.model.MedicineTime
import kr.bodywell.android.model.Note
import kr.bodywell.android.model.Profile
import kr.bodywell.android.model.Sleep
import kr.bodywell.android.model.Water
import kr.bodywell.android.model.Workout
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
//		private val powerSyncEndpoint = "https://6711c44725a60ad3b2df2ae6.powersync.journeyapps.com" // development
		private val powerSyncEndpoint = "https://677f6f80e88075f9091b99d3.powersync.journeyapps.com" // production

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

					when(entry.op) {
						UpdateType.PUT -> {
							upsert(_context, entry)
						}

						UpdateType.PATCH -> {
							update(entry)
						}

						UpdateType.DELETE -> {
							delete(entry.table, entry.id)
						}
					}

					Log.d(TAG, "${entry.op}\n${entry.id}\n${entry.opData}")
				}

				transaction.complete(null)
			}catch(e: Exception) {
				Log.e(TAG, "powerSync err - retrying last entry: ${lastEntry!!}, $e")
				throw e
			}
		}
	}

	fun watchMedicine1(): Flow<List<String>> {
		return database.watch("SELECT id FROM $MEDICINES WHERE user_id = '${getUser.uid}'", mapper = { cursor ->
			if(cursor.getString(0) == null) "" else cursor.getString(0)!!
		})
	}

	fun watchMedicine2(data: String): Flow<List<Medicine>> {
		return database.watch("SELECT id, category, name, amount, unit, strftime('%Y-%m-%d', starts), strftime('%Y-%m-%d', ends), updated_at " +
			"FROM $MEDICINES WHERE user_id = '${getUser.uid}' AND updated_at > '$data'", mapper = { cursor ->
			Medicine(
				id = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				category = if(cursor.getString(1) == null) "" else cursor.getString(1)!!,
				name = if(cursor.getString(2) == null) "" else cursor.getString(2)!!,
				amount = if(cursor.getDouble(3) == null) 0 else cursor.getDouble(3)!!.toInt(),
				unit = if(cursor.getString(4) == null) "" else cursor.getString(4)!!,
				starts = if(cursor.getString(5) == null) "" else cursor.getString(5)!!,
				ends = if(cursor.getString(6) == null) "" else cursor.getString(6)!!,
				updatedAt = if(cursor.getString(7) == null) "" else cursor.getString(7)!!
			)
		})
	}

	suspend fun getProfile(): Profile {
		return database.getOptional(sql = "SELECT id, name, birth, height, weight, gender FROM $PROFILES WHERE user_id = '${getUser.uid}'", mapper = { cursor ->
			Profile(
				id = if(cursor.getString(1) == null) "" else cursor.getString(0)!!,
				name = if(cursor.getString(1) == null) "" else cursor.getString(1)!!,
				birth = if(cursor.getString(2) == null) "" else cursor.getString(2)!!,
				height = if(cursor.getString(3) == null) 0.0 else cursor.getString(3)!!.toDouble(),
				weight = if(cursor.getString(4) == null) 0.0 else cursor.getString(4)!!.toDouble(),
				gender = if(cursor.getString(5) == null) "" else cursor.getString(5)!!
			)}
		) ?: Profile()
	}

	suspend fun getFood(data: String): Food {
		return database.getOptional(sql = "SELECT name, calorie, carbohydrate, protein, fat, quantity, volume, volume_unit " +
			"FROM $FOODS WHERE user_id = '${getUser.uid}' AND id = '$data'", mapper = { cursor ->
			Food(
				name = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				calorie = if(cursor.getString(1) == null) 0 else cursor.getDouble(1)!!.toInt(),
				carbohydrate = if(cursor.getDouble(2) == null) 0.0 else cursor.getDouble(2)!!,
				protein = if(cursor.getDouble(3) == null) 0.0 else cursor.getDouble(3)!!,
				fat = if(cursor.getDouble(4) == null) 0.0 else cursor.getDouble(4)!!,
				quantity = if(cursor.getString(5) == null) 0 else cursor.getDouble(5)!!.toInt(),
				volume = if(cursor.getString(6) == null) 0 else cursor.getDouble(6)!!.toInt(),
				volumeUnit = if(cursor.getString(7) == null) "" else cursor.getString(7)!!
			)}
		) ?: Food()
	}

	suspend fun getFoods(): List<Food> {
		return database.getAll(sql = "SELECT * FROM(SELECT id, name, calorie, carbohydrate, protein, fat, quantity_unit, volume, volume_unit, created_at " +
			"FROM $FOODS WHERE user_id = '${getUser.uid}') GROUP BY name ORDER BY created_at DESC", mapper = { cursor ->
			Food(
				id = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				name = if(cursor.getString(1) == null) "" else cursor.getString(1)!!,
				calorie = if(cursor.getString(2) == null) 0 else cursor.getDouble(2)!!.toInt(),
				carbohydrate = if(cursor.getDouble(3) == null) 0.0 else cursor.getDouble(3)!!,
				protein = if(cursor.getDouble(4) == null) 0.0 else cursor.getDouble(4)!!,
				fat = if(cursor.getDouble(5) == null) 0.0 else cursor.getDouble(5)!!,
				quantityUnit = if(cursor.getString(6) == null) "" else cursor.getString(6)!!,
				volume = if(cursor.getString(7) == null) 0 else cursor.getDouble(7)!!.toInt(),
				volumeUnit = if(cursor.getString(8) == null) "" else cursor.getString(8)!!,
				createdAt = if(cursor.getString(9) == null) "" else cursor.getString(9)!!
			)}
		)
	}

	suspend fun getFoodUsages1(): List<Food> {
		return database.getAll(sql = "SELECT * FROM (SELECT a.id, name, calorie, carbohydrate, protein, fat, quantity_unit, volume, volume_unit, " +
			"usage_count FROM $FOODS a, $FOOD_USAGES b WHERE a.user_id = '${getUser.uid}' AND a.id = b.food_id) ORDER BY usage_count DESC", mapper = { cursor ->
			Food(
				id = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				name =if(cursor.getString(1) == null) "" else cursor.getString(1)!!,
				calorie = if(cursor.getString(2) == null) 0 else cursor.getDouble(2)!!.toInt(),
				carbohydrate = if(cursor.getDouble(3) == null) 0.0 else cursor.getDouble(3)!!,
				protein = if(cursor.getDouble(4) == null) 0.0 else cursor.getDouble(4)!!,
				fat = if(cursor.getDouble(5) == null) 0.0 else cursor.getDouble(5)!!,
				quantityUnit = if(cursor.getString(6) == null) "" else cursor.getString(6)!!,
				volume = if(cursor.getString(7) == null) 0 else cursor.getDouble(7)!!.toInt(),
				volumeUnit = if(cursor.getString(8) == null) "" else cursor.getString(8)!!
			)
		})
	}

	suspend fun getFoodUsages2(): List<Food> {
		return database.getAll(sql = "SELECT * FROM (SELECT a.id, name, calorie, carbohydrate, protein, fat, quantity_unit, volume, volume_unit, b.updated_at " +
			"FROM $FOODS a, $FOOD_USAGES b WHERE a.user_id = '${getUser.uid}' AND a.id = b.food_id) ORDER BY updated_at DESC", mapper = { cursor ->
			Food(
				id = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				name = if(cursor.getString(1) == null) "" else cursor.getString(1)!!,
				calorie = if(cursor.getString(2) == null) 0 else cursor.getDouble(2)!!.toInt(),
				carbohydrate = if(cursor.getDouble(3) == null) 0.0 else cursor.getDouble(3)!!,
				protein = if(cursor.getDouble(4) == null) 0.0 else cursor.getDouble(4)!!,
				fat = if(cursor.getDouble(5) == null) 0.0 else cursor.getDouble(5)!!,
				quantityUnit = if(cursor.getString(6) == null) "" else cursor.getString(6)!!,
				volume = if(cursor.getString(7) == null) 0 else cursor.getDouble(7)!!.toInt(),
				volumeUnit = if(cursor.getString(8) == null) "" else cursor.getString(8)!!
			)}
		)
	}

	suspend fun getDiet(mealTime: String, name: String, date: String): Food {
		return database.getOptional(sql = "SELECT id, name FROM $DIETS WHERE user_id = '${getUser.uid}' AND meal_time = '$mealTime' " +
			"AND name = '$name' AND strftime('%Y-%m-%d', date) = '$date'", mapper = { cursor ->
			Food(
				id = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				name = if(cursor.getString(1) == null) "" else cursor.getString(1)!!
			)}
		) ?: Food()
	}

	suspend fun getDiets(mealTime: String, date: String): List<Food> {
		return database.getAll(sql = "SELECT * FROM(SELECT * FROM $DIETS WHERE user_id = '${getUser.uid}' AND meal_time = '$mealTime' " +
			"AND strftime('%Y-%m-%d', date) = '$date' ORDER BY created_at DESC) GROUP BY name",mapper = { cursor ->
			Food(
				id = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				mealTime = if(cursor.getString(1) == null) "" else cursor.getString(1)!!,
				name = if(cursor.getString(2) == null) "" else cursor.getString(2)!!,
				calorie = if(cursor.getString(3) == null) 0 else cursor.getDouble(3)!!.toInt(),
				carbohydrate = if(cursor.getDouble(4) == null) 0.0 else cursor.getDouble(4)!!,
				protein = if(cursor.getDouble(5) == null) 0.0 else cursor.getDouble(5)!!,
				fat = if(cursor.getDouble(6) == null) 0.0 else cursor.getDouble(6)!!,
				quantity = if(cursor.getString(7) == null) 0 else cursor.getDouble(7)!!.toInt(),
				volume = if(cursor.getString(9) == null) 0 else cursor.getDouble(9)!!.toInt(),
				volumeUnit = if(cursor.getString(10) == null) "" else cursor.getString(10)!!,
				createdAt = if(cursor.getString(13) == null) "" else cursor.getString(13)!!
			)}
		)
	}

	suspend fun getDietIds(date: String): List<String> {
		return database.getAll(sql = "SELECT id, name, date, created_at FROM(SELECT id, name, date, created_at FROM $DIETS WHERE user_id = '${getUser.uid}' " +
			"AND strftime('%Y-%m-%d', date) = '$date') ORDER BY created_at DESC",mapper = { cursor ->
			if(cursor.getString(0) == null) "" else cursor.getString(0)!!
		})
	}

	suspend fun getWater(data: String): Water {
		return database.getOptional(sql = "SELECT * FROM $WATER WHERE user_id = '${getUser.uid}' AND date = '$data' ORDER BY created_at DESC LIMIT 1", mapper = { cursor ->
			Water(
				id = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				mL = if(cursor.getString(1) == null) 0 else cursor.getString(1)!!.toInt(),
				count = if(cursor.getString(2) == null) 0 else cursor.getString(2)!!.toInt(),
				date = if(cursor.getString(3) == null) "" else cursor.getString(3)!!,
				createdAt = if(cursor.getString(4) == null) "" else cursor.getString(4)!!
			)}
		) ?: Water()
	}

	suspend fun getAllWater(start: String, end: String): List<Water> {
		return database.getAll(sql = "SELECT * FROM $WATER WHERE user_id = '${getUser.uid}' AND date BETWEEN '$start' AND '$end' ORDER BY date", mapper = { cursor ->
			Water(
				id = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				mL = if(cursor.getString(1) == null) 0 else cursor.getString(1)!!.toInt(),
				count = if(cursor.getString(2) == null) 0 else cursor.getString(2)!!.toInt(),
				date = if(cursor.getString(3) == null) "" else cursor.getString(3)!!,
				createdAt = if(cursor.getString(4) == null) "" else cursor.getString(4)!!
			)}
		)
	}

	suspend fun getActivityUsages1(): List<ActivityData> {
		return database.getAll(sql = "SELECT * FROM (SELECT a.id, a.name, register_type, usage_count FROM $ACTIVITIES a, $ACTIVITY_USAGES b " +
			"WHERE a.user_id = '${getUser.uid}' AND a.id = b.activity_id) ORDER BY usage_count DESC", mapper = { cursor ->
			ActivityData(
				id = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				name = if(cursor.getString(1) == null) "" else cursor.getString(1)!!,
				registerType = if(cursor.getString(2) == null) "" else cursor.getString(2)!!
			)}
		)
	}

	suspend fun getActivityUsages2(): List<ActivityData> {
		return database.getAll(sql = "SELECT * FROM (SELECT a.id, a.name, register_type, b.updated_at FROM $ACTIVITIES a, $ACTIVITY_USAGES b " +
			"WHERE a.user_id = '${getUser.uid}' AND a.id = b.activity_id) ORDER BY updated_at DESC", mapper = { cursor ->
			ActivityData(
				id = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				name = if(cursor.getString(1) == null) "" else cursor.getString(1)!!,
				registerType = if(cursor.getString(2) == null) "" else cursor.getString(2)!!
			)}
		)
	}

	suspend fun getActivities(): List<ActivityData> {
		return database.getAll(sql = "SELECT * FROM(SELECT id, name, register_type, created_at FROM $ACTIVITIES " +
			"WHERE user_id = '${getUser.uid}') GROUP BY name ORDER BY created_at DESC", mapper = { cursor ->
			ActivityData(
				id = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				name = if(cursor.getString(1) == null) "" else cursor.getString(1)!!,
				registerType = if(cursor.getString(2) == null) "" else cursor.getString(2)!!
			)}
		)
	}

	suspend fun getActivity(data: String): ActivityData {
		return database.getOptional(sql = "SELECT id, name, created_at FROM $ACTIVITIES WHERE user_id = '${getUser.uid}' AND id = '$data'", mapper = { cursor ->
			ActivityData(
				id = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				name = if(cursor.getString(1) == null) "" else cursor.getString(1)!!,
				createdAt = if(cursor.getString(2) == null) "" else cursor.getString(2)!!
			)}
		) ?: ActivityData()
	}

	suspend fun getWorkouts(data: String): List<Workout> {
		return database.getAll(sql = "SELECT * FROM $WORKOUTS WHERE user_id = '${getUser.uid}' AND strftime('%Y-%m-%d', date) = '$data'", mapper = { cursor ->
			Workout(
				id = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				name = if(cursor.getString(1) == null) "" else cursor.getString(1)!!,
				calorie = if(cursor.getString(2) == null) 0 else cursor.getString(2)!!.toInt(),
				intensity = if(cursor.getString(3) == null) "" else cursor.getString(3)!!,
				time = if(cursor.getString(4) == null) 0 else cursor.getString(4)!!.toInt(),
				date = if(cursor.getString(5) == null) "" else cursor.getString(5)!!,
				createdAt = if(cursor.getString(6) == null) "" else cursor.getString(6)!!,
				updatedAt = if(cursor.getString(7) == null) "" else cursor.getString(7)!!,
				activityId = if(cursor.getString(9) == null) "" else cursor.getString(9)!!
			)}
		)
	}

	suspend fun getBody(data: String): Body {
		return database.getOptional(sql = "SELECT * FROM $BODY_MEASUREMENTS WHERE user_id = '${getUser.uid}' AND " +
			"strftime('%Y-%m-%d', time) = '$data' ORDER BY updated_at DESC LIMIT 1", mapper = { cursor ->
			Body(
				id = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				height = if(cursor.getDouble(1) == null) 0.0 else cursor.getDouble(1)!!,
				weight = if(cursor.getDouble(2) == null) 0.0 else cursor.getDouble(2)!!,
				bodyMassIndex = if(cursor.getDouble(3) == null) 0.0 else cursor.getDouble(3)!!,
				bodyFatPercentage = if(cursor.getDouble(4) == null) 0.0 else cursor.getDouble(4)!!,
				skeletalMuscleMass = if(cursor.getDouble(5) == null) 0.0 else cursor.getDouble(5)!!,
				basalMetabolicRate = if(cursor.getDouble(6) == null) 0.0 else cursor.getDouble(6)!!,
				workoutIntensity = if(cursor.getString(7) == null) 0 else cursor.getString(7)!!.toInt(),
				time = if(cursor.getString(8) == null) "" else cursor.getString(8)!!,
				createdAt = if(cursor.getString(9) == null) "" else cursor.getString(9)!!,
				updatedAt = if(cursor.getString(10) == null) "" else cursor.getString(10)!!
			)}
		) ?: Body()
	}

	suspend fun getBodies(start: String, end: String): List<Body> {
		return database.getAll(sql = "SELECT weight, body_mass_index, body_fat_percentage, time FROM $BODY_MEASUREMENTS " +
			"WHERE user_id = '${getUser.uid}' AND time BETWEEN '$start' AND '$end' ORDER BY time", mapper = { cursor ->
			Body(
				weight = if(cursor.getDouble(0) == null) 0.0 else cursor.getDouble(0)!!,
				bodyMassIndex = if(cursor.getDouble(1) == null) 0.0 else cursor.getDouble(1)!!,
				bodyFatPercentage = if(cursor.getDouble(2) == null) 0.0 else cursor.getDouble(2)!!,
				time = if(cursor.getString(3) == null) "" else cursor.getString(3)!!
			)}
		)
	}

	suspend fun getSleep(data: String): Sleep {
		return database.getOptional(sql = "SELECT * FROM $SLEEP WHERE user_id = '${getUser.uid}' AND " +
			"strftime('%Y-%m-%d', created_at) = '$data' ORDER BY created_at DESC LIMIT 1", mapper = { cursor ->
			Sleep(
				id = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				starts = if(cursor.getString(1) == null) "" else cursor.getString(1)!!,
				ends = if(cursor.getString(2) == null) "" else cursor.getString(2)!!,
				createdAt = if(cursor.getString(3) == null) "" else cursor.getString(3)!!,
				updatedAt = if(cursor.getString(4) == null) "" else cursor.getString(4)!!
			)}
		) ?: Sleep()
	}

	suspend fun getMedicines(data: String): List<Medicine> {
		return database.getAll(sql = "SELECT id, category, name, amount, unit, strftime('%Y-%m-%d', starts), strftime('%Y-%m-%d', ends) FROM $MEDICINES " +
			"WHERE user_id = '${getUser.uid}' AND '$data' BETWEEN strftime('%Y-%m-%d', starts) AND strftime('%Y-%m-%d', ends)", mapper = { cursor ->
			Medicine(
				id = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				category = if(cursor.getString(1) == null) "" else cursor.getString(1)!!,
				name = if(cursor.getString(2) == null) "" else cursor.getString(2)!!,
				amount = if(cursor.getDouble(3) == null) 0 else cursor.getDouble(3)!!.toInt(),
				unit = if(cursor.getString(4) == null) "" else cursor.getString(4)!!,
				starts = if(cursor.getString(5) == null) "" else cursor.getString(5)!!,
				ends = if(cursor.getString(6) == null) "" else cursor.getString(6)!!
			)}
		)
	}

	suspend fun getMedicine(data: String): Medicine {
		return database.getOptional(sql = "SELECT id, category, name, amount, unit, strftime('%Y-%m-%d', starts), strftime('%Y-%m-%d', ends) " +
			"FROM $MEDICINES WHERE user_id = '${getUser.uid}' AND id = '$data'", mapper = { cursor ->
			Medicine(
				id = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				category = if(cursor.getString(1) == null) "" else cursor.getString(1)!!,
				name = if(cursor.getString(2) == null) "" else cursor.getString(2)!!,
				amount = if(cursor.getDouble(3) == null) 0 else cursor.getDouble(3)!!.toInt(),
				unit = if(cursor.getString(4) == null) "" else cursor.getString(4)!!,
				starts = if(cursor.getString(5) == null) "" else cursor.getString(5)!!,
				ends = if(cursor.getString(6) == null) "" else cursor.getString(6)!!
			)
		}) ?: Medicine()
	}

	suspend fun getAllMedicineTime(data: String): List<MedicineTime> {
		return database.getAll(sql = "SELECT id, time FROM $MEDICINE_TIMES WHERE user_id = '${getUser.uid}' AND medicine_id = '$data' ORDER BY time", mapper = { cursor ->
			MedicineTime(
				id = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				time = if(cursor.getString(1) == null) "" else cursor.getString(1)!!
			)}
		)
	}

	suspend fun getIntake(data1: String, data2: String): MedicineIntake {
		return database.getOptional(sql = "SELECT id, category, name, amount, unit FROM $MEDICINE_INTAKES " +
			"WHERE user_id = '${getUser.uid}' AND strftime('%Y-%m-%d', intaked_at) = '$data1' AND medicine_time_id = '$data2'", mapper = { cursor ->
			MedicineIntake(
				id = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				category = if(cursor.getString(1) == null) "" else cursor.getString(1)!!,
				name = if(cursor.getString(2) == null) "" else cursor.getString(2)!!,
				amount = if(cursor.getString(3) == null) 0 else cursor.getString(3)!!.toInt(),
				unit = if(cursor.getString(4) == null) "" else cursor.getString(4)!!
			)
		}) ?: MedicineIntake()
	}

	suspend fun getIntakesByDate(data: String): List<String> {
		return database.getAll(sql = "SELECT id FROM $MEDICINE_INTAKES WHERE user_id = '${getUser.uid}' AND strftime('%Y-%m-%d', intaked_at) = '$data'", mapper = { cursor ->
			if(cursor.getString(0) == null) "" else cursor.getString(0)!!
		})
	}

	suspend fun getIntakesById(data: String): List<String> {
		return database.getAll(sql = "SELECT id FROM $MEDICINE_INTAKES WHERE user_id = '${getUser.uid}' AND medicine_id = '$data'", mapper = { cursor ->
			if(cursor.getString(0) == null) "" else cursor.getString(0)!!
		})
	}

	suspend fun getRecentlyIntakes(data: String): List<String> {
		return database.getAll(sql = "SELECT id FROM(SELECT id, medicine_time_id, created_at FROM $MEDICINE_INTAKES WHERE user_id = '${getUser.uid}' " +
			"AND strftime('%Y-%m-%d', intaked_at) = '$data') GROUP BY medicine_time_id ORDER BY created_at DESC", mapper = { cursor ->
			if(cursor.getString(0) == null) "" else cursor.getString(0)!!
		})
	}

	suspend fun getNote(data: String): Note {
		return database.getOptional(sql = "SELECT id, title, content, emotion FROM $NOTES WHERE user_id = '${getUser.uid}' AND date = '$data' limit 1", mapper = { cursor ->
			Note(
				id = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				title = if(cursor.getString(1) == null) "" else cursor.getString(1)!!,
				content = if(cursor.getString(2) == null) "" else cursor.getString(2)!!,
				emotion = if(cursor.getString(3) == null) "" else cursor.getString(3)!!
			)
		}) ?: Note()
	}

	suspend fun getGoal(date: String): Goal {
		return database.getOptional(sql = "SELECT * FROM $GOALS WHERE user_id = '${getUser.uid}' AND " +
			"strftime('%Y-%m-%d', date) = '$date' ORDER BY created_at DESC LIMIT 1", mapper = { cursor ->
			Goal(
				id = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				weight = if(cursor.getDouble(1) == null) 0.0 else cursor.getDouble(1)!!,
				kcalOfDiet = if(cursor.getDouble(2) == null) 0 else cursor.getDouble(2)!!.toInt(),
				kcalOfWorkout = if(cursor.getDouble(3) == null) 0 else cursor.getDouble(3)!!.toInt(),
				waterAmountOfCup = if(cursor.getDouble(4) == null) 0 else cursor.getDouble(4)!!.toInt(),
				waterIntake = if(cursor.getDouble(5) == null) 0 else cursor.getDouble(5)!!.toInt(),
				sleep = if(cursor.getDouble(6) == null) 0 else cursor.getDouble(6)!!.toInt(),
				medicineIntake = if(cursor.getDouble(7) == null) 0 else cursor.getDouble(7)!!.toInt(),
				date = if(cursor.getString(8) == null) "" else cursor.getString(8)!!
			)}
		) ?: Goal()
	}

	suspend fun getFile(data: String): FileItem {
		return database.getOptional(sql = "SELECT id, name FROM $FILES WHERE user_id = '${getUser.uid}' AND profile_id = '$data' ORDER BY created_at DESC limit 1", mapper = { cursor ->
			FileItem(
				id = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				name = if(cursor.getString(1) == null) "" else cursor.getString(1)!!
			)}
		) ?: FileItem()
	}

	suspend fun getFiles(data1: String, data2: String): List<FileItem> {
		return database.getAll(sql = "SELECT id, name, data FROM $FILES WHERE user_id = '${getUser.uid}' AND $data1 = '$data2'", mapper = { cursor ->
			FileItem(
				id = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				name = if(cursor.getString(1) == null) "" else cursor.getString(1)!!,
				data = if(cursor.getString(2) == null) "" else cursor.getString(2)!!
			)}
		)
	}

	suspend fun getWorkoutRanking(data: String): List<Workout> {
		return database.getAll(sql = "SELECT count(name) AS ranking, name FROM $WORKOUTS WHERE user_id = '${getUser.uid}' AND " +
			"strftime('%Y-%m-%d', date) = '$data' GROUP BY name ORDER BY ranking DESC LIMIT 4", mapper = { cursor ->
			Workout(
				id = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				name = if(cursor.getString(1) == null) "" else cursor.getString(1)!!
			)}
		)
	}

	suspend fun getWorkoutRanking(start: String, end: String): List<Workout> {
		return database.getAll(sql = "SELECT count(name) AS ranking, name FROM $WORKOUTS WHERE user_id = '${getUser.uid}' AND " +
			"strftime('%Y-%m-%d', date) BETWEEN '$start' and '$end' GROUP BY name ORDER BY ranking DESC LIMIT 4", mapper = { cursor ->
			Workout(
				id = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				name = if(cursor.getString(1) == null) "" else cursor.getString(1)!!
			)}
		)
	}

	suspend fun getMedicineRanking(data: String): List<Medicine> {
		return database.getAll(sql = "SELECT count(name) AS ranking, name FROM $MEDICINE_INTAKES WHERE user_id = '${getUser.uid}' AND " +
			"strftime('%Y-%m-%d', intaked_at) = '$data' GROUP BY name ORDER BY ranking DESC LIMIT 4", mapper = { cursor ->
			Medicine(
				id = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				name = if(cursor.getString(1) == null) "" else cursor.getString(1)!!
			)}
		)
	}

	suspend fun getMedicineRanking(start: String, end: String): List<Medicine> {
		return database.getAll(sql = "SELECT count(name) AS ranking, name FROM $MEDICINE_INTAKES WHERE user_id = '${getUser.uid}' AND " +
			"strftime('%Y-%m-%d', intaked_at) BETWEEN '$start' AND '$end' GROUP BY name ORDER BY ranking DESC LIMIT 4", mapper = { cursor ->
			Medicine(
				id = if(cursor.getString(0) == null) "" else cursor.getString(0)!!,
				name = if(cursor.getString(1) == null) "" else cursor.getString(1)!!
			)}
		)
	}

	suspend fun getData(table: String, column1: String, column2: String, data: String): String {
		return database.getOptional(sql = "SELECT $column1 FROM $table WHERE user_id = '${getUser.uid}' AND $column2 = '$data' limit 1", mapper = { cursor ->
			if(cursor.getString(0) == null) "" else cursor.getString(0)!!
		}) ?: ""
	}

	suspend fun getDates(table: String, column: String, start: String, end: String): List<String> {
		return database.getAll(sql = "SELECT distinct strftime('%Y-%m-%d', $column) FROM $table WHERE user_id = '${getUser.uid}' AND " +
			"strftime('%Y-%m-%d', $column) BETWEEN '$start' AND '$end' ORDER BY $column", mapper = { cursor ->
			if(cursor.getString(0) == null) "" else cursor.getString(0)!!
		})
	}

	suspend fun insertProfileFile(data: FileItem) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO $FILES(id, name, profile_id) VALUES (?, ?, ?)",
				parameters = listOf(data.id, data.name, data.profileId)
			)
		}
	}

	suspend fun insertFood(data: Food) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO $FOODS(id, name, calorie, carbohydrate, protein, fat, quantity, quantity_unit, volume, volume_unit, " +
					"created_at, updated_at, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				parameters = listOf(data.id, data.name, "${data.calorie}", "${data.carbohydrate}", "${data.protein}", "${data.fat}", "1",
					"개", "${data.volume}", data.volumeUnit, data.createdAt, data.updatedAt, getUser.uid)
			)
		}
	}

	suspend fun insertDiet(data: Food) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO $DIETS(id, meal_time, name, calorie, carbohydrate, protein, fat, quantity, quantity_unit, volume, " +
					"volume_unit, date, created_at, updated_at, food_id, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				parameters = listOf(data.id, data.mealTime, data.name, "${data.calorie}", "${data.carbohydrate}", "${data.protein}", "${data.fat}",
					"1", "개", "${data.volume}", data.volumeUnit, data.date, data.createdAt, data.updatedAt, data.foodId, getUser.uid)
			)
		}
	}

	suspend fun insertDietFile(data: FileItem) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO $FILES(id, name, diet_id) VALUES (?, ?, ?)",
				parameters = listOf(data.id, data.name, data.dietId)
			)
		}
	}

	suspend fun insertWater(data: Water) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO $WATER(id, mL, count, date, created_at, updated_at, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)",
				parameters = listOf(data.id, "${data.mL}", "${data.count}", data.date, data.createdAt, data.updatedAt, getUser.uid)
			)
		}
	}

	suspend fun insertActivity(data: ActivityData) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO $ACTIVITIES(id, name, created_at, updated_at, user_id) VALUES (?, ?, ?, ?, ?)",
				parameters = listOf(data.id, data.name, data.createdAt, data.updatedAt, getUser.uid)
			)
		}
	}

	suspend fun insertWorkout(data: Workout) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO $WORKOUTS(id, name, calorie, intensity, time, date, created_at, updated_at, activity_id, user_id) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				parameters = listOf(data.id, data.name, "${data.calorie}", data.intensity, "${data.time}", data.date, data.createdAt,
					data.updatedAt, data.activityId, getUser.uid)
			)
		}
	}

	suspend fun insertBody(data: Body) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO $BODY_MEASUREMENTS(id, height, weight, body_mass_index, body_fat_percentage, skeletal_muscle_mass, " +
					"basal_metabolic_rate, workout_intensity, time, created_at, updated_at, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				parameters = listOf(data.id, "${data.height}", "${data.weight}", "${data.bodyMassIndex}", "${data.bodyFatPercentage}", "${data.skeletalMuscleMass}",
					"${data.basalMetabolicRate}", "${data.workoutIntensity}", data.time, data.createdAt, data.updatedAt, getUser.uid)
			)
		}
	}

	suspend fun insertSleep(data: Sleep) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO $SLEEP(id, starts, ends, created_at, updated_at, user_id) VALUES (?, ?, ?, ?, ?, ?)",
				parameters = listOf(data.id, data.starts, data.ends, data.createdAt, data.updatedAt, getUser.uid)
			)
		}
	}

	suspend fun insertMedicine(data: Medicine) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO $MEDICINES(id, category, name, amount, unit, starts, ends, created_at, updated_at, user_id) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				parameters = listOf(data.id, data.category, data.name, "${data.amount}", data.unit, data.starts, data.ends,
					data.createdAt, data.updatedAt, getUser.uid)
			)
		}
	}

	suspend fun insertMedicineTime(data: MedicineTime) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO $MEDICINE_TIMES(id, time, created_at, updated_at, medicine_id, user_id) VALUES(?, ?, ?, ?, ?, ?)",
				parameters = listOf(data.id, data.time, data.createdAt, data.updatedAt, data.medicineId, getUser.uid)
			)
		}
	}

	suspend fun insertMedicineIntake(data: MedicineIntake) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO $MEDICINE_INTAKES(id, name, intaked_at, created_at, updated_at, medicine_time_id, user_id) VALUES(?, ?, ?, ?, ?, ?, ?)",
				parameters = listOf(data.id, data.name, data.intakeAt, data.createdAt, data.updatedAt, data.medicineTimeId, getUser.uid)
			)
		}
	}

	suspend fun insertNote(data: Note) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO $NOTES(id, title, content, emotion, date, created_at, updated_at, user_id) VALUES(?, ?, ?, ?, ?, ?, ?, ?)",
				parameters = listOf(data.id, data.title, data.content, data.emotion, data.date, data.createdAt, data.updatedAt, getUser.uid)
			)
		}
	}

	suspend fun insertNoteFile(data: FileItem) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO $FILES(id, name, note_id) VALUES (?, ?, ?)",
				parameters = listOf(data.id, data.name, data.noteId)
			)
		}
	}

	suspend fun insertGoal(data: Goal) {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO $GOALS(id, weight, kcal_of_diet, kcal_of_workout, water_amount_of_cup, water_intake, " +
					"sleep, medicine_intake, date, created_at, updated_at, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				parameters = listOf(data.id, "${data.weight}", "${data.kcalOfDiet}", "${data.kcalOfWorkout}", "${data.waterAmountOfCup}",
					"${data.waterIntake}", "${data.sleep}", "${data.medicineIntake}", data.date, data.createdAt, data.updatedAt, getUser.uid)
			)
		}
	}

	suspend fun updateProfile(data: Profile) {
		database.writeTransaction {
			database.execute(
				sql = "UPDATE $PROFILES SET name = ?, birth = ?, height = ?, weight = ?, gender = ? WHERE user_id = ?",
				parameters = listOf(data.name, data.birth, "${data.height}", "${data.weight}", data.gender, getUser.uid)
			)
		}
	}

	suspend fun updateFood(data: Food) {
		database.execute(
			sql = "UPDATE $FOODS SET calorie = ?, carbohydrate = ?, protein = ?, fat = ?, quantity = ?, volume = ?, volume_unit = ? WHERE id = ?",
			parameters = listOf("${data.calorie}", "${data.carbohydrate}", "${data.protein}", "${data.fat}", "${data.quantity}", "${data.volume}", data.volumeUnit, data.id)
		)
	}

	suspend fun updateDiet(data: Food) {
		database.execute(
			sql = "UPDATE $DIETS SET quantity = ? WHERE id = ?",
			parameters = listOf("${data.quantity}", data.id))
	}

	suspend fun updateWater(data: Water) {
		database.execute(sql = "UPDATE $WATER SET mL = ?, count = ? WHERE id = ?", parameters = listOf("${data.mL}", "${data.count}", data.id))
	}

	suspend fun updateWorkout(data: Workout) {
		database.execute(
			sql = "UPDATE $WORKOUTS SET calorie = ?, intensity = ?, time = ? WHERE id = ?",
			parameters = listOf("${data.calorie}", data.intensity, "${data.time}", data.id)
		)
	}

	suspend fun updateBody(data: Body) {
		database.execute(
			sql = "UPDATE $BODY_MEASUREMENTS SET height = ?, weight = ?, body_mass_index = ?, body_fat_percentage = ?, skeletal_muscle_mass = ?, " +
				"basal_metabolic_rate = ?, workout_intensity = ? WHERE id = ?",
			parameters = listOf("${data.height}", "${data.weight}", "${data.bodyMassIndex}", "${data.bodyFatPercentage}", "${data.skeletalMuscleMass}",
				"${data.basalMetabolicRate}", "${data.workoutIntensity}", data.id)
		)
	}

	suspend fun updateSleep(data: Sleep) {
		database.execute(sql = "UPDATE $SLEEP SET starts = ?, ends = ? WHERE id = ?", parameters = listOf(data.starts, data.ends, data.id))
	}

	suspend fun updateMedicine(data: Medicine) {
		database.execute(
			sql = "UPDATE $MEDICINES SET category = ?, name = ?, amount = ?, unit = ?, starts = ?, ends = ? WHERE id = ?",
			parameters = listOf(data.category, data.name, "${data.amount}", data.unit, data.starts, data.ends, data.id)
		)
	}

	suspend fun updateData(table: String, column: String, data: String, id: String) {
		database.execute(sql = "UPDATE $table SET $column = '$data' WHERE id = '$id'")
	}

	suspend fun updateWaterGoal(data: Goal) {
		database.execute(
			sql = "UPDATE $GOALS SET water_amount_of_cup = ?, water_intake = ? WHERE id = ?",
			parameters = listOf("${data.waterAmountOfCup}", "${data.waterIntake}", data.id))
	}

	suspend fun updateNote(data: Note) {
		database.execute(sql = "UPDATE $NOTES SET title = ?, content = ? , emotion = ? WHERE user_id = '${getUser.uid}' AND date = ?",
			parameters = listOf(data.title, data.content, data.emotion, data.date))
	}

	suspend fun deleteItem(table: String, column: String, data: String) {
		database.writeTransaction {
			database.execute(sql = "DELETE FROM $table WHERE user_id = '${getUser.uid}' AND $column = '$data'")
		}
	}

	suspend fun deleteDiet(mealTime: String, name: String, date: String, id: String) {
		database.writeTransaction {
			database.execute(sql = "DELETE FROM $DIETS WHERE user_id = '${getUser.uid}' AND meal_time = '$mealTime' AND name = '$name' " +
				"AND strftime('%Y-%m-%d', date) = '$date' AND id <> '$id'")
		}
	}

	suspend fun deleteDuplicate(table: String, column: String, data1: String, data2: String) {
		database.writeTransaction {
			database.execute(sql = "DELETE FROM $table WHERE user_id = '${getUser.uid}' AND $column = '$data1' and id <> '$data2'")
		}
	}
}