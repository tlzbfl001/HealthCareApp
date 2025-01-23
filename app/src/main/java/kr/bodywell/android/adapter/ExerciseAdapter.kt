package kr.bodywell.android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.bodywell.android.R
import kr.bodywell.android.model.Workout

class ExerciseAdapter (
   private val itemList: ArrayList<Workout>
) : RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise_record1, parent, false)
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.tvName.text = itemList[position].name
      holder.tvTime.text = "${itemList[position].time}분"
      holder.tvKcal.text = "${itemList[position].calorie} kcal"
   }

   override fun getItemViewType(position: Int): Int {
      return position
   }

   override fun getItemCount(): Int {
      return itemList.count()
   }

   inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      val tvName: TextView = itemView.findViewById(R.id.tvName)
      val tvTime: TextView = itemView.findViewById(R.id.tvTime)
      val tvKcal: TextView = itemView.findViewById(R.id.tvKcal)
   }
}