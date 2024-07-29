package kr.bodywell.android.view.home.water

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.bodywell.android.R
import kr.bodywell.android.adapter.WaterAdapter
import kr.bodywell.android.database.DBHelper.Companion.CREATED_AT
import kr.bodywell.android.database.DBHelper.Companion.IS_UPDATED
import kr.bodywell.android.database.DBHelper.Companion.GOAL
import kr.bodywell.android.database.DBHelper.Companion.WATER
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentWaterBinding
import kr.bodywell.android.model.Goal
import kr.bodywell.android.model.Unused
import kr.bodywell.android.model.Water
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.view.MainViewModel
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

            if(dailyGoal.createdAt == "") {
               dataManager.insertGoal(Goal(waterVolume = etVolume.text.toString().toInt(), water = etGoal.text.toString().toInt(), createdAt = selectedDate.toString()))
               dailyGoal = dataManager.getGoal(selectedDate.toString())
            }else {
               dataManager.updateInt(GOAL, "waterVolume", etVolume.text.toString().toInt(), selectedDate.toString())
               dataManager.updateInt(GOAL, WATER, etGoal.text.toString().toInt(), selectedDate.toString())
               dataManager.updateInt(GOAL, IS_UPDATED, 1, "id", dailyGoal.id)
            }

            if(getWater.createdAt == "") {
               dataManager.insertWater(Water(volume = volume, createdAt = selectedDate.toString()))
            }else dataManager.updateInt(WATER, "volume", volume, selectedDate.toString())

            dailyView()
            dialog.dismiss()
         }
      }

      binding.clGoal.setOnClickListener {
         dialog.show()
      }

      binding.ivMinus.setOnClickListener {
         getWater = dataManager.getWater(selectedDate.toString())

         if(count > 0) {
            count -= 1
            binding.pbWater.setProgressStartColor(Color.parseColor("#4AC0F2"))
            binding.pbWater.setProgressEndColor(Color.parseColor("#4AC0F2"))
            binding.pbWater.progress = count

            getWater = dataManager.getWater(selectedDate.toString())

            if(getWater.createdAt == "") {
               dataManager.insertWater(Water(count = count, createdAt = selectedDate.toString()))
            }else {
               dataManager.updateInt(WATER, "count", count, "id", getWater.id)
            }

            if(getWater.uid != "") dataManager.updateInt(WATER, IS_UPDATED, 1, selectedDate.toString())
         }

         if(count == 0 && getWater.createdAt != "") {
            binding.pbWater.setProgressStartColor(Color.TRANSPARENT)
            binding.pbWater.setProgressEndColor(Color.TRANSPARENT)

            if(getWater.uid != "") dataManager.insertUnused(Unused(type = WATER, value = getWater.uid!!, createdAt = selectedDate.toString()))

            dataManager.deleteItem(WATER, CREATED_AT, selectedDate.toString())
         }

         resetData()
      }

      binding.ivPlus.setOnClickListener {
         if(count < 100) {
            count += 1

            binding.pbWater.setProgressStartColor(Color.parseColor("#4AC0F2"))
            binding.pbWater.setProgressEndColor(Color.parseColor("#4AC0F2"))
            binding.pbWater.max = dailyGoal.water
            binding.pbWater.progress = count

            getWater = dataManager.getWater(selectedDate.toString())

            if(getWater.createdAt == "") {
               dataManager.insertWater(Water(count = count, createdAt = selectedDate.toString()))
            }else {
               dataManager.updateInt(WATER, "count", count, "id", getWater.id)
            }

            if(getWater.uid != "") dataManager.updateInt(WATER, IS_UPDATED, 1, selectedDate.toString())

            resetData()
         }
      }

      viewModel.dateVM.observe(viewLifecycleOwner, Observer<LocalDate> {
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
}
