package com.makebodywell.bodywell.view.home.exercise

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.adapter.ExerciseAdapter
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DAILY_GOAL
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentExerciseBinding
import com.makebodywell.bodywell.model.DailyGoal
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CustomUtil.Companion.getExerciseCalories
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.view.home.MainFragment
import com.makebodywell.bodywell.view.home.body.BodyFragment
import com.makebodywell.bodywell.view.home.drug.DrugFragment
import com.makebodywell.bodywell.view.home.food.FoodFragment
import com.makebodywell.bodywell.view.home.sleep.SleepFragment
import com.makebodywell.bodywell.view.home.water.WaterFragment

class ExerciseFragment : Fragment() {
   private var _binding: FragmentExerciseBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private var adapter: ExerciseAdapter? = null
   private var dailyGoal = DailyGoal()
   private var sum = 0

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment1(requireActivity(), MainFragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

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
      dataManager.open()

      binding.tvDate.text = dateFormat(selectedDate)

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
            Toast.makeText(requireActivity(), "입력된 문자가 없습니다.", Toast.LENGTH_SHORT).show()
         }else {
            if(dailyGoal.regDate == "") {
               dataManager.insertDailyGoal(DailyGoal(exerciseGoal = et.text.toString().toInt(), regDate = selectedDate.toString()))
            }else {
               dataManager.updateIntByDate(TABLE_DAILY_GOAL, "exerciseGoal", et.text.toString().toInt(), selectedDate.toString())
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

      binding.clBack.setOnClickListener {
         replaceFragment1(requireActivity(), MainFragment())
      }

      binding.clPrev.setOnClickListener {
         selectedDate = selectedDate.minusDays(1)
         binding.tvDate.text = dateFormat(selectedDate)
         dailyView()
      }

      binding.clNext.setOnClickListener {
         selectedDate = selectedDate.plusDays(1)
         binding.tvDate.text = dateFormat(selectedDate)
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
         replaceFragment1(requireActivity(), ExerciseListFragment())
      }

      dailyView()

      return binding.root
   }

   private fun dailyView() {
      // 목표 초기화
      binding.pbExercise.setProgressStartColor(Color.TRANSPARENT)
      binding.pbExercise.setProgressEndColor(Color.TRANSPARENT)
      binding.tvConsume.text = "0 kcal"
      binding.tvGoal.text = "0 kcal"
      binding.tvRemain.text = "0 kcal"

      dailyGoal = dataManager.getDailyGoal(selectedDate.toString())
      sum = getExerciseCalories(requireActivity(), selectedDate.toString())

      if(sum > 0) {
         binding.pbExercise.setProgressStartColor(Color.parseColor("#FFB846"))
         binding.pbExercise.setProgressEndColor(Color.parseColor("#FFB846"))
         binding.pbExercise.max = dailyGoal.exerciseGoal
         binding.pbExercise.progress = sum
      }

      binding.tvGoal.text = "${dailyGoal.exerciseGoal} kcal"
      binding.tvConsume.text = "$sum kcal"

      val remain = dailyGoal.exerciseGoal - sum
      if(remain > 0) {
         binding.tvRemain.text = "$remain kcal"
      }else {
         binding.tvRemain.text = "0 kcal"
      }

      val getExercise = dataManager.getDailyExercise(selectedDate.toString())

      adapter = ExerciseAdapter(getExercise)
      binding.rv.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
      binding.rv.adapter = adapter
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}