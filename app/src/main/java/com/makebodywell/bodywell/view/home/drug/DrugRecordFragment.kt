package com.makebodywell.bodywell.view.home.drug

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.makebodywell.bodywell.adapter.DrugAdapter2
import com.makebodywell.bodywell.database.DBHelper
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DRUG
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DRUG_CHECK
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DRUG_DATE
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DRUG_TIME
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentDrugRecordBinding
import com.makebodywell.bodywell.model.Drug
import com.makebodywell.bodywell.util.AlarmReceiver
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1

class DrugRecordFragment : Fragment() {
   private var _binding: FragmentDrugRecordBinding? = null
   private val binding get() = _binding!!

   private val alarmReceiver = AlarmReceiver()

   private var dataManager: DataManager? = null
   private var adapter: DrugAdapter2? = null
   private val itemList = ArrayList<Drug>()

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentDrugRecordBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      binding.ivBack.setOnClickListener {
         replaceFragment1(requireActivity(), DrugFragment())
      }

      binding.cvAdd.setOnClickListener {
         replaceFragment1(requireActivity(), DrugAddFragment())
      }

      val getDrug = dataManager!!.getDrug()
      for(i in 0 until getDrug.size) {
         itemList.add(Drug(id = getDrug[i].id, type = getDrug[i].type, name = getDrug[i].name, amount = getDrug[i].amount, unit = getDrug[i].unit,
            period = getDrug[i].period, startDate = getDrug[i].startDate, endDate = getDrug[i].endDate, isSet = getDrug[i].isSet))
      }

      adapter = DrugAdapter2(requireActivity(), itemList)
      binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
      binding.recyclerView.requestLayout()

      // 데이터 삭제, 등록된 알람 취소
      adapter!!.setOnItemClickListener(object : DrugAdapter2.OnItemClickListener {
         override fun onItemClick(pos: Int) {
            val dialog = AlertDialog.Builder(context)
               .setMessage("정말 삭제하시겠습니까?")
               .setPositiveButton("확인") { _, _ ->
                  dataManager!!.deleteItem(TABLE_DRUG_DATE, "drugId", itemList[pos].id)

                  val getDrugTime = dataManager!!.getDrugTime(itemList[pos].id)
                  for(i in 0 until getDrugTime.size) {
                     dataManager!!.deleteItem(TABLE_DRUG_CHECK, "drugTimeId", getDrugTime[i].id)
                  }

                  dataManager!!.deleteItem(TABLE_DRUG_TIME, "drugId", itemList[pos].id)
                  dataManager!!.deleteItem(TABLE_DRUG, "id", itemList[pos].id)

                  alarmReceiver.cancelAlarm(requireActivity(), itemList[pos].id)

                  itemList.removeAt(pos)
                  adapter!!.notifyDataSetChanged()

                  Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
               }
               .setNegativeButton("취소", null)
               .create()
            dialog.show()
         }
      })

      binding.recyclerView.adapter = adapter

      return binding.root
   }
}