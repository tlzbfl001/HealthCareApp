package kr.bodywell.android.api.powerSync

import android.util.Log
import com.powersync.db.crud.CrudEntry
import kr.bodywell.android.api.RetrofitAPI
import kr.bodywell.android.api.dto.DietDTO
import kr.bodywell.android.api.dto.DietUpdateDTO
import kr.bodywell.android.api.dto.FoodDTO
import kr.bodywell.android.api.dto.FoodDTOData
import kr.bodywell.android.api.dto.FoodUpdateDTO
import kr.bodywell.android.api.dto.MedicineDTO
import kr.bodywell.android.api.dto.MedicineTimeDTO
import kr.bodywell.android.api.dto.WaterDTO
import kr.bodywell.android.api.dto.WaterUpdateDTO
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.CustomUtil.dateToIso
import kr.bodywell.android.util.CustomUtil.getToken
import kr.bodywell.android.util.CustomUtil.powerSync

object SyncController {
	suspend fun upsert(entry: CrudEntry) {
		when(entry.table) {
			"foods" -> {
				val op = entry.opData
				val list = ArrayList<FoodDTOData>()
				list.add(FoodDTOData(entry.id, op!!["name"]!!, op["calorie"]!!.toInt(),
					op["carbohydrate"]!!.toDouble(), op["protein"]!!.toDouble(), op["fat"]!!.toDouble(), op["quantity"]!!.toInt(),
					op["quantity_unit"]!!, op["volume"]!!.toInt(), op["volume_unit"]!!, op["created_at"]!!, op["updated_at"]!!))

				val response = RetrofitAPI.api.createFood("Bearer ${getToken.access}", FoodDTO(list))
				if(response.isSuccessful) {
					Log.d(TAG, "createFood: ${response.body()}")
				}else {
					Log.e(TAG, "createFood: $response")
				}
			}
			"diets" -> {
				val op = entry.opData
				val photos = ArrayList<String>()
				photos.add("https://example.com/picture.jpg")
				val foodId = powerSync.getData("foods", "name", op!!["name"]!!).id
				val dateToIso = dateToIso(op["date"]!!)
				val response = RetrofitAPI.api.createDiets("Bearer ${getToken.access}", DietDTO(op["meal_time"]!!, op["name"]!!, op["calorie"]!!.toInt(),
					op["carbohydrate"]!!.toDouble(), op["protein"]!!.toDouble(), op["fat"]!!.toDouble(), 1, "개", op["volume"]!!.toInt(), op["volume_unit"]!!,
					photos, dateToIso, foodId))

				if(response.isSuccessful) {
					Log.d(TAG, "createDiets: ${response.body()}")
				}else {
					Log.e(TAG, "createDiets: $response")
				}
			}
			"water" -> {
				val op = entry.opData
				val response = RetrofitAPI.api.createWater("Bearer ${getToken.access}", WaterDTO("", op!!["mL"]!!.toInt(), op["count"]!!.toInt(),
					op["date"]!!, op["created_at"]!!, op["updated_at"]!!))
				if(response.isSuccessful) {
					Log.d(TAG, "createWater: ${response.body()}")
				}else {
					Log.e(TAG, "createWater: $response")
				}
			}
			"medicines" -> {
				val op = entry.opData
				val response = RetrofitAPI.api.createMedicine("Bearer ${getToken.access}", MedicineDTO(op!!["category"]!!, op["name"]!!,
					op["amount"]!!.toInt(), op["unit"]!!, op["starts"]!!, op["ends"]!!))
				if(response.isSuccessful) {
					Log.d(TAG, "createMedicine: ${response.body()}")
				}else {
					Log.e(TAG, "createMedicine: $response")
				}
			}
			"medicine_times" -> {
				val op = entry.opData
				val split = op!!["time"]!!.split(" ", limit=2)
				var id = ""

				val getAllMedicine = RetrofitAPI.api.getAllMedicine("Bearer ${getToken.access}")
				if(getAllMedicine.isSuccessful) {
					for(i in 0 until getAllMedicine.body()!!.size){
						if(getAllMedicine.body()!![i].starts.substring(0, 10) == split[0]) id = getAllMedicine.body()!![i].id
					}
					val response = RetrofitAPI.api.createMedicineTime("Bearer ${getToken.access}", id, MedicineTimeDTO(split[1]))
					if(response.isSuccessful) {
						Log.d(TAG, "createMedicineTime: ${response.body()}")
					}else {
						Log.e(TAG, "createMedicineTime: $response")
					}
				}else {
					Log.e(TAG, "createMedicine: $getAllMedicine")
				}
			}
		}
	}

