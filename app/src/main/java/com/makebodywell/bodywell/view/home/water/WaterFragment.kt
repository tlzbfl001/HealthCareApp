package com.makebodywell.bodywell.view.home.water

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.adapter.WaterAdapter
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentWaterBinding
import com.makebodywell.bodywell.model.DailyData
import com.makebodywell.bodywell.model.Water
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.view.home.MainFragment
import com.makebodywell.bodywell.view.home.body.BodyFragment
import com.makebodywell.bodywell.view.home.drug.DrugFragment
import com.makebodywell.bodywell.view.home.exercise.ExerciseFragment
import com.makebodywell.bodywell.view.home.food.FoodFragment
import com.makebodywell.bodywell.view.home.sleep.SleepFragment
import java.time.LocalDate

class WaterFragment : Fragment() {
   private var _binding: FragmentWaterBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback

   private var calendarDate = LocalDate.now()

   private var dataManager: DataManager? = null
   private var getWater: Water? = null
   private var getDailyData: DailyData? = null

   private var adapter: WaterAdapter? = null
   private var count = 0
   private var volume = 200
   private var goal = 0

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentWaterBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      initView()
      setupGoal(calendarDate.toString())
      dailyView()

      return binding.root
   }

   private fun initView() {
      binding.clBack.setOnClickListener {
         replaceFragment1(requireActivity(), MainFragment())
      }

      binding.tvDate.text = dateFormat(calendarDate)

      binding.tvPrev.setOnClickListener {
         if(getWater!!.regDate != "") {
            dataManager!!.updateWater(Water(water = count, volume = volume, regDate = calendarDate.toString()))
         }

         calendarDate = calendarDate!!.minusDays(1)
         binding.tvDate.text = dateFormat(calendarDate)
         setupGoal(calendarDate.toString())
         dailyView()
      }

      binding.tvNext.setOnClickListener {
         if(getWater!!.regDate != "") {
            dataManager!!.updateWater(Water(water = count, volume = volume, regDate = calendarDate.toString()))
         }

         calendarDate = calendarDate!!.plusDays(1)
         binding.tvDate.text = dateFormat(calendarDate)
         setupGoal(calendarDate.toString())
         dailyView()
      }

      binding.cvFood.setOnClickListener {
         replaceFragment1(requireActivity(), FoodFragment())
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

   private fun setupGoal(date: String) {
      // 목표 초기화
      count = 0
      volume = 200
      goal = 0

      getDailyData = dataManager!!.getDailyData(date)
      goal = getDailyData!!.waterGoal
      if(goal != 0) {
         binding.pbWater.max = goal
         binding.tvGoal.text = goal.toString() + "잔/" + (goal * volume) + "ml"
      }

      // 목표 데이터 가져오기
      getWater = dataManager!!.getWater(date)
      if(getWater!!.regDate != "") {
         count = getWater!!.water
         volume = getWater!!.volume

         binding.pbWater.progress = count

         binding.tvIntake.text = "${count}잔/${count * volume}ml"
         binding.tvVolume.text = volume.toString() + "ml"
         binding.tvGoal.text = goal.toString() + "잔/" + (goal * volume) + "ml"
         val remain = goal - count
         if(remain > 0) {
            binding.tvRemain.text = remain.toString() + "잔/" + (remain * volume) + "ml"
         }else {
            binding.tvRemain.text = "0잔/0ml"
         }
      }else {
         binding.tvIntake.text = "0잔/0ml"
         binding.tvVolume.text = "200ml"
         binding.tvGoal.text = "0잔/0ml"
         binding.tvRemain.text = "0잔/0ml"
      }

      // 목표 설정
      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_water_input)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      val etGoal = dialog.findViewById<EditText>(R.id.etGoal)
      val etVolume = dialog.findViewById<EditText>(R.id.etVolume)
      val btnSave = dialog.findViewById<CardView>(R.id.btnSave)

      btnSave.setOnClickListener {
         if(etVolume.text.toString().trim() == "" || etGoal.text.toString().trim() == "") {
            Toast.makeText(requireActivity(), "전부 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else if(etVolume.text.toString().trim() == "0" || etGoal.text.toString().trim() == "0") {
            Toast.makeText(requireActivity(), "1이상 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            volume = etVolume.text.toString().toInt()
            goal = etGoal.text.toString().toInt()

            binding.pbWater.max = goal

            if(getWater!!.regDate == "") {
               dataManager!!.insertWater(Water(water = count, volume = volume, regDate = date))
            }else {
               dataManager!!.updateWater(Water(water = count, volume = volume, regDate = date))
            }

            if(getDailyData!!.regDate == "") {
               dataManager!!.insertDailyData(DailyData(waterGoal = goal, regDate = date))
            }else {
               dataManager!!.updateWaterGoal(DailyData(waterGoal = goal, regDate = date))
            }

            binding.tvIntake.text = "${count}잔/${count * volume}ml"
            binding.tvVolume.text = volume.toString() + "ml"
            binding.tvGoal.text = goal.toString() + "잔/" + (goal * volume) + "ml"
            val remain = goal - count
            if(remain > 0) {
               binding.tvRemain.text = remain.toString() + "잔/" + (remain * volume) + "ml"
            }else {
               binding.tvRemain.text = "0잔/0ml"
            }

            dialog.dismiss()
         }
      }

      binding.cvGoal.setOnClickListener {
         dialog.show()
      }
   }

   private fun dailyView() {
      val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 4)
      binding.recyclerView.layoutManager = layoutManager

      binding.tvWaterCount.text = count.toString()
      binding.tvWaterUnit.text = (count * volume).toString()

      adapter = WaterAdapter(count)
      binding.recyclerView.adapter = adapter

      binding.ivMinus.setOnClickListener {
         if(count > 0) {
            count -= 1
         }

         binding.pbWater.progress = count
         binding.tvWaterCount.text = count.toString()
         binding.tvWaterUnit.text = (count * volume).toString()
         binding.tvIntake.text = "${count}잔/${count * volume}ml"
         val remain = goal - count
         if(remain > 0) {
            binding.tvRemain.text = remain.toString() + "잔/" + (remain * volume) + "ml"
         }else {
            binding.tvRemain.text = "0잔/0ml"
         }

         adapter = WaterAdapter(count)
         binding.recyclerView.adapter = adapter
      }

      binding.ivPlus.setOnClickListener {
         count += 1

         binding.pbWater.progress = count
         binding.tvWaterCount.text = count.toString()
         binding.tvWaterUnit.text = (count * volume).toString()
         binding.tvIntake.text = "${count}잔/${count * volume}ml"
         val remain = goal - count
         if(remain > 0) {
            binding.tvRemain.text = remain.toString() + "잔/" + (remain * volume) + "ml"
         }else {
            binding.tvRemain.text = "0잔/0ml"
         }

         adapter = WaterAdapter(count)
         binding.recyclerView.adapter = adapter
      }
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

   override fun onPause() {
      super.onPause()

      if(getWater!!.regDate == "") {
         dataManager!!.insertWater(Water(water = count, volume = volume, regDate = calendarDate.toString()))
      }else {
         dataManager!!.updateWater(Water(water = count, volume = volume, regDate = calendarDate.toString()))
      }
   }
}