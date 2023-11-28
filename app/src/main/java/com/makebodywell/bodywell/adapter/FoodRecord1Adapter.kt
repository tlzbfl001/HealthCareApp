package com.makebodywell.bodywell.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.model.Food

class FoodRecord1Adapter (
   private var itemList: ArrayList<Food> = ArrayList<Food>()
) : RecyclerView.Adapter<FoodRecord1Adapter.ViewHolder>() {

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_record1, parent, false)
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.tvName.text = itemList[position].name
      holder.tvUnit.text = itemList[position].unit
      holder.tvKcal.text = "${itemList[position].kcal!!.toInt() * itemList[position].amount} kcal"
      holder.tvCal.text = itemList[position].carbohydrate + "g"
      holder.tvProtein.text = itemList[position].protein + "g"
      holder.tvFat.text = itemList[position].fat + "g"
   }

   override fun getItemCount(): Int {
      return itemList.count()
   }

   inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      val tvName: TextView = itemView.findViewById(R.id.tvName)
      val tvUnit: TextView = itemView.findViewById(R.id.tvUnit)
      val tvKcal: TextView = itemView.findViewById(R.id.tvKcal)
      val tvCal: TextView = itemView.findViewById(R.id.tvCal)
      val tvProtein: TextView = itemView.findViewById(R.id.tvProtein)
      val tvFat: TextView = itemView.findViewById(R.id.tvFat)
   }
}