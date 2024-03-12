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
import com.makebodywell.bodywell.model.Drug
import com.makebodywell.bodywell.util.CustomUtil.Companion.drugTimeList
import com.makebodywell.bodywell.view.home.MainActivity
import com.makebodywell.bodywell.view.home.drug.DrugAddFragment

class DrugAdapter4 (
    private val context: Context,
    private val itemList: ArrayList<Drug>
) : RecyclerView.Adapter<DrugAdapter4.ViewHolder>() {
    private var dataManager: DataManager? = null

    init {
        dataManager = DataManager(context)
        dataManager!!.open()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_drug_time2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fragment = (context as MainActivity).supportFragmentManager.findFragmentById(R.id.mainFrame) as DrugAddFragment

        holder.tvTime.text = itemList[position].name
        holder.tvCount.text = itemList[position].count.toString()

        holder.ivDelete.setOnClickListener {
            drugTimeList.removeAt(position)
            itemList.clear()

            for(i in 0 until drugTimeList.size) {
                val hour = String.format("%02d", drugTimeList[i].hour)
                val minute = String.format("%02d", drugTimeList[i].minute)
                itemList.add(Drug(name = "$hour:$minute", count = i + 1))
            }

            fragment.binding.tvDesc.text = "${fragment.count}일동안 ${drugTimeList.size}회 복용"

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