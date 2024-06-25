package kr.bodywell.android.adapter

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kr.bodywell.android.R
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DAILY_EXERCISE
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.Exercise
import kr.bodywell.android.model.Unused
import kr.bodywell.android.util.CalendarUtil
import kr.bodywell.android.util.CalendarUtil.Companion.selectedDate
import kr.bodywell.android.util.CustomUtil
import kr.bodywell.android.util.CustomUtil.Companion.replaceFragment2
import kr.bodywell.android.view.home.exercise.ExerciseDailyEditFragment

class ExerciseListAdapter (
   private val context: Activity,
   private val itemList: ArrayList<Exercise>
) : RecyclerView.Adapter<ExerciseListAdapter.ViewHolder>() {
   private var bundle = Bundle()
   private var dataManager: DataManager = DataManager(context)

   init { dataManager.open() }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise_list, parent, false)
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
      holder.tvName.text = itemList[pos].name
      holder.tvTime.text = "${itemList[pos].workoutTime}분"
      holder.tvKcal.text = "${itemList[pos].kcal} kcal"

      holder.cl.setOnClickListener {
         bundle.putString("id", itemList[pos].id.toString())
         replaceFragment2(context, ExerciseDailyEditFragment(), bundle)
      }

      holder.clX.setOnClickListener {
         val dialog = AlertDialog.Builder(context, R.style.AlertDialogStyle)
            .setTitle("운동 삭제")
            .setMessage("정말 삭제하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
               if(itemList[pos].uid != "") dataManager.insertUnused(Unused(type = "dailyExercise", value = itemList[pos].uid, regDate = itemList[pos].regDate))

               dataManager.deleteItem(TABLE_DAILY_EXERCISE, "id", itemList[pos].id)

               itemList.removeAt(pos)
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
      val cl: ConstraintLayout = itemView.findViewById(R.id.cl)
      val tvName: TextView = itemView.findViewById(R.id.tvName)
      val tvTime: TextView = itemView.findViewById(R.id.tvTime)
      val tvKcal: TextView = itemView.findViewById(R.id.tvKcal)
      val clX: ConstraintLayout = itemView.findViewById(R.id.clX)
   }
}