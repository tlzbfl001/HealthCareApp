package kr.bodywell.health.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kr.bodywell.health.R
import kr.bodywell.health.util.CalendarUtil.Companion.selectedDate
import java.time.DayOfWeek
import java.time.LocalDate

class CalendarAdapter2 (
   private val days: ArrayList<LocalDate?>
) : RecyclerView.Adapter<CalendarAdapter2.ViewHolder>() {
   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_date2, parent, false)
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val date = days[position]

      if (date == null) {
         holder.tvDate.text = ""
      }else {
         holder.tvDate.text = date.dayOfMonth.toString()
         if (date == selectedDate) {
            holder.cv.setCardBackgroundColor(Color.parseColor("#6645EA"))
            holder.tvWeek.setTextColor(Color.WHITE)
            holder.tvDate.setTextColor(Color.WHITE)
         }

         if (!days.contains(null) && date.month != days[6]!!.month) {
            holder.tvDate.setTextColor(Color.WHITE)
         }

         when(days[position]!!.dayOfWeek) {
            DayOfWeek.SUNDAY -> holder.tvWeek.text = "SUN"
            DayOfWeek.MONDAY -> holder.tvWeek.text = "MON"
            DayOfWeek.TUESDAY -> holder.tvWeek.text = "TUE"
            DayOfWeek.WEDNESDAY -> holder.tvWeek.text = "WED"
            DayOfWeek.THURSDAY -> holder.tvWeek.text = "THU"
            DayOfWeek.FRIDAY -> holder.tvWeek.text = "FRI"
            else -> holder.tvWeek.text = "SAT"
         }
      }
   }

   override fun getItemCount(): Int {
      return days.size
   }

   inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      val cv: CardView = itemView.findViewById(R.id.cv)
      val tvWeek: TextView = itemView.findViewById(R.id.tvWeek)
      val tvDate: TextView = itemView.findViewById(R.id.tvDate)
   }
}