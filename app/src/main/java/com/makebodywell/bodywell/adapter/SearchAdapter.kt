package com.makebodywell.bodywell.adapter

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
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DBHelper
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_EXERCISE
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_FOOD
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.model.Item
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import com.makebodywell.bodywell.view.home.exercise.ExerciseEditFragment
import com.makebodywell.bodywell.view.home.food.FoodEditFragment
import com.makebodywell.bodywell.view.home.food.FoodInputFragment

class SearchAdapter(
    private val context: Activity,
    private val back: String,
    private val type: String
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    private var bundle = Bundle()
    private var dataManager: DataManager? = null
    private var itemList = ArrayList<Item>()
    private var itemClickListener : OnItemClickListener? = null

    init {
        dataManager = DataManager(context)
        dataManager!!.open()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = itemList[position].string1

        holder.textView.setOnClickListener {
            itemClickListener?.onClick(it, holder.adapterPosition)
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
                            dataManager!!.deleteItem(TABLE_FOOD, "id", itemList[position].int1)

                            itemList.removeAt(position)
                            notifyDataSetChanged()

                            Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("취소", null)
                        .create().show()
                    dialog.dismiss()
                }else {
                    AlertDialog.Builder(context, R.style.AlertDialogStyle)
                        .setTitle("운동 삭제")
                        .setMessage("정말 삭제하시겠습니까?")
                        .setPositiveButton("확인") { _, _ ->
                            dataManager!!.deleteItem(TABLE_EXERCISE, "id", itemList[position].int1)

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

    interface OnItemClickListener {
        fun onClick(v: View, pos: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    override fun getItemCount(): Int {
        return itemList.size
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