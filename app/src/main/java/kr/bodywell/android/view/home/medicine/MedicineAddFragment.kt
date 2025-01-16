package kr.bodywell.android.view.home.medicine

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.f4b6a3.uuid.UuidCreator
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.adapter.MedicineAdapter4
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentMedicineAddBinding
import kr.bodywell.android.model.Constant.MEDICINES
import kr.bodywell.android.model.Constant.MEDICINE_INTAKES
import kr.bodywell.android.model.Constant.MEDICINE_TIMES
import kr.bodywell.android.model.Item
import kr.bodywell.android.model.Medicine
import kr.bodywell.android.model.MedicineTime
import kr.bodywell.android.service.AlarmReceiver
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil.dateTimeToIso1
import kr.bodywell.android.util.CustomUtil.dateTimeToIso2
import kr.bodywell.android.util.CustomUtil.drugTimeList
import kr.bodywell.android.util.CustomUtil.getUUID
import kr.bodywell.android.util.CustomUtil.hideKeyboard
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment3
import kr.bodywell.android.util.CustomUtil.setDrugTimeList
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.view.MainViewModel
import java.time.LocalDateTime
import java.util.Calendar

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
            replaceFragment3(parentFragmentManager, MedicineRecordFragment())
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

      val getMedicine = arguments?.getParcelable<Medicine>(MEDICINES)

      if(getMedicine != null) {
         split = getMedicine.category.split("/", limit=3)
         binding.etType.setText(split[0])
         binding.etName.setText(getMedicine.name)
         binding.etAmount.setText(getMedicine.amount.toString())
         count = split[1].toInt()

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
            getMedicineTime = powerSync.getAllMedicineTime(getMedicine.id) as ArrayList<MedicineTime>
            for(element in getMedicineTime) setDrugTimeList(element.time)
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
         replaceFragment3(parentFragmentManager, MedicineRecordFragment())
      }

      binding.tvUnit1.setOnClickListener {
         unit1()
      }

      binding.tvUnit2.setOnClickListener {
         unit2()
      }

      binding.tvUnit3.setOnClickListener {
         unit3()
      }

      binding.tvUnit4.setOnClickListener {
         unit4()
      }

      binding.tvUnit5.setOnClickListener {
         unit5()
      }

      binding.tvUnit6.setOnClickListener {
         unit6()
      }

      binding.ivMinus.setOnClickListener {
         if(count > 1) {
            count -= 1
            binding.tvCount.text = count.toString()
         }
      }

      binding.ivPlus.setOnClickListener {
         count += 1
         binding.tvCount.text = count.toString()
      }

      binding.cvSave.setOnClickListener {
         val category = if(binding.etType.text.toString() == "") "제목없음" else binding.etType.text.toString().trim()
         val name = if(binding.etName.text.toString() == "") "제목없음" else binding.etName.text.toString().trim()
         val amount = if(binding.etAmount.text.toString() == "") 1 else binding.etAmount.text.toString().trim().toInt()

         if(itemList.size == 0) {
            Toast.makeText(activity, "시간을 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            val ends = selectedDate.plusDays((count-1).toLong()).toString()
            lifecycleScope.launch {
               if(getMedicine != null) { // 약복용 정보 수정
                  powerSync.updateMedicine(Medicine(id = getMedicine.id, category = "$category/$count/${split[2]}", name = name,
                     amount = amount, unit = unit, starts = getMedicine.starts, ends = ends))

                  for(i in 0 until getMedicineTime.size) {
                     var check = false
                     for(j in 0 until drugTimeList.size) {
                        if(getMedicineTime[i].time == drugTimeList[j].time) {
                           check = true
                           break
                        }
                     }
                     if(!check) delList.add(getMedicineTime[i].id)
                  }

                  for(i in 0 until drugTimeList.size) {
                     var check = false
                     for(j in 0 until getMedicineTime.size) {
                        if(drugTimeList[i].time == getMedicineTime[j].time) {
                           check = true
                           break
                        }
                     }
                     if(!check) addList.add(drugTimeList[i].time)
                  }

                  // 필요없는 데이터 삭제
                  for(i in 0 until delList.size) {
                     powerSync.deleteItem(MEDICINE_TIMES, "id", delList[i])
                     powerSync.deleteItem(MEDICINE_INTAKES, "medicine_time_id", delList[i])
                  }

                  // 새로 생성된 데이터 저장
                  for(i in 0 until addList.size) {
                     powerSync.insertMedicineTime(MedicineTime(id = getUUID(), time = addList[i], createdAt = dateTimeToIso1(Calendar.getInstance()),
                        updatedAt = dateTimeToIso1(Calendar.getInstance()), medicineId = getMedicine.id))
                  }

                  // 알람 수정
                  val getId = dataManager.getMedicine(getMedicine.id)
                  if(getId != 0) {
                     alarmReceiver!!.setAlarm(requireActivity(), getId, selectedDate.toString(), ends, drugTimeList, "$name $amount$unit")
                  }

                  // 약복용 시간 수정
                  dataManager.updateMedicineTime(dateTimeToIso2())

                  Toast.makeText(activity, "수정되었습니다.", Toast.LENGTH_SHORT).show()
               }else { // 약복용 정보 저장
                  val uuid = UuidCreator.getTimeOrderedEpoch()

                  powerSync.insertMedicine(Medicine(id = uuid.toString(), category = "$category/$count/1", name = name, amount = amount, unit = unit,
                     starts = selectedDate.toString(), ends = ends, createdAt = dateTimeToIso1(Calendar.getInstance()), updatedAt = dateTimeToIso1(Calendar.getInstance())))

                  for(i in 0 until drugTimeList.size) {
                     powerSync.insertMedicineTime(MedicineTime(id = getUUID(), time = drugTimeList[i].time, createdAt = dateTimeToIso1(Calendar.getInstance()),
                        updatedAt = dateTimeToIso1(Calendar.getInstance()), medicineId = uuid.toString()))
                  }

                  dataManager.insertMedicine(uuid.toString())
                  val getId = dataManager.getMedicine(uuid.toString())
                  if(getId != 0) {
                     alarmReceiver!!.setAlarm(requireActivity(), getId, selectedDate.toString(), ends, drugTimeList, "$name $amount$unit")
                  }

                  dataManager.updateMedicineTime(dateTimeToIso2())

                  Toast.makeText(activity, "저장되었습니다.", Toast.LENGTH_SHORT).show()
               }
            }

            replaceFragment3(parentFragmentManager, MedicineRecordFragment())
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
      }

      adapter = MedicineAdapter4(itemList, viewModel)
      binding.recyclerView.adapter = adapter
   }

   private fun unit1() {
      binding.tvUnit1.background = resources.getDrawable(R.drawable.rec_medicine)
      binding.tvUnit1.setTextColor(Color.WHITE)
      binding.tvUnit2.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit2.setTextColor(Color.BLACK)
      binding.tvUnit3.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit3.setTextColor(Color.BLACK)
      binding.tvUnit4.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit4.setTextColor(Color.BLACK)
      binding.tvUnit5.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit5.setTextColor(Color.BLACK)
      binding.tvUnit6.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit6.setTextColor(Color.BLACK)
      unit = "정"
   }

   private fun unit2() {
      binding.tvUnit1.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit1.setTextColor(Color.BLACK)
      binding.tvUnit2.background = resources.getDrawable(R.drawable.rec_medicine)
      binding.tvUnit2.setTextColor(Color.WHITE)
      binding.tvUnit3.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit3.setTextColor(Color.BLACK)
      binding.tvUnit4.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit4.setTextColor(Color.BLACK)
      binding.tvUnit5.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit5.setTextColor(Color.BLACK)
      binding.tvUnit6.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit6.setTextColor(Color.BLACK)
      unit = "개"
   }

   private fun unit3() {
      binding.tvUnit1.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit1.setTextColor(Color.BLACK)
      binding.tvUnit2.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit2.setTextColor(Color.BLACK)
      binding.tvUnit3.background = resources.getDrawable(R.drawable.rec_medicine)
      binding.tvUnit3.setTextColor(Color.WHITE)
      binding.tvUnit4.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit4.setTextColor(Color.BLACK)
      binding.tvUnit5.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit5.setTextColor(Color.BLACK)
      binding.tvUnit6.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit6.setTextColor(Color.BLACK)
      unit = "봉"
   }

   private fun unit4() {
      binding.tvUnit1.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit1.setTextColor(Color.BLACK)
      binding.tvUnit2.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit2.setTextColor(Color.BLACK)
      binding.tvUnit3.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit3.setTextColor(Color.BLACK)
      binding.tvUnit4.background = resources.getDrawable(R.drawable.rec_medicine)
      binding.tvUnit4.setTextColor(Color.WHITE)
      binding.tvUnit5.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit5.setTextColor(Color.BLACK)
      binding.tvUnit6.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit6.setTextColor(Color.BLACK)
      unit = "mg"
   }

   private fun unit5() {
      binding.tvUnit1.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit1.setTextColor(Color.BLACK)
      binding.tvUnit2.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit2.setTextColor(Color.BLACK)
      binding.tvUnit3.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit3.setTextColor(Color.BLACK)
      binding.tvUnit4.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit4.setTextColor(Color.BLACK)
      binding.tvUnit5.background = resources.getDrawable(R.drawable.rec_medicine)
      binding.tvUnit5.setTextColor(Color.WHITE)
      binding.tvUnit6.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit6.setTextColor(Color.BLACK)
      unit = "ml"
   }

   private fun unit6() {
      binding.tvUnit1.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit1.setTextColor(Color.BLACK)
      binding.tvUnit2.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit2.setTextColor(Color.BLACK)
      binding.tvUnit3.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit3.setTextColor(Color.BLACK)
      binding.tvUnit4.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit4.setTextColor(Color.BLACK)
      binding.tvUnit5.background = resources.getDrawable(R.drawable.rec_25_border_gray)
      binding.tvUnit5.setTextColor(Color.BLACK)
      binding.tvUnit6.background = resources.getDrawable(R.drawable.rec_medicine)
      binding.tvUnit6.setTextColor(Color.WHITE)
      unit = "set"
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}