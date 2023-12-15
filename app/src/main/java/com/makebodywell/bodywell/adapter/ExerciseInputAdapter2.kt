package com.makebodywell.bodywell.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R

class ExerciseInputAdapter2 (
    private val context: Context,
    private val itemList: ArrayList<String>
) : RecyclerView.Adapter<ExerciseInputAdapter2.ViewHolder>() {
    private lateinit var itemClickListener : OnItemClickListener
    private var selectPos = -1
    var selected = ""

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise_input, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = itemList[position]

        if(selectPos == holder.adapterPosition) {
            holder.clBox.background = context.getDrawable(R.drawable.rec_5_purple)
            holder.tvName.setTextColor(Color.WHITE)
        }else {
            holder.clBox.background = context.getDrawable(R.drawable.rec_5_border_gray)
            holder.tvName.setTextColor(Color.BLACK)
        }

        holder.itemView.setOnClickListener {
            selected = itemList[position]
            selectPos = holder.adapterPosition
            notifyDataSetChanged()
        }

        holder.clDelete.setOnClickListener {
            itemClickListener.onClick(it, holder.adapterPosition)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clBox: ConstraintLayout = itemView.findViewById(R.id.clBox)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val clDelete: ConstraintLayout = itemView.findViewById(R.id.clDelete)
    }
}