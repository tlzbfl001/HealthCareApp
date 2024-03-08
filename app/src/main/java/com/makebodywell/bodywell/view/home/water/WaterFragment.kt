package com.makebodywell.bodywell.view.home.water

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
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
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.MyApp
import com.makebodywell.bodywell.view.home.MainActivity
import com.makebodywell.bodywell.view.home.MainFragment
import com.makebodywell.bodywell.view.home.body.BodyFragment
import com.makebodywell.bodywell.view.home.drug.DrugFragment
import com.makebodywell.bodywell.view.home.exercise.ExerciseFragment
import com.makebodywell.bodywell.view.home.food.FoodFragment
import com.makebodywell.bodywell.view.home.sleep.SleepFragment
import java.time.LocalDate

class WaterFragment : Fragment(), MainActivity.OnBackPressedListener {
   private var _binding: FragmentWaterBinding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null
   private var getDailyData = DailyData()
   private var getWater = Water()
   private var adapter: WaterAdapter? = null
   private var goal = 0
   private var volume = 200
   private var count = 0

   override fun onAttach(context: Context) {
      super.onAttach(context)
      (context as MainActivity).setOnBackPressedListener(this)
   }

   @SuppressLint("InternalInsetResource", "DiscouragedApi", "SetTextI18n")
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

      binding.tvDate.text = dateFormat(selectedDate)

      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_water_input)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      val etGoal = dialog.findViewById<EditText>(R.id.etGoal)
      val etVolume = dialog.findViewById<EditText>(R.id.etVolume)
      val btnSave = dialog.findViewById<CardView>(R.id.btnSave)

      btnSave.setOnClickListener {
         if(etGoal.text.toString().trim() == "") {
            Toast.makeText(requireActivity(), "목표를 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else if(etVolume.text.toString().toInt() < 1 || etGoal.text.toString().toInt() < 1) {
            Toast.makeText(requireActivity(), "1이상 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else if(etGoal.text.toString().toInt() > 100) {
            Toast.makeText(requireActivity(), "섭취량은 100잔을 넘을 수 없습니다.", Toast.LENGTH_SHORT).show()
         }else {
            goal = etGoal.text.toString().toInt()

            if(etVolume.text.toString() != "") {
               volume = etVolume.text.toString().toInt()
            }

            if(getDailyData.regDate == "") {
               dataManager!!.insertDailyData(DailyData(waterGoal = goal, regDate = selectedDate.toString()))
            }else {
               dataManager!!.updateGoal("waterGoal", goal, selectedDate.toString())
            }

            if(getWater.regDate == "") {
               dataManager!!.insertWater(Water(water = count, volume = volume, regDate = selectedDate.toString()))
            }else {
               dataManager!!.updateWater(Water(water = count, volume = volume, regDate = selectedDate.toString()))
            }

            dailyGoal()

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
         if(getWater.regDate != "") {
            dataManager!!.updateWater(Water(water = count, volume = volume, regDate = selectedDate.toString()))
         }

         selectedDate = selectedDate.minusDays(1)
         binding.tvDate.text = dateFormat(selectedDate)

         dailyGoal()
         dailyWater()
      }

      binding.clNext.setOnClickListener {
         if(getWater.regDate != "") {
            dataManager!!.updateWater(Water(water = count, volume = volume, regDate = selectedDate.toString()))
         }

         selectedDate = selectedDate.plusDays(1)
         binding.tvDate.text = dateFormat(selectedDate)

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

   @SuppressLint("SetTextI18n")
   private fun dailyGoal() {
      // 목표 초기화
      getDailyData = dataManager!!.getDailyData(selectedDate.toString())
      getWater = dataManager!!.getWater(selectedDate.toString())

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

   @SuppressLint("SetTextI18n")
   private fun dailyWater() {
      binding.tvCount.text = "${count}잔"
      binding.tvUnit.text = "(${count * volume} ml)"
      getWater = dataManager!!.getWater(selectedDate.toString())

      val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 4)
      binding.rv.layoutManager = layoutManager
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

         if(getWater.regDate == "") {
            dataManager!!.insertWater(Water(water = count, volume = volume, regDate = selectedDate.toString()))
         }else {
            dataManager!!.updateWater(Water(water = count, volume = volume, regDate = selectedDate.toString()))
         }
      }

      binding.ivPlus.setOnClickListener {
         if(count < 100) {
            count += 1
         }

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

         if(getWater.regDate == "") {
            dataManager!!.insertWater(Water(water = count, volume = getWater.volume, regDate = selectedDate.toString()))
         }else {
            dataManager!!.updateWater(Water(water = count, volume = getWater.volume, regDate = selectedDate.toString()))
         }
      }
   }

   override fun onBackPressed() {
      val activity = activity as MainActivity?
      activity!!.setOnBackPressedListener(null)
      replaceFragment1(requireActivity(), MainFragment())
   }
}
