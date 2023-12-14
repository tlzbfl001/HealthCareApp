package com.makebodywell.bodywell.view.home.drug

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.makebodywell.bodywell.adapter.DrugAdapter2
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

      initView()
      setupList()

      return binding.root
   }

   private fun initView() {
      binding.ivBack.setOnClickListener {
         replaceFragment1(requireActivity(), DrugFragment())
      }
      binding.cvAdd.setOnClickListener {
         replaceFragment1(requireActivity(), DrugAddFragment())
      }
   }

   private fun setupList() {
      val getDrug = dataManager!!.getDrug()
      for(i in 0 until getDrug.size) {
         itemList.add(Drug(id = getDrug[i].id, type = getDrug[i].type, name = getDrug[i].name, amount = getDrug[i].amount, unit = getDrug[i].unit,
            period = getDrug[i].period, startDate = getDrug[i].startDate, endDate = getDrug[i].endDate))
      }

      adapter = DrugAdapter2(requireActivity(), itemList)
      binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
      binding.recyclerView.requestLayout()

      // 데이터 삭제, 등록된 알람 취소
      adapter!!.setOnItemClickListener(object : DrugAdapter2.OnItemClickListener {
         override fun onItemClick(pos: Int) {
            dataManager!!.deleteDrugDate(itemList[pos].id)
            dataManager!!.deleteDrugTime(itemList[pos].id)
            dataManager!!.deleteDrug(itemList[pos].id)

            alarmReceiver.cancelAlarm(requireActivity(), itemList[pos].id)

            itemList.removeAt(pos)
            adapter!!.notifyDataSetChanged()
         }
      })

      binding.recyclerView.adapter = adapter
   }
}