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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.f4b6a3.uuid.UuidCreator
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.adapter.WaterAdapter
import kr.bodywell.android.databinding.FragmentWaterBinding
import kr.bodywell.android.model.Constants.WATER
import kr.bodywell.android.model.Goal
import kr.bodywell.android.model.Water
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil
import kr.bodywell.android.util.CustomUtil.dateTimeToIso
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.view.MainViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Calendar

class WaterFragment : Fragment() {
   private var _binding: FragmentWaterBinding? = null
   private val binding get() = _binding!!

   private val viewModel: MainViewModel by activityViewModels()
   private var adapter: WaterAdapter? = null
   private var getGoal = Goal()
   private var getWater = Water()
   private var volume = 200
   private var count = 0
   private var uuid = UuidCreator.getTimeOrderedEpoch()

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentWaterBinding.inflate(layoutInflater)

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

               if(getGoal.id == "") {
                  val uuid = UuidCreator.getTimeOrderedEpoch()
                  powerSync.insertGoal(Goal(id = uuid.toString(), waterAmountOfCup = volume, waterIntake = goal, date = selectedDate.toString(),
                     createdAt = LocalDateTime.now().toString(), updatedAt = LocalDateTime.now().toString()))
                  getGoal = powerSync.getGoal(selectedDate.toString())
               }else {
                  powerSync.updateWaterGoal(Goal(id = getGoal.id, waterAmountOfCup = volume, waterIntake = goal))
               }

               if(getWater.date == "") {
                  val dateTimeFormat = dateTimeToIso(Calendar.getInstance())
                  powerSync.insertWater(Water(id = uuid.toString(), mL = volume, count = count, date = selectedDate.toString(),
                     createdAt = dateTimeFormat, updatedAt = dateTimeFormat))
               }else {
                  powerSync.updateWater(Water(id = getWater.id, mL = volume, count = count))
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
         if(count > 0) {
            count -= 1
            binding.rv.visibility = View.VISIBLE
            binding.pbWater.setProgressStartColor(resources.getColor(R.color.water))
            binding.pbWater.setProgressEndColor(resources.getColor(R.color.water))
            binding.pbWater.progress = count

            lifecycleScope.launch {
               getWater = powerSync.getWater(selectedDate.toString())

               if(getWater.id == "") {
                  val dateTimeFormat = dateTimeToIso(Calendar.getInstance())
                  powerSync.insertWater(Water(id = uuid.toString(), mL = volume, count = count, date = selectedDate.toString(),
                     createdAt = dateTimeFormat, updatedAt = dateTimeFormat))
               }else {
                  powerSync.updateWater(Water(id = getWater.id, mL = volume, count = count))
               }

               if(count == 0) binding.rv.visibility = View.GONE

               if(count == 0 && getWater.id != "") {
                  binding.pbWater.setProgressStartColor(Color.TRANSPARENT)
                  binding.pbWater.setProgressEndColor(Color.TRANSPARENT)
                  powerSync.deleteItem(WATER, "date", selectedDate.toString())
                  uuid = UuidCreator.getTimeOrderedEpoch()
               }
            }

            resetData()
         }
      }

      binding.ivPlus.setOnClickListener {
         if(count <= 100) {
            count += 1

            binding.rv.visibility = View.VISIBLE
            binding.pbWater.setProgressStartColor(resources.getColor(R.color.water))
            binding.pbWater.setProgressEndColor(resources.getColor(R.color.water))
            binding.pbWater.max = getGoal.waterIntake
            binding.pbWater.progress = count

            lifecycleScope.launch {
               getWater = powerSync.getWater(selectedDate.toString())

               if(getWater.id == "") {
                  val dateTimeFormat = dateTimeToIso(Calendar.getInstance())
                  powerSync.insertWater(Water(id = uuid.toString(), mL = volume, count = count, date = selectedDate.toString(),
                     createdAt = dateTimeFormat, updatedAt = dateTimeFormat))
               }else {
                  powerSync.updateWater(Water(id = getWater.id, mL = volume, count = count))
               }
            }

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
      lifecycleScope.launch {
         getGoal = powerSync.getGoal(selectedDate.toString())
         getWater = powerSync.getWater(selectedDate.toString())
         powerSync.deleteDuplicate(WATER, "date", selectedDate.toString(), getWater.id)

         volume = if(getWater.mL > 0) getWater.mL else 200
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

         resetData()
      }
   }

   private fun resetData() {
      binding.tvGoal.text = "${getGoal.waterIntake}잔/${getGoal.waterIntake * volume}ml"
      binding.tvCount.text = "${count}잔"
      binding.tvUnit.text = "(${count * volume}ml)"
      binding.tvMl.text = "${volume}ml"
      binding.tvIntake.text = "${count}잔/${count * volume}ml"

      val remain = getGoal.waterIntake - count
      if(remain > 0) binding.tvRemain.text = "${remain}잔/${remain * volume}ml" else binding.tvRemain.text = "0잔/0ml"

      val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 5)
      binding.rv.layoutManager = layoutManager
      adapter = WaterAdapter(count)
      binding.rv.adapter = adapter
   }
}