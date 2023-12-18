package com.makebodywell.bodywell.view.home.drug

import android.app.Dialog
<<<<<<< HEAD
=======
<<<<<<< HEAD
=======
import android.content.Context
>>>>>>> e5f18d1dfc2f1449657445a53cc6b46714d681ac
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
<<<<<<< HEAD
=======
<<<<<<< HEAD
=======
import androidx.activity.OnBackPressedCallback
>>>>>>> e5f18d1dfc2f1449657445a53cc6b46714d681ac
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.adapter.DrugAdapter1
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentDrugBinding
import com.makebodywell.bodywell.model.DailyData
import com.makebodywell.bodywell.model.Drug
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.view.home.MainFragment
import com.makebodywell.bodywell.view.home.body.BodyFragment
import com.makebodywell.bodywell.view.home.exercise.ExerciseFragment
import com.makebodywell.bodywell.view.home.food.FoodFragment
import com.makebodywell.bodywell.view.home.sleep.SleepFragment
import com.makebodywell.bodywell.view.home.water.WaterFragment
import java.time.LocalDate

class DrugFragment : Fragment() {
   private var _binding: FragmentDrugBinding? = null
   val binding get() = _binding!!

   private var calendarDate = LocalDate.now()

   private var dataManager: DataManager? = null
   private var adapter: DrugAdapter1? = null
   private val itemList = ArrayList<Drug>()
<<<<<<< HEAD
   private var getDrugDaily = ArrayList<Drug>()
   private var getDailyData = DailyData()

=======
<<<<<<< HEAD
   private var getDrugDaily = ArrayList<Drug>()
   private var getDailyData = DailyData()

=======

   private var getDrugDaily = ArrayList<Drug>()
>>>>>>> e5f18d1dfc2f1449657445a53cc6b46714d681ac
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
   private var checkedCount = 0

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentDrugBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      initView()
      setupGoal()
<<<<<<< HEAD
      recordView()
=======
<<<<<<< HEAD
      recordView()
=======
      setupList()
>>>>>>> e5f18d1dfc2f1449657445a53cc6b46714d681ac
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130

      return binding.root
   }

   private fun initView() {
      binding.tvDate.text = dateFormat(calendarDate)

<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
      // 목표 설정
      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_input)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
      val et = dialog.findViewById<TextView>(R.id.et)
      val tvUnit = dialog.findViewById<TextView>(R.id.tvUnit)
      val btnSave = dialog.findViewById<CardView>(R.id.btnSave)
      tvTitle.text = "약복용 / 하루 복용 횟수"
      tvUnit.text = "회"
      btnSave.setCardBackgroundColor(Color.parseColor("#8F6FF5"))

      btnSave.setOnClickListener {
         if(et.text.toString().trim() != "") {
            if(getDailyData.regDate == "") {
               dataManager?.insertDailyData(DailyData(drugGoal = et.text.toString().toInt(), regDate = calendarDate.toString()))
            }else {
               dataManager?.updateDrugGoal(DailyData(drugGoal = et.text.toString().toInt(), regDate = calendarDate.toString()))
            }

            itemList.clear()

            for(i in 0 until getDrugDaily.size) {
               val getDrugTime = dataManager!!.getDrugTime(getDrugDaily[i].id)
               val getDrugCheckCount = dataManager!!.getDrugCheckCount(getDrugDaily[i].id)
               checkedCount += getDrugCheckCount.count

               for(j in 0 until getDrugTime.size) {
                  itemList.add(Drug(id = getDrugDaily[i].id, type = calendarDate.toString(), name = getDrugDaily[i].name, amount = getDrugDaily[i].amount,
                     unit = getDrugDaily[i].unit, startDate = getDrugTime[j].name, endDate = checkedCount.toString(), count = getDrugTime[j].count))
               }
            }

            adapter!!.notifyDataSetChanged()
            binding.tvGoal.text = "${et.text}회"
         }
         dialog.dismiss()
      }

      binding.cvGoal.setOnClickListener {
         dialog.show()
      }
<<<<<<< HEAD
=======
=======
      getDrugDaily = dataManager!!.getDrugDaily()
