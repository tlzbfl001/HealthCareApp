package kr.bodywell.android.adapter

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.runBlocking
import kr.bodywell.android.R
import kr.bodywell.android.model.Constants.FOODS
import kr.bodywell.android.model.Constants.USER
import kr.bodywell.android.model.Food
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment2
import kr.bodywell.android.view.home.food.FoodEditFragment

class FoodRecordAdapter(
   private val context: Activity,
   private val fragmentManager: FragmentManager,
   private var itemList: ArrayList<Food> = ArrayList(),
   private val type: String
) : RecyclerView.Adapter<FoodRecordAdapter.ViewHolder>() {
   private var onItemClickListener: OnItemClickListener? = null
   private var bundle = Bundle()

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false)
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
      holder.textView.text = itemList[pos].name
      holder.textView.setOnClickListener { onItemClickListener!!.onItemClick(pos) }

      if(itemList[pos].registerType == USER) holder.cl.visibility = View.VISIBLE else holder.cl.visibility = View.GONE

      holder.cl.setOnClickListener {
         val dialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
         val bottomSheetView = context.layoutInflater.inflate(R.layout.dialog_menu1, null)
         val clEdit = bottomSheetView.findViewById<ConstraintLayout>(R.id.clEdit)
         val clDelete = bottomSheetView.findViewById<ConstraintLayout>(R.id.clDelete)

         clEdit.setOnClickListener {
            bundle.putParcelable(FOODS, itemList[pos])
            bundle.putString("type", type)
            replaceFragment2(fragmentManager, FoodEditFragment(), bundle)
            dialog.dismiss()
         }

         clDelete.setOnClickListener {
            AlertDialog.Builder(context, R.style.AlertDialogStyle)
               .setTitle("음식 삭제")
               .setMessage("정말 삭제하시겠습니까?")
               .setPositiveButton("확인") { _, _ ->
                  runBlocking {
                     powerSync.deleteItem(FOODS, "id", itemList[pos].id)
                  }

                  itemList.removeAt(pos)
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