package com.makebodywell.bodywell.adapter

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import com.makebodywell.bodywell.view.home.food.FoodEditFragment
import com.makebodywell.bodywell.view.home.food.FoodRecord2Fragment

class FoodRecord1Adapter (
   private val context: Activity,
   private var itemList: ArrayList<Food> = ArrayList()
) : RecyclerView.Adapter<FoodRecord1Adapter.ViewHolder>() {
   private var bundle = Bundle()

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_record1, parent, false)
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.textView.text = itemList[position].name

      holder.textView.setOnClickListener {
         bundle.putString("id", itemList[position].id.toString())
         replaceFragment2(context, FoodEditFragment(), bundle)
      }
   }

   override fun getItemCount(): Int {
      return itemList.count()
   }

   inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      val textView: TextView = itemView.findViewById(R.id.textView)
   }
}