	suspend fun update(table: String, id: String, op: Map<String, String?>?) {
		when(table) {
			"foods" -> {
				val getFood = RetrofitAPI.api.getFood("Bearer ${getToken.access}", id)
				if(getFood.isSuccessful) {
					val calorie = if(op!!["calorie"] == null) getFood.body()!!.calorie else op["calorie"]!!.toInt()
					val carbohydrate = if(op["carbohydrate"] == null) getFood.body()!!.carbohydrate else op["carbohydrate"]!!.toDouble()
					val protein = if(op["protein"] == null) getFood.body()!!.protein else op["protein"]!!.toDouble()
					val fat = if(op["fat"] == null) getFood.body()!!.fat else op["fat"]!!.toDouble()
					val volume = if(op["volume"] == null) getFood.body()!!.volume else op["volume"]!!.toInt()
					val volumeUnit = if(op["volume_unit"] == null) getFood.body()!!.volumeUnit else op["volume_unit"]

					val updateFood = RetrofitAPI.api.updateFood("Bearer ${getToken.access}", id, FoodUpdateDTO(calorie, carbohydrate, protein, fat, volume, volumeUnit!!))
					if(updateFood.isSuccessful) {
						Log.d(TAG, "updateFood: ${updateFood.body()}")
					}else {
						Log.e(TAG, "updateFood: $updateFood")
					}
				}else {
					Log.e(TAG, "getFood: $getFood")
				}
			}
			"diets" -> {
				val updateDiets = RetrofitAPI.api.updateDiets("Bearer ${getToken.access}", id, DietUpdateDTO(op!!["calorie"]!!.toInt(),
					op["carbohydrate"]!!.toDouble(), op["protein"]!!.toDouble(), op["fat"]!!.toDouble(), op["quantity"]!!.toInt(), op["volume"]!!.toInt()))
				if(updateDiets.isSuccessful) {
					Log.d(TAG, "updateDiets: ${updateDiets.body()}")
				}else {
					Log.e(TAG, "updateDiets: $updateDiets")
				}
			}
			"water" -> {
				val response = RetrofitAPI.api.updateWater("Bearer ${getToken.access}", id, WaterUpdateDTO(op!!["mL"]!!.toInt(), op["count"]!!.toInt()))
				if(response.isSuccessful) {
					Log.d(TAG, "updateWater: ${response.body()}")
				}else {
					Log.e(TAG, "updateWater: $response")
				}
			}
		}
	}

	suspend fun delete(table: String, id: String) {
		when(table) {
			"foods" -> {
				val response = RetrofitAPI.api.deleteFood("Bearer ${getToken.access}", id)
				if(response.isSuccessful) {
					Log.d(TAG, "deleteFood: ${response.body()}")
				}else {
					Log.e(TAG, "deleteFood: $response")
				}
			}
			"diets" -> {
				val response = RetrofitAPI.api.deleteDiet("Bearer ${getToken.access}", id)
				if(response.isSuccessful) {
					Log.d(TAG, "deleteDiet: ${response.body()}")
				}else {
					Log.e(TAG, "deleteDiet: $response")
				}
			}
			"water" -> {
				val response = RetrofitAPI.api.deleteWater("Bearer ${getToken.access}", id)
				if(response.isSuccessful) {
					Log.d(TAG, "deleteWater: ${response.body()}")
				}else {
					Log.e(TAG, "deleteWater: $response")
				}
			}
			"medicines" -> {
				val response = RetrofitAPI.api.deleteMedicine("Bearer ${getToken.access}", id)
				if(response.isSuccessful) {
					Log.d(TAG, "deleteMedicine: ${response.body()}")
				}else {
					Log.e(TAG, "deleteMedicine: $response")
				}
			}
		}
	}
}