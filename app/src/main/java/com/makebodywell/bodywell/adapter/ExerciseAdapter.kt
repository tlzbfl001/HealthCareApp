package com.makebodywell.bodywell.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.model.Exercise

class ExerciseAdapter (
   private val itemList: ArrayList<Exercise>
) : RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise_main, parent, false)
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.tvCategory.text = itemList[position].category
      holder.tvName.text = itemList[position].name
      holder.tvWorkoutTime.text = "${itemList[position].workoutTime}분"
      holder.tvDistance.text = itemList[position].distance.toString() + "km"
      holder.tvKcal.text = itemList[position].calories.toString() + "kcal"
   }

   override fun getItemCount(): Int {
      return itemList.count()
   }

   inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
      val tvName: TextView = itemView.findViewById(R.id.tvName)
      val tvWorkoutTime: TextView = itemView.findViewById(R.id.tvWorkoutTime)
      val tvDistance: TextView = itemView.findViewById(R.id.tvDistance)
      val tvKcal: TextView = itemView.findViewById(R.id.tvKcal)
   }
}