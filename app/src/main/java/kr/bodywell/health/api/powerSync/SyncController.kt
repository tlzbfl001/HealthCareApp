package kr.bodywell.health.api.powerSync

import android.content.Context
import android.util.Log
import com.powersync.db.crud.CrudEntry
import kr.bodywell.health.api.RetrofitAPI
import kr.bodywell.health.api.dto.ActivityDTO
import kr.bodywell.health.api.dto.BodyDTO
import kr.bodywell.health.api.dto.BodyUpdateDTO
import kr.bodywell.health.api.dto.DietDTO
import kr.bodywell.health.api.dto.DietUpdateDTO
import kr.bodywell.health.api.dto.FoodDTO
import kr.bodywell.health.api.dto.FoodUpdateDTO
import kr.bodywell.health.api.dto.GoalDTO
import kr.bodywell.health.api.dto.GoalUpdateDTO
import kr.bodywell.health.api.dto.MedicineDTO
import kr.bodywell.health.api.dto.MedicineIntakeDTO
import kr.bodywell.health.api.dto.MedicineTimeDTO
import kr.bodywell.health.api.dto.MedicineUpdateDTO
import kr.bodywell.health.api.dto.NoteDTO
import kr.bodywell.health.api.dto.NoteUpdateDTO
import kr.bodywell.health.api.dto.ProfileDTO
import kr.bodywell.health.api.dto.SleepDTO
import kr.bodywell.health.api.dto.SleepUpdateDTO
import kr.bodywell.health.api.dto.WaterDTO
import kr.bodywell.health.api.dto.WaterUpdateDTO
import kr.bodywell.health.api.dto.WorkoutDTO
import kr.bodywell.health.api.dto.WorkoutUpdateDTO
import kr.bodywell.health.model.Constant.ACTIVITIES
import kr.bodywell.health.model.Constant.BODY_MEASUREMENTS
import kr.bodywell.health.model.Constant.DIETS
import kr.bodywell.health.model.Constant.FILES
import kr.bodywell.health.model.Constant.FOODS
import kr.bodywell.health.model.Constant.GOALS
import kr.bodywell.health.model.Constant.MEDICINES
import kr.bodywell.health.model.Constant.MEDICINE_INTAKES
import kr.bodywell.health.model.Constant.MEDICINE_TIMES
import kr.bodywell.health.model.Constant.NOTES
import kr.bodywell.health.model.Constant.PROFILES
import kr.bodywell.health.model.Constant.SLEEP
import kr.bodywell.health.model.Constant.WATER
import kr.bodywell.health.model.Constant.WORKOUTS
import kr.bodywell.health.util.CustomUtil.TAG
import kr.bodywell.health.util.CustomUtil.getToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

