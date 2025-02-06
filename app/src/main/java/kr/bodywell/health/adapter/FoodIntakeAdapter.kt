package kr.bodywell.health.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import kr.bodywell.health.R
import kr.bodywell.health.model.Constant.DIETS
import kr.bodywell.health.model.Food
import kr.bodywell.health.util.CustomUtil.replaceFragment2
import kr.bodywell.health.view.home.food.FoodDailyEditFragment

class FoodIntakeAdapter (
    private val fragmentManager: FragmentManager,
    private var itemList: ArrayList<Food> = ArrayList(),
    private val type: String
) : RecyclerView.Adapter<FoodIntakeAdapter.ViewHolder>() {
    private var bundle = Bundle()
    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val count = itemList[position].quantity
        holder.tvName.text = itemList[position].name
        holder.tvKcal.text = "${itemList[position].calorie!! * count} kcal"
        holder.tvDesc.text = "${count}개/${itemList[position].volume!! * count}${itemList[position].volumeUnit}"

        holder.cl.setOnClickListener {
            bundle.putParcelable(DIETS, itemList[position])
            bundle.putString("type", type)
            replaceFragment2(fragmentManager, FoodDailyEditFragment(), bundle)
        }

        holder.clX.setOnClickListener {
            onItemClickListener!!.onItemClick(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
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
        val cl: ConstraintLayout = itemView.findViewById(R.id.cl)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvDesc: TextView = itemView.findViewById(R.id.tvDesc)
        val tvKcal: TextView = itemView.findViewById(R.id.tvKcal)
        val clX: ConstraintLayout = itemView.findViewById(R.id.clX)
    }
}