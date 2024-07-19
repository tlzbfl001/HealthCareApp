package kr.bodywell.test.adapter

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kr.bodywell.test.R
import kr.bodywell.test.database.DBHelper.Companion.TABLE_EXERCISE
import kr.bodywell.test.database.DBHelper.Companion.TABLE_FOOD
import kr.bodywell.test.database.DataManager
import kr.bodywell.test.model.Item
import kr.bodywell.test.model.Unused
import kr.bodywell.test.util.CalendarUtil.Companion.selectedDate
import kr.bodywell.test.util.CustomUtil.Companion.replaceFragment2
import kr.bodywell.test.view.home.exercise.ExerciseEditFragment
import kr.bodywell.test.view.home.food.FoodEditFragment

class SearchAdapter(
    private val context: Activity,
    private val back: String,
    private val type: String
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    private var bundle = Bundle()
    private var dataManager: DataManager = DataManager(context)
    private var itemList = ArrayList<Item>()
    private var itemClickListener : OnItemClickListener? = null

    init { dataManager.open() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = itemList[position].string2

        holder.textView.setOnClickListener {
            itemClickListener?.onClick(it, holder.adapterPosition)
        }

        if(itemList[position].int2 == 1) holder.cl.visibility = View.GONE

        holder.cl.setOnClickListener {
            val dialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
            val bottomSheetView = context.layoutInflater.inflate(R.layout.dialog_menu, null)

            val clX = bottomSheetView.findViewById<ConstraintLayout>(R.id.clX)
            val clEdit = bottomSheetView.findViewById<ConstraintLayout>(R.id.clEdit)
            val clDelete = bottomSheetView.findViewById<ConstraintLayout>(R.id.clDelete)

            clX.setOnClickListener { dialog.dismiss() }

            clEdit.setOnClickListener {
                if(type != "") {
                    bundle.putString("id", itemList[position].int1.toString())
                    bundle.putString("type", type)
                    bundle.putString("back", back)
                    replaceFragment2(context, FoodEditFragment(), bundle)
                    dialog.dismiss()
                }else {
                    bundle.putString("id", itemList[position].int1.toString())
                    bundle.putString("back", back)
                    replaceFragment2(context, ExerciseEditFragment(), bundle)
                    dialog.dismiss()
                }
            }

            clDelete.setOnClickListener {
                if(type != "") {
                    AlertDialog.Builder(context, R.style.AlertDialogStyle)
                        .setTitle("음식 삭제")
                        .setMessage("정말 삭제하시겠습니까?")
                        .setPositiveButton("확인") { _, _ ->
                            val result = dataManager.deleteItem(TABLE_FOOD, "id", itemList[position].int1)

                            if(result > 0) {
                                if(itemList[position].string1 != "") {
                                    dataManager.insertUnused(Unused(type = "food", value = itemList[position].string1, created = selectedDate.toString()))
                                }

                                itemList.removeAt(position)
                                notifyDataSetChanged()

                                Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                            }else Toast.makeText(context, "삭제 실패", Toast.LENGTH_SHORT).show()
                        }.setNegativeButton("취소", null).create().show()
                    dialog.dismiss()
                }else {
                    AlertDialog.Builder(context, R.style.AlertDialogStyle)
                        .setTitle("운동 삭제")
                        .setMessage("정말 삭제하시겠습니까?")
                        .setPositiveButton("확인") { _, _ ->
                            val result = dataManager.deleteItem(TABLE_EXERCISE, "id", itemList[position].int1)

                            if(result > 0) {
                                if(itemList[position].string1 != "") dataManager.insertUnused(Unused(type = "exercise", value = itemList[position].string1, created = selectedDate.toString()))

                                itemList.removeAt(position)
                                notifyDataSetChanged()

                                Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                            }else {
                                Toast.makeText(context, "삭제 실패", Toast.LENGTH_SHORT).show()
                            }
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
        return itemList.size
    }

    interface OnItemClickListener {
        fun onClick(v: View, pos: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    fun setItems(list: ArrayList<Item>) {
        itemList = list
        notifyDataSetChanged()
    }

    fun clearItems() {
        itemList.clear()
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textView)
        val cl: ConstraintLayout = itemView.findViewById(R.id.cl)
    }
}