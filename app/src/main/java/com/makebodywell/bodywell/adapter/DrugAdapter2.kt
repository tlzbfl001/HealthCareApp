package com.makebodywell.bodywell.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.model.Drug
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import java.text.SimpleDateFormat

class DrugAdapter2 (
   private val context: Context,
   private val itemList1: ArrayList<Drug> = ArrayList()
) : RecyclerView.Adapter<DrugAdapter2.ViewHolder>() {
   private var dataManager: DataManager? = null
   private val itemList2: ArrayList<String> = ArrayList()
   private val itemList3: ArrayList<Drug> = ArrayList()

   private val formatter1 = SimpleDateFormat("yyyy-MM-dd")
   private val formatter2 = SimpleDateFormat("yyyy. MM. dd")
   private val formatter3 = SimpleDateFormat("M/dd")

   init {
      dataManager = DataManager(context)
      dataManager!!.open()
   }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_drug_record, parent, false)
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.tvType.text = itemList1[position].type
      holder.tvName.text = itemList1[position].name
      holder.tvPeriod.text = itemList1[position].period
      holder.tvCount.text = itemList1[position].amount
      holder.tvUnit.text = itemList1[position].unit

      val startDate = formatter2.format(formatter1.parse(itemList1[position].startDate)!!)
      val endDate = formatter2.format(formatter1.parse(itemList1[position].endDate)!!)
      holder.tvDate.text = "$startDate ~ $endDate"

      val getDrugDate = dataManager?.getDrugDate(itemList1[position].id)
      if(getDrugDate!!.isNotEmpty()) {
         itemList2.clear()
         for(i in 0 until getDrugDate.size) {
            val date = formatter3.format(formatter1.parse(getDrugDate[i].name)!!)
            itemList2.add(date)
         }
         val adapter1 = DrugAdapter3(itemList2)
         holder.recyclerView1.layoutManager = GridLayoutManager(context, 6)
         holder.recyclerView1.adapter = adapter1
      }

      val getDrugTime = dataManager?.getDrugTime(itemList1[position].id)
      if(getDrugTime!!.isNotEmpty()) {
         itemList3.clear()
         for(i in 0 until getDrugTime.size) {
            itemList3.add(Drug(name = getDrugTime[i].name, count = i+1))
         }
         val adapter2 = DrugAdapter4(itemList3)
         holder.recyclerView2.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
         holder.recyclerView2.adapter = adapter2
      }

      holder.switchOnOff.setOnCheckedChangeListener { buttonView, isChecked ->
         if (isChecked) {
            Log.d(TAG, "id: ${itemList1[position].id}")
         }else {
            Log.d(TAG, "id: ${itemList1[position].id}")
         }
      }
   }

   override fun getItemCount(): Int {
      return itemList1.count()
   }

   interface OnItemClickListener {
      fun onItemClick(pos: Int)
   }

   private var onItemClickListener: OnItemClickListener? = null

   fun setOnItemClickListener(listener: OnItemClickListener?) {
      onItemClickListener = listener
   }

   inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      val tvType: TextView = itemView.findViewById(R.id.tvType)
      val switchOnOff: SwitchCompat = itemView.findViewById(R.id.switchOnOff)
      val tvName: TextView = itemView.findViewById(R.id.tvName)
      val tvPeriod: TextView = itemView.findViewById(R.id.tvPeriod)
      val tvDate: TextView = itemView.findViewById(R.id.tvDate)
      val tvCount: TextView = itemView.findViewById(R.id.tvCount)
      val tvUnit: TextView = itemView.findViewById(R.id.tvUnit)
      val recyclerView1: RecyclerView = itemView.findViewById(R.id.recyclerView1)
      val recyclerView2: RecyclerView = itemView.findViewById(R.id.recyclerView2)
      private val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)

      init {
         ivDelete.setOnClickListener {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
               onItemClickListener!!.onItemClick(position)
            }
         }
      }
   }
}