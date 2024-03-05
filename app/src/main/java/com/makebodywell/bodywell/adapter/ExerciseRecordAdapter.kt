package com.makebodywell.bodywell.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.model.Exercise

class ExerciseRecordAdapter (
   private var itemList: ArrayList<Exercise> = ArrayList<Exercise>()
) : RecyclerView.Adapter<ExerciseRecordAdapter.ViewHolder>() {
   private var onItemClickListener: OnItemClickListener? = null

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false)
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.textView.text = itemList[position].name

      holder.mainLayout.setOnClickListener {
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
      val mainLayout: ConstraintLayout = itemView.findViewById(R.id.mainLayout)
      val textView: TextView = itemView.findViewById(R.id.textView)
   }
}