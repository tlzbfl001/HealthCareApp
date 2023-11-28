package com.makebodywell.bodywell.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.util.CalendarUtil.Companion.isItemClick
import com.makebodywell.bodywell.util.CalendarUtil.Companion.deleteList
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDays
import com.makebodywell.bodywell.view.home.drug.DrugSelectDateFragment2
import com.makebodywell.bodywell.view.init.MainActivity
import java.time.LocalDate

class CalendarAdapter4 (
    private val context: Context,
    private val days: ArrayList<LocalDate?>,
    private val onItemListener: OnItemListener
) : RecyclerView.Adapter<CalendarAdapter4.ViewHolder>() {
    private var isExistence = false
    private var isPrev = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_day, parent, false)
        return ViewHolder(view, days, onItemListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val date = days[position]

        // 프래그먼트 가져오기
        val fragment: DrugSelectDateFragment2 = (context as MainActivity).supportFragmentManager.findFragmentById(R.id.mainFrame) as DrugSelectDateFragment2

        if(date == null) {
            holder.tvDate.text = ""
        }else {
            holder.tvDate.text = date.dayOfMonth.toString()
            if(isItemClick && date == selectedDate) {
                for(i in 0 until selectedDays.size) {
                    if(selectedDate == selectedDays[i]) {
                        deleteList.add(i)
                        isExistence = true
                    }
                }

                if(deleteList.size > 0) {
                    for(i in 0 until deleteList.size) {
                        selectedDays.removeAt(deleteList[i])
                    }
                    deleteList.clear()
                }

                if(isExistence) {
                    isExistence = false
                }else {
                    for(i in 0 until selectedDays.size) {
                        if(date < selectedDays[i]) {
                            Toast.makeText(context, "순서대로 입력해주세요.", Toast.LENGTH_SHORT).show()
                            isPrev=true
                            return
                        }
                    }

                    if(isPrev) {
                        isPrev = false
                    }else {
                        selectedDays.add(date)
                    }
                }
            }

            if(selectedDays.size > 0) {
                for(i in 0 until selectedDays.size) {
                    if(date == selectedDays[i]) {
                        holder.tvDate.setBackgroundResource(R.drawable.oval_cal_select2)
                    }
                }
            }

            if(selectedDays.size == 0) {
                fragment.binding.tvStart.text = ""
            }else if(selectedDays.size == 1) {
                fragment.binding.tvEnd.text = ""
                fragment.binding.tvStart.text = selectedDays[0].toString()
            }else {
                fragment.binding.tvEnd.text = selectedDays[selectedDays.size-1].toString()
            }

            if (!days.contains(null) && (date.month != days[6]!!.month)) {
                holder.tvDate.setTextColor(Color.LTGRAY)
            }
        }
    }

    override fun getItemCount(): Int {
        return days.size
    }

    interface OnItemListener {
        fun onItemClick(position: Int, date: LocalDate?)
    }

    inner class ViewHolder(itemView: View, days: ArrayList<LocalDate?>, onItemListener: OnItemListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val days: ArrayList<LocalDate?>
        private val onItemListener: OnItemListener
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)

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