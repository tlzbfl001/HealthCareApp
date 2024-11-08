package kr.bodywell.android.adapter

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
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
import kotlinx.coroutines.runBlocking
import kr.bodywell.android.R
import kr.bodywell.android.model.Item
import kr.bodywell.android.model.Medicine
import kr.bodywell.android.model.MedicineTime
import kr.bodywell.android.service.AlarmReceiver
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment2
import kr.bodywell.android.view.home.drug.DrugAddFragment

class DrugAdapter2 (
   private val context: Activity,
   private val itemList: ArrayList<Medicine> = ArrayList()
) : RecyclerView.Adapter<DrugAdapter2.ViewHolder>() {
   private var bundle = Bundle()
//   private var dataManager: DataManager = DataManager(context)
   private var alarmReceiver: AlarmReceiver = AlarmReceiver()
   private var getMedicineTime = ArrayList<MedicineTime>()
   private val timeList = ArrayList<Item>()

   init {
//      dataManager.open()
   }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_drug_record, parent, false)
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
      val split = itemList[pos].category.split("/", limit=4)

      holder.tvType.text = split[0]
      holder.tvName.text = itemList[pos].name
      holder.tvCount.text = itemList[pos].amount.toString() + itemList[pos].unit

      runBlocking {
         getMedicineTime = powerSync.getMedicineTime("medicine_id", itemList[pos].id) as ArrayList<MedicineTime>
      }

      holder.tvPeriod.text = "${split[2]}일동안 ${getMedicineTime.size}회 복용"

      if(getMedicineTime.isNotEmpty()) {
         for(i in 0 until getMedicineTime.size) {
//            timeList.add(DrugTime(id = getDrugTime[i].id, time = getDrugTime[i].time, drugId = i+1))
            timeList.add(Item(string1 = getMedicineTime[i].time, int1 = i+1))
         }

         val adapter = DrugAdapter3(timeList)
         holder.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
         holder.recyclerView.adapter = adapter
      }

      if(split[3].toInt() == 1) {
         holder.switchOnOff.isChecked = true
         holder.ivAlarm.setColorFilter(Color.parseColor("#A47AE8"))
      }

      holder.switchOnOff.setOnCheckedChangeListener { _, isChecked ->
         if(isChecked) {
            val message = itemList[pos].name + " " + itemList[pos].amount + itemList[pos].unit
            alarmReceiver.setAlarm(context, split[1].toInt(), itemList[pos].starts, itemList[pos].ends, timeList, message)
            runBlocking {
               powerSync.updateStr("medicine", "category", "${split[0]}${split[1]}${split[2]}1", itemList[pos].id)
            }
         }else {
            alarmReceiver.cancelAlarm(context, split[1].toInt())
            runBlocking {
               powerSync.updateStr("medicine", "category", "${split[0]}${split[1]}${split[2]}0", itemList[pos].id)
            }
         }
      }

      holder.cvEdit.setOnClickListener {
         bundle.putParcelable("medicine", itemList[pos])
         replaceFragment2(context, DrugAddFragment(), bundle)
      }

      holder.ivDelete.setOnClickListener {
         val dialog = AlertDialog.Builder(context, R.style.AlertDialogStyle)
            .setTitle("복용약 삭제")
            .setMessage("정말 삭제하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
               runBlocking {
                  powerSync.deleteItem("medicine_intakes", "source_id", itemList[pos].id)
                  powerSync.deleteItem("medicine_times", "medicine_id", itemList[pos].id)
                  powerSync.deleteItem("medicines", "id", itemList[pos].id)
               }

               alarmReceiver.cancelAlarm(context, split[1].toInt())
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
      val ivAlarm: ImageView = itemView.findViewById(R.id.ivAlarm)
      val tvName: TextView = itemView.findViewById(R.id.tvName)
      val tvPeriod: TextView = itemView.findViewById(R.id.tvPeriod)
      val tvCount: TextView = itemView.findViewById(R.id.tvCount)
      val recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView)
      val cvEdit: CardView = itemView.findViewById(R.id.cvEdit)
      val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
   }
}
