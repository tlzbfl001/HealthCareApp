package com.makebodywell.bodywell.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R

class ExerciseCategoryAdapter (
   private val context: Context,
   private val itemList: ArrayList<String>
) : RecyclerView.Adapter<ExerciseCategoryAdapter.ViewHolder>() {
   private var itemClickListener : OnItemClickListener? = null
   private var selectPos = -1
   var categorySelected = ""

   interface OnItemClickListener {
      fun onClick(v: View, position: Int)
   }

   fun setItemClickListener(onItemClickListener: OnItemClickListener) {
      this.itemClickListener = onItemClickListener
   }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise_input, parent, false)
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.tvName.text = itemList[position]

      if(selectPos == holder.adapterPosition) {
         holder.clBox.background = context.getDrawable(R.drawable.rec_5_purple)
         holder.tvName.setTextColor(Color.WHITE)
         categorySelected = itemList[position]
      }else {
         holder.clBox.background = context.getDrawable(R.drawable.rec_5_border_gray)
         holder.tvName.setTextColor(Color.BLACK)
      }

      holder.clBox.setOnClickListener {
         val beforePos = selectPos
         selectPos = holder.adapterPosition

         notifyItemChanged(beforePos)
         notifyItemChanged(selectPos)
      }

      holder.clDelete.setOnClickListener {
         itemClickListener?.onClick(it, holder.adapterPosition)
      }
   }

   override fun getItemCount(): Int {
      return itemList.count()
   }

   inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      val clBox: ConstraintLayout = itemView.findViewById(R.id.clBox)
      val tvName: TextView = itemView.findViewById(R.id.tvName)
      val clDelete: ConstraintLayout = itemView.findViewById(R.id.clDelete)
   }
}