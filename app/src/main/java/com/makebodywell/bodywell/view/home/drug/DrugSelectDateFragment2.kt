package com.makebodywell.bodywell.view.home.drug

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.adapter.CalendarAdapter4
import com.makebodywell.bodywell.adapter.CalendarAdapter5
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentDrugSelectDateBinding
import com.makebodywell.bodywell.util.CalendarUtil.Companion.isItemClick
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateTitle
import com.makebodywell.bodywell.util.CalendarUtil.Companion.monthArray
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDays
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import com.makebodywell.bodywell.util.MainViewModel
import java.time.LocalDate

class DrugSelectDateFragment2 : Fragment(), CalendarAdapter4.OnItemListener {
    private var _binding: FragmentDrugSelectDateBinding? = null
    val binding get() = _binding!!

    private lateinit var callback: OnBackPressedCallback

    private var bundle = Bundle()

    private val viewModel: MainViewModel by activityViewModels()

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

        selectedDays.clear()
        selectedDate = LocalDate.now()
        setMonthView()

        binding.marginView.visibility = View.GONE
        binding.view.visibility = View.VISIBLE
        binding.recyclerView2.visibility = View.VISIBLE

        binding.ivBack.setOnClickListener {
            replaceFragment2(requireActivity(), DrugAddFragment(), bundle)
        }

        binding.btnCalPrev.setOnClickListener {
            selectedDate = selectedDate!!.minusMonths(1)
            isItemClick = false
            setMonthView()
        }

        binding.btnCalNext.setOnClickListener {
            selectedDate = selectedDate!!.plusMonths(1)
            isItemClick = false
            setMonthView()
        }

        binding.tvSave.setOnClickListener {
            if(binding.tvStart.text != "" && binding.tvEnd.text != "") {
                viewModel.setDrugStartDate(binding.tvStart.text.toString())
                viewModel.setDrugEndDate(binding.tvEnd.text.toString())
                replaceFragment2(requireActivity(), DrugAddFragment(), bundle)
            }else {
                Toast.makeText(activity, "입력을 확인해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setMonthView() {
        binding.tvCalTitle.text = dateTitle(selectedDate!!)
        val days = monthArray(selectedDate!!)
        val adapter = CalendarAdapter4(requireContext(), days, this)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(context, 7)
        binding.recyclerView1.layoutManager = layoutManager
        binding.recyclerView1.adapter = adapter

        setupBottom()
    }

    override fun onItemClick(position: Int, date: LocalDate?) {
        selectedDate = date
        isItemClick = true
        setMonthView()
    }

    private fun setupBottom() {
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 5)
        binding.recyclerView2.layoutManager = layoutManager
        binding.recyclerView2.requestLayout()

        adapter = CalendarAdapter5(selectedDays)
        binding.recyclerView2.adapter = adapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                replaceFragment2(requireActivity(), DrugAddFragment(), bundle)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}