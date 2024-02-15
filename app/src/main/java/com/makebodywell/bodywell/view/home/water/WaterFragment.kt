package com.makebodywell.bodywell.view.home.water

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
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
import com.makebodywell.bodywell.util.MyApp
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

   private var calendarDate = LocalDate.now()

   private var dataManager: DataManager? = null
   private var getDailyData = DailyData()
   private var getWater = Water()
   private var adapter: WaterAdapter? = null

   private var goal = 0
   private var volume = 0
   private var count = 0

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentWaterBinding.inflate(layoutInflater)

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
         if(getWater.regDate != "") {
            dataManager!!.updateWater(Water(water = count, volume = volume, regDate = calendarDate.toString()))
         }

         calendarDate = calendarDate!!.minusDays(1)
         binding.tvDate.text = dateFormat(calendarDate)

         dailyGoal()
         dailyWater()
      }

      binding.clNext.setOnClickListener {
         if(getWater.regDate != "") {
            dataManager!!.updateWater(Water(water = count, volume = volume, regDate = calendarDate.toString()))
         }

         calendarDate = calendarDate!!.plusDays(1)
         binding.tvDate.text = dateFormat(calendarDate)

         dailyGoal()
         dailyWater()
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

      dailyGoal()
      dailyWater()

      return binding.root
   }

   private fun settingGoal() {
      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_water_input)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      val etGoal = dialog.findViewById<EditText>(R.id.etGoal)
      val etVolume = dialog.findViewById<EditText>(R.id.etVolume)
      val btnSave = dialog.findViewById<CardView>(R.id.btnSave)

      btnSave.setOnClickListener {
         if(etVolume.text.toString().trim() == "" || etGoal.text.toString().trim() == "") {
            Toast.makeText(requireActivity(), "전부 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else if(etVolume.text.toString().trim() == "0"  || etGoal.text.toString().trim() == "0") {
            Toast.makeText(requireActivity(), "1이상 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            goal = etGoal.text.toString().toInt()
            volume = etVolume.text.toString().toInt()

            if(getDailyData.regDate == "") {
               dataManager!!.insertDailyData(DailyData(waterGoal = goal, regDate = calendarDate.toString()))
            }else {
               dataManager!!.updateGoal("waterGoal", goal, MyApp.prefs.getId(), calendarDate.toString())
            }

            if(getWater.regDate == "") {
               dataManager!!.insertWater(Water(water = count, volume = volume, regDate = calendarDate.toString()))
            }else {
               dataManager!!.updateWater(Water(water = count, volume = volume, regDate = calendarDate.toString()))
            }

            binding.pbWater.max = goal
            binding.tvIntake.text = "${count}잔/${count * volume}ml"
            binding.tvVolume.text = "${volume}ml"
            binding.tvGoal.text = "${goal}잔/${goal * volume}ml"
            binding.tvUnit.text = (count * volume).toString()

            val remain = goal - count
            if(remain > 0) {
               binding.tvRemain.text = "${remain}잔/${remain * volume}ml"
            }else {
               binding.tvRemain.text = "0잔/0ml"
            }

            dialog.dismiss()
         }
      }

      binding.clGoal.setOnClickListener {
         dialog.show()
      }
   }

   private fun dailyGoal() {
      // 목표 초기화
      getDailyData = dataManager!!.getDailyData(MyApp.prefs.getId(), calendarDate.toString())
      getWater = dataManager!!.getWater(MyApp.prefs.getId(), calendarDate.toString())

      binding.pbWater.max = 0
      binding.pbWater.setProgressStartColor(Color.TRANSPARENT)
      binding.pbWater.setProgressEndColor(Color.TRANSPARENT)
      binding.tvIntake.text = "0잔/0ml"
      binding.tvVolume.text = "200ml"
      binding.tvGoal.text = "0잔/0ml"
      binding.tvRemain.text = "0잔/0ml"

      goal = getDailyData.waterGoal
      volume = getWater.volume
      count = getWater.water

      if(count > 0) {
         binding.pbWater.setProgressStartColor(Color.parseColor("#4AC0F2"))
         binding.pbWater.setProgressEndColor(Color.parseColor("#4AC0F2"))
         binding.pbWater.max = goal
         binding.pbWater.progress = count
      }

      binding.tvIntake.text = "${count}잔/${count * volume}ml"
      binding.tvVolume.text = "${volume}ml"
      binding.tvGoal.text = "${goal}잔/${goal * volume}ml"

      val remain = goal - count
      if(remain > 0) {
         binding.tvRemain.text = "${remain}잔/${remain * volume}ml"
      }else {
         binding.tvRemain.text = "0잔/0ml"
      }
   }

   private fun dailyWater() {
      val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 4)
      binding.rv.layoutManager = layoutManager

      binding.tvCount.text = "${count}잔"
      binding.tvUnit.text = "(${count * volume} ml)"

      adapter = WaterAdapter(count)
      binding.rv.adapter = adapter

      binding.ivMinus.setOnClickListener {
         if(count > 0) {
            count -= 1
            binding.pbWater.setProgressStartColor(Color.parseColor("#4AC0F2"))
            binding.pbWater.setProgressEndColor(Color.parseColor("#4AC0F2"))
            binding.pbWater.progress = count
         }

         if(count == 0) {
            binding.pbWater.setProgressStartColor(Color.TRANSPARENT)
            binding.pbWater.setProgressEndColor(Color.TRANSPARENT)
         }

         binding.tvCount.text = "${count}잔"
         binding.tvUnit.text = "(${count * volume} ml)"
         binding.tvIntake.text = "${count}잔/${count * volume}ml"

         val remain = goal - count
         if(remain > 0) {
            binding.tvRemain.text = "${remain}잔/${remain * volume}ml"
         }else {
            binding.tvRemain.text = "0잔/0ml"
         }

         adapter = WaterAdapter(count)
         binding.rv.adapter = adapter

         getWater = dataManager!!.getWater(MyApp.prefs.getId(), calendarDate.toString())
         if(getWater.regDate == "") {
            dataManager!!.insertWater(Water(water = count, volume = volume, regDate = calendarDate.toString()))
         }else {
            dataManager!!.updateWater(Water(water = count, volume = volume, regDate = calendarDate.toString()))
         }
      }

      binding.ivPlus.setOnClickListener {
         count += 1

         binding.pbWater.setProgressStartColor(Color.parseColor("#4AC0F2"))
         binding.pbWater.setProgressEndColor(Color.parseColor("#4AC0F2"))
         binding.pbWater.max = goal
         binding.pbWater.progress = count
         binding.tvCount.text = "${count}잔"
         binding.tvUnit.text = "(${count * volume} ml)"
         binding.tvIntake.text = "${count}잔/${count * volume}ml"

         val remain = goal - count
         if(remain > 0) {
            binding.tvRemain.text = "${remain}잔/${remain * volume}ml"
         }else {
            binding.tvRemain.text = "0잔/0ml"
         }

         adapter = WaterAdapter(count)
         binding.rv.adapter = adapter

         getWater = dataManager!!.getWater(MyApp.prefs.getId(), calendarDate.toString())
         if(getWater.regDate == "") {
            dataManager!!.insertWater(Water(water = count, volume = getWater.volume, regDate = calendarDate.toString()))
         }else {
            dataManager!!.updateWater(Water(water = count, volume = getWater.volume, regDate = calendarDate.toString()))
         }
      }
   }
}
