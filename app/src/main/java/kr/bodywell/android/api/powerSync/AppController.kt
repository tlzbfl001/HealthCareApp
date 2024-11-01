package kr.bodywell.android.api.powerSync

import android.content.Context
import android.util.Log
import com.powersync.db.crud.CrudEntry
import kr.bodywell.android.api.RetrofitAPI
import kr.bodywell.android.api.dto.FoodDTO
import kr.bodywell.android.database.DBHelper.Companion.FOOD
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.CustomUtil.getToken

object AppController {
	suspend fun insert(context: Context, entry: CrudEntry) {
		val dataManager = DataManager(context)
		dataManager.open()

		when(entry.table) {
			"foods" -> {
				val response = RetrofitAPI.api.createFood("Bearer ${getToken.access}", FoodDTO(entry.opData!!["name"]!!, entry.opData!!["calorie"]!!.toInt(),
					entry.opData!!["carbohydrate"]!!.toDouble(), entry.opData!!["protein"]!!.toDouble(), entry.opData!!["fat"]!!.toDouble(),
					entry.opData!!["quantity"]!!.toInt(), entry.opData!!["quantity_unit"]!!, entry.opData!!["volume"]!!.toInt(), entry.opData!!["volume_unit"]!!))
				if(response.isSuccessful) {
					Log.d(TAG, "createFood: ${response.body()}")
					dataManager.updateStrByStr(FOOD, "uid", response.body()!!.id, "name", entry.opData!!["name"]!!)
				}else {
					Log.e(TAG, "createFood: $response")
				}
			}
			"water" -> {

			}
		}
	}
}