>>>>>>> e5f18d1dfc2f1449657445a53cc6b46714d681ac
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130

      binding.clBack.setOnClickListener {
         replaceFragment1(requireActivity(), MainFragment())
      }

      binding.ivPrev.setOnClickListener {
         calendarDate = calendarDate!!.minusDays(1)
         binding.tvDate.text = dateFormat(calendarDate)
         setupGoal()
<<<<<<< HEAD
         recordView()
=======
<<<<<<< HEAD
         recordView()
=======
         setupList()
>>>>>>> e5f18d1dfc2f1449657445a53cc6b46714d681ac
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
      }

      binding.ivNext.setOnClickListener {
         calendarDate = calendarDate!!.plusDays(1)
         binding.tvDate.text = dateFormat(calendarDate)
         setupGoal()
<<<<<<< HEAD
         recordView()
=======
<<<<<<< HEAD
         recordView()
=======
         setupList()
>>>>>>> e5f18d1dfc2f1449657445a53cc6b46714d681ac
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
      }

      binding.clRecord.setOnClickListener {
         replaceFragment1(requireActivity(), DrugRecordFragment())
      }

      binding.cvFood.setOnClickListener {
         replaceFragment1(requireActivity(), FoodFragment())
      }

      binding.cvWater.setOnClickListener {
         replaceFragment1(requireActivity(), WaterFragment())
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

   private fun setupGoal() {
      binding.tvGoal.text = "0회"
      binding.tvRemain.text = "0회"

<<<<<<< HEAD
      getDailyData = dataManager!!.getDailyData(calendarDate.toString())

=======
<<<<<<< HEAD
      getDailyData = dataManager!!.getDailyData(calendarDate.toString())
=======
      val getDailyData = dataManager!!.getDailyData(calendarDate.toString())
>>>>>>> e5f18d1dfc2f1449657445a53cc6b46714d681ac
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
      val goal = getDailyData.drugGoal
      if(goal != 0) {
         binding.pbDrug.max = goal
         binding.tvGoal.text = "${goal}회"
      }
<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
   }

   private fun recordView() {
      itemList.clear()
      checkedCount = 0

      getDrugDaily = dataManager!!.getDrugDaily(calendarDate.toString())

<<<<<<< HEAD
=======
=======

      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_input)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
      val et = dialog.findViewById<TextView>(R.id.et)
      val tvUnit = dialog.findViewById<TextView>(R.id.tvUnit)
      val btnSave = dialog.findViewById<CardView>(R.id.btnSave)
      tvTitle.text = "약복용 / 하루 복용 횟수"
      tvUnit.text = "회"
      btnSave.setCardBackgroundColor(Color.parseColor("#8F6FF5"))

      btnSave.setOnClickListener {
         if(et.text.toString().trim() != "") {
            if(getDailyData.regDate == "") {
               dataManager?.insertDailyData(DailyData(drugGoal = et.text.toString().toInt(), regDate = calendarDate.toString()))
            }else {
               dataManager?.updateDrugGoal(DailyData(drugGoal = et.text.toString().toInt(), regDate = calendarDate.toString()))
            }

            itemList.clear()
            for(i in 0 until getDrugDaily.size) {
               val getDrugTime = dataManager!!.getDrugTime(getDrugDaily[i].id)
               val getDrugCheckCount = dataManager!!.getDrugCheckCount(getDrugDaily[i].id)
               checkedCount += getDrugCheckCount.count
               for(j in 0 until getDrugTime.size) {
                  itemList.add(Drug(id = getDrugDaily[i].id, type = calendarDate.toString(), name = getDrugDaily[i].name, amount = getDrugDaily[i].amount,
                     unit = getDrugDaily[i].unit, startDate = getDrugTime[j].name, endDate = checkedCount.toString(), count = getDrugTime[j].count))
               }
            }
            adapter!!.notifyDataSetChanged()
            binding.tvGoal.text = "${et.text}회"
         }
         dialog.dismiss()
      }

      binding.cvGoal.setOnClickListener {
         dialog.show()
      }
   }

   private fun setupList() {
      var checkedCount = 0
>>>>>>> e5f18d1dfc2f1449657445a53cc6b46714d681ac
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
      for(i in 0 until getDrugDaily.size) {
         val getDrugTime = dataManager!!.getDrugTime(getDrugDaily[i].id)
         val getDrugCheckCount = dataManager!!.getDrugCheckCount(getDrugDaily[i].id)
         checkedCount += getDrugCheckCount.count
<<<<<<< HEAD

=======
<<<<<<< HEAD

=======
>>>>>>> e5f18d1dfc2f1449657445a53cc6b46714d681ac
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
         for(j in 0 until getDrugTime.size) {
            itemList.add(Drug(id = getDrugDaily[i].id, type = calendarDate.toString(), name = getDrugDaily[i].name, amount = getDrugDaily[i].amount, unit = getDrugDaily[i].unit,
               startDate = getDrugTime[j].name, endDate = checkedCount.toString(), count = getDrugTime[j].count))
         }
      }

      adapter = DrugAdapter1(requireActivity(), itemList)
      binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
      binding.recyclerView.requestLayout()
      binding.recyclerView.adapter = adapter
   }
}