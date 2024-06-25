package kr.bodywell.android.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.bodywell.android.R
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DRUG_CHECK
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.DrugCheck
import kr.bodywell.android.model.DrugList
import kr.bodywell.android.model.Unused
import kr.bodywell.android.util.CalendarUtil
import kr.bodywell.android.util.CalendarUtil.Companion.selectedDate
import kr.bodywell.android.util.CustomUtil
import kr.bodywell.android.util.CustomUtil.Companion.TAG
import kr.bodywell.android.view.home.MainActivity
import kr.bodywell.android.view.home.drug.DrugFragment
import okhttp3.internal.userAgent

class DrugAdapter1 (
    private val context: Context,
    private val itemList: List<DrugList>,
    private val drugGoal: Int
) : RecyclerView.Adapter<DrugAdapter1.ViewHolder>() {
    private var dataManager: DataManager = DataManager(context)
    private var check = 0

    init { dataManager.open() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_drug_daily, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val fragment = (context as MainActivity).supportFragmentManager.findFragmentById(R.id.mainFrame) as DrugFragment

        holder.tvTime.text = itemList[pos].time
        holder.tvName.text = itemList[pos].name
        holder.tvAmount.text = itemList[pos].amount.toString() + itemList[pos].unit

        check = itemList[pos].initCheck

        if(check > 0) {
            fragment.binding.pbDrug.setProgressStartColor(Color.parseColor("#9F76DF"))
            fragment.binding.pbDrug.setProgressEndColor(Color.parseColor("#9F76DF"))
            fragment.binding.tvDrugCount.text = "${check}회"
            fragment.binding.pbDrug.progress = check
        }else {
            fragment.binding.pbDrug.setProgressStartColor(Color.TRANSPARENT)
            fragment.binding.pbDrug.setProgressEndColor(Color.TRANSPARENT)
        }

        if(itemList[pos].checked > 0) holder.tvCheck.isChecked = true

        var result = drugGoal - check
        if(result > 0) fragment.binding.tvRemain.text = "${result}회"

        // 체크박스 체크시 복용횟수 설정
        holder.tvCheck.setOnClickListener {
            val getDrugCheck = dataManager.getDrugCheck(itemList[pos].drugTimeId, itemList[pos].date)

            if(holder.tvCheck.isChecked) {
                check += 1

                if(getDrugCheck.regDate == "") {
                    dataManager.insertDrugCheck(DrugCheck(uid = "", drugId = itemList[pos].drugId, drugTimeId = itemList[pos].drugTimeId, regDate = selectedDate.toString()))
                }
            }else {
                if(check > 0) check -= 1

                if(getDrugCheck.regDate != "") {
                    if(getDrugCheck.uid != "") {
                        dataManager.insertUnused(Unused(type = "drugCheck", value = getDrugCheck.uid, regDate = selectedDate.toString()))
                    }

                    dataManager.deleteItem(TABLE_DRUG_CHECK, "drugTimeId", itemList[pos].drugTimeId, "regDate", itemList[pos].date)
                }
            }

            result = drugGoal - check
            if(result > 0) fragment.binding.tvRemain.text = "${result}회"

            if(check > 0) {
                fragment.binding.pbDrug.setProgressStartColor(Color.parseColor("#9F76DF"))
                fragment.binding.pbDrug.setProgressEndColor(Color.parseColor("#9F76DF"))
                fragment.binding.tvDrugCount.text = "${check}회"
                fragment.binding.pbDrug.progress = check
            }else {
                fragment.binding.pbDrug.setProgressStartColor(Color.TRANSPARENT)
                fragment.binding.pbDrug.setProgressEndColor(Color.TRANSPARENT)
            }
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