package com.makebodywell.bodywell.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.view.home.food.FoodEditFragment
import com.makebodywell.bodywell.view.home.food.FoodSnackFragment

class FoodIntakeAdapter (
    private val context: Activity,
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
        val count = itemList[position].count
        val kcal = itemList[position].kcal
        val amount = itemList[position].amount
        foodData.id = itemList[position].id
        foodData.count = count

        holder.tvName.text = itemList[position].name
        holder.tvKcal.text = "${kcal * count} kcal"
        holder.tvDesc.text = "${count}개/${amount * count}${itemList[position].unit}"

        holder.cvDelete.setOnClickListener {
            onItemClickListener!!.onItemClick(position)
        }

        holder.cvEdit.setOnClickListener {
            replaceFragment1(context, FoodEditFragment())
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
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvDesc: TextView = itemView.findViewById(R.id.tvDesc)
        val tvKcal: TextView = itemView.findViewById(R.id.tvKcal)
        val cvDelete: CardView = itemView.findViewById(R.id.cvDelete)
        val cvEdit: CardView = itemView.findViewById(R.id.cvEdit)
    }
}