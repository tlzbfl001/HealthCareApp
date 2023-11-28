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

class CalendarAdapter2 (
    private val days: ArrayList<LocalDate?>,
    private val onItemListener: OnItemListener
) : RecyclerView.Adapter<CalendarAdapter2.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_day, parent, false)
        return ViewHolder(view, onItemListener, days)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val date = days[position]

        if (date == null) {
            holder.tvDate.text = ""
        }else {
            holder.tvDate.text = date.dayOfMonth.toString()
            if (date == selectedDate) {
                holder.tvDate.setBackgroundResource(R.drawable.oval_cal_select1)
                holder.tvDate.setTextColor(Color.WHITE )
            }
        }

        /*if (days.size > 15) {
           holder.tvWeek.visibility = View.GONE
        } else {
           holder.tvWeek.visibility = View.VISIBLE
           val week = days[position]!!.dayOfWeek
           if (week == DayOfWeek.SUNDAY) {
              holder.tvWeek.text = "SUN"
           } else if (week == DayOfWeek.MONDAY) {
              holder.tvWeek.text = "MON"
           } else if (week == DayOfWeek.TUESDAY) {
              holder.tvWeek.text = "TUE"
           } else if (week == DayOfWeek.WEDNESDAY) {
              holder.tvWeek.text = "WED"
           } else if (week == DayOfWeek.THURSDAY) {
              holder.tvWeek.text = "THU"
           } else if (week == DayOfWeek.FRIDAY) {
              holder.tvWeek.text = "FRI"
           } else {
              holder.tvWeek.text = "SAT"
           }
        }*/

        if (!days.contains(null) && date!!.month != days[6]!!.month) {
            holder.tvDate.setTextColor(Color.LTGRAY)
        }
    }

    override fun getItemCount(): Int {
        return days.size
    }

    interface OnItemListener {
        fun onItemClick(position: Int, date: LocalDate?)
    }

    inner class ViewHolder(itemView: View, onItemListener: OnItemListener, days: ArrayList<LocalDate?>) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val days: ArrayList<LocalDate?>
        //   private val parentView: View
//   val weekPoint: LinearLayout
//   val tvWeek: TextView
        val tvDate: TextView
        private val onItemListener: OnItemListener

        init {
//      parentView = itemView.findViewById(R.id.parentView)
//      weekPoint = itemView.findViewById(R.id.weekPoint)
//      tvWeek = itemView.findViewById(R.id.tvWeek)
            tvDate = itemView.findViewById(R.id.tvDate)
            this.onItemListener = onItemListener
            itemView.setOnClickListener(this)
            this.days = days
            if (days.size <= 7) {
                for (i in days.indices) {
                    val week = days[i]?.dayOfWeek
                }
            }
        }

        override fun onClick(view: View) {
            onItemListener.onItemClick(adapterPosition, days[adapterPosition])
        }
    }
}