package com.makebodywell.bodywell.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.model.Exercise
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import com.makebodywell.bodywell.view.home.exercise.ExerciseDetailFragment
import com.makebodywell.bodywell.view.home.food.FoodBreakfastFragment

class ExerciseRecord1Adapter (
    private val context: FragmentActivity,
    private val itemList: ArrayList<Exercise>
) : RecyclerView.Adapter<ExerciseRecord1Adapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise_record1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = itemList[position].name

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            val exercise = Exercise(id = itemList[position].id, name = itemList[position].name, workoutTime = itemList[position].workoutTime,
                calories = itemList[position].calories)
            bundle.putParcelable("exercise", exercise)

            replaceFragment2(context, ExerciseDetailFragment(), bundle)
        }
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
    }
}