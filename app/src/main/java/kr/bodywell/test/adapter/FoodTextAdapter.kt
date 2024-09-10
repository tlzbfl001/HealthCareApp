package kr.bodywell.test.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.bodywell.test.R
import kr.bodywell.test.model.Food

class FoodTextAdapter (
    private val item: ArrayList<Food> = ArrayList<Food>()
) : RecyclerView.Adapter<FoodTextAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_text, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodTextAdapter.ViewHolder, pos: Int) {
        holder.tvName.text = "${item[pos].name}(${item[pos].amount * item[pos].count}${item[pos].unit})"
        holder.tvKcal.text = "${item[pos].kcal * item[pos].count} kcal"
    }

    override fun getItemCount(): Int {
        return item.count()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvKcal: TextView = itemView.findViewById(R.id.tvKcal)
    }
}