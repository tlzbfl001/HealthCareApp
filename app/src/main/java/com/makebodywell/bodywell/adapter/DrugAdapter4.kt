package com.makebodywell.bodywell.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.model.Drug

class DrugAdapter4 (
    private val itemList: ArrayList<Drug>
) : RecyclerView.Adapter<DrugAdapter4.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_drug_time1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvCount.text = itemList[position].count.toString()
        holder.tvTime.text = itemList[position].name
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCount: TextView = itemView.findViewById(R.id.tvCount)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    }
}