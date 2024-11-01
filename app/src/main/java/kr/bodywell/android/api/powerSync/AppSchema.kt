package kr.bodywell.android.api.powerSync

import com.powersync.db.schema.Column
import com.powersync.db.schema.Schema
import com.powersync.db.schema.Table

val AppSchema: Schema = Schema(
	listOf(
		Table(
			localOnly = false,
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
			localOnly = false,
			name = "diets",
			columns = listOf(
				Column.text("meal_time"),
				Column.text("name"),
				Column.text("calorie"),
				Column.text("carbohydrate"),
				Column.text("protein"),
				Column.real("fat"),
				Column.real("quantity"),
				Column.integer("quantity_unit"),
				Column.text("volume"),
				Column.text("volume_unit"),
				Column.text("photos"),
				Column.text("date"),
				Column.text("created_at"),
				Column.text("updated_at"),
				Column.text("deleted_at"),
				Column.text("user_id"),
				Column.text("food_id")
			)
		)
	)
)