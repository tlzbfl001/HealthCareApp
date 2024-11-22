package kr.bodywell.android.adapter

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.runBlocking
import kr.bodywell.android.R
import kr.bodywell.android.database.DBHelper.Companion.TYPE_USER
import kr.bodywell.android.model.Activities
import kr.bodywell.android.util.CustomUtil.powerSync

class ExerciseRecordAdapter (
   private val context: Activity,
   private var itemList: ArrayList<Activities> =ArrayList<Activities>()
) : RecyclerView.Adapter<ExerciseRecordAdapter.ViewHolder>() {
   private var onItemClickListener: OnItemClickListener? = null

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false)
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.textView.text = itemList[position].name

      holder.textView.setOnClickListener {
         onItemClickListener!!.onItemClick(position)
      }

      if(itemList[position].registerType == TYPE_USER) holder.cl.visibility = View.VISIBLE else holder.cl.visibility = View.GONE

      holder.cl.setOnClickListener {
         val dialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
         val bottomSheetView = context.layoutInflater.inflate(R.layout.dialog_menu2, null)
         val clDelete = bottomSheetView.findViewById<ConstraintLayout>(R.id.clDelete)

         clDelete.setOnClickListener {
            AlertDialog.Builder(context, R.style.AlertDialogStyle)
               .setTitle("운동 삭제")
               .setMessage("정말 삭제하시겠습니까?")
               .setPositiveButton("확인") { _, _ ->
                  runBlocking {
                     powerSync.deleteItem("activities", "id", itemList[position].id)
                  }

                  itemList.removeAt(position)
                  notifyDataSetChanged()

                  Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
               }
               .setNegativeButton("취소", null)
               .create().show()
            dialog.dismiss()
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