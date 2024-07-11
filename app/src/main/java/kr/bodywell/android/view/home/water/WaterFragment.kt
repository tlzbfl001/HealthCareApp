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
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.bodywell.android.R
import kr.bodywell.android.adapter.WaterAdapter
import kr.bodywell.android.database.DBHelper.Companion.CREATED
import kr.bodywell.android.database.DBHelper.Companion.IS_UPDATED
import kr.bodywell.android.database.DBHelper.Companion.TABLE_GOAL
import kr.bodywell.android.database.DBHelper.Companion.TABLE_WATER
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentWaterBinding
import kr.bodywell.android.model.Goal
import kr.bodywell.android.model.Water
import kr.bodywell.android.util.CalendarUtil.Companion.dateFormat
import kr.bodywell.android.util.CalendarUtil.Companion.selectedDate
import kr.bodywell.android.util.CustomUtil
import kr.bodywell.android.util.CustomUtil.Companion.replaceFragment1
import kr.bodywell.android.util.MainViewModel
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
   private var dailyGoal = Goal()
   private var getWater = Water()
   private var volume = 200
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

      binding.clBack.setOnClickListener {
         replaceFragment1(requireActivity(), MainFragment())
      }

      binding.clPrev.setOnClickListener {
         selectedDate = selectedDate.minusDays(1)
         binding.tvDate.text = dateFormat(selectedDate)
      }

      binding.clNext.setOnClickListener {
         selectedDate = selectedDate.plusDays(1)
         binding.tvDate.text = dateFormat(selectedDate)
      }

      binding.tvFood.setOnClickListener {
         replaceFragment1(requireActivity(), FoodFragment())
      }

      binding.tvExercise.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseFragment())
      }

      binding.tvBody.setOnClickListener {
         replaceFragment1(requireActivity(), BodyFragment())
      }

      binding.tvSleep.setOnClickListener {
         replaceFragment1(requireActivity(), SleepFragment())
      }

      binding.tvDrug.setOnClickListener {
         replaceFragment1(requireActivity(), DrugFragment())
      }

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
            if(etVolume.text.toString() != "") volume = etVolume.text.toString().toInt()

            if(dailyGoal.created == "") {
               dataManager.insertGoal(Goal(waterVolume = etVolume.text.toString().toInt(), water = etGoal.text.toString().toInt(), created = selectedDate.toString()))
               dailyGoal = dataManager.getGoal(selectedDate.toString())
            }else {
               dataManager.updateInt(TABLE_GOAL, "waterVolume", etVolume.text.toString().toInt(), selectedDate.toString())
               dataManager.updateInt(TABLE_GOAL, TABLE_WATER, etGoal.text.toString().toInt(), selectedDate.toString())
               dataManager.updateInt(TABLE_GOAL, IS_UPDATED, 1, "id", dailyGoal.id)
            }

            if(getWater.created == "") {
               dataManager.insertWater(Water(volume = volume, created = selectedDate.toString()))
            }else dataManager.updateInt(TABLE_WATER, "volume", volume, selectedDate.toString())

            dailyView()
            dialog.dismiss()
         }
      }

      binding.clGoal.setOnClickListener {
         dialog.show()
      }

      dailyView()

      return binding.root
   }

   private fun dailyView() {
      dailyGoal = dataManager.getGoal(selectedDate.toString())
      getWater = dataManager.getWater(selectedDate.toString())
      volume = getWater.volume
      count = getWater.count

      if(count > 0) {
         binding.pbWater.setProgressStartColor(Color.parseColor("#4AC0F2"))
         binding.pbWater.setProgressEndColor(Color.parseColor("#4AC0F2"))
         binding.pbWater.max = dailyGoal.water
         binding.pbWater.progress = count
      }else {
         binding.pbWater.setProgressStartColor(Color.TRANSPARENT)
         binding.pbWater.setProgressEndColor(Color.TRANSPARENT)
      }

      binding.tvIntake.text = "${count}잔/${count * volume}ml"
      binding.tvMl.text = "${volume}ml"
      binding.tvGoal.text = "${dailyGoal.water}잔/${dailyGoal.water * volume}ml"
      binding.tvCount.text = "${count}잔"
      binding.tvUnit.text = "(${count * volume}ml)"

      val remain = dailyGoal.water - count
      if(remain > 0) binding.tvRemain.text = "${remain}잔/${remain * volume}ml" else binding.tvRemain.text = "0잔/0ml"

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
            dataManager.deleteItem(TABLE_WATER, CREATED, selectedDate.toString())
         }

         getWater = dataManager.getWater(selectedDate.toString())
         if(getWater.created == "") {
            dataManager.insertWater(Water(count = count, created = selectedDate.toString()))
         }else {
            dataManager.updateInt(TABLE_WATER, "count", count, selectedDate.toString())
         }

         if(getWater.uid != "") dataManager.updateInt(TABLE_WATER, IS_UPDATED, 1, selectedDate.toString())

         resetData()
      }

      binding.ivPlus.setOnClickListener {
         if(count < 100) count += 1

         binding.pbWater.setProgressStartColor(Color.parseColor("#4AC0F2"))
         binding.pbWater.setProgressEndColor(Color.parseColor("#4AC0F2"))
         binding.pbWater.max = dailyGoal.water
         binding.pbWater.progress = count
         getWater = dataManager.getWater(selectedDate.toString())

         if(count > 0) {
            if(getWater.created == "") {
               dataManager.insertWater(Water(count = count, created = selectedDate.toString()))
            }else {
               dataManager.updateInt(TABLE_WATER, "count", count, selectedDate.toString())
            }
         }

         if(getWater.uid != "") dataManager.updateInt(TABLE_WATER, IS_UPDATED, 1, selectedDate.toString())

         resetData()
      }
   }

   private fun resetData() {
      binding.tvCount.text = "${count}잔"
      binding.tvUnit.text = "(${count * volume}ml)"
      binding.tvIntake.text = "${count}잔/${count * volume}ml"

      val remain = dailyGoal.water - count
      if(remain > 0) binding.tvRemain.text = "${remain}잔/${remain * volume}ml" else binding.tvRemain.text = "0잔/0ml"

      adapter = WaterAdapter(count)
      binding.rv.adapter = adapter
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}
