package kr.bodywell.android.api.powerSync

import android.content.Context
import android.util.Log
import com.powersync.db.crud.CrudEntry
import kr.bodywell.android.api.RetrofitAPI
import kr.bodywell.android.api.dto.ActivityDTO
import kr.bodywell.android.api.dto.BodyDTO
import kr.bodywell.android.api.dto.BodyUpdateDTO
import kr.bodywell.android.api.dto.DietDTO
import kr.bodywell.android.api.dto.DietUpdateDTO
import kr.bodywell.android.api.dto.FoodDTO
import kr.bodywell.android.api.dto.FoodUpdateDTO
import kr.bodywell.android.api.dto.GoalDTO
import kr.bodywell.android.api.dto.GoalUpdateDTO
import kr.bodywell.android.api.dto.MedicineDTO
import kr.bodywell.android.api.dto.MedicineIntakeDTO
import kr.bodywell.android.api.dto.MedicineTimeDTO
import kr.bodywell.android.api.dto.MedicineUpdateDTO
import kr.bodywell.android.api.dto.ProfileDTO
import kr.bodywell.android.api.dto.SleepDTO
import kr.bodywell.android.api.dto.SleepUpdateDTO
import kr.bodywell.android.api.dto.WaterDTO
import kr.bodywell.android.api.dto.WaterUpdateDTO
import kr.bodywell.android.api.dto.WorkoutDTO
import kr.bodywell.android.api.dto.WorkoutUpdateDTO
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.CustomUtil.getToken
import kr.bodywell.android.util.CustomUtil.powerSync

