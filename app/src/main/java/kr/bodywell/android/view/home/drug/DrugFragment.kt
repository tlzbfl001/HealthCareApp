package kr.bodywell.android.view.home.drug

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
import androidx.recyclerview.widget.LinearLayoutManager
import kr.bodywell.android.R
import kr.bodywell.android.adapter.DrugAdapter1
import kr.bodywell.android.database.DBHelper.Companion.IS_UPDATED
import kr.bodywell.android.database.DBHelper.Companion.DRUG
import kr.bodywell.android.database.DBHelper.Companion.GOAL
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentDrugBinding
import kr.bodywell.android.model.Goal
import kr.bodywell.android.model.DrugList
import kr.bodywell.android.util.CalendarUtil.selectedDate
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
   private lateinit var dataManager: DataManager
   private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
   private var adapter: DrugAdapter1? = null
   private var dailyGoal = Goal()

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentDrugBinding.inflate(layoutInflater)

      pLauncher = registerForActivityResult(
         ActivityResultContracts.RequestMultiplePermissions()
      ){

      }

      dataManager = DataManager(activity)
      dataManager.open()

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
            if(dailyGoal.createdAt == "") {
               dataManager.insertGoal(Goal(drug = et.text.toString().toInt(), createdAt = selectedDate.toString()))
               dailyGoal = dataManager.getGoal(selectedDate.toString())
            }else {
               dataManager.updateInt(GOAL, DRUG, et.text.toString().toInt(), selectedDate.toString())
               dataManager.updateInt(GOAL, IS_UPDATED, 1, "id", dailyGoal.id)
            }

            dailyView()
            dialog.dismiss()
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

         val result = dailyGoal.drug - item
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

      dailyGoal = dataManager.getGoal(selectedDate.toString())
      val check = dataManager.getDrugCheckCount(selectedDate.toString())

      binding.tvGoal.text = "${dailyGoal.drug}회"
      binding.pbDrug.max = dailyGoal.drug
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
         val getDrugDaily = dataManager.getDrug(selectedDate.toString())

         if(getDrugDaily.size > 0) {
            binding.cv.visibility = View.VISIBLE

            for(i in 0 until getDrugDaily.size) {
               val getDrugTime = dataManager.getDrugTime(getDrugDaily[i].id)
               for(j in 0 until getDrugTime.size) {
                  val getDrugCheck = dataManager.getDrugCheck(getDrugTime[j].id, selectedDate.toString())
                  itemList.add(DrugList(uid = getDrugCheck.uid, drugId = getDrugDaily[i].id, drugTimeId = getDrugTime[j].id, date = getDrugDaily[i].startDate,
                     name = getDrugDaily[i].name, amount = getDrugDaily[i].amount, unit = getDrugDaily[i].unit, time = getDrugTime[j].time, initCheck = check, checked = getDrugCheck.id)
                  )
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