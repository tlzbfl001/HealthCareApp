package kr.bodywell.android.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.bodywell.android.R
import kr.bodywell.android.database.DBHelper.Companion.DRUG
import kr.bodywell.android.database.DBHelper.Companion.DRUG_CHECK
import kr.bodywell.android.database.DBHelper.Companion.DRUG_TIME
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.DrugCheck
import kr.bodywell.android.model.DrugList
import kr.bodywell.android.model.Unused
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil.dateTimeFormatter
import kr.bodywell.android.util.CustomUtil.dateTimeToIso
import kr.bodywell.android.view.MainViewModel
import java.time.LocalDateTime

class DrugAdapter1 (
    private val context: Context,
    private val itemList: List<DrugList>,
    private val viewModel: MainViewModel
) : RecyclerView.Adapter<DrugAdapter1.ViewHolder>() {
    private var dataManager: DataManager = DataManager(context)
    private var check = 0

    init { dataManager.open() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_drug_daily, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.tvTime.text = itemList[pos].time
        holder.tvName.text = itemList[pos].name
        holder.tvAmount.text = itemList[pos].amount.toString() + itemList[pos].unit
        check = itemList[pos].initCheck
        viewModel.setInt(check)
        if(itemList[pos].checked > 0) holder.tvCheck.isChecked = true

        // 체크박스 체크시 복용횟수 설정
        holder.tvCheck.setOnClickListener {
            val getDrugCheck = dataManager.getDrugCheck(itemList[pos].drugTimeId, selectedDate.toString())
            if(holder.tvCheck.isChecked) {
                check += 1
                if(getDrugCheck.createdAt == "") {
                    dataManager.insertDrugCheck(DrugCheck(drugId = itemList[pos].drugId, drugTimeId = itemList[pos].drugTimeId, time = itemList[pos].time,
                        createdAt = selectedDate.toString(), checkedAt = LocalDateTime.now().toString()))
                }
            }else {
                if(check > 0) check -= 1
                if(getDrugCheck.id > 0) {
                    val getDrug = dataManager.getData(DRUG, itemList[pos].drugId)
                    val getDrugTime = dataManager.getData(DRUG_TIME, itemList[pos].drugTimeId)
                    if(getDrug.uid != "" && getDrugTime.uid != "" && getDrugCheck.uid != "") {
                        dataManager.insertUnused(Unused(type = DRUG_CHECK, value = getDrugCheck.uid, drugUid = getDrug.uid, drugTimeUid = getDrugTime.uid, createdAt = itemList[pos].date))
                    }
                    dataManager.deleteItem(DRUG_CHECK, "id", getDrugCheck.id)
                }
            }

            viewModel.setInt(check)
        }
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