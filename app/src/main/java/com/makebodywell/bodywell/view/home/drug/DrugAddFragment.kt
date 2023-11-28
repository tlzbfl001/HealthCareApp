package com.makebodywell.bodywell.view.home.drug

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.adapter.DrugAdapter5
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentDrugAddBinding
import com.makebodywell.bodywell.model.Drug
import com.makebodywell.bodywell.util.AlarmReceiver
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDays
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.MainViewModel
import java.time.LocalDateTime

class DrugAddFragment : Fragment() {
   private var _binding: FragmentDrugAddBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback

   private val viewModel : MainViewModel by activityViewModels()

   private val alarmReceiver = AlarmReceiver()

   private var dataManager: DataManager? = null
   private var adapter: DrugAdapter5? = null
   private val itemList = ArrayList<Drug>()
   private var unit = "정"
   private var period = "매일"
   private var startDate = ""
   private var endDate = ""

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
      binding.ivBack.setOnClickListener {
         replaceFragment1(requireActivity(), DrugRecordFragment())
      }

      binding.cvDaily.setOnClickListener {
         replaceFragment1(requireActivity(), DrugSelectDateFragment1())
      }

      binding.clUnit1.setOnClickListener {
         unit1()
         viewModel.setDrugUnitNum(1)
      }
      binding.clUnit2.setOnClickListener {
         unit2()
         viewModel.setDrugUnitNum(2)
      }
      binding.clUnit3.setOnClickListener {
         unit3()
         viewModel.setDrugUnitNum(3)
      }
      binding.clUnit4.setOnClickListener {
         unit4()
         viewModel.setDrugUnitNum(4)
      }
      binding.clUnit5.setOnClickListener {
         unit5()
         viewModel.setDrugUnitNum(5)
      }
      binding.clUnit6.setOnClickListener {
         unit6()
         viewModel.setDrugUnitNum(6)
      }

      binding.cvDaily.setOnClickListener {
         period1()
         viewModel.setDrugPeriodNum(1)
         viewModel.setDrugType(binding.etType.text.toString())
         viewModel.setDrugName(binding.etName.text.toString())
         viewModel.setDrugCount(binding.etCount.text.toString())
         replaceFragment1(requireActivity(), DrugSelectDateFragment1())
      }

      binding.cvSpecific.setOnClickListener {
         period2()
         viewModel.setDrugPeriodNum(2)
         viewModel.setDrugType(binding.etType.text.toString())
         viewModel.setDrugName(binding.etName.text.toString())
         viewModel.setDrugCount(binding.etCount.text.toString())
         replaceFragment1(requireActivity(), DrugSelectDateFragment2())
      }

      binding.tvSave.setOnClickListener {
         if(binding.etType.text.isEmpty() || binding.etName.text.isEmpty() || binding.etCount.text.isEmpty() ||
            binding.tvPeriod.text == "" || itemList.size == 0) {
            Toast.makeText(activity, "입력을 확인해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            settingAlarm()
            Toast.makeText(activity, "저장되었습니다.", Toast.LENGTH_SHORT).show()
            replaceFragment1(requireActivity(), DrugRecordFragment())
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//               when {
//                  alarmManager.canScheduleExactAlarms() -> {
//                     settingAlarm()
//                     Toast.makeText(activity, "저장되었습니다.", Toast.LENGTH_SHORT).show()
//                     replaceFragment1(requireActivity(), DrugRecordFragment())
//                  }
//                  else -> {
//                     Intent().apply {
//                        action = ACTION_REQUEST_SCHEDULE_EXACT_ALARM
//                     }.also {
//                        startActivity(it)
//                     }
//                  }
//               }
//            }else {
//               settingAlarm()
//               Toast.makeText(activity, "저장되었습니다.", Toast.LENGTH_SHORT).show()
//               replaceFragment1(requireActivity(), DrugRecordFragment())
//            }
         }
      }

      val data = arguments?.getString("data")
      if(data == "DrugSelectDateFragment") {
         binding.etType.setText(viewModel.getDrugType())
         binding.etName.setText(viewModel.getDrugName())
         binding.etCount.setText(viewModel.getDrugCount())
         settingPeriod()

         when(viewModel.getDrugUnitNum()) {
            1 -> unit1()
            2 -> unit2()
            3 -> unit3()
            4 -> unit4()
            5 -> unit5()
            6 -> unit6()
         }

         when(viewModel.getDrugPeriodNum()) {
            1 -> period1()
            2 -> period2()
         }
      }else {
         viewModel.clearDrugTimeList()
      }
   }

   private fun settingPeriod() {
      val getDrugStartDate = viewModel.getDrugStartDate()
      val getDrugEndDate = viewModel.getDrugEndDate()

      if(getDrugStartDate != "" && getDrugEndDate != "") {
         startDate = getDrugStartDate
         endDate = getDrugEndDate
         binding.tvPeriod.text = "$getDrugStartDate ~ $getDrugEndDate"
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

               viewModel.setDrugTimeList(h.toString(), m.toString())
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

      for(i in 0 until viewModel.getDrugTimeList().size) {
         val hour = String.format("%02d", Integer.parseInt(viewModel.getDrugTimeList()[i].hour))
         val minute = String.format("%02d", Integer.parseInt(viewModel.getDrugTimeList()[i].minute))
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

   private fun settingAlarm() {
      dataManager!!.insertDrug(Drug(type = binding.etType.text.toString(), name = binding.etName.text.toString(),
         amount = binding.etCount.text.toString(), unit = unit, period = period, startDate = startDate, endDate = endDate))

      val getDrugId = dataManager!!.getDrugId()

      for(i in 0 until viewModel.getDrugTimeList().size) {
         val hour = String.format("%02d", Integer.parseInt(viewModel.getDrugTimeList()[i].hour))
         val minute = String.format("%02d", Integer.parseInt(viewModel.getDrugTimeList()[i].minute))
         dataManager!!.insertDrugTime("$hour:$minute", getDrugId.id)
      }

      if(period == "매일") {
         val time = viewModel.getDrugTimeList()
         val message = binding.etName.text.toString() + " " + binding.etCount.text.toString() + unit
         alarmReceiver.setAlarmDaily(requireActivity(), getDrugId.id, startDate, endDate, time, message)
      }else {
         for(i in 0 until selectedDays.size) {
            dataManager!!.insertDrugDate(selectedDays[i].toString(), getDrugId.id)
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