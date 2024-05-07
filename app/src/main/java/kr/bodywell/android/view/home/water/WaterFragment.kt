package kr.bodywell.android.view.home.water

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
import kr.bodywell.android.R
import kr.bodywell.android.adapter.WaterAdapter
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DAILY_GOAL
import kr.bodywell.android.database.DBHelper.Companion.TABLE_WATER
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentWaterBinding
import kr.bodywell.android.model.DailyGoal
import kr.bodywell.android.model.Water
import kr.bodywell.android.util.CalendarUtil.Companion.dateFormat
import kr.bodywell.android.util.CalendarUtil.Companion.selectedDate
import kr.bodywell.android.util.CustomUtil.Companion.replaceFragment1
import kr.bodywell.android.view.home.MainFragment
import kr.bodywell.android.view.home.body.BodyFragment
import kr.bodywell.android.view.home.drug.DrugFragment
import kr.bodywell.android.view.home.exercise.ExerciseFragment
import kr.bodywell.android.view.home.food.FoodFragment
import kr.bodywell.android.view.home.sleep.SleepFragment

class WaterFragment : Fragment() {
   private var _binding: FragmentWaterBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private var adapter: WaterAdapter? = null
   private var dailyGoal = DailyGoal()
   private var water = Water()
   private var mL = 200
   private var count = 0

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
      dataManager.open()

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
         }else if(etGoal.text.toString().toInt() < 1) {
            Toast.makeText(requireActivity(), "1이상 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else if(etGoal.text.toString().toInt() > 100) {
            Toast.makeText(requireActivity(), "목표 섭취물은 100을 넘을 수 없습니다.", Toast.LENGTH_SHORT).show()
         }else {
            if(etVolume.text.toString() != "") {
               mL = etVolume.text.toString().toInt()
            }

            if(dailyGoal.regDate == "") {
               dataManager.insertDailyGoal(DailyGoal(waterGoal = etGoal.text.toString().toInt(), regDate = selectedDate.toString()))
            }else {
               dataManager.updateIntByDate(TABLE_DAILY_GOAL, "waterGoal", etGoal.text.toString().toInt(), selectedDate.toString())
            }

            if(water.regDate == "") {
               dataManager.insertWater(Water(mL = mL, regDate = selectedDate.toString()))
            }else {
               dataManager.updateIntByDate(TABLE_WATER, "mL", mL, selectedDate.toString())
            }

            dailyWater()

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

         dailyWater()
      }

      binding.clNext.setOnClickListener {
         selectedDate = selectedDate.plusDays(1)
         binding.tvDate.text = dateFormat(selectedDate)

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

      dailyWater() // 데이터 초기화

      return binding.root
   }

   private fun dailyWater() {
      dailyGoal = dataManager.getDailyGoal(selectedDate.toString())
      water = dataManager.getWater(selectedDate.toString())
      mL = water.mL
      count = water.count

      if(count > 0) {
         binding.pbWater.setProgressStartColor(Color.parseColor("#4AC0F2"))
         binding.pbWater.setProgressEndColor(Color.parseColor("#4AC0F2"))
         binding.pbWater.max = dailyGoal.waterGoal
         binding.pbWater.progress = count
      }else {
         binding.pbWater.setProgressStartColor(Color.TRANSPARENT)
         binding.pbWater.setProgressEndColor(Color.TRANSPARENT)
      }

      binding.tvIntake.text = "${count}잔/${count * mL}ml"
      binding.tvMl.text = "${mL}ml"
      binding.tvGoal.text = "${dailyGoal.waterGoal}잔/${dailyGoal.waterGoal * mL}ml"
      binding.tvCount.text = "${count}잔"
      binding.tvUnit.text = "(${count * mL}ml)"

      val remain = dailyGoal.waterGoal - count
      if(remain > 0) {
         binding.tvRemain.text = "${remain}잔/${remain * mL}ml"
      }else {
         binding.tvRemain.text = "0잔/0ml"
      }

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
            dataManager.deleteItem(TABLE_WATER, "regDate", selectedDate.toString())
         }

         water = dataManager.getWater(selectedDate.toString())
         if(water.regDate == "") {
            dataManager.insertWater(Water(count = count, regDate = selectedDate.toString()))
         }else {
            dataManager.updateIntByDate(TABLE_WATER, "count", count, selectedDate.toString())
         }

         resetData()
      }

      binding.ivPlus.setOnClickListener {
         if(count < 100) {
            count += 1
         }

         binding.pbWater.setProgressStartColor(Color.parseColor("#4AC0F2"))
         binding.pbWater.setProgressEndColor(Color.parseColor("#4AC0F2"))
         binding.pbWater.max = dailyGoal.waterGoal
         binding.pbWater.progress = count

         water = dataManager.getWater(selectedDate.toString())
         if(count > 0) {
            if(water.regDate == "") {
               dataManager.insertWater(Water(count = count, regDate = selectedDate.toString()))
            }else {
               dataManager.updateIntByDate(TABLE_WATER, "count", count, selectedDate.toString())
            }
         }

         resetData()
      }
   }

   private fun resetData() {
      binding.tvCount.text = "${count}잔"
      binding.tvUnit.text = "(${count * mL}ml)"
      binding.tvIntake.text = "${count}잔/${count * mL}ml"

      val remain = dailyGoal.waterGoal - count
      if(remain > 0) {
         binding.tvRemain.text = "${remain}잔/${remain * mL}ml"
      }else {
         binding.tvRemain.text = "0잔/0ml"
      }

      adapter = WaterAdapter(count)
      binding.rv.adapter = adapter
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}
