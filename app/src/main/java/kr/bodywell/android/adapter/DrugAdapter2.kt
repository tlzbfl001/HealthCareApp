package kr.bodywell.android.adapter

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.bodywell.android.R
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DRUG
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DRUG_CHECK
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DRUG_TIME
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.Drug
import kr.bodywell.android.model.DrugTime
import kr.bodywell.android.model.Unused
import kr.bodywell.android.util.AlarmReceiver
import kr.bodywell.android.util.CalendarUtil.Companion.selectedDate
import kr.bodywell.android.util.CustomUtil.Companion.replaceFragment2
import kr.bodywell.android.view.home.drug.DrugAddFragment

class DrugAdapter2 (
   private val context: Activity,
   private val itemList: ArrayList<Drug> = ArrayList()
) : RecyclerView.Adapter<DrugAdapter2.ViewHolder>() {
   private var bundle = Bundle()
   private var dataManager: DataManager = DataManager(context)
   private var alarmReceiver: AlarmReceiver
   private val timeList: ArrayList<DrugTime> = ArrayList()

   init {
      dataManager.open()
      alarmReceiver = AlarmReceiver()
   }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_drug_record, parent, false)
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
      holder.tvType.text = itemList[pos].type
      holder.tvName.text = itemList[pos].name
      holder.tvCount.text = itemList[pos].amount.toString() + itemList[pos].unit

      val getDrugTime = dataManager.getDrugTime(itemList[pos].id)

      holder.tvPeriod.text = "${itemList[pos].count}일동안 ${getDrugTime.size}회 복용"

      if(getDrugTime.isNotEmpty()) {
         timeList.clear()

         for(i in 0 until getDrugTime.size) {
            timeList.add(DrugTime(id = getDrugTime[i].id, time = getDrugTime[i].time, drugId = i+1))
         }

         val adapter = DrugAdapter3(timeList)
         holder.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
         holder.recyclerView.adapter = adapter
      }

      holder.switchOnOff.isChecked = itemList[pos].isSet == 1

      holder.switchOnOff.setOnCheckedChangeListener { _, isChecked ->
         if(isChecked) {
            val message = itemList[pos].name + " " + itemList[pos].amount + itemList[pos].unit
            alarmReceiver.setAlarm(context, itemList[pos].id, itemList[pos].startDate, itemList[pos].endDate, timeList, message)
            dataManager.updateInt(TABLE_DRUG, "isSet", 1, "id", itemList[pos].id)
         }else {
            alarmReceiver.cancelAlarm(context, itemList[pos].id)
            dataManager.updateInt(TABLE_DRUG, "isSet", 0, "id", itemList[pos].id)
         }
      }

      holder.cvEdit.setOnClickListener {
         bundle.putString("id", itemList[pos].id.toString())
         replaceFragment2(context, DrugAddFragment(), bundle)
      }

      holder.ivDelete.setOnClickListener {
         val dialog = AlertDialog.Builder(context, R.style.AlertDialogStyle)
            .setTitle("복용약 삭제")
            .setMessage("정말 삭제하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
               val drugCheckUid = dataManager.getDrugUid(TABLE_DRUG_CHECK, "drugTimeId", "drugId", itemList[pos].id)
               for(i in 0 until drugCheckUid.size) {
                  val drugTimeUid = dataManager.getDrugTimeUid(TABLE_DRUG_TIME, drugCheckUid[i].drugTimeId)
                  if(drugTimeUid != "" && drugCheckUid[i].uid != "") {
                     dataManager.insertUnused(Unused(type = "drugCheck", value = drugCheckUid[i].uid,
                        drugUid = itemList[pos].uid, drugTimeUid = drugTimeUid, created = itemList[pos].startDate))
                  }
               }

               val drugTimeUid = dataManager.getDrugUid(TABLE_DRUG_TIME, "id", "drugId", itemList[pos].id)
               for(i in 0 until drugTimeUid.size) {
                  if(drugTimeUid[i].uid != "") dataManager.insertUnused(Unused(type = "drugTime", value = drugTimeUid[i].uid, drugUid = itemList[pos].uid, created = itemList[pos].startDate))
               }

               if(itemList[pos].uid != "") dataManager.insertUnused(Unused(type = "drug", value = itemList[pos].uid, created = itemList[pos].startDate))

               dataManager.deleteItem(TABLE_DRUG_CHECK, "drugId", itemList[pos].id)
               dataManager.deleteItem(TABLE_DRUG_TIME, "drugId", itemList[pos].id)
               dataManager.deleteItem(TABLE_DRUG, "id", itemList[pos].id)

               alarmReceiver.cancelAlarm(context, itemList[pos].id)
               itemList.removeAt(pos)

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
      val cvEdit: CardView = itemView.findViewById(R.id.cvEdit)
      val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
   }
}
