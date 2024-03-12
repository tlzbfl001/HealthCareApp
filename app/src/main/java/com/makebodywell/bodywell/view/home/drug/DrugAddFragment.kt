package com.makebodywell.bodywell.view.home.drug

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.adapter.DrugAdapter4
import com.makebodywell.bodywell.database.DBHelper
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DRUG_CHECK
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DRUG_TIME
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentDrugAddBinding
import com.makebodywell.bodywell.model.Drug
import com.makebodywell.bodywell.model.DrugCheck
import com.makebodywell.bodywell.model.DrugTime
import com.makebodywell.bodywell.util.AlarmReceiver
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.util.CustomUtil.Companion.drugTimeList
import com.makebodywell.bodywell.util.CustomUtil.Companion.hideKeyboard
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.CustomUtil.Companion.setDrugTimeList
import com.makebodywell.bodywell.util.MyApp
import com.makebodywell.bodywell.view.home.MainActivity
import java.time.LocalDateTime

class DrugAddFragment : Fragment(), MainActivity.OnBackPressedListener {
   private var _binding: FragmentDrugAddBinding? = null
   val binding get() = _binding!!

   private var dataManager: DataManager? = null
   private var alarmReceiver: AlarmReceiver? = null
   private var adapter: DrugAdapter4? = null
   private val itemList = ArrayList<Drug>()
   private val idList = ArrayList<Int>()
   private var unit = "정"
   var count = 1

   override fun onAttach(context: Context) {
      super.onAttach(context)
      (context as MainActivity).setOnBackPressedListener(this)
   }

   @SuppressLint("InternalInsetResource", "DiscouragedApi", "ClickableViewAccessibility")
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

      alarmReceiver = AlarmReceiver()

      drugTimeList.clear()

      val id = if(arguments?.getString("id") == null) -1 else arguments?.getString("id").toString().toInt()

      if(id > 0) {
         val getDrug = dataManager!!.getDrug(id)
         binding.etType.setText(getDrug.type)
         binding.etName.setText(getDrug.name)
         binding.etAmount.setText(getDrug.amount.toString())

         when(getDrug.unit) {
            "정" -> unit1()
            "개" -> unit2()
            "봉" -> unit3()
            "mg" -> unit4()
            "ml" -> unit5()
            "set" -> unit6()
         }

         count = getDrug.count
         binding.tvCount.text = count.toString()

         val getDrugTime = dataManager!!.getDrugTime(id)
         for(i in 0 until getDrugTime.size) {
            setDrugTimeList(getDrugTime[i].hour, getDrugTime[i].minute)
         }

         binding.tvDesc.text = "${count}일동안 ${drugTimeList.size}회 복용"
      }

      binding.mainLayout.setOnTouchListener { view, motionEvent ->
         hideKeyboard(requireActivity())
         true
      }

      binding.linear.setOnTouchListener { view, motionEvent ->
         hideKeyboard(requireActivity())
         true
      }

      binding.clX.setOnClickListener {
         replaceFragment1(requireActivity(), DrugRecordFragment())
      }

