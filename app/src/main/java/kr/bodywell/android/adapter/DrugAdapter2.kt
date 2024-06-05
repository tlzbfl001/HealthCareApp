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
import kr.bodywell.android.util.AlarmReceiver
import kr.bodywell.android.util.CustomUtil.Companion.replaceFragment2
import kr.bodywell.android.view.home.drug.DrugAddFragment

class DrugAdapter2 (
   private val context: Activity,
   private val itemList: ArrayList<Drug> = ArrayList()
) : RecyclerView.Adapter<DrugAdapter2.ViewHolder>() {
   private var bundle = Bundle()
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
      holder.tvCount.text = itemList[position].amount.toString() + itemList[position].unit

      val getDrugTime = dataManager!!.getDrugTime(itemList[position].id)

      holder.tvPeriod.text = "${itemList[position].count}일동안 ${getDrugTime.size}회 복용"

      if(getDrugTime.isNotEmpty()) {
         timeList.clear()

         for(i in 0 until getDrugTime.size) {
            timeList.add(DrugTime(id = getDrugTime[i].id, time = getDrugTime[i].time, drugId = i+1))
         }

         val adapter = DrugAdapter3(timeList)
         holder.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
         holder.recyclerView.adapter = adapter
      }

      holder.switchOnOff.isChecked = itemList[position].isSet == 1

      holder.switchOnOff.setOnCheckedChangeListener { _, isChecked ->
         if(isChecked) {
            val message = itemList[position].name + " " + itemList[position].amount + itemList[position].unit
            alarmReceiver.setAlarm(context, itemList[position].id, itemList[position].startDate, itemList[position].endDate, timeList, message)
            dataManager!!.updateInt(TABLE_DRUG, "isSet", 1, "id", itemList[position].id)
         }else {
            alarmReceiver.cancelAlarm(context, itemList[position].id)
            dataManager!!.updateInt(TABLE_DRUG, "isSet", 0, "id", itemList[position].id)
         }
      }

      holder.cvEdit.setOnClickListener {
         bundle.putString("id", itemList[position].id.toString())
         replaceFragment2(context, DrugAddFragment(), bundle)
      }

      holder.ivDelete.setOnClickListener {
         val dialog = AlertDialog.Builder(context, R.style.AlertDialogStyle)
            .setTitle("복용약 삭제")
            .setMessage("해당 약과 관련된 모든 데이터가 삭제됩니다.\n정말 삭제하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
               dataManager!!.deleteItem(TABLE_DRUG_CHECK, "drugId", itemList[position].id)
               dataManager!!.deleteItem(TABLE_DRUG_TIME, "drugId", itemList[position].id)
               dataManager!!.deleteItem(TABLE_DRUG, "id", itemList[position].id)

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
      val cvEdit: CardView = itemView.findViewById(R.id.cvEdit)
      val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
   }
}
