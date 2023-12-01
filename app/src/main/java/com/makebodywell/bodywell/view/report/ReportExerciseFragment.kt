package com.makebodywell.bodywell.view.report

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.makebodywell.bodywell.databinding.FragmentReportExerciseBinding
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.view.home.MainFragment
import java.time.LocalDate

class ReportExerciseFragment : Fragment() {
   private var _binding: FragmentReportExerciseBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback

   private var calendarDate = LocalDate.now()

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentReportExerciseBinding.inflate(layoutInflater)

      setupView()

      settingChart1(binding.chart1)
      settingChart2(binding.chart2)

      return binding.root
   }

   private fun setupView() {
      binding.pbBody.max = 100
      binding.pbBody.progress = 50
      binding.pbFood.max = 100
      binding.pbFood.progress = 50
      binding.pbExercise.max = 100
      binding.pbExercise.progress = 50
      binding.pbDrug.max = 100
      binding.pbDrug.progress = 50

      binding.tvCalTitle.text = dateFormat(calendarDate)

      binding.pbBody.setOnClickListener {
         replaceFragment1(requireActivity(), ReportBodyFragment())
      }

      binding.pbFood.setOnClickListener {
         replaceFragment1(requireActivity(), ReportFoodFragment())
      }

      binding.pbDrug.setOnClickListener {
         replaceFragment1(requireActivity(), ReportDrugFragment())
      }

      binding.ivPrev.setOnClickListener {
         calendarDate = calendarDate!!.minusDays(1)
         binding.tvCalTitle.text = dateFormat(calendarDate)
      }

      binding.ivNext.setOnClickListener {
         calendarDate = calendarDate!!.plusDays(1)
         binding.tvCalTitle.text = dateFormat(calendarDate)
      }
   }

   private fun settingChart1(chart: CombinedChart) {
      chartCommon(chart, "workoutTime")

      val data = CombinedData()

      // lineChart 설정
      val lineData = LineData()
      val entries = ArrayList<Entry>()
      val lineList = floatArrayOf(200f, 320f, 200f, 410f, 450f, 235f, 320f)
      for (index in lineList.indices) {
         entries.add(Entry(index.toFloat(), lineList[index]))
      }

      val lineDataSet = LineDataSet(entries, "Line DataSet")
      lineDataSet.color = Color.parseColor("#BBBBBB")
      lineDataSet.lineWidth = 0.5f
      lineDataSet.setCircleColor(Color.parseColor("#D3B479"))
      lineDataSet.setDrawValues(true)
      lineDataSet.valueTextSize = 8f
      lineDataSet.valueTextColor = Color.parseColor("#BBBBBB")
      lineDataSet.axisDependency = YAxis.AxisDependency.RIGHT
      lineDataSet.setValueFormatter { value, entry, dataSetIndex, viewPortHandler ->
         var result = ""
         if (value.toInt() > 0) {
            when(value.toInt().toString().length) {
               3 -> {
                  result = if(value.toInt().toString().substring(1 until 3) == "00") {
                     value.toInt().toString().substring(0 until 1) + "시간"
                  }else {
                     value.toInt().toString().substring(0 until 1) + "시간" + value.toInt().toString().substring(1 until 3) + "분"
                  }
               }
               4 -> {
                  result = if(value.toInt().toString().substring(2 until 4) == "00") {
                     value.toInt().toString().substring(0 until 2) + "시간"
                  }else {
                     value.toInt().toString().substring(0 until 2) + "시간" + value.toInt().toString().substring(2 until 4) + "분"
                  }
               }
            }
         }
         return@setValueFormatter result
      }

      lineData.addDataSet(lineDataSet)
      data.setData(lineData)

      // barChart 설정
      val barEntries = ArrayList<BarEntry>()
      barEntries.add(BarEntry(0f, 200f))
      barEntries.add(BarEntry(1f, 320f))
      barEntries.add(BarEntry(2f, 200f))
      barEntries.add(BarEntry(3f, 410f))
      barEntries.add(BarEntry(4f, 450f))
      barEntries.add(BarEntry(5f, 235f))
      barEntries.add(BarEntry(6f, 320f))

      val barDataSet = BarDataSet(barEntries, "")
      barDataSet.color = Color.parseColor("#D3B479")
      barDataSet.valueTextSize = 0f

      val barData = BarData(barDataSet)
      barData.barWidth = 0.27f

      data.setData(barData)

      chart.data = data
      chart.invalidate()
   }

   private fun settingChart2(chart: CombinedChart) {
      chartCommon(chart, "")

      val data = CombinedData()

      // lineChart 설정
      val lineData = LineData()
      val entries = ArrayList<Entry>()
      val lineList = floatArrayOf(600f, 900f, 800f, 1200f, 1500f, 600f, 1200f)
      for (index in lineList.indices) {
         entries.add(Entry(index.toFloat(), lineList[index]))
      }

      val lineDataSet = LineDataSet(entries, "Line DataSet")
      lineDataSet.color = Color.parseColor("#BBBBBB")
      lineDataSet.lineWidth = 0.5f
      lineDataSet.setCircleColor(Color.parseColor("#8F8C6E"))
      lineDataSet.setDrawValues(true)
      lineDataSet.valueTextSize = 8f
      lineDataSet.valueTextColor = Color.parseColor("#BBBBBB")
      lineDataSet.axisDependency = YAxis.AxisDependency.RIGHT
      lineDataSet.valueFormatter = DefaultValueFormatter(0)

      lineData.addDataSet(lineDataSet)
      data.setData(lineData)

      // barChart 설정
      val barEntries = ArrayList<BarEntry>()
      barEntries.add(BarEntry(0f, 600f))
      barEntries.add(BarEntry(1f, 900f))
      barEntries.add(BarEntry(2f, 800f))
      barEntries.add(BarEntry(3f, 1200f))
      barEntries.add(BarEntry(4f, 1500f))
      barEntries.add(BarEntry(5f, 600f))
      barEntries.add(BarEntry(6f, 1200f))

      val barDataSet = BarDataSet(barEntries, "")
      barDataSet.color = Color.parseColor("#8F8C6E")
      barDataSet.valueTextSize = 0f

      val barData = BarData(barDataSet)
      barData.barWidth = 0.27f

      data.setData(barData)

      chart.data = data
      chart.invalidate()
   }

   private fun chartCommon(chart: CombinedChart, text: String) {
      chart.description.isEnabled = false
      chart.legend.isEnabled = false
      chart.setScaleEnabled(false)
      chart.isClickable = false
      chart.isHighlightPerDragEnabled = false
      chart.isHighlightPerTapEnabled = false
      chart.setExtraOffsets(15f, 15f, 15f, 10f)

      val xAxis = chart.xAxis
      xAxis.axisLineColor = Color.BLACK
      xAxis.axisLineWidth = 0.8f
      xAxis.position = XAxis.XAxisPosition.BOTTOM
      xAxis.spaceMax = 0.6f
      xAxis.spaceMin = 0.6f
      xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("7.10", "7.11","7.12", "7.13", "7.14", "7.15", "오늘"))
      xAxis.setDrawGridLines(false)

      val rightAxis = chart.axisRight
      rightAxis.axisMinimum = 0f
      rightAxis.isEnabled = false

      val leftAxis = chart.axisLeft
      leftAxis.axisLineColor = Color.BLACK
      leftAxis.axisLineWidth = 0.8f
      leftAxis.gridColor = Color.parseColor("#bbbbbb")
      leftAxis.enableGridDashedLine(10f, 15f, 0f)
      leftAxis.axisMinimum = 0f

      if(text == "workoutTime") {
         leftAxis.setValueFormatter { value, axis ->
            var result = ""
            if (value.toInt() > 0) {
               when(value.toInt().toString().length) {
                  3 -> result = value.toInt().toString().substring(0 until 1) + "시간"
                  4 -> result = value.toInt().toString().substring(0 until 2) + "시간"
               }
            }else {
               result = "0"
            }
            return@setValueFormatter result
         }
      }
   }

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment1(requireActivity(), MainFragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}
