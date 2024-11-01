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
import kr.bodywell.android.api.powerSync.AppController.insert
import kr.bodywell.android.model.Food
import kr.bodywell.android.util.CustomUtil

class AppService(context: Context) {
	private val _context = context
	private val driverFactory = DatabaseDriverFactory(context)
	private val database = PowerSyncDatabase(driverFactory, AppSchema)
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
				CustomUtil.getToken.access,
				CustomUtil.getUser.uid
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
							Log.d(CustomUtil.TAG, "table: ${entry.table} / data: ${entry.opData} / id:${entry.id}")
							insert(_context, entry)
						}

						UpdateType.PATCH -> {
							Log.d(CustomUtil.TAG, "opData: ${entry.opData} / id: ${entry.id}")
						}

						UpdateType.DELETE -> {
							Log.d(CustomUtil.TAG, "opData: ${entry.opData} / id: ${entry.id}")
						}
					}
				}

				transaction.complete(null);

			} catch (e: Exception) {
				println("Data upload error - retrying last entry: ${lastEntry!!}, $e")
				throw e
			}
		}
	}

	suspend fun getFood(): Food {
		return database.getOptional(
			sql = "SELECT * FROM foods order by created_at desc LIMIT 1", mapper = { cursor ->
				Food(
					uid = cursor.getString(0)!!,
					name = cursor.getString(1)!!,
					calorie = cursor.getDouble(2)!!.toInt(),
					carbohydrate = cursor.getDouble(3)!!,
					protein = cursor.getDouble(4)!!,
					fat = cursor.getDouble(5)!!,
					amount = cursor.getDouble(8)!!.toInt(),
					unit = cursor.getString(9)!!
				)
			}
		) ?: return Food()
	}

	fun watchFood(): Flow<List<Food>> {
		// TODO: implement your UI based on the result set
		return database.watch("SELECT * FROM foods", mapper = { cursor ->
			Food(
				uid = cursor.getString(0)!!,
				name = cursor.getString(1)!!,
				calorie = cursor.getDouble(2)!!.toInt(),
				carbohydrate = cursor.getDouble(3)!!,
				protein = cursor.getDouble(4)!!,
				fat = cursor.getDouble(5)!!,
				amount = cursor.getDouble(8)!!.toInt(),
				unit = cursor.getString(9)!!
			)
		})
	}

	suspend fun insertFood(data: Food){
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO foods(id, name, calorie, carbohydrate, protein, fat, quantity, quantity_unit, volume, volume_unit) " +
					"VALUES (uuid(), ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				parameters = listOf(data.name, "${data.calorie}", "${data.carbohydrate}", "${data.protein}", "${data.fat}", "1", "개", "${data.amount}", data.unit)
			)
		}
	}

	suspend fun updateFood(data: Food) {
		database.execute(
			sql = "UPDATE foods SET calorie = ?, carbohydrate = ?, protein = ?, fat = ?, volume = ?, volume_unit = ? WHERE id = ?",
			parameters = listOf(data.calorie.toString(), data.carbohydrate.toString(), data.protein.toString(), data.fat.toString(), data.amount.toString(), data.unit, data.uid)
		)
	}

	suspend fun deleteFood(id: String): Long {
		return database.writeTransaction {
			database.execute(
				sql = "DELETE FROM foods WHERE id = ?",
				parameters = listOf(id)
			)
		}
	}
}