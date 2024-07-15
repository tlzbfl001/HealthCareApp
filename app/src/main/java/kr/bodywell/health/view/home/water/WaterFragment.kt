package kr.bodywell.health.view.home.water

import android.animation.ValueAnimator
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.EditText
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.bodywell.health.R
import kr.bodywell.health.adapter.WaterAdapter
import kr.bodywell.health.database.DBHelper.Companion.CREATED
import kr.bodywell.health.database.DBHelper.Companion.IS_UPDATED
import kr.bodywell.health.database.DBHelper.Companion.TABLE_GOAL
import kr.bodywell.health.database.DBHelper.Companion.TABLE_WATER
import kr.bodywell.health.database.DataManager
import kr.bodywell.health.databinding.FragmentWaterBinding
import kr.bodywell.health.model.Goal
import kr.bodywell.health.model.Water
import kr.bodywell.health.util.CalendarUtil.Companion.selectedDate
import kr.bodywell.health.util.MainViewModel
import java.time.LocalDate


class WaterFragment : Fragment() {
   private var _binding: FragmentWaterBinding? = null
   private val binding get() = _binding!!

   private val viewModel: MainViewModel by activityViewModels()
   private lateinit var dataManager: DataManager
   private var adapter: WaterAdapter? = null
   private var dailyGoal = Goal()
   private var getWater = Water()
   private var volume = 200
   private var count = 0

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentWaterBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager.open()

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

      viewModel.dateVM.observe(viewLifecycleOwner, Observer<LocalDate> { item ->
         dailyView()
      })

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

      val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 5)
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

   private fun setValueAnimator(data: Int) {
      val animator = ValueAnimator.ofInt(0, data)
      animator.interpolator = AccelerateDecelerateInterpolator()
      animator.startDelay = 0
      animator.duration = 1000
      animator.addUpdateListener { valueAnimator ->
         val value = valueAnimator.animatedValue as Int
         binding.pbWater.progress = value
      }
      animator.start()
   }
}
