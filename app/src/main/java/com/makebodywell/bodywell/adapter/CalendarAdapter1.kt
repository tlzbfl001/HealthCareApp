package com.makebodywell.bodywell.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import java.time.LocalDate

class CalendarAdapter1 (
   private val days: ArrayList<LocalDate?>,
   private val type: Int = 0
) : RecyclerView.Adapter<CalendarAdapter1.ViewHolder>() {
   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_date1, parent, false)

      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val date = days[position]

      if (date == null) {
         holder.tvDate.text = ""
      }else {
         holder.tvDate.text = date.dayOfMonth.toString()
         if (date == selectedDate) {
            holder.tvDate.setBackgroundResource(R.drawable.oval_cal_select1)
            holder.tvDate.setTextColor(Color.WHITE)
         }
      }

      if (!days.contains(null) && date!!.month != days[6]!!.month) {
         when(type) {
            1 -> holder.tvDate.setTextColor(Color.WHITE)
            else -> holder.tvDate.setTextColor(Color.LTGRAY)
         }
      }
   }

   override fun getItemCount(): Int {
      return days.size
   }

   inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      val tvDate: TextView = itemView.findViewById(R.id.tvDate)
   }
}