package com.makebodywell.bodywell.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.model.Exercise

class ExerciseListAdapter (
   private val context: Context,
   private val itemList: ArrayList<Exercise>
) : RecyclerView.Adapter<ExerciseListAdapter.ViewHolder>() {
   private var dataManager: DataManager? = null

   init {
      dataManager = DataManager(context)
      dataManager!!.open()
   }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise_list, parent, false)
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.tvCategory.text = itemList[position].category
      holder.tvName.text = itemList[position].name
      holder.tvWorkoutTime.text = itemList[position].workoutTime
      holder.tvDistance.text = itemList[position].distance.toString()
      holder.tvKcal.text = itemList[position].calories.toString()

      holder.clDelete.setOnClickListener {
         val dialog = AlertDialog.Builder(context)
            .setMessage("정말 삭제하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
               dataManager!!.deleteExercise(itemList[position].id!!)
               itemList.removeAt(position)

               notifyDataSetChanged()
               Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("취소", null)
            .create()
         dialog.show()
      }
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
      val clDelete: ConstraintLayout = itemView.findViewById(R.id.clDelete)
   }
}