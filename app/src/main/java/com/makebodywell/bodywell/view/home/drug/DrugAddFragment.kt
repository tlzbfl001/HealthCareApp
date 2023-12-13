package com.makebodywell.bodywell.view.home.drug

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.adapter.DrugAdapter5
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentDrugAddBinding
import com.makebodywell.bodywell.model.Drug
import com.makebodywell.bodywell.model.DrugDate
import com.makebodywell.bodywell.util.AlarmReceiver
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.DrugUtil
import com.makebodywell.bodywell.util.DrugUtil.Companion.clearDrugData
import com.makebodywell.bodywell.util.DrugUtil.Companion.drugCount
import com.makebodywell.bodywell.util.DrugUtil.Companion.drugDateList
import com.makebodywell.bodywell.util.DrugUtil.Companion.drugEndDate
import com.makebodywell.bodywell.util.DrugUtil.Companion.drugName
import com.makebodywell.bodywell.util.DrugUtil.Companion.drugPeriodNum
import com.makebodywell.bodywell.util.DrugUtil.Companion.drugStartDate
import com.makebodywell.bodywell.util.DrugUtil.Companion.drugTimeList
import com.makebodywell.bodywell.util.DrugUtil.Companion.drugType
import com.makebodywell.bodywell.util.DrugUtil.Companion.drugUnitNum
import com.makebodywell.bodywell.util.DrugUtil.Companion.setDrugTimeList
import java.text.SimpleDateFormat
import java.time.LocalDateTime

class DrugAddFragment : Fragment() {
   private var _binding: FragmentDrugAddBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback

   private val alarmReceiver = AlarmReceiver()
   private var dataManager: DataManager? = null

   private var adapter: DrugAdapter5? = null
   private val itemList = ArrayList<Drug>()
   private var unit = "정"
   private var period = "매일"

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentDrugAddBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      initView()
      settingTime()
      showTimeList()

