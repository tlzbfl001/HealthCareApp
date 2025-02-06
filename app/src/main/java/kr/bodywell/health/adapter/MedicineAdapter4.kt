package kr.bodywell.health.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.bodywell.health.R
import kr.bodywell.health.model.Item
import kr.bodywell.health.util.CustomUtil.drugTimeList
import kr.bodywell.health.view.MainViewModel

class MedicineAdapter4 (
    private val itemList: ArrayList<Item>,
    private val viewModel: MainViewModel
) : RecyclerView.Adapter<MedicineAdapter4.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_medicine_time2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvTime.text = itemList[position].string1
        holder.tvCount.text = itemList[position].int1.toString()

        holder.ivDelete.setOnClickListener {
            drugTimeList.removeAt(position)
            itemList.clear()

            for(i in 0 until drugTimeList.size) itemList.add(Item(string1 = drugTimeList[i].time, int1 = i + 1))

            viewModel.setMedicineCheckState(drugTimeList.size)

            notifyDataSetChanged()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCount: TextView = itemView.findViewById(R.id.tvCount)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
    }
}