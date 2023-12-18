package com.makebodywell.bodywell.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.util.CalendarUtil
import com.makebodywell.bodywell.util.CalendarUtil.Companion.drugSelected1
import com.makebodywell.bodywell.util.CalendarUtil.Companion.drugSelected2
import com.makebodywell.bodywell.util.DrugUtil
import com.makebodywell.bodywell.util.DrugUtil.Companion.drugEndDate
import com.makebodywell.bodywell.util.DrugUtil.Companion.drugStartDate
import com.makebodywell.bodywell.view.home.drug.DrugSelectDateFragment1
import com.makebodywell.bodywell.view.init.MainActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalendarAdapter3 (
    private val context: Context,
    private val onItemListener: OnItemListener,
    private val days: ArrayList<LocalDate?>
) : RecyclerView.Adapter<CalendarAdapter3.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_day, parent, false)
        return ViewHolder(view, onItemListener, days)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val date = days[position]
        val fragment: DrugSelectDateFragment1 = (context as MainActivity).supportFragmentManager.findFragmentById(R.id.mainFrame) as DrugSelectDateFragment1

        if (date == null) {
            holder.tvDate.text = ""
        }else {
            holder.tvDate.text = date.dayOfMonth.toString()

            if (!days.contains(null) && (date.month != days[6]!!.month)) {
                holder.tvDate.setTextColor(Color.LTGRAY)
            }

            if (date == CalendarUtil.selectedDate) {
                if(drugSelected1 == null) {
                    drugSelected1 = date
                    fragment.binding.tvStart.text = date.toString()
                }else if(drugSelected2 == null) {
                    if(drugSelected1!! > date) {
                        drugSelected1 = date
                        fragment.binding.tvStart.text = date.toString()
                        fragment.binding.tvEnd.text = ""
                    }else {
                        drugSelected2 = date
                        fragment.binding.tvEnd.text = date.toString()
                    }
                }
            }

            if (date == drugSelected1) {
                holder.tvDate.setBackgroundResource(R.drawable.oval_cal_select2)
            }

            if (date == drugSelected2) {
                holder.tvDate.setBackgroundResource(R.drawable.oval_cal_select2)
            }

            if(drugSelected1 != null && drugSelected2 != null) {
                if(drugSelected1!!.month != drugSelected2!!.month) {
                    drugSelected2 = null
                }else {
                    drugSelected1 = null
                    drugSelected2 = null
                }
            }
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
        private val onItemListener: OnItemListener
        val tvDate: TextView  = itemView.findViewById(R.id.tvDate)

        init {
            this.onItemListener = onItemListener
            itemView.setOnClickListener(this)
            this.days = days
        }

        override fun onClick(view: View) {
            onItemListener.onItemClick(adapterPosition, days[adapterPosition])
        }
    }
}