package com.makebodywell.bodywell.view.home.drug

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.adapter.DrugAdapter5
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentDrugAddBinding
import com.makebodywell.bodywell.model.Drug
import com.makebodywell.bodywell.model.DrugDate
import com.makebodywell.bodywell.model.DrugTime
import com.makebodywell.bodywell.util.CustomUtil.Companion.clearDrugData
import com.makebodywell.bodywell.util.CustomUtil.Companion.drugCount
import com.makebodywell.bodywell.util.CustomUtil.Companion.drugDateList
import com.makebodywell.bodywell.util.CustomUtil.Companion.drugEndDate
import com.makebodywell.bodywell.util.CustomUtil.Companion.drugName
import com.makebodywell.bodywell.util.CustomUtil.Companion.drugPeriodNum
import com.makebodywell.bodywell.util.CustomUtil.Companion.drugStartDate
import com.makebodywell.bodywell.util.CustomUtil.Companion.drugTimeList
import com.makebodywell.bodywell.util.CustomUtil.Companion.drugType
import com.makebodywell.bodywell.util.CustomUtil.Companion.drugUnitNum
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.CustomUtil.Companion.setDrugTimeList
import java.time.LocalDate
import java.time.LocalDateTime

class DrugAddFragment : Fragment() {
   private var _binding: FragmentDrugAddBinding? = null
   private val binding get() = _binding!!

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

      requireActivity().window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK

         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.mainLayout.setPadding(0, statusBarHeight, 0, 0)
      }

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
         binding.etAmount.setText(drugCount)

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
         drugCount = binding.etAmount.text.toString()

         replaceFragment1(requireActivity(), DrugSelectDateFragment1())
      }

      binding.cvSpecific.setOnClickListener {
         period2()
         drugPeriodNum = 2
         drugType = binding.etType.text.toString()
         drugName = binding.etName.text.toString()
         drugCount = binding.etAmount.text.toString()

         replaceFragment1(requireActivity(), DrugSelectDateFragment2())
      }

      binding.tvSave.setOnClickListener {
         if(binding.etType.text.isEmpty() || binding.etName.text.isEmpty() || binding.etAmount.text.isEmpty() || binding.tvPeriod.text == "" || itemList.size == 0) {
            Toast.makeText(activity, "입력을 확인해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            // 약 데이터 저장
            dataManager!!.insertDrug(Drug(type = binding.etType.text.toString(), name = binding.etName.text.toString(), amount = binding.etAmount.text.toString(),
               unit = unit, period = period, startDate = drugStartDate, endDate = drugEndDate, regDate = LocalDate.now().toString()))

            val getDrugId = dataManager!!.getDrugId(LocalDate.now().toString())

            // 시간 데이터 저장
            for(i in 0 until drugTimeList.size) {
               dataManager!!.insertDrugTime(DrugTime(hour = drugTimeList[i].hour, minute = drugTimeList[i].minute, drugId = getDrugId.id))
            }

            // 특정 날짜 데이터 저장
            if(period == "특정일 지정") {
               val drugDate = ArrayList<DrugDate>()
               for(i in 0 until drugDateList.size) {
                  dataManager!!.insertDrugDate(DrugDate(date = drugDateList[i].toString(), drugId = getDrugId.id))
                  drugDate.add(DrugDate(date = drugDateList[i].toString()))
               }
            }

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

               setDrugTimeList(h, m)
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
         val hour = String.format("%02d", drugTimeList[i].hour)
         val minute = String.format("%02d", drugTimeList[i].minute)
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

   private fun unit1() {
      binding.clUnit1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#A47AE8"))
      binding.tvUnit1.setTextColor(Color.WHITE)
      binding.clUnit2.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit2.setTextColor(Color.BLACK)
      binding.clUnit3.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit3.setTextColor(Color.BLACK)
      binding.clUnit4.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit4.setTextColor(Color.BLACK)
      binding.clUnit5.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit5.setTextColor(Color.BLACK)
      binding.clUnit6.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit6.setTextColor(Color.BLACK)
      unit = "정"
   }

   private fun unit2() {
      binding.clUnit1.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit1.setTextColor(Color.BLACK)
      binding.clUnit2.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#A47AE8"))
      binding.tvUnit2.setTextColor(Color.WHITE)
      binding.clUnit3.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit3.setTextColor(Color.BLACK)
      binding.clUnit4.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit4.setTextColor(Color.BLACK)
      binding.clUnit5.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit5.setTextColor(Color.BLACK)
      binding.clUnit6.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit6.setTextColor(Color.BLACK)
      unit = "개"
   }

   private fun unit3() {
      binding.clUnit1.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit1.setTextColor(Color.BLACK)
      binding.clUnit2.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit2.setTextColor(Color.BLACK)
      binding.clUnit3.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#A47AE8"))
      binding.tvUnit3.setTextColor(Color.WHITE)
      binding.clUnit4.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit4.setTextColor(Color.BLACK)
      binding.clUnit5.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit5.setTextColor(Color.BLACK)
      binding.clUnit6.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit6.setTextColor(Color.BLACK)
      unit = "ml"
   }

   private fun unit4() {
      binding.clUnit1.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit1.setTextColor(Color.BLACK)
      binding.clUnit2.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit2.setTextColor(Color.BLACK)
      binding.clUnit3.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit3.setTextColor(Color.BLACK)
      binding.clUnit4.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#A47AE8"))
      binding.tvUnit4.setTextColor(Color.WHITE)
      binding.clUnit5.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit5.setTextColor(Color.BLACK)
      binding.clUnit6.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit6.setTextColor(Color.BLACK)
      unit = "mg"
   }

   private fun unit5() {
      binding.clUnit1.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit1.setTextColor(Color.BLACK)
      binding.clUnit2.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit2.setTextColor(Color.BLACK)
      binding.clUnit3.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit3.setTextColor(Color.BLACK)
      binding.clUnit4.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit4.setTextColor(Color.BLACK)
      binding.clUnit5.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#A47AE8"))
      binding.tvUnit5.setTextColor(Color.WHITE)
      binding.clUnit6.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit6.setTextColor(Color.BLACK)
      unit = "set"
   }

   private fun unit6() {
      binding.clUnit1.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit1.setTextColor(Color.BLACK)
      binding.clUnit2.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit2.setTextColor(Color.BLACK)
      binding.clUnit3.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit3.setTextColor(Color.BLACK)
      binding.clUnit4.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit4.setTextColor(Color.BLACK)
      binding.clUnit5.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
      binding.tvUnit5.setTextColor(Color.BLACK)
      binding.clUnit6.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#A47AE8"))
      binding.tvUnit6.setTextColor(Color.WHITE)
      unit = "봉"
   }

   private fun period1() {
      binding.cvDaily.setCardBackgroundColor(Color.parseColor("#A47AE8"))
      binding.tvDaily.setTextColor(Color.WHITE)
      binding.cvSpecific.setCardBackgroundColor(Color.WHITE)
      binding.tvSpecific.setTextColor(Color.BLACK)
      period = "매일"
   }

   private fun period2() {
      binding.cvDaily.setCardBackgroundColor(Color.WHITE)
      binding.tvDaily.setTextColor(Color.BLACK)
      binding.cvSpecific.setCardBackgroundColor(Color.parseColor("#A47AE8"))
      binding.tvSpecific.setTextColor(Color.WHITE)
      period = "특정일 지정"
   }
}