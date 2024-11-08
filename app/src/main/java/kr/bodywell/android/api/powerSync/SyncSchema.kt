package kr.bodywell.android.api.powerSync

import com.powersync.db.schema.Column
import com.powersync.db.schema.Schema
import com.powersync.db.schema.Table

val SyncSchema: Schema = Schema(
	listOf(
		Table(
			name = "foods",
			columns = listOf(
				Column.text("name"),
				Column.integer("calorie"),
				Column.real("carbohydrate"),
				Column.real("protein"),
				Column.real("fat"),
				Column.integer("quantity"),
				Column.text("quantity_unit"),
				Column.integer("volume"),
				Column.text("volume_unit"),
				Column.text("register_type"),
				Column.text("created_at"),
				Column.text("updated_at"),
				Column.text("deleted_at"),
				Column.text("user_id")
			)
		),
		Table(
			name = "diets",
			columns = listOf(
				Column.text("meal_time"),
				Column.text("name"),
				Column.integer("calorie"),
				Column.real("carbohydrate"),
				Column.real("protein"),
				Column.real("fat"),
				Column.integer("quantity"),
				Column.text("quantity_unit"),
				Column.integer("volume"),
				Column.text("volume_unit"),
				Column.text("photos"),
				Column.text("date"),
				Column.text("created_at"),
				Column.text("updated_at"),
				Column.text("deleted_at"),
				Column.text("user_id"),
				Column.text("food_id")
			)
		),
		Table(
			name = "water",
			columns = listOf(
				Column.text("mL"),
				Column.text("count"),
				Column.text("date")
			)
		),
		Table(
			name = "medicines",
			columns = listOf(
				Column.text("category"),
				Column.text("name"),
				Column.integer("amount"),
				Column.text("unit"),
				Column.text("starts"),
				Column.text("ends"),
				Column.text("created_at"),
				Column.text("updated_at"),
				Column.text("deleted_at"),
				Column.text("user_id")
			)
		),
		Table(
			name = "medicine_times",
			columns = listOf(
				Column.text("time"),
				Column.text("created_at"),
				Column.text("updated_at"),
				Column.text("deleted_at"),
				Column.text("medicine_id")
			)
		),
		Table(
			name = "medicine_intakes",
			columns = listOf(
				Column.text("category"),
				Column.text("name"),
				Column.integer("amount"),
				Column.text("unit"),
				Column.text("intaked_at"),
				Column.text("created_at"),
				Column.text("updated_at"),
				Column.text("deleted_at"),
				Column.text("medicine_time_id"),
				Column.text("source_id")
			)
		),
		Table(
			name = "goals",
			columns = listOf(
				Column.real("weight"),
				Column.integer("kcal_of_diet"),
				Column.integer("kcal_of_workout"),
				Column.integer("water_amount_of_cup"),
				Column.integer("water_intake"),
				Column.integer("sleep"),
				Column.integer("medicine_intake"),
				Column.text("date"),
				Column.text("created_at"),
				Column.text("updated_at"),
				Column.text("deleted_at"),
				Column.text("user_id")
			)
		)
	)
)