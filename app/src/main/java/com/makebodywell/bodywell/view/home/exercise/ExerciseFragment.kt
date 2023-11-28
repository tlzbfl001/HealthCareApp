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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.adapter.ExerciseAdapter
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentExerciseBinding
import com.makebodywell.bodywell.model.DailyData
import com.makebodywell.bodywell.model.Exercise
import com.makebodywell.bodywell.model.Water
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.util.CustomUtil.Companion.getExerciseCalories
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.view.home.MainFragment
import com.makebodywell.bodywell.view.home.food.FoodFragment
import com.makebodywell.bodywell.view.home.sleep.SleepFragment
import com.makebodywell.bodywell.view.home.water.WaterFragment
import com.makebodywell.bodywell.view.home.body.BodyFragment
import com.makebodywell.bodywell.view.home.drug.DrugFragment
import java.text.SimpleDateFormat
import java.time.LocalDate

class ExerciseFragment : Fragment() {
   private var _binding: FragmentExerciseBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback

   private var calendarDate = LocalDate.now()
   private val formatter1 = SimpleDateFormat("yyyy-MM-dd")
   private val formatter2 = SimpleDateFormat("yyyy년 MM월 dd일")

   private var dataManager: DataManager? = null

   private var adapter: ExerciseAdapter? = null

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentExerciseBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      initView()
      setupGoal(calendarDate.toString())
      dailyView()

      return binding.root
   }

   private fun initView() {
      binding.tvDate.text = formatter2.format(formatter1.parse(calendarDate.toString())!!)

      binding.clBack.setOnClickListener {
         replaceFragment1(requireActivity(), MainFragment())
      }

      binding.tvPrev.setOnClickListener {
         calendarDate = calendarDate!!.minusDays(1)
         binding.tvDate.text = formatter2.format(formatter1.parse(calendarDate.toString())!!)
         setupGoal(calendarDate.toString())
         dailyView()
      }

      binding.tvNext.setOnClickListener {
         calendarDate = calendarDate!!.plusDays(1)
         binding.tvDate.text = formatter2.format(formatter1.parse(calendarDate.toString())!!)
         setupGoal(calendarDate.toString())
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
   }

   private fun setupGoal(date: String) {
      // 텍스트 초기화
      binding.tvConsume.text = "0 kcal"
      binding.tvGoal.text = "0 kcal"
      binding.tvRemain.text = "0 kcal"

      // 목표 칼로리 초기화
      val getDailyData = dataManager!!.getDailyData(date)
      val goal = getDailyData.exerciseGoal
      if(goal != 0) {
         binding.pbExercise.max = goal
         binding.tvGoal.text = "$goal kcal"
      }

      // 소모 칼로리 초기화
      val sum = getExerciseCalories(requireActivity(), date)
      binding.pbExercise.progress = sum
      binding.tvConsume.text = "$sum kcal"

      // 남은 칼로리 초기화
      val remain = goal - sum
      if(remain > 0) {
         binding.tvRemain.text = "$remain kcal"
      }else {
         binding.tvRemain.text = "0 kcal"
      }

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
               binding.tvGoal.text = "${et.text} kcal"

               val remain = et.text.toString().toInt() - sum
               if(remain > 0) {
                  binding.tvRemain.text = "$remain kcal"
               }else {
                  binding.tvRemain.text = "0 kcal"
               }

               dataManager!!.insertDailyData(DailyData(exerciseGoal = et.text.toString().toInt(), regDate = date))
            }else {
               binding.tvGoal.text = "${et.text} kcal"

               val remain = et.text.toString().toInt() - sum
               if(remain > 0) {
                  binding.tvRemain.text = "$remain kcal"
               }else {
                  binding.tvRemain.text = "0 kcal"
               }

               dataManager!!.updateExerciseGoal(DailyData(exerciseGoal = et.text.toString().toInt(), regDate = date))
            }

            dialog.dismiss()
         }
      }

      binding.cvGoal.setOnClickListener {
         dialog.show()
      }
   }

   private fun dailyView() {
      val itemList = ArrayList<Exercise>()

      val getExercise = dataManager!!.getExercise(calendarDate.toString())

      for(i in 0 until getExercise.size) {
         itemList.add(Exercise(category = getExercise[i].category, name = getExercise[i].name, workoutTime = getExercise[i].workoutTime,
            distance = getExercise[i].distance, calories = getExercise[i].calories))
      }

      val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 2)
      binding.recyclerView.layoutManager = layoutManager
      adapter = ExerciseAdapter(itemList)
      binding.recyclerView.adapter = adapter
   }

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment1(requireActivity(), MainFragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}