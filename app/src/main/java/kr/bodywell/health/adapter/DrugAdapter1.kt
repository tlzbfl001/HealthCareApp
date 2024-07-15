package kr.bodywell.health.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.bodywell.health.R
import kr.bodywell.health.database.DBHelper.Companion.TABLE_DRUG
import kr.bodywell.health.database.DBHelper.Companion.TABLE_DRUG_CHECK
import kr.bodywell.health.database.DBHelper.Companion.TABLE_DRUG_TIME
import kr.bodywell.health.database.DataManager
import kr.bodywell.health.model.DrugCheck
import kr.bodywell.health.model.DrugList
import kr.bodywell.health.model.Unused
import kr.bodywell.health.util.CalendarUtil.Companion.selectedDate
import kr.bodywell.health.util.MainViewModel

class DrugAdapter1 (
    private val context: Context,
    private val itemList: List<DrugList>,
    private val drugGoal: Int,
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
//        val fragment = manager.findFragmentById(R.id.mainFrame) as DrugFragment

        holder.tvTime.text = itemList[pos].time
        holder.tvName.text = itemList[pos].name
        holder.tvAmount.text = itemList[pos].amount.toString() + itemList[pos].unit

        check = itemList[pos].initCheck

        viewModel.setInt(check)

//        if(check > 0) {
//            viewModel.setCheck(check)
//            fragment.binding.pbDrug.setProgressStartColor(Color.parseColor("#9F76DF"))
//            fragment.binding.pbDrug.setProgressEndColor(Color.parseColor("#9F76DF"))
//            fragment.binding.tvDrugCount.text = "${check}회"
//            fragment.binding.pbDrug.progress = check
//        }else {
//            fragment.binding.pbDrug.setProgressStartColor(Color.TRANSPARENT)
//            fragment.binding.pbDrug.setProgressEndColor(Color.TRANSPARENT)
//        }

        if(itemList[pos].checked > 0) holder.tvCheck.isChecked = true

//        var result = drugGoal - check
//        if(result > 0) fragment.binding.tvRemain.text = "${result}회"

        // 체크박스 체크시 복용횟수 설정
        holder.tvCheck.setOnClickListener {
            val getDrugCheck = dataManager.getDrugCheck(itemList[pos].drugTimeId, selectedDate.toString())

            if(holder.tvCheck.isChecked) {
                check += 1

                if(getDrugCheck.created == "") {
                    dataManager.insertDrugCheck(DrugCheck(uid = "", drugId = itemList[pos].drugId, drugTimeId = itemList[pos].drugTimeId, created = selectedDate.toString()))
                }
            }else {
                if(check > 0) check -= 1

                if(getDrugCheck.created != "") {
                    val getDrugUid = dataManager.getDrugTimeUid(TABLE_DRUG, itemList[pos].drugId)
                    val getDrugTimeUid = dataManager.getDrugTimeUid(TABLE_DRUG_TIME, itemList[pos].drugTimeId)
                    if(getDrugUid != "" && getDrugTimeUid != "" && getDrugCheck.uid != "") {
                        dataManager.insertUnused(Unused(type = "drugCheck", value = getDrugCheck.uid, drugUid = getDrugUid, drugTimeUid = getDrugTimeUid, created = itemList[pos].date))
                    }
                    dataManager.deleteItem(TABLE_DRUG_CHECK, "drugTimeId", itemList[pos].drugTimeId, "created", selectedDate.toString())
                }
            }

            viewModel.setInt(check)

//            result = drugGoal - check
//            if(result > 0) fragment.binding.tvRemain.text = "${result}회"
//
//            if(check > 0) {
//                fragment.binding.pbDrug.setProgressStartColor(Color.parseColor("#9F76DF"))
//                fragment.binding.pbDrug.setProgressEndColor(Color.parseColor("#9F76DF"))
//                fragment.binding.tvDrugCount.text = "${check}회"
//                fragment.binding.pbDrug.progress = check
//            }else {
//                fragment.binding.pbDrug.setProgressStartColor(Color.TRANSPARENT)
//                fragment.binding.pbDrug.setProgressEndColor(Color.TRANSPARENT)
//            }
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