      return binding.root
   }

   private fun initView() {
      // 복용주기 화면이동 후 설정값 재지정
      if(arguments?.getString("data") == "DrugSelectDateFragment") {
         binding.etType.setText(drugType)
         binding.etName.setText(drugName)
         binding.etCount.setText(drugCount)

         if(drugStartDate != "" && drugEndDate != "") {
            binding.tvPeriod.text = "$drugStartDate ~ $drugEndDate"
         }

         when(drugUnitNum) {
            1 -> unit1()
            2 -> unit2()
            3 -> unit3()
            4 -> unit4()
            5 -> unit5()
            6 -> unit6()
         }

         when(drugPeriodNum) {
            1 -> period1()
            2 -> period2()
         }
      }else {
         clearDrugData()
      }

      binding.ivBack.setOnClickListener {
         replaceFragment1(requireActivity(), DrugRecordFragment())
      }

      binding.cvDaily.setOnClickListener {
         replaceFragment1(requireActivity(), DrugSelectDateFragment1())
      }

      binding.clUnit1.setOnClickListener {
         unit1()
         drugUnitNum = 1
      }

      binding.clUnit2.setOnClickListener {
         unit2()
         drugUnitNum = 2
      }

      binding.clUnit3.setOnClickListener {
         unit3()
         drugUnitNum = 3
      }

      binding.clUnit4.setOnClickListener {
         unit4()
         drugUnitNum = 4
      }

      binding.clUnit5.setOnClickListener {
         unit5()
         drugUnitNum = 5
      }

      binding.clUnit6.setOnClickListener {
         unit6()
         drugUnitNum = 6
      }

      binding.cvDaily.setOnClickListener {
         period1()
         drugPeriodNum = 1
         drugType = binding.etType.text.toString()
         drugName = binding.etName.text.toString()
         drugCount = binding.etCount.text.toString()

         replaceFragment1(requireActivity(), DrugSelectDateFragment1())
      }

      binding.cvSpecific.setOnClickListener {
         period2()
         drugPeriodNum = 2
         drugType = binding.etType.text.toString()
         drugName = binding.etName.text.toString()
         drugCount = binding.etCount.text.toString()

         replaceFragment1(requireActivity(), DrugSelectDateFragment2())
      }

      binding.tvSave.setOnClickListener {
         if(binding.etType.text.isEmpty() || binding.etName.text.isEmpty() || binding.etCount.text.isEmpty() || binding.tvPeriod.text == "" || itemList.size == 0) {
            Toast.makeText(activity, "입력을 확인해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            // 약 데이터 저장
            dataManager!!.insertDrug(Drug(type = binding.etType.text.toString(), name = binding.etName.text.toString(), amount = binding.etCount.text.toString(),
               unit = unit, period = period, startDate = drugStartDate, endDate = drugEndDate))

            val getDrugId = dataManager!!.getDrugId()

            // 시간 데이터 저장
            for(i in 0 until drugTimeList.size) {
               val hour = String.format("%02d", Integer.parseInt(drugTimeList[i].hour))
               val minute = String.format("%02d", Integer.parseInt(drugTimeList[i].minute))
               dataManager!!.insertDrugTime("$hour:$minute", getDrugId.id)
            }

            // 알람 설정
            settingAlarm(getDrugId)

            Toast.makeText(activity, "저장되었습니다.", Toast.LENGTH_SHORT).show()
            replaceFragment1(requireActivity(), DrugRecordFragment())
         }
      }
   }

   private fun settingTime() {
      binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
      binding.recyclerView.requestLayout()

      binding.cvAddTime.setOnClickListener {
         val dialog = TimePickerDialog(requireActivity(), object : TimePickerClickListener {
            override fun onPositiveClick(hour: Int, minute: Int) {
               var h = hour
               var m = minute

               val dateTime = LocalDateTime.now()
               if(hour == 0 && minute == 0 && (hour != dateTime.hour) && (minute != dateTime.minute)) {
                  h = dateTime.hour
                  m = dateTime.minute
               }

               setDrugTimeList(h.toString(), m.toString())
               showTimeList()
            }

            override fun onNegativeClick() {}
         })

         dialog.window?.setBackgroundDrawableResource(R.drawable.rec_15)
         dialog.show()
      }
   }

   private fun showTimeList() {
      itemList.clear()

      for(i in 0 until drugTimeList.size) {
         val hour = String.format("%02d", Integer.parseInt(drugTimeList[i].hour))
         val minute = String.format("%02d", Integer.parseInt(drugTimeList[i].minute))
         itemList.add(Drug(name = "$hour:$minute", count = i + 1))
      }

      if(itemList.isEmpty()) {
         binding.recyclerView.visibility = View.GONE
      }else {
         binding.recyclerView.visibility = View.VISIBLE
      }

      adapter = DrugAdapter5(itemList)
      binding.recyclerView.adapter = adapter
   }

   private fun settingAlarm(getDrugId: Drug) {
      val message = binding.etName.text.toString() + " " + binding.etCount.text.toString() + unit

      if(period == "매일") {
         alarmReceiver.setAlarm1(requireActivity(), getDrugId.id, drugStartDate, drugEndDate, drugTimeList, message)
      }else {
         if(drugDateList.size > 0) {
            val drugDate = ArrayList<DrugDate>()

            // 특정 날짜 데이터 저장
            for(i in 0 until drugDateList.size) {
               dataManager!!.insertDrugDate(drugDateList[i].toString(), getDrugId.id)
               drugDate.add(DrugDate(date = drugDateList[i].toString()))
            }

            alarmReceiver.setAlarm2(requireActivity(), getDrugId.id, drugTimeList, drugDate, message)
         }
      }
   }

   private fun unit1() {
      binding.clUnit1.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.drugSelected))
      binding.tvUnit1.setTextColor(Color.WHITE)
      binding.clUnit2.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit2.setTextColor(Color.BLACK)
      binding.clUnit3.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit3.setTextColor(Color.BLACK)
      binding.clUnit4.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit4.setTextColor(Color.BLACK)
      binding.clUnit5.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit5.setTextColor(Color.BLACK)
      binding.clUnit6.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit6.setTextColor(Color.BLACK)
      unit = "정"
   }

   private fun unit2() {
      binding.clUnit1.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit1.setTextColor(Color.BLACK)
      binding.clUnit2.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.drugSelected))
      binding.tvUnit2.setTextColor(Color.WHITE)
      binding.clUnit3.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit3.setTextColor(Color.BLACK)
      binding.clUnit4.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit4.setTextColor(Color.BLACK)
      binding.clUnit5.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit5.setTextColor(Color.BLACK)
      binding.clUnit6.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit6.setTextColor(Color.BLACK)
      unit = "개"
   }

   private fun unit3() {
      binding.clUnit1.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit1.setTextColor(Color.BLACK)
      binding.clUnit2.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit2.setTextColor(Color.BLACK)
      binding.clUnit3.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.drugSelected))
      binding.tvUnit3.setTextColor(Color.WHITE)
      binding.clUnit4.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit4.setTextColor(Color.BLACK)
      binding.clUnit5.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit5.setTextColor(Color.BLACK)
      binding.clUnit6.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit6.setTextColor(Color.BLACK)
      unit = "ml"
   }

   private fun unit4() {
      binding.clUnit1.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit1.setTextColor(Color.BLACK)
      binding.clUnit2.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit2.setTextColor(Color.BLACK)
      binding.clUnit3.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit3.setTextColor(Color.BLACK)
      binding.clUnit4.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.drugSelected))
      binding.tvUnit4.setTextColor(Color.WHITE)
      binding.clUnit5.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit5.setTextColor(Color.BLACK)
      binding.clUnit6.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit6.setTextColor(Color.BLACK)
      unit = "mg"
   }

   private fun unit5() {
      binding.clUnit1.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit1.setTextColor(Color.BLACK)
      binding.clUnit2.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit2.setTextColor(Color.BLACK)
      binding.clUnit3.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit3.setTextColor(Color.BLACK)
      binding.clUnit4.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit4.setTextColor(Color.BLACK)
      binding.clUnit5.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.drugSelected))
      binding.tvUnit5.setTextColor(Color.WHITE)
      binding.clUnit6.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit6.setTextColor(Color.BLACK)
      unit = "set"
   }

   private fun unit6() {
      binding.clUnit1.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit1.setTextColor(Color.BLACK)
      binding.clUnit2.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit2.setTextColor(Color.BLACK)
      binding.clUnit3.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit3.setTextColor(Color.BLACK)
      binding.clUnit4.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit4.setTextColor(Color.BLACK)
      binding.clUnit5.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvUnit5.setTextColor(Color.BLACK)
      binding.clUnit6.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.drugSelected))
      binding.tvUnit6.setTextColor(Color.WHITE)
      unit = "봉"
   }

   private fun period1() {
      binding.cvDaily.setCardBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.drugSelected))
      binding.tvDaily.setTextColor(Color.WHITE)
      binding.cvSpecific.setCardBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvSpecific.setTextColor(Color.BLACK)
      period = "매일"
   }

   private fun period2() {
      binding.cvDaily.setCardBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.white))
      binding.tvDaily.setTextColor(Color.BLACK)
      binding.cvSpecific.setCardBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.drugSelected))
      binding.tvSpecific.setTextColor(Color.WHITE)
      period = "특정일 지정"
   }

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment1(requireActivity(), DrugRecordFragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}