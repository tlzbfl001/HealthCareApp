package kr.bodywell.android.view.home.medicine

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.adapter.MedicineAdapter1
import kr.bodywell.android.databinding.FragmentMedicineBinding
import kr.bodywell.android.model.Constant.GOALS
import kr.bodywell.android.model.Constant.MEDICINE_INTAKES
import kr.bodywell.android.model.MedicineList
import kr.bodywell.android.model.Goal
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil.dateTimeToIso1
import kr.bodywell.android.util.CustomUtil.getUUID
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.util.PermissionUtil.checkAlarmPermission1
import kr.bodywell.android.util.PermissionUtil.checkAlarmPermission2
import kr.bodywell.android.view.MainViewModel
import kr.bodywell.android.view.setting.AlarmFragment
import java.time.LocalDate
import java.util.Calendar

class MedicineFragment : Fragment() {
   private var _binding: FragmentMedicineBinding? = null
   val binding get() = _binding!!

   private val viewModel: MainViewModel by activityViewModels()
   private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
   private var adapter: MedicineAdapter1? = null
   private val deleteList = ArrayList<String>()
   private var getGoal = Goal()

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentMedicineBinding.inflate(layoutInflater)

      pLauncher = registerForActivityResult(
         ActivityResultContracts.RequestMultiplePermissions()
      ){}

      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_input)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
      val et = dialog.findViewById<TextView>(R.id.et)
      val tvUnit = dialog.findViewById<TextView>(R.id.tvUnit)
      val btnSave = dialog.findViewById<CardView>(R.id.btnSave)
      tvTitle.text = "약복용 횟수 입력"
      tvUnit.text = "회"
      btnSave.setCardBackgroundColor(Color.parseColor("#E2FF955D"))

      btnSave.setOnClickListener {
         if(et.text.toString().trim() == "") {
            Toast.makeText(requireActivity(), "목표를 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            lifecycleScope.launch {
               if(getGoal.id == "") {
                  powerSync.insertGoal(Goal(id = getUUID(), medicineIntake = et.text.toString().toInt(), date = selectedDate.toString(),
                     createdAt = dateTimeToIso1(Calendar.getInstance()), updatedAt = dateTimeToIso1(Calendar.getInstance())))
                  getGoal = powerSync.getGoal(selectedDate.toString())
               }else {
                  powerSync.updateData(GOALS, MEDICINE_INTAKES, et.text.toString(), getGoal.id)
               }

               dialog.dismiss()
               dailyView()
            }
         }
      }

      binding.clGoal.setOnClickListener {
         dialog.show()
      }

      binding.clRecord.setOnClickListener {
         if(!checkAlarmPermission1(requireActivity()) || !checkAlarmPermission2(requireActivity())) {
            Toast.makeText(requireActivity(), "권한이 없습니다.", Toast.LENGTH_SHORT).show()
         }else {
            replaceFragment1(parentFragmentManager, MedicineRecordFragment())
         }
      }

      viewModel.dateVM.observe(viewLifecycleOwner, Observer<LocalDate> {
         dailyView()
      })

      viewModel.medicineCheckVM.observe(viewLifecycleOwner, Observer<Int> { item ->
         if(item > 0) {
            binding.pbDrug.setProgressStartColor(resources.getColor(R.color.medicine))
            binding.pbDrug.setProgressEndColor(resources.getColor(R.color.medicine))
            binding.pbDrug.progress = item
         }else {
            binding.pbDrug.setProgressStartColor(Color.TRANSPARENT)
            binding.pbDrug.setProgressEndColor(Color.TRANSPARENT)
         }

         binding.tvDrugCount.text = "${item}회"

         val result = getGoal.medicineIntake - item
         if(result > -1) binding.tvRemain.text = "${result}회"
      })

      dailyView()

      return binding.root
   }

   private fun dailyView() {
      val itemList = ArrayList<MedicineList>()
      binding.pbDrug.setProgressEndColor(Color.TRANSPARENT)
      binding.pbDrug.setProgressStartColor(Color.TRANSPARENT)
      binding.tvGoal.text = "0회"
      binding.tvRemain.text = "0회"
      binding.tvDrugCount.text = "0회"

      lifecycleScope.launch {
         getGoal = powerSync.getGoal(selectedDate.toString())
         val getIntakes = powerSync.getIntakes(selectedDate.toString())
         val getRecently = powerSync.getRecentlyIntakes(selectedDate.toString())

         for(i in getIntakes.indices) {
            var check = false
            for(j in getRecently.indices){
               if(getIntakes[i] == getRecently[j]){
                  check = true
                  break
               }
            }
            if(!check) deleteList.add(getIntakes[i])
         }

         // 중복된 약복용 기록 삭제
         for(i in deleteList.indices) powerSync.deleteItem(MEDICINE_INTAKES, "id", deleteList[i])

         binding.tvGoal.text = "${getGoal.medicineIntake}회"
         binding.pbDrug.max = getGoal.medicineIntake
         binding.pbDrug.progress = getRecently.size

         if(!checkAlarmPermission1(requireActivity()) || !checkAlarmPermission2(requireActivity())) {
            binding.clPerm.visibility = View.VISIBLE
            binding.cl1.visibility = View.GONE
            binding.cv.visibility = View.GONE
            binding.btnPerm.setOnClickListener {
               replaceFragment1(parentFragmentManager, AlarmFragment())
            }
         }else {
            binding.clPerm.visibility = View.GONE
            binding.cl1.visibility = View.VISIBLE

            // 약복용 리스트 생성
            val getMedicine = powerSync.getMedicines(selectedDate.toString())
            if(getMedicine.isNotEmpty()) {
               binding.cv.visibility = View.VISIBLE

               for(i in getMedicine.indices) {
                  val getMedicineTime = powerSync.getAllMedicineTime(getMedicine[i].id)

                  for(j in getMedicineTime.indices) {
                     val getIntake = powerSync.getIntake(selectedDate.toString(), getMedicineTime[j].id)
                     itemList.add(MedicineList(name = getMedicine[i].name, amount = getMedicine[i].amount, unit = getMedicine[i].unit, time = getMedicineTime[j].time,
                        date = getMedicine[i].starts, medicineId = getMedicine[i].id, medicineTimeId = getMedicineTime[j].id, initCheck = getRecently.size, isChecked = getIntake.id))
                  }
               }

               adapter = MedicineAdapter1(itemList, viewModel)
               binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
               binding.recyclerView.adapter = adapter
               binding.recyclerView.requestLayout()
            }else {
               binding.cv.visibility = View.GONE
            }
         }
      }
   }
}