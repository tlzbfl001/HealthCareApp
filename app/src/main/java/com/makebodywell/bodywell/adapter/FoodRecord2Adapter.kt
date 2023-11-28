package com.makebodywell.bodywell.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.model.Food

class FoodRecord2Adapter (
   private var itemList: ArrayList<Food>
) : RecyclerView.Adapter<FoodRecord2Adapter.ViewHolder>() {

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_record2, parent, false)
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.tvName.text = itemList[position].name
      holder.ivStar.setImageResource(itemList[position].star)
   }

   override fun getItemCount(): Int {
      return itemList.count()
   }

   inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      val tvName: TextView = itemView.findViewById(R.id.tvName)
      val ivStar: ImageView = itemView.findViewById(R.id.ivStar)
   }
}