object SyncController {
	suspend fun upsert(entry: CrudEntry) {
		val op = entry.opData!!

		when(entry.table) {
			"foods" -> {
				val response = RetrofitAPI.api.createFood("Bearer ${getToken.access}", entry.id, FoodDTO(op["name"]!!, op["calorie"]!!.toInt(),
					op["carbohydrate"]!!.toDouble(), op["protein"]!!.toDouble(), op["fat"]!!.toDouble(), op["quantity"]!!.toInt(), op["quantity_unit"]!!,
					op["volume"]!!.toInt(), op["volume_unit"]!!, op["created_at"]!!, op["updated_at"]!!))
				if(response.isSuccessful) {
					Log.d(TAG, "createFood: ${response.body()}")
				}else {
					Log.e(TAG, "createFood: $response")
				}
			}
			"diets" -> {
				val photos = ArrayList<String>()
				photos.add("https://example.com/picture.jpg")
				val response = RetrofitAPI.api.createDiets("Bearer ${getToken.access}", entry.id, DietDTO(op["meal_time"]!!, op["name"]!!,
					op["calorie"]!!.toInt(), op["carbohydrate"]!!.toDouble(), op["protein"]!!.toDouble(), op["fat"]!!.toDouble(), 1, "개",
					op["volume"]!!.toInt(), op["volume_unit"]!!, photos, op["date"]!!, op["created_at"]!!, op["updated_at"]!!, op["food_id"]!!))
				if(response.isSuccessful) {
					Log.d(TAG, "createDiets: ${response.body()}")
				}else {
					Log.e(TAG, "createDiets: $response")
				}
			}
			"water" -> {
				val response = RetrofitAPI.api.createWater("Bearer ${getToken.access}", entry.id, WaterDTO(entry.opData!!["mL"]!!.toInt(),
					entry.opData!!["count"]!!.toInt(), entry.opData!!["date"]!!, entry.opData!!["created_at"]!!, entry.opData!!["updated_at"]!!))
				if(response.isSuccessful) {
					Log.d(TAG, "createWater: ${response.body()}")
				}else {
					Log.e(TAG, "createWater: $response")
				}
			}
			"activities" -> {
				val response = RetrofitAPI.api.createActivity("Bearer ${getToken.access}", entry.id, ActivityDTO(entry.id, op["name"]!!, op["created_at"]!!, op["updated_at"]!!))
				if(response.isSuccessful) {
					Log.d(TAG, "createActivity: ${response.body()}")
				}else {
					Log.e(TAG, "createActivity: $response")
				}
			}
			"workouts" -> {
				val response = RetrofitAPI.api.createWorkout("Bearer ${getToken.access}", entry.id, WorkoutDTO(op["name"]!!, op["calorie"]!!.toInt(),
					op["intensity"]!!, op["time"]!!.toInt(), op["date"]!!, op["created_at"]!!, op["updated_at"]!!, op["activity_id"]!!))
				if(response.isSuccessful) {
					Log.d(TAG, "createWorkout: ${response.body()}")
				}else {
					Log.e(TAG, "createWorkout: $response")
				}
			}
			"body_measurements" -> {
				val response = RetrofitAPI.api.createBody("Bearer ${getToken.access}", entry.id, BodyDTO(op["height"]!!.toDouble(), op["weight"]!!.toDouble(),
					op["body_mass_index"]!!.toDouble(), op["body_fat_percentage"]!!.toDouble(), op["skeletal_muscle_mass"]!!.toDouble(),
					op["basal_metabolic_rate"]!!.toDouble(), op["workout_intensity"]!!.toInt(), op["time"]!!, op["created_at"]!!, op["updated_at"]!!))
				if(response.isSuccessful) {
					Log.d(TAG, "createBody: ${response.body()}")
				}else {
					Log.e(TAG, "createBody: $response")
				}
			}
			"sleep" -> {
				val response = RetrofitAPI.api.createSleep("Bearer ${getToken.access}", entry.id, SleepDTO(op["starts"]!!, op["ends"]!!, op["created_at"]!!, op["updated_at"]!!))
				if(response.isSuccessful) {
					Log.d(TAG, "createSleep: ${response.body()}")
				}else {
					Log.e(TAG, "createSleep: $response")
				}
			}
			"medicines" -> {
				val response = RetrofitAPI.api.createMedicine("Bearer ${getToken.access}", entry.id, MedicineDTO(op["category"]!!, op["name"]!!,
					op["amount"]!!.toInt(), op["unit"]!!, op["starts"]!!, op["ends"]!!, op["created_at"]!!, op["updated_at"]!!))
				if(response.isSuccessful) {
					Log.d(TAG, "createMedicine: ${response.body()}")
				}else {
					Log.e(TAG, "createMedicine: $response")
				}
			}
			"medicine_times" -> {
				val response = RetrofitAPI.api.createMedicineTime("Bearer ${getToken.access}", op["medicine_id"]!!, entry.id,
					MedicineTimeDTO(op["time"]!!, op["created_at"]!!, op["updated_at"]!!))
				if(response.isSuccessful) {
					Log.d(TAG, "createMedicineTime: ${response.body()}")
				}else {
					Log.e(TAG, "createMedicineTime: $response")
				}
			}
			"medicine_intakes" -> {
				Log.d(TAG, "source_id: ${op["source_id"]!!}")
				Log.d(TAG, "medicine_time_id: ${op["medicine_time_id"]!!}")
				Log.d(TAG, "entry.id: ${entry.id}")

				val response = RetrofitAPI.api.createMedicineIntake("Bearer ${getToken.access}", op["source_id"]!!, op["medicine_time_id"]!!, entry.id,
					MedicineIntakeDTO(op["intaked_at"]!!, op["created_at"]!!, op["updated_at"]!!))
				if(response.isSuccessful) {
					Log.d(TAG, "createMedicineIntake: ${response.body()}")
				}else {
					Log.e(TAG, "createMedicineIntake: $response")
				}
			}
			"goals" -> {
				val response = RetrofitAPI.api.createGoal("Bearer ${getToken.access}", entry.id, GoalDTO(op["weight"]!!.toDouble(),
					op["kcal_of_diet"]!!.toInt(), op["kcal_of_workout"]!!.toInt(), op["water_amount_of_cup"]!!.toInt(), op["water_intake"]!!.toInt(),
					op["sleep"]!!.toInt(), op["medicine_intake"]!!.toInt(), op["date"]!!, op["created_at"]!!, op["updated_at"]!!))
				if(response.isSuccessful) {
					Log.d(TAG, "createGoal: ${response.body()}")
				}else {
					Log.e(TAG, "createGoal: $response")
				}
			}
		}
	}

