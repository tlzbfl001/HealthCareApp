package com.makebodywell.bodywell.view.home.drug

import android.Manifest
import android.app.Dialog
import android.content.Context
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
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.adapter.DrugAdapter1
import com.makebodywell.bodywell.database.DBHelper
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DAILY_GOAL
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DRUG
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentDrugBinding
import com.makebodywell.bodywell.model.DailyGoal
import com.makebodywell.bodywell.model.Drug
import com.makebodywell.bodywell.model.DrugList
import com.makebodywell.bodywell.model.Item
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.view.home.MainActivity
import com.makebodywell.bodywell.view.home.MainFragment
import com.makebodywell.bodywell.view.home.body.BodyFragment
import com.makebodywell.bodywell.view.home.exercise.ExerciseFragment
import com.makebodywell.bodywell.view.home.food.FoodFragment
import com.makebodywell.bodywell.view.home.sleep.SleepFragment
import com.makebodywell.bodywell.view.home.water.WaterFragment

class DrugFragment : Fragment(), MainActivity.OnBackPressedListener {
   private var _binding: FragmentDrugBinding? = null
   val binding get() = _binding!!

   private var dataManager: DataManager? = null
   private var adapter: DrugAdapter1? = null
   private val itemList = ArrayList<DrugList>()
   private var getDailyGoal = DailyGoal()

   override fun onAttach(context: Context) {
      super.onAttach(context)
      (context as MainActivity).setOnBackPressedListener(this)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentDrugBinding.inflate(layoutInflater)

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

      binding.tvDate.text = dateFormat(selectedDate)

      binding.clBack.setOnClickListener {
         replaceFragment1(requireActivity(), MainFragment())
      }

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
         if(et.text.toString().trim() == "") {
            Toast.makeText(requireActivity(), "입력된 문자가 없습니다.", Toast.LENGTH_SHORT).show()
         }else {
            if(getDailyGoal.regDate == "") {
               dataManager!!.insertDailyGoal(DailyGoal(drugGoal = et.text.toString().toInt(), regDate = selectedDate.toString()))
            }else {
               dataManager!!.updateIntByDate(TABLE_DAILY_GOAL, "drugGoal", et.text.toString().toInt(), selectedDate.toString())
            }

            recordView()
            dialog.dismiss()
         }
      }

      binding.clGoal.setOnClickListener {
         dialog.show()
      }

      binding.clPrev.setOnClickListener {
         selectedDate = selectedDate.minusDays(1)
         binding.tvDate.text = dateFormat(selectedDate)
         recordView()
      }

      binding.clNext.setOnClickListener {
         selectedDate = selectedDate.plusDays(1)
         binding.tvDate.text = dateFormat(selectedDate)
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

      recordView()

      return binding.root
   }

   private fun recordView() {
      itemList.clear()
      binding.tvGoal.text = "0회"
      binding.tvRemain.text = "0회"
      binding.tvDrugCount.text = "0회"
      binding.pbDrug.setProgressEndColor(Color.TRANSPARENT)
      binding.pbDrug.setProgressStartColor(Color.TRANSPARENT)

      getDailyGoal = dataManager!!.getDailyGoal(selectedDate.toString())
      binding.pbDrug.max = getDailyGoal.drugGoal
      binding.tvGoal.text = "${getDailyGoal.drugGoal}회"

      // 약복용 체크값 초기화
      val check = dataManager!!.getDrugCheckCount(selectedDate.toString())

      // 약복용 리스트 생성
      val getDrugDaily = dataManager!!.getDrugDaily(selectedDate.toString())
      for(i in 0 until getDrugDaily.size) {
         val getDrugTime = dataManager!!.getDrugTime(getDrugDaily[i].id)
         for(j in 0 until getDrugTime.size) {
            val getDrugCheckCount = dataManager!!.getDrugCheckCount(getDrugTime[j].id, selectedDate.toString())
            itemList.add(DrugList(drugId = getDrugDaily[i].id, drugTimeId = getDrugTime[j].id, date = selectedDate.toString(), name = getDrugDaily[i].name,
               amount = getDrugDaily[i].amount, unit = getDrugDaily[i].unit, time = String.format("%02d", getDrugTime[j].hour)+":"+String.format("%02d", getDrugTime[j].minute),
               initCheck = check, checked = getDrugCheckCount)
            )
         }
      }

      adapter = DrugAdapter1(requireActivity(), itemList, getDailyGoal.drugGoal)
      binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
      binding.recyclerView.adapter = adapter
      binding.recyclerView.requestLayout()
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

   override fun onBackPressed() {
      val activity = activity as MainActivity?
      activity!!.setOnBackPressedListener(null)
      replaceFragment1(requireActivity(), MainFragment())
   }
}