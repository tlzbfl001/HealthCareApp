package com.makebodywell.bodywell.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.model.Food

class FoodIntakeAdapter (
    private val context: Context,
    private var itemList: ArrayList<Food> = ArrayList()
) : RecyclerView.Adapter<FoodIntakeAdapter.ViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null
    private var dataManager: DataManager? = null
    private var foodData = Food()

    init {
        dataManager = DataManager(context)
        dataManager!!.open()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = itemList[position].name
        holder.tvKcal.text = "${itemList[position].kcal!!.toInt() * itemList[position].count} kcal"
        holder.tvCount.text = itemList[position].count.toString()
        holder.tvDesc.text = "${itemList[position].count}개/${itemList[position].amount}${itemList[position].unit}"

        // 섭취한 식단 카운트하기
        var count = itemList[position].count
        val kcal = itemList[position].kcal.toInt()
        val amount = itemList[position].amount.toInt()
        foodData.id = itemList[position].id
        foodData.count = count

        holder.ivMinus.setOnClickListener {
            if (count > 1) {
                count -= 1
                holder.tvCount.text = count.toString()
                holder.tvKcal.text = "${kcal * count} kcal"
                holder.tvDesc.text = "${count}개/${amount * count}g"
                foodData.count = count
            }
        }

        holder.ivPlus.setOnClickListener {
            count += 1
            holder.tvCount.text = count.toString()
            holder.tvKcal.text = "${kcal * count} kcal"
            holder.tvDesc.text = "${count}개/${amount * count}g"
            foodData.count = count
        }

        holder.ivDelete.setOnClickListener {
            onItemClickListener!!.onItemClick(position)
//            val dialog = AlertDialog.Builder(context)
//                .setMessage("삭제하시겠습니까?")
//                .setPositiveButton("확인") { _, _ ->
//                    dataManager!!.deleteItem(TABLE_FOOD, "id", itemList[position].id)
//                    dataManager!!.deleteItem(TABLE_IMAGE, "foodId", itemList[position].id)
//                    itemList.removeAt(position)
//                    notifyDataSetChanged()
//                }
//                .setNegativeButton("취소", null)
//                .create()
//            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    fun getFoodData(): Food {
        return foodData
    }

    interface OnItemClickListener {
        fun onItemClick(pos: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        onItemClickListener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvDesc: TextView = itemView.findViewById(R.id.tvDesc)
        val tvKcal: TextView = itemView.findViewById(R.id.tvKcal)
        val tvCount: TextView = itemView.findViewById(R.id.tvCount)
        val ivMinus: ImageView = itemView.findViewById(R.id.ivMinus)
        val ivPlus: ImageView = itemView.findViewById(R.id.ivPlus)
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
    }
}