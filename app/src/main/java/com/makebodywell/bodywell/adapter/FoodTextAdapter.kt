package com.makebodywell.bodywell.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.model.FoodText

class FoodTextAdapter (
   private var itemList: ArrayList<FoodText> = ArrayList<FoodText>()
) : RecyclerView.Adapter<FoodTextAdapter.ViewHolder>() {

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_text, parent, false)
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: FoodTextAdapter.ViewHolder, position: Int) {
      holder.tvName1.text = itemList[position].name1
      holder.tvName2.text = itemList[position].name2
      holder.tvName3.text = itemList[position].name3

      if(itemList[position].kcal1 != 0) {
         val text = "${itemList[position].kcal1} kcal"
         holder.tvKcal1.text = text
      }
      if(itemList[position].kcal2 != 0) {
         val text = "${itemList[position].kcal2} kcal"
         holder.tvKcal2.text = text
      }
      if(itemList[position].kcal3 != 0) {
         val text = "${itemList[position].kcal3} kcal"
         holder.tvKcal3.text = text
      }

      if(itemList[position].unit1 != "") {
         val text = "(" + itemList[position].unit1 + ")"
         holder.tvUnit1.text = text
      }
      if(itemList[position].unit2 != "") {
         val text = "(" + itemList[position].unit2 + ")"
         holder.tvUnit2.text = text
      }
      if(itemList[position].unit3 != "") {
         val text = "(" + itemList[position].unit3 + ")"
         holder.tvUnit3.text = text
      }
   }

   override fun getItemCount(): Int {
      return itemList.count()
   }

   inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      val tvName1: TextView = itemView.findViewById(R.id.tvName1)
      val tvKcal1: TextView = itemView.findViewById(R.id.tvKcal1)
      val tvUnit1: TextView = itemView.findViewById(R.id.tvUnit1)
      val tvName2: TextView = itemView.findViewById(R.id.tvName2)
      val tvKcal2: TextView = itemView.findViewById(R.id.tvKcal2)
      val tvUnit2: TextView = itemView.findViewById(R.id.tvUnit2)
      val tvName3: TextView = itemView.findViewById(R.id.tvName3)
      val tvKcal3: TextView = itemView.findViewById(R.id.tvKcal3)
      val tvUnit3: TextView = itemView.findViewById(R.id.tvUnit3)
   }
}