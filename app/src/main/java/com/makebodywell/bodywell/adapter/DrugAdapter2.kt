package com.makebodywell.bodywell.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.model.Drug
import com.makebodywell.bodywell.model.DrugDate
import com.makebodywell.bodywell.model.DrugTime
import com.makebodywell.bodywell.util.AlarmReceiver
import java.text.SimpleDateFormat

class DrugAdapter2 (
   private val context: Context,
   private val itemList: ArrayList<Drug> = ArrayList()
) : RecyclerView.Adapter<DrugAdapter2.ViewHolder>() {
   private var dataManager: DataManager? = null
   private var alarmReceiver: AlarmReceiver
   private val timeList: ArrayList<DrugTime> = ArrayList()
   private val dateList: ArrayList<DrugDate> = ArrayList()
   private val format1 = SimpleDateFormat("yyyy-MM-dd")
   private val format2 = SimpleDateFormat("yyyy. MM. dd")
   private val format3 = SimpleDateFormat("M/dd")

   init {
      dataManager = DataManager(context)
      dataManager!!.open()

      alarmReceiver = AlarmReceiver()
   }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_drug_record, parent, false)
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.tvType.text = itemList[position].type
      holder.tvName.text = itemList[position].name
      holder.tvPeriod.text = itemList[position].period
      holder.tvCount.text = itemList[position].amount
      holder.tvUnit.text = itemList[position].unit

      val startDate = format2.format(format1.parse(itemList[position].startDate)!!)
      val endDate = format2.format(format1.parse(itemList[position].endDate)!!)
      holder.tvDate.text = "$startDate ~ $endDate"

      val getDrugTime = dataManager?.getDrugTime(itemList[position].id)
      if(getDrugTime!!.isNotEmpty()) {
         timeList.clear()

         for(i in 0 until getDrugTime.size) {
            timeList.add(DrugTime(hour = getDrugTime[i].hour, minute = getDrugTime[i].minute, drugId = i+1))
         }

         val adapter = DrugAdapter3(timeList)
         holder.recyclerView2.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
         holder.recyclerView2.adapter = adapter
      }

      val getDrugDate = dataManager?.getDrugDate(itemList[position].id)
      if(getDrugDate!!.isNotEmpty()) {
         dateList.clear()

         for(i in 0 until getDrugDate.size) {
            dateList.add(DrugDate(date = format3.format(format1.parse(getDrugDate[i].date)!!)))
         }

         val adapter = DrugAdapter4(dateList)
         holder.recyclerView1.layoutManager = GridLayoutManager(context, 6)
         holder.recyclerView1.adapter = adapter
      }

      holder.switchOnOff.isChecked = itemList[position].isSet == 1

      holder.switchOnOff.setOnCheckedChangeListener { _, isChecked ->
         if (isChecked) {
            if(itemList[position].period == "매일") {
               val message = itemList[position].name + " " + itemList[position].amount + itemList[position].unit
               alarmReceiver.setAlarm1(context, itemList[position].id, itemList[position].startDate, itemList[position].endDate, timeList, message)
            }else {
               val message = itemList[position].name + " " + itemList[position].amount + itemList[position].unit
               alarmReceiver.setAlarm2(context, itemList[position].id, timeList, dateList, message)
            }
            dataManager!!.updateDrugSet(1)
         }else {
            alarmReceiver.cancelAlarm(context, itemList[position].id)
            dataManager!!.updateDrugSet(0)
         }
      }

      holder.ivDelete.setOnClickListener {
         onItemClickListener!!.onItemClick(position)
      }
   }

   override fun getItemCount(): Int {
      return itemList.count()
   }

   interface OnItemClickListener {
      fun onItemClick(pos: Int)
   }

   private var onItemClickListener: OnItemClickListener? = null

   fun setOnItemClickListener(listener: OnItemClickListener?) {
      onItemClickListener = listener
   }

   inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      val tvType: TextView = itemView.findViewById(R.id.tvType)
      val switchOnOff: SwitchCompat = itemView.findViewById(R.id.switchOnOff)
      val tvName: TextView = itemView.findViewById(R.id.tvName)
      val tvPeriod: TextView = itemView.findViewById(R.id.tvPeriod)
      val tvDate: TextView = itemView.findViewById(R.id.tvDate)
      val tvCount: TextView = itemView.findViewById(R.id.tvCount)
      val tvUnit: TextView = itemView.findViewById(R.id.tvUnit)
      val recyclerView1: RecyclerView = itemView.findViewById(R.id.recyclerView1)
      val recyclerView2: RecyclerView = itemView.findViewById(R.id.recyclerView2)
      val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
   }
}
