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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.runBlocking
import kr.bodywell.android.R
import kr.bodywell.android.database.DBHelper.Companion.TYPE_ADMIN
import kr.bodywell.android.model.Item
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment2
import kr.bodywell.android.view.home.food.FoodEditFragment

class SearchAdapter(
    private val context: Activity,
    private val back: String,
    private val type: String
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    private var bundle = Bundle()
    private var itemList = ArrayList<Item>()
    private var itemClickListener : OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = itemList[position].string2

        holder.textView.setOnClickListener {
            itemClickListener?.onClick(it, holder.adapterPosition)
        }

        if(itemList[position].string3 == TYPE_ADMIN) holder.cl.visibility = View.GONE else holder.cl.visibility = View.VISIBLE

        holder.cl.setOnClickListener {
            if(type != "") {
                val dialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
                val bottomSheetView = context.layoutInflater.inflate(R.layout.dialog_menu1, null)
                val clEdit = bottomSheetView.findViewById<ConstraintLayout>(R.id.clEdit)
                val clDelete = bottomSheetView.findViewById<ConstraintLayout>(R.id.clDelete)

                clEdit.setOnClickListener {
                    bundle.putString("id", itemList[position].string1)
                    bundle.putString("type", type)
                    bundle.putString("back", back)
                    replaceFragment2(context, FoodEditFragment(), bundle)
                    dialog.dismiss()
                }

                clDelete.setOnClickListener {
                    AlertDialog.Builder(context, R.style.AlertDialogStyle)
                        .setTitle("음식 삭제")
                        .setMessage("정말 삭제하시겠습니까?")
                        .setPositiveButton("확인") { _, _ ->
                            runBlocking {
                                powerSync.deleteItem("foods", "id", itemList[position].string1)
                            }

                            itemList.removeAt(position)
                            notifyDataSetChanged()

                            Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        }.setNegativeButton("취소", null).create().show()
                    dialog.dismiss()
                }

                dialog.setContentView(bottomSheetView)
                dialog.show()
            }else {
                val dialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
                val bottomSheetView = context.layoutInflater.inflate(R.layout.dialog_menu2, null)
                val clDelete = bottomSheetView.findViewById<ConstraintLayout>(R.id.clDelete)

                clDelete.setOnClickListener {
                    AlertDialog.Builder(context, R.style.AlertDialogStyle)
                        .setTitle("운동 삭제")
                        .setMessage("정말 삭제하시겠습니까?")
                        .setPositiveButton("확인") { _, _ ->
                            runBlocking {
                                powerSync.deleteItem("activities", "id", itemList[position].string1)
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