object SyncController {
	suspend fun upsert(context: Context, entry: CrudEntry) {
		val op = entry.opData!!

		when(entry.table) {
			FOODS -> {
				val response = RetrofitAPI.api.createFood("Bearer ${getToken.access}", entry.id, FoodDTO(op["name"]!!, op["calorie"]!!.toInt(),
					op["carbohydrate"]!!.toDouble(), op["protein"]!!.toDouble(), op["fat"]!!.toDouble(), op["quantity"]!!.toInt(), op["quantity_unit"]!!,
					op["volume"]!!.toInt(), op["volume_unit"]!!, op["created_at"]!!, op["updated_at"]!!))
				if(response.isSuccessful) {
//					Log.i(TAG, "createFood: ${response.body()}")
				} else Log.e(TAG, "createFood: $response")
			}
			DIETS -> {
				val response = RetrofitAPI.api.createDiets("Bearer ${getToken.access}", entry.id, DietDTO(op["meal_time"]!!, op["name"]!!,
					op["calorie"]!!.toInt(), op["carbohydrate"]!!.toDouble(), op["protein"]!!.toDouble(), op["fat"]!!.toDouble(), 1, "개",
					op["volume"]!!.toInt(), op["volume_unit"]!!, op["date"]!!, op["created_at"]!!, op["updated_at"]!!, op["food_id"]!!))
				if(response.isSuccessful) {
//					Log.i(TAG, "createDiets: ${response.body()}")
				} else Log.e(TAG, "createDiets: $response")
			}
			WATER -> {
				val response = RetrofitAPI.api.createWater("Bearer ${getToken.access}", entry.id, WaterDTO(entry.opData!!["mL"]!!.toInt(),
					entry.opData!!["count"]!!.toInt(), entry.opData!!["date"]!!, entry.opData!!["created_at"]!!, entry.opData!!["updated_at"]!!))
				if(response.isSuccessful) {
//					Log.i(TAG, "createWater: ${response.body()}")
				} else Log.e(TAG, "createWater: $response")
			}
			ACTIVITIES -> {
				val response = RetrofitAPI.api.createActivity("Bearer ${getToken.access}", entry.id, ActivityDTO(entry.id, op["name"]!!, op["created_at"]!!, op["updated_at"]!!))
				if(response.isSuccessful) {
//					Log.i(TAG, "createActivity: ${response.body()}")
				} else Log.e(TAG, "createActivity: $response")
			}
			WORKOUTS -> {
				val response = RetrofitAPI.api.createWorkout("Bearer ${getToken.access}", entry.id, WorkoutDTO(op["name"]!!, op["calorie"]!!.toInt(),
					op["intensity"]!!, op["time"]!!.toInt(), op["date"]!!, op["created_at"]!!, op["updated_at"]!!, op["activity_id"]!!))
				if(response.isSuccessful) {
//					Log.i(TAG, "createWorkout: ${response.body()}")
				} else Log.e(TAG, "createWorkout: $response")
			}
			BODY_MEASUREMENTS -> {
				val response = RetrofitAPI.api.createBody("Bearer ${getToken.access}", entry.id, BodyDTO(op["height"]!!.toDouble(), op["weight"]!!.toDouble(),
					op["body_mass_index"]!!.toDouble(), op["body_fat_percentage"]!!.toDouble(), op["skeletal_muscle_mass"]!!.toDouble(),
					op["basal_metabolic_rate"]!!.toDouble(), op["workout_intensity"]!!.toInt(), op["time"]!!, op["created_at"]!!, op["updated_at"]!!))
				if(response.isSuccessful) {
//					Log.i(TAG, "createBody: ${response.body()}")
				} else Log.e(TAG, "createBody: $response")
			}
			SLEEP -> {
				val response = RetrofitAPI.api.createSleep("Bearer ${getToken.access}", entry.id, SleepDTO(op["starts"]!!, op["ends"]!!, op["created_at"]!!, op["updated_at"]!!))
				if(response.isSuccessful) {
//					Log.i(TAG, "createSleep: ${response.body()}")
				} else Log.e(TAG, "createSleep: $response")
			}
			MEDICINES -> {
				val response = RetrofitAPI.api.createMedicine("Bearer ${getToken.access}", entry.id, MedicineDTO(op["category"]!!, op["name"]!!,
					op["amount"]!!.toInt(), op["unit"]!!, op["starts"]!!, op["ends"]!!, op["created_at"]!!, op["updated_at"]!!))
				if(response.isSuccessful) {
//					Log.i(TAG, "createMedicine: ${response.body()}")
				} else Log.e(TAG, "createMedicine: $response")
			}
			MEDICINE_TIMES -> {
				val response = RetrofitAPI.api.createMedicineTime("Bearer ${getToken.access}", entry.id,
					MedicineTimeDTO(op["time"]!!, op["medicine_id"]!!, op["created_at"]!!, op["updated_at"]!!))
				if(response.isSuccessful) {
//					Log.i(TAG, "createMedicineTime: ${response.body()}")
				} else Log.e(TAG, "createMedicineTime: $response")
			}
			MEDICINE_INTAKES -> {
				val response = RetrofitAPI.api.createMedicineIntake("Bearer ${getToken.access}", entry.id,
					MedicineIntakeDTO(op["intaked_at"]!!, op["medicine_time_id"]!!, op["created_at"]!!, op["updated_at"]!!))
				if(response.isSuccessful) {
//					Log.i(TAG, "createMedicineIntake: ${response.body()}")
				} else Log.e(TAG, "createMedicineIntake: $response")
			}
			GOALS -> {
				val response = RetrofitAPI.api.createGoal("Bearer ${getToken.access}", entry.id, GoalDTO(op["weight"]!!.toDouble(),
					op["kcal_of_diet"]!!.toInt(), op["kcal_of_workout"]!!.toInt(), op["water_amount_of_cup"]!!.toInt(), op["water_intake"]!!.toInt(),
					op["sleep"]!!.toInt(), op["medicine_intake"]!!.toInt(), op["date"]!!, op["created_at"]!!, op["updated_at"]!!))
				if(response.isSuccessful) {
//					Log.i(TAG, "createGoal: ${response.body()}")
				} else Log.e(TAG, "createGoal: $response")
			}
			NOTES -> {
				val response = RetrofitAPI.api.createNote("Bearer ${getToken.access}", entry.id,
					NoteDTO(op["title"]!!, op["content"]!!, op["emotion"]!!, op["date"]!!, op["created_at"]!!, op["updated_at"]!!))
				if(response.isSuccessful) {
//					Log.i(TAG, "createNote: ${response.body()}")
				} else Log.e(TAG, "createNote: $response")
			}
			FILES -> {
				val file = File(context.filesDir.toString() + "/" + op["name"]!!)
				if(file.exists()) {
					val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
					if(op["profile_id"] != null) {
						val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
						val response = RetrofitAPI.api.createProfileFile("Bearer ${getToken.access}", op["profile_id"]!!, body)
						if(response.isSuccessful) {
//							Log.i(TAG, "createProfileFile: ${response.body()}")
						}else {
							Log.e(TAG, "createProfileFile: $response")
							file.delete()
						}
					}else if(op["diet_id"] != null) {
						val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)
						val response = RetrofitAPI.api.createDietFile("Bearer ${getToken.access}", op["diet_id"]!!, entry.id, body)
						if(response.isSuccessful) {
//							Log.i(TAG, "createDietFile: ${response.body()}")
						}else {
							Log.e(TAG, "createDietFile: $response")
							file.delete()
						}
					}else if(op["note_id"] != null) {
						val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)
						val response = RetrofitAPI.api.createNoteFile("bearer ${getToken.access}", op["note_id"]!!, entry.id, body)
						if(response.isSuccessful) {
//							Log.i(TAG, "createNoteFile: ${response.body()}")
						}else {
							Log.e(TAG, "createNoteFile: $response")
							file.delete()
						}
					}
				}
			}
		}
	}

	suspend fun update(entry: CrudEntry) {
		val op = entry.opData!!
		when(entry.table) {
			PROFILES -> {
				val height = if(op["height"] == null) null else op["height"]!!.toDouble()
				val weight = if(op["weight"] == null) null else op["weight"]!!.toDouble()
				val response = RetrofitAPI.api.updateProfile("Bearer ${getToken.access}", entry.id, ProfileDTO(op["name"], op["birth"], op["gender"], height,weight))
				if(response.isSuccessful) {
//					Log.i(TAG, "updateProfile: ${response.body()}")
				} else Log.e(TAG, "updateProfile: $response")
			}
			FOODS -> {
				val getFood = RetrofitAPI.api.getFood("Bearer ${getToken.access}", entry.id)
				if(getFood.isSuccessful) {
					val calorie = if(op["calorie"] == null) getFood.body()!!.calorie else op["calorie"]!!.toInt()
					val carbohydrate = if(op["carbohydrate"] == null) getFood.body()!!.carbohydrate else op["carbohydrate"]!!.toDouble()
					val protein = if(op["protein"] == null) getFood.body()!!.protein else op["protein"]!!.toDouble()
					val fat = if(op["fat"] == null) getFood.body()!!.fat else op["fat"]!!.toDouble()
					val quantityUnit = if(op["quantity_unit"] == null) getFood.body()!!.quantityUnit else op["quantity_unit"]!!
					val volume = if(op["volume"] == null) getFood.body()!!.volume else op["volume"]!!.toInt()
					val volumeUnit = if(op["volume_unit"] == null) getFood.body()!!.volumeUnit else op["volume_unit"]

					val response = RetrofitAPI.api.updateFood("Bearer ${getToken.access}", entry.id, FoodUpdateDTO(calorie, carbohydrate, protein, fat, quantityUnit, volume, volumeUnit!!))
					if(response.isSuccessful) {
//						Log.i(TAG, "updateFood: ${response.body()}")
					} else Log.e(TAG, "updateFood: $response")
				}
			}
			DIETS -> {
				val getDiets = RetrofitAPI.api.getDiets("Bearer ${getToken.access}", entry.id)
				if(getDiets.isSuccessful) {
					val quantity = if(op["quantity"] == null) getDiets.body()!!.quantity else op["quantity"]!!.toInt()
					val response = RetrofitAPI.api.updateDiets("Bearer ${getToken.access}", entry.id, DietUpdateDTO(quantity))
					if(response.isSuccessful) {
//						Log.i(TAG, "updateDiets: ${response.body()}")
					} else Log.e(TAG, "updateDiets: $response")
				}
			}
			WATER -> {
				val getWater = RetrofitAPI.api.getWater("Bearer ${getToken.access}", entry.id)
				if(getWater.isSuccessful) {
					val mL = if(op["mL"] == null) getWater.body()!!.mL else op["mL"]!!.toInt()
					val count = if(op["count"] == null) getWater.body()!!.count else op["count"]!!.toInt()
					val response = RetrofitAPI.api.updateWater("Bearer ${getToken.access}", entry.id, WaterUpdateDTO(mL, count))
					if(response.isSuccessful) {
//						Log.i(TAG, "updateWater: ${response.body()}")
					} else Log.e(TAG, "updateWater: $response")
				}
			}
			WORKOUTS -> {
				val getWorkout = RetrofitAPI.api.getWorkout("Bearer ${getToken.access}", entry.id)
				if(getWorkout.isSuccessful) {
					val calorie = if(op["calorie"] == null) getWorkout.body()!!.calorie else op["calorie"]!!.toInt()
					val intensity = if(op["intensity"] == null) getWorkout.body()!!.intensity else op["intensity"]!!
					val time = if(op["time"] == null) getWorkout.body()!!.time else op["time"]!!.toInt()
					val response = RetrofitAPI.api.updateWorkout("Bearer ${getToken.access}", entry.id, WorkoutUpdateDTO(calorie, intensity, time))
					if(response.isSuccessful) {
//						Log.i(TAG, "updateWorkout: ${response.body()}")
					} else Log.e(TAG, "updateWorkout: $response")
				}
			}
			BODY_MEASUREMENTS -> {
				val getBody = RetrofitAPI.api.getBody("Bearer ${getToken.access}", entry.id)
				if(getBody.isSuccessful) {
					val height = if (op["height"] == null) getBody.body()!!.height else op["height"]!!.toDouble()
					val weight = if (op["weight"] == null) getBody.body()!!.weight else op["weight"]!!.toDouble()
					val bodyMassIndex = if (op["body_mass_index"] == null) getBody.body()!!.bodyMassIndex else op["body_mass_index"]!!.toDouble()
					val bodyFatPercentage = if (op["body_fat_percentage"] == null) getBody.body()!!.bodyFatPercentage else op["body_fat_percentage"]!!.toDouble()
					val skeletalMuscleMass = if (op["skeletal_muscle_mass"] == null) getBody.body()!!.skeletalMuscleMass else op["skeletal_muscle_mass"]!!.toDouble()
					val basalMetabolicRate = if (op["basal_metabolic_rate"] == null) getBody.body()!!.basalMetabolicRate else op["basal_metabolic_rate"]!!.toDouble()
					val workoutIntensity = if (op["workout_intensity"] == null) getBody.body()!!.workoutIntensity else op["workout_intensity"]!!.toInt()

					val response = RetrofitAPI.api.updateBody("Bearer ${getToken.access}", entry.id, BodyUpdateDTO(height,
						weight, bodyMassIndex, bodyFatPercentage, skeletalMuscleMass, basalMetabolicRate, workoutIntensity))
					if(response.isSuccessful) {
//						Log.i(TAG, "updateBody: ${response.body()}")
					} else Log.e(TAG, "updateBody: $response")
				}
			}
			SLEEP -> {
				val response = RetrofitAPI.api.updateSleep("Bearer ${getToken.access}", entry.id, SleepUpdateDTO(op["starts"]!!, op["ends"]!!))
				if(response.isSuccessful) Log.i(TAG, "updateSleep: ${response.body()}") else Log.e(TAG, "updateSleep: $response")
			}
			MEDICINES -> {
				val getMedicine = RetrofitAPI.api.getMedicine("Bearer ${getToken.access}", entry.id)
				if(getMedicine.isSuccessful) {
					val category = if(op["category"] == null) getMedicine.body()!!.category else op["category"]!!
					val name = if(op["name"] == null) getMedicine.body()!!.name else op["name"]!!
					val amount = if(op["amount"] == null) getMedicine.body()!!.amount else op["amount"]!!.toInt()
					val unit = if(op["unit"] == null) getMedicine.body()!!.unit else op["unit"]!!
					val starts = if(op["starts"] == null) getMedicine.body()!!.starts else op["starts"]!!
					val ends = if(op["ends"] == null) getMedicine.body()!!.ends else op["ends"]!!

					val response = RetrofitAPI.api.updateMedicine("Bearer ${getToken.access}", entry.id, MedicineUpdateDTO(category, name, amount, unit, starts, ends))
					if(response.isSuccessful) {
//						Log.i(TAG, "updateMedicine: ${response.body()}")
					} else Log.e(TAG, "updateMedicine: $response")
				}
			}
			NOTES -> {
				val getNote = RetrofitAPI.api.getNote("Bearer ${getToken.access}", entry.id)
				if(getNote.isSuccessful) {
					val title = if(op["title"] == null) getNote.body()!!.title else op["title"]!!
					val content = if(op["content"] == null) getNote.body()!!.content else op["content"]!!
					val emotion = if(op["emotion"] == null) getNote.body()!!.emotion else op["emotion"]!!

					val response = RetrofitAPI.api.updateNote("Bearer ${getToken.access}", entry.id, NoteUpdateDTO(title, content, emotion))
					if(response.isSuccessful) {
//						Log.i(TAG, "updateNote: ${response.body()}")
					} else Log.e(TAG, "updateNote: $response")
				}
			}
			GOALS -> {
				val getGoal = RetrofitAPI.api.getGoal("Bearer ${getToken.access}", entry.id)
				if(getGoal.isSuccessful) {
					val weight = if(op["weight"] == null) getGoal.body()!!.weight else op["weight"]!!.toDouble()
					val kcalOfDiet = if(op["kcal_of_diet"] == null) getGoal.body()!!.kcalOfDiet else op["kcal_of_diet"]!!.toInt()
					val kcalOfWorkout = if(op["kcal_of_workout"] == null) getGoal.body()!!.kcalOfWorkout else op["kcal_of_workout"]!!.toInt()
					val waterAmountOfCup = if(op["water_amount_of_cup"] == null) getGoal.body()!!.waterAmountOfCup else op["water_amount_of_cup"]!!.toInt()
					val waterIntake = if(op["water_intake"] == null) getGoal.body()!!.waterIntake else op["water_intake"]!!.toInt()
					val sleep = if(op["sleep"] == null) getGoal.body()!!.sleep else op["sleep"]!!.toInt()
					val medicineIntake = if(op["medicine_intake"] == null) getGoal.body()!!.medicineIntake else op["medicine_intake"]!!.toInt()

					val response = RetrofitAPI.api.updateGoal("Bearer ${getToken.access}", entry.id, GoalUpdateDTO(weight, kcalOfDiet, kcalOfWorkout, waterAmountOfCup, waterIntake, sleep, medicineIntake))
					if(response.isSuccessful) {
//						Log.i(TAG, "updateGoal: ${response.body()}")
					} else Log.e(TAG, "updateGoal: $response")
				}
			}
		}
	}

	suspend fun delete(table: String, id: String) {
		when(table) {
			FOODS -> {
				val response = RetrofitAPI.api.deleteFood("Bearer ${getToken.access}", id)
				if(response.isSuccessful) {
//					Log.i(TAG, "deleteFood: ${response.raw().code}")
				} else Log.e(TAG, "deleteFood: $response")
			}
			DIETS -> {
				val response = RetrofitAPI.api.deleteDiet("Bearer ${getToken.access}", id)
				if(response.isSuccessful) {
//					Log.i(TAG, "deleteDiet: ${response.raw().code}")
				} else Log.e(TAG, "deleteDiet: $response")
			}
			WATER -> {
				val response = RetrofitAPI.api.deleteWater("Bearer ${getToken.access}", id)
				if(response.isSuccessful) {
//					Log.i(TAG, "deleteWater: ${response.raw().code}")
				} else Log.e(TAG, "deleteWater: $response")
			}
			ACTIVITIES -> {
				val response = RetrofitAPI.api.deleteActivity("Bearer ${getToken.access}", id)
				if(response.isSuccessful) {
//					Log.i(TAG, "deleteActivity: ${response.raw().code}")
				} else Log.e(TAG, "deleteActivity: $response")
			}
			WORKOUTS -> {
				val response = RetrofitAPI.api.deleteWorkout("Bearer ${getToken.access}", id)
				if(response.isSuccessful) {
//					Log.i(TAG, "deleteWorkout: ${response.raw().code}")
				} else Log.e(TAG, "deleteWorkout: $response")
			}
			BODY_MEASUREMENTS -> {
				val response = RetrofitAPI.api.deleteBody("Bearer ${getToken.access}", id)
				if(response.isSuccessful) {
//					Log.i(TAG, "deleteBody: ${response.raw().code}")
				} else Log.e(TAG, "deleteBody: $response")
			}
			MEDICINES -> {
				val response = RetrofitAPI.api.deleteMedicine("Bearer ${getToken.access}", id)
				if(response.isSuccessful) {
//					Log.i(TAG, "deleteMedicine: ${response.raw().code}")
				} else Log.e(TAG, "deleteMedicine: $response")
			}
			MEDICINE_TIMES -> {
				val response = RetrofitAPI.api.deleteMedicineTime("Bearer ${getToken.access}", id)
				if(response.isSuccessful) {
//					Log.i(TAG, "deleteMedicineTime: ${response.raw().code}")
				} else Log.e(TAG, "deleteMedicineTime: $response")
			}
			MEDICINE_INTAKES -> {
				val response = RetrofitAPI.api.deleteMedicineIntake("Bearer ${getToken.access}", id)
				if(response.isSuccessful) {
//					Log.i(TAG, "deleteMedicineIntake: ${response.raw().code}")
				} else Log.e(TAG, "deleteMedicineIntake: $response")
			}
			FILES -> {
				val response = RetrofitAPI.api.deleteFile("Bearer ${getToken.access}", id)
				if(response.isSuccessful) {
//					Log.i(TAG, "deleteFile: ${response.raw().code}")
				} else Log.e(TAG, "deleteFile: $response")
			}
		}
	}
}