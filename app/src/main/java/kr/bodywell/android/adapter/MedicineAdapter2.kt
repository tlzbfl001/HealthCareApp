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
import kr.bodywell.android.model.Medicine
import kr.bodywell.android.model.MedicineTime
import kr.bodywell.android.service.AlarmReceiver
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment2
import kr.bodywell.android.view.home.medicine.MedicineAddFragment

class MedicineAdapter2 (
   private val context: Activity,
   private val itemList: ArrayList<Medicine> = ArrayList()
) : RecyclerView.Adapter<MedicineAdapter2.ViewHolder>() {
   private var bundle = Bundle()
   private var alarmReceiver: AlarmReceiver = AlarmReceiver()
   private var getMedicineTime = ArrayList<MedicineTime>()
   private val timeList = ArrayList<MedicineTime>()

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_drug_record, parent, false)
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
      val split = itemList[pos].name.split("/", limit=4)

      holder.tvType.text = split[0]
      holder.tvName.text = split[1]
      holder.tvCount.text = itemList[pos].amount.toString() + itemList[pos].unit

      runBlocking {
         getMedicineTime = powerSync.getAllMedicineTime("medicine_id", itemList[pos].id) as ArrayList<MedicineTime>

         holder.tvPeriod.text = "${split[2]}일동안 ${getMedicineTime.size}회 복용"

         if(getMedicineTime.isNotEmpty()) {
            val timeList = ArrayList<MedicineTime>()
            for(i in 0 until getMedicineTime.size) {
               timeList.add(MedicineTime(time = getMedicineTime[i].time, userId = i+1))
            }

            val adapter = MedicineAdapter3(timeList)
            holder.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            holder.recyclerView.adapter = adapter
         }

         if(split[3].toInt() == 1) {
            holder.switchOnOff.isChecked = true
            holder.ivAlarm.setColorFilter(Color.parseColor("#A47AE8"))
         }
      }

      holder.switchOnOff.setOnCheckedChangeListener { _, isChecked ->
         if(isChecked) {
            val message = split[1] + " " + itemList[pos].amount + itemList[pos].unit
            alarmReceiver.setAlarm(context, itemList[pos].category.toInt(), itemList[pos].starts, itemList[pos].ends, timeList, message)
            runBlocking {
               powerSync.updateData("medicines", "name", "${split[0]}/${split[1]}/${split[2]}/1", itemList[pos].id)
            }
         }else {
            alarmReceiver.cancelAlarm(context, itemList[pos].category.toInt())
            runBlocking {
               powerSync.updateData("medicines", "name", "${split[0]}/${split[1]}/${split[2]}/0", itemList[pos].id)
            }
         }
      }

      holder.cvEdit.setOnClickListener {
         bundle.putParcelable("medicine", itemList[pos])
         replaceFragment2(context, MedicineAddFragment(), bundle)
      }

      holder.ivDelete.setOnClickListener {
         val dialog = AlertDialog.Builder(context, R.style.AlertDialogStyle)
            .setTitle("복용약 삭제")
            .setMessage("정말 삭제하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
               runBlocking {
                  powerSync.deleteItem("medicines", "id", itemList[pos].id)
               }

               alarmReceiver.cancelAlarm(context, itemList[pos].category.toInt())
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
