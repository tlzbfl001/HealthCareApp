package com.makebodywell.bodywell.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DRUG_CHECK
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.model.DrugCheck
import com.makebodywell.bodywell.model.DrugList
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.view.home.drug.DrugFragment
import com.makebodywell.bodywell.view.home.MainActivity

class DrugAdapter1 (
    private val context: Context,
    private val itemList: List<DrugList>,
    private val drugGoal: Int
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fragment = (context as MainActivity).supportFragmentManager.findFragmentById(R.id.mainFrame) as DrugFragment

        holder.tvTime.text = itemList[position].time
        holder.tvName.text = itemList[position].name
        holder.tvAmount.text = itemList[position].amount.toString() + itemList[position].unit

        // 복용횟수 초기화
        check = itemList[position].initCheck
        if(check > 0) {
            fragment.binding.pbDrug.setProgressStartColor(Color.parseColor("#9F76DF"))
            fragment.binding.pbDrug.setProgressEndColor(Color.parseColor("#9F76DF"))
            fragment.binding.tvDrugCount.text = "${check}회"
            fragment.binding.pbDrug.progress = check
        }else {
            fragment.binding.pbDrug.setProgressStartColor(Color.TRANSPARENT)
            fragment.binding.pbDrug.setProgressEndColor(Color.TRANSPARENT)
        }

        if(itemList[position].checked == 1) holder.tvCheck.isChecked = true

        var result = drugGoal - check
        if(result > 0) {
            fragment.binding.tvRemain.text = "${result}회"
        }

        // 체크박스 체크시 복용횟수 설정
        holder.tvCheck.setOnClickListener {
            val getDrugCheckCount = dataManager!!.getDrugCheckCount(itemList[position].id, itemList[position].date)
            if(holder.tvCheck.isChecked) {
                check++
                if(getDrugCheckCount == 0) {
                    dataManager!!.insertDrugCheck(DrugCheck(drugTimeId = itemList[position].id, regDate = itemList[position].date))
                }
            }else {
                if(check > 0) {
                    check--
                }
                if(getDrugCheckCount > 0) {
                    dataManager!!.deleteItem(TABLE_DRUG_CHECK, "drugTimeId", itemList[position].id, "regDate", itemList[position].date)
                }
            }

            result = drugGoal - check
            if(result > 0) {
                fragment.binding.tvRemain.text = "${result}회"
            }

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