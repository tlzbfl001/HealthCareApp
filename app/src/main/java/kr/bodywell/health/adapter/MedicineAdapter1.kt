package kr.bodywell.health.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.f4b6a3.uuid.UuidCreator
import kotlinx.coroutines.runBlocking
import kr.bodywell.health.R
import kr.bodywell.health.model.Constant.MEDICINE_INTAKES
import kr.bodywell.health.model.MedicineList
import kr.bodywell.health.model.MedicineIntake
import kr.bodywell.health.util.CalendarUtil.selectedDate
import kr.bodywell.health.util.MyApp.Companion.powerSync
import kr.bodywell.health.view.MainViewModel
import java.time.LocalDateTime

class MedicineAdapter1 (
   private val itemList: List<MedicineList>,
   private val viewModel: MainViewModel
) : RecyclerView.Adapter<MedicineAdapter1.ViewHolder>() {
    private var check = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_medicine_daily, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.tvTime.text = itemList[pos].time
        holder.tvName.text = itemList[pos].name
        holder.tvAmount.text = itemList[pos].amount.toString() + itemList[pos].unit

        check = itemList[pos].initSet
        viewModel.setMedicineCheckState(check)
        if(itemList[pos].isSet != "") holder.tvCheck.isChecked = true

        // 체크박스 체크시 복용횟수 설정
        holder.tvCheck.setOnClickListener {
            runBlocking {
                val getData = powerSync.getIntake(selectedDate.toString(), itemList[pos].medicineTimeId)

                if(holder.tvCheck.isChecked) {
                    check += 1
                    if(getData.id == "") {
                        val uuid = UuidCreator.getTimeOrderedEpoch()
                        powerSync.insertMedicineIntake(MedicineIntake(id = uuid.toString(), name = itemList[pos].name, intakeAt = selectedDate.toString(),
                            createdAt = LocalDateTime.now().toString(), updatedAt = LocalDateTime.now().toString(), medicineTimeId = itemList[pos].medicineTimeId))
                    }
                }else {
                    if(check > 0) check -= 1
                    if(getData.id != "") powerSync.deleteItem(MEDICINE_INTAKES, "id", getData.id)
                }

                viewModel.setMedicineCheckState(check)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvCheck: CheckBox = itemView.findViewById(R.id.tvCheck)
    }
}