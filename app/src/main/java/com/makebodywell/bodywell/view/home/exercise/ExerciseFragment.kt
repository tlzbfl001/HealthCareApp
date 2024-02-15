package com.makebodywell.bodywell.view.home.exercise

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.adapter.ExerciseAdapter
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentExerciseBinding
import com.makebodywell.bodywell.model.DailyData
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
import com.makebodywell.bodywell.util.CustomUtil.Companion.getExerciseCalories
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import com.makebodywell.bodywell.util.MyApp
import com.makebodywell.bodywell.view.home.MainFragment
import com.makebodywell.bodywell.view.home.body.BodyFragment
import com.makebodywell.bodywell.view.home.drug.DrugFragment
import com.makebodywell.bodywell.view.home.food.FoodFragment
import com.makebodywell.bodywell.view.home.sleep.SleepFragment
import com.makebodywell.bodywell.view.home.water.WaterFragment
import java.time.LocalDate

class ExerciseFragment : Fragment() {
   private var _binding: FragmentExerciseBinding? = null
   private val binding get() = _binding!!

   private var bundle = Bundle()
   private var dataManager: DataManager? = null
   private var adapter: ExerciseAdapter? = null
   private var getDailyData = DailyData()

   private var calendarDate = LocalDate.now()
   private var sum = 0

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentExerciseBinding.inflate(layoutInflater)

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

      binding.tvDate.text = dateFormat(calendarDate)

      settingGoal()

      binding.clBack.setOnClickListener {
         replaceFragment1(requireActivity(), MainFragment())
      }

      binding.clPrev.setOnClickListener {
         calendarDate = calendarDate!!.minusDays(1)
         binding.tvDate.text = dateFormat(calendarDate)
         dailyView()
      }

      binding.clNext.setOnClickListener {
         calendarDate = calendarDate!!.plusDays(1)
         binding.tvDate.text = dateFormat(calendarDate)
         dailyView()
      }

      binding.cvFood.setOnClickListener {
         replaceFragment1(requireActivity(), FoodFragment())
      }

      binding.cvWater.setOnClickListener {
         replaceFragment1(requireActivity(), WaterFragment())
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

      binding.clRecord.setOnClickListener {
         bundle.putString("calendarDate", calendarDate.toString())
         replaceFragment2(requireActivity(), ExerciseListFragment(), bundle)
      }

      dailyView()

      return binding.root
   }

   private fun settingGoal() {
      // 목표 설정
      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_input)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
      val et = dialog.findViewById<TextView>(R.id.et)
      val btnSave = dialog.findViewById<CardView>(R.id.btnSave)

      tvTitle.text = "운동 / 목표 칼로리 입력"
      btnSave.setCardBackgroundColor(Color.parseColor("#FFA511"))
      btnSave.setOnClickListener {
         if(et.text.toString().trim() == "") {
            Toast.makeText(requireActivity(), "전부 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            if(getDailyData.regDate == "") {
               dataManager!!.insertDailyData(DailyData(exerciseGoal = et.text.toString().toInt(), regDate = calendarDate.toString()))
            }else {
               dataManager!!.updateGoal("exerciseGoal",  et.text.toString().toInt(), MyApp.prefs.getId(), calendarDate.toString())
            }

            binding.pbExercise.max = et.text.toString().toInt()
            binding.tvGoal.text = "${et.text} kcal"

            val remain = et.text.toString().toInt() - sum
            if(remain > 0) {
               binding.tvRemain.text = "$remain kcal"
            }else {
               binding.tvRemain.text = "0 kcal"
            }

            dialog.dismiss()
         }
      }

      binding.clGoal.setOnClickListener {
         dialog.show()
      }
   }

   private fun dailyView() {
      // 목표 초기화
      binding.pbExercise.max = 0
      binding.pbExercise.setProgressStartColor(Color.TRANSPARENT)
      binding.pbExercise.setProgressEndColor(Color.TRANSPARENT)
      binding.tvConsume.text = "0 kcal"
      binding.tvGoal.text = "0 kcal"
      binding.tvRemain.text = "0 kcal"

      getDailyData = dataManager!!.getDailyData(MyApp.prefs.getId(), calendarDate.toString())
      sum = getExerciseCalories(requireActivity(), calendarDate.toString())

      if(sum > 0) {
         binding.pbExercise.setProgressStartColor(Color.parseColor("#FFB846"))
         binding.pbExercise.setProgressEndColor(Color.parseColor("#FFB846"))
         binding.pbExercise.max = getDailyData.exerciseGoal
         binding.pbExercise.progress = sum
      }

      binding.tvGoal.text = "${getDailyData.exerciseGoal} kcal"
      binding.tvConsume.text = "$sum kcal"

      val remain = getDailyData.exerciseGoal - sum
      if(remain > 0) {
         binding.tvRemain.text = "$remain kcal"
      }else {
         binding.tvRemain.text = "0 kcal"
      }

      val getExercise = dataManager!!.getExercise(MyApp.prefs.getId(), calendarDate.toString())

      adapter = ExerciseAdapter(getExercise)
      binding.rv.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
      binding.rv.adapter = adapter
   }
}