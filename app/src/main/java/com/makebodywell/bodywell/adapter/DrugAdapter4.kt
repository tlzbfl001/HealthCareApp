package com.makebodywell.bodywell.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.model.DrugDate
import java.text.SimpleDateFormat

class DrugAdapter4 (
    private val itemList: ArrayList<DrugDate>
) : RecyclerView.Adapter<DrugAdapter4.ViewHolder>() {
    private val format1 = SimpleDateFormat("yyyy-MM-dd")
    private val format2 = SimpleDateFormat("M/dd")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_drug_date, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvTime.text = format2.format(format1.parse(itemList[position].date))
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    }
}