	suspend fun update(entry: CrudEntry) {
		val op = entry.opData!!
		when(entry.table) {
			"profiles" -> {
				val getProfile = RetrofitAPI.api.getProfile("Bearer ${getToken.access}")
				if(getProfile.isSuccessful) {
					val name = if(op["name"] == null) getProfile.body()!!.name!! else op["name"]!!
					val birth = if(op["birth"] == null) getProfile.body()!!.birth!! else op["birth"]!!
					val gender = if(op["gender"] == null) getProfile.body()!!.gender!! else op["gender"]!!
					val height = if(op["height"] == null) getProfile.body()!!.height!! else op["height"]!!.toDouble()
					val weight = if(op["weight"] == null) getProfile.body()!!.weight!! else op["weight"]!!.toDouble()

					val updateProfile = RetrofitAPI.api.updateProfile("Bearer ${getToken.access}", ProfileDTO(name, birth, gender, height, weight))
					if(updateProfile.isSuccessful) Log.d(TAG, "updateProfile: ${updateProfile.body()}") else Log.e(TAG, "updateProfile: $updateProfile")
				}else Log.e(TAG, "getProfile: $getProfile")
			}
			"foods" -> {
				val getFood = RetrofitAPI.api.getFood("Bearer ${getToken.access}", entry.id)
				if(getFood.isSuccessful) {
					val calorie = if(op["calorie"] == null) getFood.body()!!.calorie else op["calorie"]!!.toInt()
					val carbohydrate = if(op["carbohydrate"] == null) getFood.body()!!.carbohydrate else op["carbohydrate"]!!.toDouble()
					val protein = if(op["protein"] == null) getFood.body()!!.protein else op["protein"]!!.toDouble()
					val fat = if(op["fat"] == null) getFood.body()!!.fat else op["fat"]!!.toDouble()
					val volume = if(op["volume"] == null) getFood.body()!!.volume else op["volume"]!!.toInt()
					val volumeUnit = if(op["volume_unit"] == null) getFood.body()!!.volumeUnit else op["volume_unit"]

					val updateFood = RetrofitAPI.api.updateFood("Bearer ${getToken.access}", entry.id, FoodUpdateDTO(calorie, carbohydrate, protein, fat, volume, volumeUnit!!))
					if(updateFood.isSuccessful) Log.d(TAG, "updateFood: ${updateFood.body()}") else Log.e(TAG, "updateFood: $updateFood")
				}else Log.e(TAG, "getFood: $getFood")
			}
			"diets" -> {
				val updateDiets = RetrofitAPI.api.updateDiets("Bearer ${getToken.access}", entry.id, DietUpdateDTO(op["quantity"]!!.toInt()))
				if(updateDiets.isSuccessful) Log.d(TAG, "updateDiets: ${updateDiets.body()}") else Log.e(TAG, "updateDiets: $updateDiets")
			}
			"water" -> {
				val getWater = RetrofitAPI.api.getWater("Bearer ${getToken.access}", entry.id)
				if(getWater.isSuccessful) {
					val mL = if(op["mL"] == null) getWater.body()!!.mL else op["mL"]!!.toInt()
					val count = if(op["count"] == null) getWater.body()!!.count else op["count"]!!.toInt()

					val response = RetrofitAPI.api.updateWater("Bearer ${getToken.access}", entry.id, WaterUpdateDTO(mL, count))
					if(response.isSuccessful) Log.d(TAG, "updateWater: ${response.body()}") else Log.e(TAG, "updateWater: $response")
				}else Log.e(TAG, "getWater: $getWater")
			}
			"workouts" -> {
				val getWorkout = RetrofitAPI.api.getWorkout("Bearer ${getToken.access}", entry.id)
				if(getWorkout.isSuccessful) {
					val calorie = if(op["calorie"] == null) getWorkout.body()!!.calorie else op["calorie"]!!.toInt()
					val intensity = if(op["intensity"] == null) getWorkout.body()!!.intensity else op["intensity"]!!
					val time = if(op["time"] == null) getWorkout.body()!!.time else op["time"]!!.toInt()

					val response = RetrofitAPI.api.updateWorkout("Bearer ${getToken.access}", entry.id, WorkoutUpdateDTO(calorie, intensity, time))
					if(response.isSuccessful) Log.d(TAG, "updateWorkout: ${response.body()}") else Log.e(TAG, "updateWorkout: $response")
				}
			}
			"body_measurements" -> {
				val response = RetrofitAPI.api.updateBody("Bearer ${getToken.access}", entry.id, BodyUpdateDTO(op["height"]!!.toDouble(),
					op["weight"]!!.toDouble(), op["body_mass_index"]!!.toDouble(), op["body_fat_percentage"]!!.toDouble(), op["skeletal_muscle_mass"]!!.toDouble(),
					op["basal_metabolic_rate"]!!.toDouble(), op["workout_intensity"]!!.toInt()))
				if(response.isSuccessful) Log.d(TAG, "updateBody: ${response.body()}") else Log.e(TAG, "updateBody: $response")
			}
			"sleep" -> {
				val response = RetrofitAPI.api.updateSleep("Bearer ${getToken.access}", entry.id, SleepUpdateDTO(op["starts"]!!, op["ends"]!!))
				if(response.isSuccessful) Log.d(TAG, "updateSleep: ${response.body()}") else Log.e(TAG, "updateSleep: $response")
			}
			"medicines" -> {
				val getMedicine = RetrofitAPI.api.getMedicine("Bearer ${getToken.access}", entry.id)
				if(getMedicine.isSuccessful) {
					val category = if(op["category"] == null) getMedicine.body()!!.category else op["category"]!!
					val name = if(op["name"] == null) getMedicine.body()!!.name else op["name"]!!
					val amount = if(op["amount"] == null) getMedicine.body()!!.amount else op["amount"]!!.toInt()
					val unit = if(op["unit"] == null) getMedicine.body()!!.unit else op["unit"]!!
					val starts = if(op["starts"] == null) getMedicine.body()!!.starts else op["starts"]!!
					val ends = if(op["ends"] == null) getMedicine.body()!!.ends else op["ends"]!!

					val response = RetrofitAPI.api.updateMedicine("Bearer ${getToken.access}", entry.id, MedicineUpdateDTO(category, name, amount, unit, starts, ends))
					if(response.isSuccessful) Log.d(TAG, "updateMedicine: ${response.body()}") else Log.e(TAG, "updateMedicine: $response")
				}
			}
			"goals" -> {
				val getGoal = RetrofitAPI.api.getGoal("Bearer ${getToken.access}", entry.id)
				if(getGoal.isSuccessful) {
					Log.d(TAG, "getGoal: ${getGoal.body()}")
					val weight = if(op["weight"] == null) getGoal.body()!!.weight else op["weight"]!!.toDouble()
					val kcalOfDiet = if(op["kcal_of_diet"] == null) getGoal.body()!!.kcalOfDiet else op["kcal_of_diet"]!!.toInt()
					val kcalOfWorkout = if(op["kcal_of_workout"] == null) getGoal.body()!!.kcalOfWorkout else op["kcal_of_workout"]!!.toInt()
					val waterAmountOfCup = if(op["water_amount_of_cup"] == null) getGoal.body()!!.waterAmountOfCup else op["water_amount_of_cup"]!!.toInt()
					val waterIntake = if(op["water_intake"] == null) getGoal.body()!!.waterIntake else op["water_intake"]!!.toInt()
					val sleep = if(op["sleep"] == null) getGoal.body()!!.sleep else op["sleep"]!!.toInt()
					val medicineIntake = if(op["medicine_intake"] == null) getGoal.body()!!.medicineIntake else op["medicine_intake"]!!.toInt()

					val response = RetrofitAPI.api.updateGoal("Bearer ${getToken.access}", entry.id, GoalUpdateDTO(weight, kcalOfDiet, kcalOfWorkout, waterAmountOfCup, waterIntake, sleep, medicineIntake))
					if(response.isSuccessful) Log.d(TAG, "updateGoal: ${response.body()}") else Log.e(TAG, "updateGoal: $response")
				}else Log.e(TAG, "getGoal: $getGoal")
			}
		}
	}

