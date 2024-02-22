package com.makebodywell.bodywell.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.model.DrugTime

class DrugAdapter3 (
    private val itemList: ArrayList<DrugTime>
) : RecyclerView.Adapter<DrugAdapter3.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_drug_time1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvCount.text = "${itemList[position].drugId}회"
        holder.tvTime.text = String.format("%02d", itemList[position].hour)+":"+String.format("%02d", itemList[position].minute)
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCount: TextView = itemView.findViewById(R.id.tvCount)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    }
}