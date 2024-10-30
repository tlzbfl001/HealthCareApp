package kr.bodywell.android.api

import com.powersync.DatabaseDriverFactory
import com.powersync.PowerSyncDatabase
import com.powersync.connectors.PowerSyncBackendConnector
import com.powersync.connectors.PowerSyncCredentials
import kotlinx.coroutines.runBlocking
import kr.bodywell.android.model.Food
import kr.bodywell.android.util.MyApp.Companion.getToken
import kr.bodywell.android.util.MyApp.Companion.getUser

class PowerSync(
	driverFactory: DatabaseDriverFactory
) {
	class MyConnector : PowerSyncBackendConnector() {
		private val powerSyncEndpoint = "https://6711c44725a60ad3b2df2ae6.powersync.journeyapps.com"

		override suspend fun fetchCredentials(): PowerSyncCredentials {
			return PowerSyncCredentials(
				powerSyncEndpoint,
				getUser.uid,
				getToken.access
			)
		}

		override suspend fun uploadData(database: PowerSyncDatabase) {}
	}

	private val database = PowerSyncDatabase(driverFactory, AppSchema)

	private val db: PowerSyncDatabase
		get() = database

	init {
		runBlocking {
			db.connect(MyConnector())
		}
	}

//	fun watchUsers(): Flow<List<User>> {
//		return database.watch("SELECT * FROM customers", mapper = { cursor ->
//			User(
//				id = cursor.getString(0)!!,
//				name = cursor.getString(1)!!,
//				email = cursor.getString(2)!!
//			)
//		})
//	}

	suspend fun getFood(id: Any): Food {
		return database.get("SELECT * FROM foods WHERE id = ?", listOf(id), mapper = { cursor ->
			Food(
				name = cursor.getString(0)!!
			)
		})
	}

	suspend fun insertFood(data: Food): String? {
		database.writeTransaction {
			database.execute(
				sql = "INSERT INTO foods(id, name, calorie, carbohydrate, protein, fat, quantity, quantity_unit, volume, volume_unit, " +
					"register_type, created_at, user_id VALUES (uuid(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				parameters = listOf(data.name, data.calorie, data.carbohydrate, data.protein, data.fat, 1, "개", data.amount, data.unit,
					data.registerType, data.createdAt, getUser.uid)
			)
		}

		val targetId = database.getOptional(
				sql = "SELECT id FROM foods ORDER BY created_at DESC LIMIT 1",
				mapper = { cursor ->
					cursor.getString(0)!!
				}
			) ?: return null

		return targetId
	}

	suspend fun updateFood(id: String, name: String) {
		database.execute(
			sql = "UPDATE customers SET name = ? WHERE id = ?",
			parameters = listOf(name, id)
		)
	}

	suspend fun deleteFood(id: String? = null) {
		val targetId =
			id ?: database.getOptional(
				sql = "SELECT id FROM customers LIMIT 1",
				mapper = { cursor ->
					cursor.getString(0)!!
				}
			) ?: return

		database.writeTransaction {
			database.execute(
				sql = "DELETE FROM customers WHERE id = ?",
				parameters = listOf(targetId)
			)
		}
	}
}