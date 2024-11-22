package kr.bodywell.android.view.home.medicine

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
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.f4b6a3.uuid.UuidCreator
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.adapter.MedicineAdapter4
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentMedicineAddBinding
import kr.bodywell.android.model.Item
import kr.bodywell.android.model.Medicine
import kr.bodywell.android.model.MedicineTime
import kr.bodywell.android.service.AlarmReceiver
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil.drugTimeList
import kr.bodywell.android.util.CustomUtil.hideKeyboard
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment3
import kr.bodywell.android.util.CustomUtil.setDrugTimeList
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.view.MainViewModel
import java.time.LocalDateTime

class MedicineAddFragment : Fragment() {
   private var _binding: FragmentMedicineAddBinding? = null
   val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private val viewModel: MainViewModel by activityViewModels()
   private lateinit var dataManager: DataManager
   private var alarmReceiver: AlarmReceiver? = null
   private var adapter: MedicineAdapter4? = null
   private var getMedicineTime = ArrayList<MedicineTime>()
   private val itemList = ArrayList<Item>()
   private val addList = ArrayList<String>()
   private val delList = ArrayList<String>()
   private var split = listOf<String>()
   private var unit = "정"
   private var count = 1
   private var check = false

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment3(requireActivity(), MedicineRecordFragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ):View {
      _binding = FragmentMedicineAddBinding.inflate(layoutInflater)

      setStatusBar(requireActivity(), binding.mainLayout)

      dataManager = DataManager(requireActivity())
      dataManager.open()

      alarmReceiver = AlarmReceiver()

      drugTimeList.clear()

      val getMedicine = arguments?.getParcelable<Medicine>("medicine")

      // 약복용 수정일경우 데이터 가져오기
      if(getMedicine != null) {
         split = getMedicine.name.split("/", limit=4)
         binding.etType.setText(split[0])
         binding.etName.setText(split[1])
         binding.etAmount.setText(getMedicine.amount.toString())
         count = split[2].toInt()

         when(getMedicine.unit) {
            "정" -> unit1()
            "개" -> unit2()
            "봉" -> unit3()
            "mg" -> unit4()
            "ml" -> unit5()
            "set" -> unit6()
         }

         binding.tvCount.text = count.toString()

         lifecycleScope.launch {
            getMedicineTime = powerSync.getAllMedicineTime("medicine_id", getMedicine.id) as ArrayList<MedicineTime>
            for(element in getMedicineTime) setDrugTimeList(element.time)
            binding.tvDesc.text = "${count}일동안 ${drugTimeList.size}회 복용"
         }
      }

      binding.tvCount.text = count.toString()

      binding.mainLayout.setOnTouchListener { view, motionEvent ->
         hideKeyboard(requireActivity())
         true
      }

      binding.linear.setOnTouchListener { view, motionEvent ->
         hideKeyboard(requireActivity())
         true
      }

      binding.clX.setOnClickListener {
         replaceFragment3(requireActivity(), MedicineRecordFragment())
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
            count -= 1
            binding.tvCount.text = count.toString()
            binding.tvDesc.text = "${count}일동안 ${drugTimeList.size}회 복용"
         }
      }

      binding.ivPlus.setOnClickListener {
         count += 1
         binding.tvCount.text = count.toString()
         binding.tvDesc.text = "${count}일동안 ${drugTimeList.size}회 복용"
      }

