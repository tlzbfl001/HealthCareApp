package com.makebodywell.bodywell.view.home.drug

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.adapter.DrugAdapter1
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentDrugBinding
import com.makebodywell.bodywell.model.DailyData
import com.makebodywell.bodywell.model.DrugList
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.view.home.MainFragment
import com.makebodywell.bodywell.view.home.body.BodyFragment
import com.makebodywell.bodywell.view.home.exercise.ExerciseFragment
import com.makebodywell.bodywell.view.home.food.FoodFragment
import com.makebodywell.bodywell.view.home.sleep.SleepFragment
import com.makebodywell.bodywell.view.home.water.WaterFragment
import java.time.LocalDate

class DrugFragment : Fragment() {
   private var _binding: FragmentDrugBinding? = null
   val binding get() = _binding!!

   private var calendarDate = LocalDate.now()

   private var dataManager: DataManager? = null
   private var adapter: DrugAdapter1? = null
   private val itemList = ArrayList<DrugList>()
   private var getDailyData = DailyData()

   private var check = 0

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentDrugBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      initView()
      recordView()

      return binding.root
   }

   private fun initView() {
      binding.tvDate.text = dateFormat(calendarDate)

      // 목표 설정
      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_input)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
      val et = dialog.findViewById<TextView>(R.id.et)
      val tvUnit = dialog.findViewById<TextView>(R.id.tvUnit)
      val btnSave = dialog.findViewById<CardView>(R.id.btnSave)
      tvTitle.text = "약복용 / 하루 복용 횟수"
      tvUnit.text = "회"
      btnSave.setCardBackgroundColor(Color.parseColor("#8F6FF5"))

      btnSave.setOnClickListener {
         if(et.text.toString().trim() != "") {
            if(getDailyData.regDate == "") {
               dataManager?.insertDailyData(DailyData(drugGoal = et.text.toString().toInt(), regDate = calendarDate.toString()))
            }else {
               dataManager!!.updateGoal("drugGoal",  et.text.toString().toInt(), calendarDate.toString())
            }
            recordView()
         }
         dialog.dismiss()
      }

      binding.clGoal.setOnClickListener {
         dialog.show()
      }

      binding.clBack.setOnClickListener {
         replaceFragment1(requireActivity(), MainFragment())
      }

      binding.clPrev.setOnClickListener {
         calendarDate = calendarDate!!.minusDays(1)
         binding.tvDate.text = dateFormat(calendarDate)
         recordView()
      }

      binding.clNext.setOnClickListener {
         calendarDate = calendarDate!!.plusDays(1)
         binding.tvDate.text = dateFormat(calendarDate)
         recordView()
      }

      binding.clRecord.setOnClickListener {
         if(requestPermission()) {
            replaceFragment1(requireActivity(), DrugRecordFragment())
         }
      }

      binding.cvFood.setOnClickListener {
         replaceFragment1(requireActivity(), FoodFragment())
      }

      binding.cvWater.setOnClickListener {
         replaceFragment1(requireActivity(), WaterFragment())
      }

      binding.cvExercise.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseFragment())
      }

      binding.cvBody.setOnClickListener {
         replaceFragment1(requireActivity(), BodyFragment())
      }

      binding.cvSleep.setOnClickListener {
         replaceFragment1(requireActivity(), SleepFragment())
      }

      binding.cvDrug.setOnClickListener {
         replaceFragment1(requireActivity(), DrugFragment())
      }
   }

   private fun recordView() {
      binding.tvGoal.text = "0회"
      binding.tvRemain.text = "0회"
      binding.tvDrugCount.text = "0회"
      binding.pbDrug.setProgressEndColor(Color.TRANSPARENT)
      binding.pbDrug.setProgressStartColor(Color.TRANSPARENT)
      itemList.clear()
      check = 0

      getDailyData = dataManager!!.getDailyData(calendarDate.toString())
      binding.pbDrug.max = getDailyData.drugGoal
      binding.tvGoal.text = "${getDailyData.drugGoal}회"

      // 약복용 체크값 초기화
      val getDrugCheckCount = dataManager!!.getDrugCheckCount(calendarDate.toString())
      check += getDrugCheckCount

      // 약복용 리스트 생성
      val getDrugDaily = dataManager!!.getDrugDaily(calendarDate.toString())
      for(i in 0 until getDrugDaily.size) {
         if(getDrugDaily[i].period == "매일") {
            val getDrugTime = dataManager!!.getDrugTime(getDrugDaily[i].id)
            for(j in 0 until getDrugTime.size) {
               val getDrugCheck = dataManager!!.getDrugCheck(getDrugTime[j].id, calendarDate.toString())
               itemList.add(DrugList(id = getDrugTime[j].id, date = calendarDate.toString(), name = getDrugDaily[i].name, amount = getDrugDaily[i].amount,
                  unit = getDrugDaily[i].unit, time = String.format("%02d", getDrugTime[j].hour)+":"+String.format("%02d", getDrugTime[j].minute),
                  initCheck = check, checked = getDrugCheck.checked)
               )
            }
         }
         if(getDrugDaily[i].period == "특정일 지정") {
            val getDrugDate = dataManager!!.getDrugDate(getDrugDaily[i].id)
            for(j in 0 until getDrugDate.size) {
               if(getDrugDate[j].date == calendarDate.toString()) {
                  val getDrugTime = dataManager!!.getDrugTime(getDrugDaily[i].id)
                  for(k in 0 until getDrugTime.size) {
                     val getDrugCheck = dataManager!!.getDrugCheck(getDrugTime[k].id, calendarDate.toString())
                     itemList.add(DrugList(id = getDrugTime[k].id, date = calendarDate.toString(), name = getDrugDaily[i].name, amount = getDrugDaily[i].amount,
                        unit = getDrugDaily[i].unit, time = String.format("%02d", getDrugTime[k].hour)+":"+String.format("%02d", getDrugTime[k].minute),
                        initCheck = check, checked = getDrugCheck.checked)
                     )
                  }
               }
            }
         }
      }

      val sortedList = itemList.sortedBy{ it.time }

      adapter = DrugAdapter1(requireActivity(), sortedList, getDailyData.drugGoal)
      binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
      binding.recyclerView.requestLayout()
      binding.recyclerView.adapter = adapter
   }

   private fun requestPermission(): Boolean {
      var check = true
      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
         if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM, Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE)
            check = false
         }
      }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
         if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM), REQUEST_CODE)
            check = false
         }
      }
      return check
   }

   companion object {
      private const val REQUEST_CODE = 1
   }
}