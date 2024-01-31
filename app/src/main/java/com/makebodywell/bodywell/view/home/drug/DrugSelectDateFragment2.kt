package com.makebodywell.bodywell.view.home.drug

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.adapter.CalendarAdapter4
import com.makebodywell.bodywell.adapter.CalendarAdapter5
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentDrugSelectDateBinding
import com.makebodywell.bodywell.util.CalendarUtil.Companion.calendarTitle
import com.makebodywell.bodywell.util.CalendarUtil.Companion.isItemClick
import com.makebodywell.bodywell.util.CalendarUtil.Companion.monthArray
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CustomUtil.Companion.drugDateList
import com.makebodywell.bodywell.util.CustomUtil.Companion.drugEndDate
import com.makebodywell.bodywell.util.CustomUtil.Companion.drugStartDate
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import java.time.LocalDate

class DrugSelectDateFragment2 : Fragment(), CalendarAdapter4.OnItemListener {
    private var _binding: FragmentDrugSelectDateBinding? = null
    val binding get() = _binding!!

    private var bundle = Bundle()

    private var dataManager: DataManager? = null

    private var adapter: CalendarAdapter5? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDrugSelectDateBinding.inflate(layoutInflater)

        dataManager = DataManager(activity)
        dataManager!!.open()

        initView()

        return binding.root
    }

    private fun initView() {
        bundle.putString("data", "DrugSelectDateFragment")

        selectedDate = LocalDate.now()
        drugDateList.clear()
        setMonthView()

        binding.marginView.visibility = View.GONE
        binding.view.visibility = View.VISIBLE
        binding.dateView.visibility = View.VISIBLE

        binding.ivBack.setOnClickListener {
            replaceFragment2(requireActivity(), DrugAddFragment(), bundle)
        }

        binding.btnCalPrev.setOnClickListener {
            selectedDate = selectedDate.minusMonths(1)
            isItemClick = false
            setMonthView()
        }

        binding.btnCalNext.setOnClickListener {
            selectedDate = selectedDate.plusMonths(1)
            isItemClick = false
            setMonthView()
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
    }

    private fun setMonthView() {
        binding.tvCalTitle.text = calendarTitle()
        val days = monthArray()
        val adapter = CalendarAdapter4(requireContext(), this, days)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(context, 7)

        binding.calenderView.layoutManager = layoutManager
        binding.calenderView.adapter = adapter

        setupBottom()
    }

    private fun setupBottom() {
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 5)
        binding.dateView.layoutManager = layoutManager

        adapter = CalendarAdapter5(drugDateList)
        binding.dateView.adapter = adapter
    }

    override fun onItemClick(position: Int, date: LocalDate?) {
        selectedDate = date!!
        isItemClick = true
        setMonthView()
    }
}