      binding.etAmount.addTextChangedListener(object : TextWatcher {
         override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            binding.tvDesc.text = "${count}일동안 ${drugTimeList.size}회 복용"
         }

         override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
         override fun afterTextChanged(p0: Editable?) {}
      })

      binding.clUnit1.setOnClickListener {
         unit1()
      }

      binding.clUnit2.setOnClickListener {
         unit2()
      }

      binding.clUnit3.setOnClickListener {
         unit3()
      }

      binding.clUnit4.setOnClickListener {
         unit4()
      }

      binding.clUnit5.setOnClickListener {
         unit5()
      }

      binding.clUnit6.setOnClickListener {
         unit6()
      }

      binding.ivMinus.setOnClickListener {
         if(count > 1) {
            count--
            binding.tvCount.text = count.toString()
            binding.tvDesc.text = "${count}일동안 ${drugTimeList.size}회 복용"
         }
      }

      binding.ivPlus.setOnClickListener {
         count++
         binding.tvCount.text = count.toString()
         binding.tvDesc.text = "${count}일동안 ${drugTimeList.size}회 복용"
      }

      binding.cvSave.setOnClickListener {
         val type = if(binding.etType.text.toString() == "") "untitled" else binding.etType.text.toString().trim()
         val name = if(binding.etName.text.toString() == "") "untitled" else binding.etName.text.toString().trim()
         val amount = if(binding.etAmount.text.toString() == "") 1 else binding.etAmount.text.toString().trim().toInt()

         if(itemList.size == 0) {
            Toast.makeText(activity, "시간 미입력", Toast.LENGTH_SHORT).show()
         }else {
            val endDate = selectedDate.plusDays((count - 1).toLong()).toString()

            if(id > -1) {
               // 약 데이터 수정
               dataManager!!.updateDrug(Drug(type = type, name = name, amount = amount, unit = unit, count = count,
                  startDate = selectedDate.toString(), endDate = endDate, isSet = 1))

               // 시간 데이터 수정
               val getDrugTime = dataManager!!.getDrugTime(id)
               for(i in 0 until getDrugTime.size) {
                  var check = false
                  for(j in 0 until drugTimeList.size) {
                     if(getDrugTime[i].hour == drugTimeList[j].hour && getDrugTime[i].minute == drugTimeList[j].minute) {
                        check = true
                     }

                     if(j == (drugTimeList.size - 1) && !check) idList.add(getDrugTime[i].id)
                  }
               }

               if(idList.size > 0) {
                  for(i in 0 until idList.size) {
                     dataManager!!.deleteItem(TABLE_DRUG_CHECK, "drugTimeId", idList[i])
                  }
               }

               dataManager!!.deleteItem(TABLE_DRUG_TIME, "drugId", id)

               for(i in 0 until drugTimeList.size) {
                  dataManager!!.insertDrugTime(DrugTime(hour = drugTimeList[i].hour, minute = drugTimeList[i].minute, drugId = id))
               }

               alarmReceiver!!.cancelAlarm(requireActivity(), id)

               val message = binding.etName.text.toString() + " " + binding.etAmount.text.toString() + unit
               alarmReceiver!!.setAlarm(requireActivity(), id, selectedDate.toString(), endDate, drugTimeList, message)

               Toast.makeText(activity, "수정되었습니다.", Toast.LENGTH_SHORT).show()
            }else {
               // 약 데이터 저장
               dataManager!!.insertDrug(Drug(type = type, name = name, amount = amount, unit = unit, count = count,
                  startDate = selectedDate.toString(), endDate = endDate, isSet = 1, regDate = selectedDate.toString()))

               val getDrugId = dataManager!!.getDrugId(selectedDate.toString())

               // 시간 데이터 저장
               for(i in 0 until drugTimeList.size) {
                  dataManager!!.insertDrugTime(DrugTime(hour = drugTimeList[i].hour, minute = drugTimeList[i].minute, drugId = getDrugId.id))
               }

               val message = binding.etName.text.toString() + " " + binding.etAmount.text.toString() + unit
               alarmReceiver!!.setAlarm(requireActivity(), getDrugId.id, selectedDate.toString(), endDate, drugTimeList, message)

               Toast.makeText(activity, "저장되었습니다.", Toast.LENGTH_SHORT).show()
            }

            replaceFragment1(requireActivity(), DrugRecordFragment())
         }
      }

      settingTime()
      showTimeList()

      return binding.root
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
         binding.tvDesc.text = "${count}일동안 ${drugTimeList.size}회 복용"
      }

      adapter = DrugAdapter4(requireActivity(), itemList)
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
      unit = "봉"
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
      unit = "ml"
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
      unit = "set"
   }

   override fun onBackPressed() {
      val activity = activity as MainActivity?
      activity!!.setOnBackPressedListener(null)
      replaceFragment1(requireActivity(), DrugRecordFragment())
   }
}