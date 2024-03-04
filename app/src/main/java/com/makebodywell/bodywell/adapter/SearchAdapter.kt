package com.makebodywell.bodywell.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.model.Item
import com.makebodywell.bodywell.model.Search

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    private var itemList = ArrayList<Search>()
    private var itemClickListener : OnItemClickListener? = null

    interface OnItemClickListener {
        fun onClick(v: View, pos: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = itemList[position].name

        holder.textView.setOnClickListener {
            itemClickListener?.onClick(it, holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun setItems(list: ArrayList<Search>) {
        itemList = list
        notifyDataSetChanged()
    }

    fun clearItems() {
        itemList.clear()
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textView)
    }
}