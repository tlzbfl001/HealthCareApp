package kr.bodywell.android.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.bodywell.android.R
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.Drug
import kr.bodywell.android.util.CustomUtil.Companion.drugTimeList
import kr.bodywell.android.view.MainViewModel

class DrugAdapter4 (
    private val context: Context,
    private val itemList: ArrayList<Drug>,
    private val viewModel: MainViewModel
) : RecyclerView.Adapter<DrugAdapter4.ViewHolder>() {
    private var dataManager: DataManager = DataManager(context)

    init {
        dataManager.open()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_drug_time2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvTime.text = itemList[position].name
        holder.tvCount.text = itemList[position].count.toString()

        holder.ivDelete.setOnClickListener {
            drugTimeList.removeAt(position)
            itemList.clear()

            for(i in 0 until drugTimeList.size) {
                itemList.add(Drug(name = "${drugTimeList[i].time}", count = i + 1))
            }

            viewModel.setInt(drugTimeList.size)

            notifyDataSetChanged()
        }
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