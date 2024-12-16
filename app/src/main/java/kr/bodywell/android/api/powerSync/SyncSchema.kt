package kr.bodywell.android.api.powerSync

import com.powersync.db.schema.Column
import com.powersync.db.schema.Schema
import com.powersync.db.schema.Table

val SyncSchema: Schema = Schema(
	listOf(
		Table(
			name = "profiles",
			columns = listOf(
				Column.text("name"),
				Column.text("birth"),
				Column.real("height"),
				Column.real("weight"),
				Column.text("created_at"),
				Column.text("updated_at"),
				Column.text("user_id"),
				Column.text("gender")
			)
		),
		Table(
			name = "files",
			columns = listOf(
				Column.text("name"),
				Column.text("size"),
				Column.text("created_at"),
				Column.text("updated_at"),
				Column.text("data"),
				Column.text("filetype"),
				Column.text("mimetype"),
				Column.text("user_id"),
				Column.text("diet_id"),
				Column.text("profile_id")
			)
		),
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
				Column.text("user_id")
			)
		),
		Table(
			name = "food_usages",
			columns = listOf(
				Column.text("usage_count"),
				Column.text("created_at"),
				Column.text("updated_at"),
				Column.text("food_id"),
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
				Column.text("date"),
				Column.text("created_at"),
				Column.text("updated_at"),
				Column.text("user_id"),
				Column.text("food_id")
			)
		),
		Table(
			name = "water",
			columns = listOf(
				Column.integer("mL"),
				Column.integer("count"),
				Column.text("date"),
				Column.text("created_at"),
				Column.text("updated_at"),
				Column.text("deleted_at"),
				Column.text("user_id")
			)
		),
		Table(
			name = "activities",
			columns = listOf(
				Column.text("name"),
				Column.text("register_type"),
				Column.text("created_at"),
				Column.text("updated_at"),
				Column.text("deleted_at"),
				Column.text("user_id")
			)
		),
		Table(
			name = "workouts",
			columns = listOf(
				Column.text("name"),
				Column.integer("calorie"),
				Column.text("intensity"),
				Column.integer("time"),
				Column.text("date"),
				Column.text("created_at"),
				Column.text("updated_at"),
				Column.text("deleted_at"),
				Column.text("activity_id"),
				Column.text("user_id")
			)
		),
		Table(
			name = "body_measurements",
			columns = listOf(
				Column.real("height"),
				Column.real("weight"),
				Column.real("body_mass_index"),
				Column.real("body_fat_percentage"),
				Column.real("skeletal_muscle_mass"),
				Column.real("basal_metabolic_rate"),
				Column.integer("workout_intensity"),
				Column.text("time"),
				Column.text("created_at"),
				Column.text("updated_at"),
				Column.text("deleted_at"),
				Column.text("user_id")
			)
		),
		Table(
			name = "sleep",
			columns = listOf(
				Column.text("starts"),
				Column.text("ends"),
				Column.text("created_at"),
				Column.text("updated_at"),
				Column.text("deleted_at"),
				Column.text("user_id")
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
				Column.text("medicine_id"),
				Column.text("user_id")
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
				Column.text("medicine_id"),
				Column.text("user_id")
			)
		),
		Table(
			name = "notes",
			columns = listOf(
				Column.text("title"),
				Column.text("content"),
				Column.text("emotion"),
				Column.text("date"),
				Column.text("created_at"),
				Column.text("updated_at"),
				Column.text("user_id")
			)
		),
		Table(
			name = "goals",
			columns = listOf(
				Column.text("weight"),
				Column.text("kcal_of_diet"),
				Column.text("kcal_of_workout"),
				Column.text("water_amount_of_cup"),
				Column.text("water_intake"),
				Column.text("sleep"),
				Column.text("medicine_intake"),
				Column.text("date"),
				Column.text("created_at"),
				Column.text("updated_at"),
				Column.text("deleted_at"),
				Column.text("user_id")
			)
		)
	)
)