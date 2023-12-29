package com.makebodywell.bodywell.adapter

import android.app.AlertDialog
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
import com.makebodywell.bodywell.view.home.food.FoodBreakfastFragment
import com.makebodywell.bodywell.view.home.food.FoodDinnerFragment
import com.makebodywell.bodywell.view.home.food.FoodLunchFragment
import com.makebodywell.bodywell.view.home.food.FoodSnackFragment
import com.makebodywell.bodywell.view.init.MainActivity

class FoodIntakeAdapter (
    private val context: Context,
    private var itemList: ArrayList<Food> = ArrayList(),
    private val type: Int
) : RecyclerView.Adapter<FoodIntakeAdapter.ViewHolder>() {
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
        holder.tvDesc.text = itemList[position].unit
        holder.tvKcal.text = (itemList[position].kcal!!.toInt() * itemList[position].amount).toString()
        holder.tvCount.text = itemList[position].amount.toString()

        // 섭취한 식단 카운트하기
        var amount = itemList[position].amount
        val kcal = itemList[position].kcal!!
        foodData.id = itemList[position].id
        foodData.amount = amount

        holder.ivMinus.setOnClickListener {
            if (amount > 1) {
                amount -= 1
                holder.tvCount.text = amount.toString()
                holder.tvKcal.text =(kcal.toInt() * amount).toString()
                foodData.amount = amount
            }
        }

        holder.ivPlus.setOnClickListener {
            amount += 1
            holder.tvCount.text = amount.toString()
            holder.tvKcal.text = (kcal.toInt() * amount).toString()
            foodData.amount = amount
        }

        holder.ivDelete.setOnClickListener {
            val dialog = AlertDialog.Builder(context)
                .setMessage("삭제하시겠습니까?")
                .setPositiveButton("확인") { _, _ ->
                    when (type) {
                        1 -> {
                            dataManager!!.deleteFood(itemList[position].id)
                            itemList.removeAt(position)
                            notifyDataSetChanged()

                            val fragment = (context as MainActivity).supportFragmentManager.findFragmentById(R.id.mainFrame) as FoodBreakfastFragment
                            if (itemList.size == 0) {
                                fragment.binding.clList.visibility = View.GONE
                                fragment.binding.view.visibility = View.GONE
                            }
                        }

                        2 -> {
                            dataManager!!.deleteFood(itemList[position].id)
                            itemList.removeAt(position)
                            notifyDataSetChanged()

                            val fragment = (context as MainActivity).supportFragmentManager.findFragmentById(R.id.mainFrame) as FoodLunchFragment
                            if (itemList.size == 0) {
                                fragment.binding.clList.visibility = View.GONE
                                fragment.binding.view.visibility = View.GONE
                            }
                        }

                        3 -> {
                            dataManager!!.deleteFood(itemList[position].id)
                            itemList.removeAt(position)
                            notifyDataSetChanged()

                            val fragment = (context as MainActivity).supportFragmentManager.findFragmentById(R.id.mainFrame) as FoodDinnerFragment
                            if (itemList.size == 0) {
                                fragment.binding.clList.visibility = View.GONE
                                fragment.binding.view.visibility = View.GONE
                            }
                        }

                        4 -> {
                            dataManager!!.deleteFood(itemList[position].id)
                            itemList.removeAt(position)
                            notifyDataSetChanged()

                            val fragment = (context as MainActivity).supportFragmentManager.findFragmentById(R.id.mainFrame) as FoodSnackFragment
                            if (itemList.size == 0) {
                                fragment.binding.clList.visibility = View.GONE
                                fragment.binding.view.visibility = View.GONE
                            }
                        }
                    }
                }
                .setNegativeButton("취소", null)
                .create()
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    fun getFoodData(): Food {
        return foodData
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