      binding.cvSave.setOnClickListener {
         val category = if(binding.etType.text.toString() == "") "untitled" else binding.etType.text.toString().trim()
         val name = if(binding.etName.text.toString() == "") "untitled" else binding.etName.text.toString().trim()
         val amount = if(binding.etAmount.text.toString() == "") 1 else binding.etAmount.text.toString().trim().toInt()

         if(itemList.size == 0) {
            Toast.makeText(activity, "시간 미입력", Toast.LENGTH_SHORT).show()
         }else {
            val ends = selectedDate.plusDays((count-1).toLong()).toString()
//            val instance = Calendar.getInstance()
//            val createdAt = dateTimeToIso2(instance)

            lifecycleScope.launch {
               if(getMedicine != null) { // 약복용 정보 수정
                  powerSync.updateMedicine(Medicine(id = getMedicine.id, category = getMedicine.category, name = "$category/$name/$count/${split[3]}",
                     amount = amount, unit = unit, starts = getMedicine.starts, ends = ends))

                  for(i in 0 until getMedicineTime.size) {
                     var check = false
                     for(j in 0 until drugTimeList.size) {
                        if(getMedicineTime[i].time == drugTimeList[j].time) check = true
                        if(j == drugTimeList.size - 1 && !check) delList.add(getMedicineTime[i].id)
                     }
                  }

                  for(i in 0 until drugTimeList.size) {
                     var check = false
                     for(j in 0 until getMedicineTime.size) {
                        if(drugTimeList[i].time == getMedicineTime[j].time) check = true
                        if(j == getMedicineTime.size - 1 && !check) addList.add(drugTimeList[i].time)
                     }
                  }

                  // 필요없는 데이터 삭제
                  for(i in 0 until delList.size) {
                     powerSync.deleteItem("medicine_times", "id", delList[i])
                     powerSync.deleteItem("medicine_intakes", "medicine_time_id", delList[i])
                  }

                  // 새로 생성된 데이터 저장
                  for(i in 0 until addList.size) {
                     val uuid = UuidCreator.getTimeOrderedEpoch()
                     powerSync.insertMedicineTime(MedicineTime(id = uuid.toString(), time = addList[i], createdAt = LocalDateTime.now().toString(),
                        updatedAt = LocalDateTime.now().toString(), medicineId = getMedicine.id))
                     dataManager.insertMedicineTime(MedicineTime(id = uuid.toString(), medicineId = getMedicine.id))
                  }

                  alarmReceiver!!.setAlarm(requireActivity(), getMedicine.category.toInt(), selectedDate.toString(), ends, drugTimeList, "$name $amount$unit")
                  Toast.makeText(activity, "수정되었습니다.", Toast.LENGTH_SHORT).show()
               }else { // 약복용 정보 저장
                  val uuid1 = UuidCreator.getTimeOrderedEpoch()
                  val alarmId = dataManager.getAlarmId() + 1

                  powerSync.insertMedicine(Medicine(id = uuid1.toString(), category = alarmId.toString(), name = "$category/$name/$count/1", amount = amount, unit = unit,
                     starts = selectedDate.toString(), ends = ends, createdAt = LocalDateTime.now().toString(), updatedAt = LocalDateTime.now().toString()))
                  dataManager.insertMedicine(uuid1.toString(), alarmId)

                  for(i in 0 until drugTimeList.size) {
                     val uuid2 = UuidCreator.getTimeOrderedEpoch()
                     powerSync.insertMedicineTime(MedicineTime(id = uuid2.toString(), time = drugTimeList[i].time, createdAt = LocalDateTime.now().toString(),
                        updatedAt = LocalDateTime.now().toString(), medicineId = uuid1.toString()))
                     dataManager.insertMedicineTime(MedicineTime(id = uuid2.toString(), medicineId = uuid1.toString()))
                  }

                  alarmReceiver!!.setAlarm(requireActivity(), alarmId, selectedDate.toString(), ends, drugTimeList, "$name $amount$unit 복용")
                  Toast.makeText(activity, "저장되었습니다.", Toast.LENGTH_SHORT).show()
               }
            }

            replaceFragment3(requireActivity(), MedicineRecordFragment())
         }
      }

      viewModel.intVM.observe(viewLifecycleOwner, Observer<Int> { item ->
         binding.tvDesc.text = "${count}일동안 ${item}회 복용"
      })

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

               val time = String.format("%02d", h)+":"+String.format("%02d", m)

               for(i in 0 until drugTimeList.size) {
                  if(drugTimeList[i].time == time) check = true
               }

               if(check) {
                  check = false
                  Toast.makeText(activity, "시간이 중복됩니다.", Toast.LENGTH_SHORT).show()
               }else {
                  setDrugTimeList(time)
                  showTimeList()
               }
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
         itemList.add(Item(string1 = drugTimeList[i].time, int1 = i + 1))
      }

      if(itemList.isEmpty()) {
         binding.recyclerView.visibility = View.GONE
      }else {
         binding.recyclerView.visibility = View.VISIBLE
         binding.tvDesc.text = "${count}일동안 ${drugTimeList.size}회 복용"
      }

      adapter = MedicineAdapter4(itemList, viewModel)
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

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}