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
import com.google.android.material.bottomsheet.BottomSheetDialog
import kr.bodywell.android.R
import kr.bodywell.android.database.DBHelper.Companion.TABLE_EXERCISE
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.Exercise
import kr.bodywell.android.model.Unused
import kr.bodywell.android.util.CustomUtil
import kr.bodywell.android.util.CustomUtil.Companion.replaceFragment2
import kr.bodywell.android.view.home.exercise.ExerciseEditFragment

class ExerciseRecordAdapter (
   private val context: Activity,
   private var itemList: ArrayList<Exercise> = ArrayList<Exercise>(),
   private val back: String
) : RecyclerView.Adapter<ExerciseRecordAdapter.ViewHolder>() {
   private var bundle = Bundle()
   private var dataManager: DataManager = DataManager(context)
   private var onItemClickListener: OnItemClickListener? = null

   init {
      dataManager.open()
   }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false)
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.textView.text = itemList[position].name

      holder.textView.setOnClickListener {
         onItemClickListener!!.onItemClick(position)
      }

      holder.cl.setOnClickListener {
         val dialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
         val bottomSheetView = context.layoutInflater.inflate(R.layout.dialog_menu, null)

         val clX = bottomSheetView.findViewById<ConstraintLayout>(R.id.clX)
         val clEdit = bottomSheetView.findViewById<ConstraintLayout>(R.id.clEdit)
         val clDelete = bottomSheetView.findViewById<ConstraintLayout>(R.id.clDelete)

         clX.setOnClickListener {
            dialog.dismiss()
         }

         clEdit.setOnClickListener {
            bundle.putString("id", itemList[position].id.toString())
            bundle.putString("back", back)
            replaceFragment2(context, ExerciseEditFragment(), bundle)
            dialog.dismiss()
         }

         clDelete.setOnClickListener {
            val getDailyExercise = dataManager.getDailyExercise("exerciseId", itemList[position].id)
            if(getDailyExercise.id > 0) {
               Toast.makeText(context, "사용중인 데이터는 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }else {
               AlertDialog.Builder(context, R.style.AlertDialogStyle)
                  .setTitle("운동 삭제")
                  .setMessage("정말 삭제하시겠습니까?")
                  .setPositiveButton("확인") { _, _ ->
                     dataManager.deleteItem(TABLE_EXERCISE, "id", itemList[position].id)

                     if(itemList[position].uid != "") {
                        dataManager.insertUnused(Unused(type = "exercise", value = itemList[position].uid))
                     }

                     itemList.removeAt(position)
                     notifyDataSetChanged()

                     Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                  }
                  .setNegativeButton("취소", null)
                  .create().show()
               dialog.dismiss()
            }
         }

         dialog.setContentView(bottomSheetView)
         dialog.show()
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
      val cl: ConstraintLayout = itemView.findViewById(R.id.cl)
   }
}