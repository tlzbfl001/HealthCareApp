package kr.bodywell.android.adapter

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.runBlocking
import kr.bodywell.android.R
import kr.bodywell.android.model.Constant.MEDICINES
import kr.bodywell.android.model.Constant.MEDICINE_INTAKES
import kr.bodywell.android.model.Medicine
import kr.bodywell.android.model.MedicineTime
import kr.bodywell.android.service.AlarmReceiver
import kr.bodywell.android.util.CustomUtil.replaceFragment2
import kr.bodywell.android.util.MyApp.Companion.dataManager
import kr.bodywell.android.util.MyApp.Companion.powerSync
import kr.bodywell.android.view.home.medicine.MedicineAddFragment
import java.time.LocalDate
import java.time.Period

class MedicineAdapter2 (
   private val fragmentManager: FragmentManager,
   private val itemList: ArrayList<Medicine> = ArrayList()
) : RecyclerView.Adapter<MedicineAdapter2.ViewHolder>() {
   private lateinit var context: Context
//   private lateinit var dataManager: DataManager
   private var bundle = Bundle()
   private var alarmReceiver: AlarmReceiver = AlarmReceiver()
   private var getData = ArrayList<MedicineTime>()
   private val timeList = ArrayList<MedicineTime>()

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_medicine_record, parent, false)
      context = parent.context
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
      val getMedicine = dataManager.getMedicine(itemList[pos].id)

      holder.tvCategory.text = itemList[pos].category
      holder.tvName.text = itemList[pos].name
      holder.tvCount.text = itemList[pos].amount.toString() + itemList[pos].unit

      // 약복용 시간목록 보여주기
      runBlocking {
         getData = powerSync.getAllMedicineTime(itemList[pos].id) as ArrayList<MedicineTime>

         val period = Period.between(LocalDate.parse(itemList[pos].starts), LocalDate.parse(itemList[pos].ends))
         holder.tvPeriod.text = "${period.days + 1}일동안 ${getData.size}회 복용"

         if(getData.isNotEmpty()) {
            val timeList = ArrayList<MedicineTime>()
            for(i in 0 until getData.size) timeList.add(MedicineTime(time = getData[i].time, userId = i + 1))

            val adapter = MedicineAdapter3(timeList)
            holder.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            holder.recyclerView.adapter = adapter
         }

         if(getMedicine.isSet == 1) holder.switchOnOff.isChecked = true
      }

      // 알람 체크 설정
      holder.switchOnOff.setOnCheckedChangeListener { _, isChecked ->
         if(isChecked) {
            val message = itemList[pos].name + " " + itemList[pos].amount + itemList[pos].unit
            alarmReceiver.setAlarm(context, getMedicine.id, itemList[pos].starts, itemList[pos].ends, timeList, message)
            dataManager.updateAlarmSet(1)
         }else {
            alarmReceiver.cancelAlarm(context, getMedicine.id)
            dataManager.updateAlarmSet(0)
         }
      }

      // 약복용 수정페이지로 이동
      holder.cvEdit.setOnClickListener {
         bundle.putParcelable(MEDICINES, itemList[pos])
         replaceFragment2(fragmentManager, MedicineAddFragment(), bundle)
      }

      // 약복용 삭제
      holder.ivDelete.setOnClickListener {
         val dialog = AlertDialog.Builder(context, R.style.AlertDialogStyle)
            .setTitle("복용약 삭제")
            .setMessage("정말 삭제하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
               runBlocking {
                  powerSync.deleteItem(MEDICINES, "id", itemList[pos].id)
                  val data = powerSync.getIntakesById(itemList[pos].id)
                  for(i in data.indices) powerSync.deleteItem(MEDICINE_INTAKES, "id", data[i])
               }

               dataManager.deleteMedicine(getMedicine.id)
               dataManager.deleteMedicineTime(getMedicine.id)
               alarmReceiver.cancelAlarm(context, getMedicine.id)
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
      val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
      val switchOnOff: SwitchCompat = itemView.findViewById(R.id.switchOnOff)
      val tvName: TextView = itemView.findViewById(R.id.tvName)
      val tvPeriod: TextView = itemView.findViewById(R.id.tvPeriod)
      val tvCount: TextView = itemView.findViewById(R.id.tvCount)
      val recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView)
      val cvEdit: CardView = itemView.findViewById(R.id.cvEdit)
      val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
   }
}
