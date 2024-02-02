package com.makebodywell.bodywell.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DBHelper
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.model.Drug
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
      holder.tvCount.text = itemList[position].amount + itemList[position].unit

      val getDrugTime = dataManager?.getDrugTime(itemList[position].id)

      holder.tvPeriod.text = "${getDrugTime!!.size}일동안 ${itemList[position].count}회 복용"

      if(getDrugTime.isNotEmpty()) {
         timeList.clear()

         for(i in 0 until getDrugTime.size) {
            timeList.add(DrugTime(hour = getDrugTime[i].hour, minute = getDrugTime[i].minute, drugId = i+1))
         }

         val adapter = DrugAdapter3(timeList)
         holder.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
         holder.recyclerView.adapter = adapter
      }

      holder.switchOnOff.isChecked = itemList[position].isSet == 1

      holder.switchOnOff.setOnCheckedChangeListener { _, isChecked ->
         if (isChecked) {
            val message = itemList[position].name + " " + itemList[position].amount + itemList[position].unit
               alarmReceiver.setAlarm(context, itemList[position].id, itemList[position].startDate, itemList[position].endDate, timeList, message)

            dataManager!!.updateDrugSet(1)
         }else {
            alarmReceiver.cancelAlarm(context, itemList[position].id)
            dataManager!!.updateDrugSet(0)
         }
      }

      holder.ivDelete.setOnClickListener {
         val dialog = AlertDialog.Builder(context)
            .setMessage("정말 삭제하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
               for(i in 0 until timeList.size) {
                  dataManager!!.deleteItem(DBHelper.TABLE_DRUG_CHECK, "drugTimeId", timeList[i].id)
               }

               dataManager!!.deleteItem(DBHelper.TABLE_DRUG_TIME, "drugId", itemList[position].id)
               dataManager!!.deleteItem(DBHelper.TABLE_DRUG, "id", itemList[position].id)

               alarmReceiver.cancelAlarm(context, itemList[position].id)

               itemList.removeAt(position)
               notifyDataSetChanged()

               Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("취소", null)
            .create()
         dialog.show()
      }
   }

   override fun getItemCount(): Int {
      return itemList.count()
   }

   inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      val tvType: TextView = itemView.findViewById(R.id.tvType)
      val switchOnOff: SwitchCompat = itemView.findViewById(R.id.switchOnOff)
      val tvName: TextView = itemView.findViewById(R.id.tvName)
      val tvPeriod: TextView = itemView.findViewById(R.id.tvPeriod)
      val tvCount: TextView = itemView.findViewById(R.id.tvCount)
      val recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView)
      val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
   }
}