	suspend fun delete(context: Context, table: String, id: String) {
		val dataManager = DataManager(context)
		dataManager.open()

		when(table) {
			"foods" -> {
				val response = RetrofitAPI.api.deleteFood("Bearer ${getToken.access}", id)
				if(response.isSuccessful) Log.d(TAG, "deleteFood: ${response.raw().code}") else Log.e(TAG, "deleteFood: $response")
			}
			"diets" -> {
				val response = RetrofitAPI.api.deleteDiet("Bearer ${getToken.access}", id)
				if(response.isSuccessful) Log.d(TAG, "deleteDiet: ${response.raw().code}") else Log.e(TAG, "deleteDiet: $response")
			}
			"water" -> {
				val response = RetrofitAPI.api.deleteWater("Bearer ${getToken.access}", id)
				if(response.isSuccessful) Log.d(TAG, "deleteWater: ${response.raw().code}") else Log.e(TAG, "deleteWater: $response")
			}
			"activities" -> {
				val response = RetrofitAPI.api.deleteActivity("Bearer ${getToken.access}", id)
				if(response.isSuccessful) Log.d(TAG, "deleteActivity: ${response.raw().code}") else Log.e(TAG, "deleteActivity: $response")
			}
			"workouts" -> {
				val response = RetrofitAPI.api.deleteWorkout("Bearer ${getToken.access}", id)
				if(response.isSuccessful) Log.d(TAG, "deleteWorkout: ${response.raw().code}") else Log.e(TAG, "deleteWorkout: $response")
			}
			"medicines" -> {
				val getMedicineTime = powerSync.getAllMedicineTime("medicine_id", id)
				// 약복용 기록 삭제
				for(i in getMedicineTime.indices) {
					val getData = powerSync.getData("medicine_intakes", "id", "medicine_time_id", getMedicineTime[i].id)
					if(getData != "") {
						val deleteMedicineIntake = RetrofitAPI.api.deleteMedicineIntake("Bearer ${getToken.access}", id, getMedicineTime[i].id, getData)
						if(deleteMedicineIntake.isSuccessful) {
							Log.d(TAG, "deleteMedicineIntake: ${deleteMedicineIntake.raw().code}")
							dataManager.deleteItem("medicineIntake", "uid", getData)
						}else Log.e(TAG, "deleteMedicineIntake: $deleteMedicineIntake")
					}
				}
				// 약복용 시간 삭제
				for(i in getMedicineTime.indices) {
					val deleteMedicineTime = RetrofitAPI.api.deleteMedicineTime("Bearer ${getToken.access}", id, getMedicineTime[i].id)
					if(deleteMedicineTime.isSuccessful) {
						Log.d(TAG, "deleteMedicineTime: ${deleteMedicineTime.raw().code}")
						dataManager.deleteItem("medicineTime", "uid", getMedicineTime[i].id)
					}else Log.e(TAG, "deleteMedicineTime: $deleteMedicineTime")
				}
				// 약복용 삭제
				val response = RetrofitAPI.api.deleteMedicine("Bearer ${getToken.access}", id)
				if(response.isSuccessful) {
					Log.d(TAG, "deleteMedicine: ${response.raw().code}")
					dataManager.deleteItem("medicines", "uid", id)
				}else Log.e(TAG, "deleteMedicine: $response")
			}
			"medicine_times" -> {
				val getData = dataManager.getMedicineTime(id)
				Log.d(TAG, "medicineId: ${getData.medicineId}")

				val response = RetrofitAPI.api.deleteMedicineTime("Bearer ${getToken.access}", getData.medicineId, id)
				if(response.isSuccessful) {
					Log.d(TAG, "deleteMedicineTime: ${response.raw().code}")
					dataManager.deleteItem("medicineTime", "uid", id)
				}else Log.e(TAG, "deleteMedicineTime: $response")
			}
			"medicine_intakes" -> {
				val getData = dataManager.getMedicineIntake(id)
				Log.d(TAG, "medicineId: ${getData.medicineId}\nmedicineTimeId: ${getData.medicineTimeId}")

				val response = RetrofitAPI.api.deleteMedicineIntake("Bearer ${getToken.access}", getData.medicineId, getData.medicineTimeId, id)
				if(response.isSuccessful) {
					Log.d(TAG, "deleteMedicineIntake: ${response.raw().code}")
					dataManager.deleteItem("medicineIntake", "uid", id)
				}else Log.e(TAG, "deleteMedicineIntake: $response")
			}
		}
	}
}