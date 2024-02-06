package com.makebodywell.bodywell.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.view.home.food.CalendarDialog
import java.time.LocalDate

class CalendarAdapter2 (
	private val context: Context,
	private val days: ArrayList<LocalDate?>,
	private val onItemListener: OnItemListener
) : RecyclerView.Adapter<CalendarAdapter2.ViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_day, parent, false)
		return ViewHolder(view, onItemListener, days)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val dialog = CalendarDialog(context)

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
		val tvDate: TextView
		private val onItemListener: OnItemListener

		init {
			tvDate = itemView.findViewById(R.id.tvDate)
			this.onItemListener = onItemListener
			itemView.setOnClickListener(this)
			this.days = days
		}

		override fun onClick(view: View) {
			onItemListener.onItemClick(adapterPosition, days[adapterPosition])
		}
	}
}