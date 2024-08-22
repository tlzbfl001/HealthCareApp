package kr.bodywell.android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.bodywell.android.R
import kr.bodywell.android.model.Item

class ReportAdapter (
	private val itemList: ArrayList<Item>
) : RecyclerView.Adapter<ReportAdapter.ViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rank, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.tvCount.text = itemList[position].string1 + "회"
		holder.tvName.text = itemList[position].string2
	}

	override fun getItemCount(): Int {
		return itemList.count()
	}

	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val tvCount: TextView = itemView.findViewById(R.id.tvCount)
		val tvName: TextView = itemView.findViewById(R.id.tvName)
	}
}