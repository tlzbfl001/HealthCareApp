package com.makebodywell.bodywell.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.model.Item

class FoodRecordAdapter (
   private var itemList: ArrayList<Item> = ArrayList()
) : RecyclerView.Adapter<FoodRecordAdapter.ViewHolder>() {
   private var onItemClickListener: OnItemClickListener? = null

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_record1, parent, false)
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.textView.text = itemList[position].string1

      holder.textView.setOnClickListener {
         onItemClickListener!!.onItemClick(position)
      }
   }

   override fun getItemCount(): Int {
      return itemList.count()
   }

   interface OnItemClickListener {
      fun onItemClick(pos: Int)
   }

   fun setOnItemClickListener(listener: OnItemClickListener?) {
      onItemClickListener = listener
   }

   inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      val textView: TextView = itemView.findViewById(R.id.textView)
   }
}