package com.makebodywell.bodywell.view.home.drug

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.adapter.CalendarAdapter3
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentDrugSelectDateBinding
import com.makebodywell.bodywell.util.CalendarUtil.Companion.calendarTitle
import com.makebodywell.bodywell.util.CalendarUtil.Companion.monthArray
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CustomUtil.Companion.drugEndDate
import com.makebodywell.bodywell.util.CustomUtil.Companion.drugStartDate
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import java.time.LocalDate

class DrugSelectDateFragment1 : Fragment(), CalendarAdapter3.OnItemListener {
   private var _binding: FragmentDrugSelectDateBinding? = null
   val binding get() = _binding!!

   private var bundle = Bundle()

   private var dataManager: DataManager? = null

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentDrugSelectDateBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      selectedDate = LocalDate.now()
      setMonthView()

      binding.btnCalPrev.setOnClickListener {
         selectedDate = selectedDate.minusMonths(1)
         setMonthView()
      }

      binding.btnCalNext.setOnClickListener {
         selectedDate = selectedDate.plusMonths(1)
         setMonthView()
      }

      bundle.putString("data", "DrugSelectDateFragment")

      binding.ivBack.setOnClickListener {
         replaceFragment2(requireActivity(), DrugAddFragment(), bundle)
      }

      binding.tvSave.setOnClickListener {
         if(binding.tvStart.text != "" && binding.tvEnd.text != "") {
            drugStartDate = binding.tvStart.text.toString()
            drugEndDate = binding.tvEnd.text.toString()

            replaceFragment2(requireActivity(), DrugAddFragment(), bundle)
         }else {
            Toast.makeText(activity, "입력을 확인해주세요.", Toast.LENGTH_SHORT).show()
         }
      }

      return binding.root
   }

   private fun setMonthView() {
      binding.tvCalTitle.text = calendarTitle()
      val days = monthArray()
      val adapter = CalendarAdapter3(requireContext(), this, days)
      val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(context, 7)

      binding.calenderView.layoutManager = layoutManager
      binding.calenderView.adapter = adapter
   }

   override fun onItemClick(position: Int, date: LocalDate?) {
      selectedDate = date!!
      setMonthView()
   }
}