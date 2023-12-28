package com.makebodywell.bodywell.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.model.Drug
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.view.home.drug.DrugFragment
import com.makebodywell.bodywell.view.home.drug.DrugSelectDateFragment2
import com.makebodywell.bodywell.view.init.MainActivity

class DrugAdapter1 (
    private val context: Context,
    private val itemList: ArrayList<Drug>
) : RecyclerView.Adapter<DrugAdapter1.ViewHolder>() {
    private var dataManager: DataManager? = null
    private var check = 0

    init {
        dataManager = DataManager(context)
        dataManager!!.open()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_drug_daily, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvTime.text = itemList[position].startDate
        holder.tvName.text = itemList[position].name
        holder.tvAmount.text = itemList[position].amount + itemList[position].unit

        val fragment: DrugFragment = (context as MainActivity).supportFragmentManager.findFragmentById(R.id.mainFrame) as DrugFragment

        // 복용횟수 초기화
        check = itemList[position].endDate.toInt()

        // 체크값 설정
        if(itemList[position].count == 1) {
            holder.tvCheck.isChecked = true
        }

        fragment.binding.tvDrugCount.text = "${check}회"

        val getDailyData = dataManager!!.getDailyData(itemList[position].type)
        val result = getDailyData.drugGoal - check
        if(result > 0) {
            fragment.binding.tvRemain.text = "${result}회"
        }

        // 체크박스 체크시 복용횟수 증가
        holder.tvCheck.setOnClickListener {
            if(holder.tvCheck.isChecked) {
                check += 1
                dataManager!!.updateDrugTime(Drug(id = itemList[position].id, name = itemList[position].startDate, count = 1))
            }else {
                if(check > 0) {
                    check -= 1
                    dataManager!!.updateDrugTime(Drug(id = itemList[position].id, name = itemList[position].startDate, count = 0))
                }
            }

            val result = getDailyData.drugGoal - check
            if(result > 0) {
                fragment.binding.tvRemain.text = "${result}회"
            }
            fragment.binding.tvDrugCount.text = "${check}회"
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