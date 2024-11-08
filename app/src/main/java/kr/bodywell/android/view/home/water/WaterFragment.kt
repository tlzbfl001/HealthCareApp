package kr.bodywell.android.view.home.water

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.bodywell.android.R
import kr.bodywell.android.adapter.WaterAdapter
import kr.bodywell.android.databinding.FragmentWaterBinding
import kr.bodywell.android.model.Goal
import kr.bodywell.android.model.Water
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.view.MainViewModel
import java.time.LocalDate

class WaterFragment : Fragment() {
   private var _binding: FragmentWaterBinding? = null
   private val binding get() = _binding!!

   private val viewModel: MainViewModel by activityViewModels()
//   private lateinit var dataManager: DataManager
   private var adapter: WaterAdapter? = null
   private var getGoal = Goal()
   private var getWater = Water()
   private var volume = 200
   private var count = 0

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentWaterBinding.inflate(layoutInflater)

//      dataManager = DataManager(activity)
//      dataManager.open()

      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_water_input)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      val etGoal = dialog.findViewById<EditText>(R.id.etGoal)
      val etVolume = dialog.findViewById<EditText>(R.id.etVolume)
      val btnSave = dialog.findViewById<CardView>(R.id.btnSave)

      btnSave.setOnClickListener {
         if(etGoal.text.toString() != "" && etGoal.text.toString().toInt() > 100) {
            Toast.makeText(requireActivity(), "목표 섭취물은 100을 넘을 수 없습니다.", Toast.LENGTH_SHORT).show()
         }else {
            lifecycleScope.launch {
               val goal = if(etGoal.text.toString() != "") etGoal.text.toString().toInt() else 5
               volume = if(etVolume.text.toString() != "") etVolume.text.toString().toInt() else 200

               if(getGoal.createdAt == "") {
                  powerSync.insertGoal(Goal(waterAmountOfCup = volume, waterIntake = goal, date = selectedDate.toString()))
                  getGoal = powerSync.getGoal(selectedDate.toString())
               }else {
                  powerSync.updateInt("goals", "water_amount_of_cup", volume, getGoal.id)
                  powerSync.updateInt("goals", "water_intake", goal, getGoal.id)
               }

               if(getWater.date == "") {
                  powerSync.insertWater(Water(count = count, date = selectedDate.toString()))
               }else {
                  powerSync.updateWater(Water(id = getWater.id, mL = volume, count = count, date = selectedDate.toString()))
               }

               dailyView()
               dialog.dismiss()
            }
         }
      }

      binding.clGoal.setOnClickListener {
         dialog.show()
      }

      binding.ivMinus.setOnClickListener {
         lifecycleScope.launch {
            getWater = powerSync.getWater(selectedDate.toString())

            if(count > 0) {
               count -= 1
               binding.rv.visibility = View.VISIBLE
               binding.pbWater.setProgressStartColor(resources.getColor(R.color.water))
               binding.pbWater.setProgressEndColor(resources.getColor(R.color.water))
               binding.pbWater.progress = count

               getWater = powerSync.getWater(selectedDate.toString())

               if(getWater.date == "") {
                  powerSync.insertWater(Water(count = count, date = selectedDate.toString()))
               }else {
                  powerSync.updateWater(Water(id = getWater.id, mL = getWater.mL, count = count, date = selectedDate.toString()))
               }
            }

            if(count == 0) binding.rv.visibility = View.GONE

            if(count == 0 && getWater.date != "") {
               binding.pbWater.setProgressStartColor(Color.TRANSPARENT)
               binding.pbWater.setProgressEndColor(Color.TRANSPARENT)

               powerSync.deleteItem("water", "date", selectedDate.toString())
            }

            resetData()
         }
      }

      binding.ivPlus.setOnClickListener {
         lifecycleScope.launch {
            if(count <= 100) {
               count += 1

               binding.rv.visibility = View.VISIBLE
               binding.pbWater.setProgressStartColor(resources.getColor(R.color.water))
               binding.pbWater.setProgressEndColor(resources.getColor(R.color.water))
               binding.pbWater.max = getGoal.waterIntake
               binding.pbWater.progress = count

               getWater = powerSync.getWater(selectedDate.toString())

               if(getWater.date == "") {
                  powerSync.insertWater(Water(count = count, date = selectedDate.toString()))
               }else {
                  powerSync.updateWater(Water(id = getWater.id, mL = getWater.mL, count = count, date = selectedDate.toString()))
               }

               resetData()
            }
         }
      }

      viewModel.dateVM.observe(viewLifecycleOwner, Observer<LocalDate> {
         dailyView()
      })

      dailyView()

      return binding.root
   }

   private fun dailyView() {
      lifecycleScope.launch {
         getGoal = powerSync.getGoal(selectedDate.toString())
         getWater = powerSync.getWater(selectedDate.toString())
         Log.d(CustomUtil.TAG, "selectedDate: $selectedDate")
         Log.d(CustomUtil.TAG, "getWater: $getWater")
         volume = getWater.mL
         count = getWater.count

         if(count > 0) {
            binding.rv.visibility = View.VISIBLE
            binding.pbWater.setProgressStartColor(resources.getColor(R.color.water))
            binding.pbWater.setProgressEndColor(resources.getColor(R.color.water))
            binding.pbWater.max = getGoal.waterIntake
            binding.pbWater.progress = count
         }else {
            binding.rv.visibility = View.GONE
            binding.pbWater.setProgressStartColor(Color.TRANSPARENT)
            binding.pbWater.setProgressEndColor(Color.TRANSPARENT)
         }

         binding.tvIntake.text = "${count}잔/${count * volume}ml"
         binding.tvMl.text = "${volume}ml"
         binding.tvGoal.text = "${getGoal.waterIntake}잔/${getGoal.waterIntake * volume}ml"
         binding.tvCount.text = "${count}잔"
         binding.tvUnit.text = "(${count * volume}ml)"

         val remain = getGoal.waterIntake - count
         if(remain > 0) binding.tvRemain.text = "${remain}잔/${remain * volume}ml" else binding.tvRemain.text = "0잔/0ml"

         val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 5)
         binding.rv.layoutManager = layoutManager
         adapter = WaterAdapter(count)
         binding.rv.adapter = adapter
      }
   }

   private fun resetData() {
      binding.tvCount.text = "${count}잔"
      binding.tvUnit.text = "(${count * volume}ml)"
      binding.tvIntake.text = "${count}잔/${count * volume}ml"

      val remain = getGoal.waterIntake - count
      if(remain > 0) binding.tvRemain.text = "${remain}잔/${remain * volume}ml" else binding.tvRemain.text = "0잔/0ml"

      adapter = WaterAdapter(count)
      binding.rv.adapter = adapter
   }
}
