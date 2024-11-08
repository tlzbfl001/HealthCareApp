package kr.bodywell.android.view.home.drug

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
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
import kotlinx.coroutines.runBlocking
import kr.bodywell.android.R
import kr.bodywell.android.adapter.DrugAdapter1
import kr.bodywell.android.databinding.FragmentDrugBinding
import kr.bodywell.android.model.DrugList
import kr.bodywell.android.model.Goal
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.util.PermissionUtil.checkAlarmPermission1
import kr.bodywell.android.util.PermissionUtil.checkAlarmPermission2
import kr.bodywell.android.view.MainViewModel
import kr.bodywell.android.view.setting.AlarmFragment
import java.time.LocalDate

class DrugFragment : Fragment() {
   private var _binding: FragmentDrugBinding? = null
   val binding get() = _binding!!

   private val viewModel: MainViewModel by activityViewModels()
//   private lateinit var dataManager: DataManager
   private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
   private var adapter: DrugAdapter1? = null
   private var getGoal = Goal()

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentDrugBinding.inflate(layoutInflater)

      pLauncher = registerForActivityResult(
         ActivityResultContracts.RequestMultiplePermissions()
      ){

      }

//      dataManager = DataManager(activity)
//      dataManager.open()

      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_input)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
      val et = dialog.findViewById<TextView>(R.id.et)
      val tvUnit = dialog.findViewById<TextView>(R.id.tvUnit)
      val btnSave = dialog.findViewById<CardView>(R.id.btnSave)
      tvTitle.text = "약복용 / 하루 복용 횟수"
      tvUnit.text = "회"
      btnSave.setCardBackgroundColor(Color.parseColor("#CC9E63FC"))

      btnSave.setOnClickListener {
         if(et.text.toString().trim() == "") {
            Toast.makeText(requireActivity(), "목표를 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            runBlocking {
               if(getGoal.date == "") {
                  powerSync.insertGoal(Goal(medicineIntake = et.text.toString().toInt(), date = selectedDate.toString()))
                  getGoal = powerSync.getGoal(selectedDate.toString())
               }else {
                  powerSync.updateStr("goals", "medicine_intake", et.text.toString(), getGoal.id)
               }

               dailyView()
               dialog.dismiss()
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
            replaceFragment1(requireActivity(), DrugRecordFragment())
         }
      }

      viewModel.dateVM.observe(viewLifecycleOwner, Observer<LocalDate> {
         dailyView()
      })

      viewModel.intVM.observe(viewLifecycleOwner, Observer<Int> { item ->
         if(item > 0) {
            binding.pbDrug.setProgressStartColor(resources.getColor(R.color.drug))
            binding.pbDrug.setProgressEndColor(resources.getColor(R.color.drug))
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
      val itemList = ArrayList<DrugList>()
      binding.tvGoal.text = "0회"
      binding.tvRemain.text = "0회"
      binding.tvDrugCount.text = "0회"
      binding.pbDrug.setProgressEndColor(Color.TRANSPARENT)
      binding.pbDrug.setProgressStartColor(Color.TRANSPARENT)

      lifecycleScope.launch {
         getGoal = powerSync.getGoal(selectedDate.toString())
         val check = powerSync.getMedicineIntakeCount(selectedDate.toString())

         binding.tvGoal.text = "${getGoal.medicineIntake}회"
         binding.pbDrug.max = getGoal.medicineIntake
         binding.pbDrug.progress = check

         if(!checkAlarmPermission1(requireActivity()) || !checkAlarmPermission2(requireActivity())) {
            binding.clPerm.visibility = View.VISIBLE
            binding.cl1.visibility = View.GONE
            binding.cv.visibility = View.GONE
            binding.btnPerm.setOnClickListener {
               replaceFragment1(requireActivity(), AlarmFragment())
            }
         }else {
            binding.clPerm.visibility = View.GONE
            binding.cl1.visibility = View.VISIBLE

            // 약복용 리스트 생성
//            val getDrugDaily = dataManager.getDrug(selectedDate.toString())
            val getMedicine = powerSync.getMedicine(selectedDate.toString())
            Log.d(CustomUtil.TAG, "getMedicine: $getMedicine")

            if(getMedicine.isNotEmpty()) {
               binding.cv.visibility = View.VISIBLE

               for(i in getMedicine.indices) {
                  val getMedicineTime = powerSync.getMedicineTime("medicine_id", getMedicine[i].id)
                  Log.d(CustomUtil.TAG, "getMedicineTime: $getMedicineTime")

                  for(j in getMedicineTime.indices) {
                     val getMedicineIntake = powerSync.getMedicineIntake(selectedDate.toString(), getMedicineTime[j].id)
                     Log.d(CustomUtil.TAG, "getMedicineIntake: $getMedicineIntake")
                     itemList.add(DrugList(drugId = getMedicine[i].id, drugTimeId = getMedicineTime[j].id, date = getMedicine[i].starts,
                        name = getMedicine[i].name, amount = getMedicine[i].amount, unit = getMedicine[i].unit, time = getMedicineTime[j].time,
                        initCheck = check, checked = getMedicineIntake.id))
                  }
               }

               adapter = DrugAdapter1(requireActivity(), itemList, viewModel)
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