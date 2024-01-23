package com.makebodywell.bodywell.view.report

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_EXERCISE
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentReportExerciseBinding
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
import com.makebodywell.bodywell.util.CalendarUtil.Companion.monthArray2
import com.makebodywell.bodywell.util.CalendarUtil.Companion.monthFormat
import com.makebodywell.bodywell.util.CalendarUtil.Companion.weekArray
import com.makebodywell.bodywell.util.CalendarUtil.Companion.weekFormat
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import java.text.SimpleDateFormat
import java.time.LocalDate

class ReportExerciseFragment : Fragment() {
   private var _binding: FragmentReportExerciseBinding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null

   private var calendarDate = LocalDate.now()
   private var dateType = 0

   private val format1 = SimpleDateFormat("yyyy-MM-dd")
   private val format2 = SimpleDateFormat("M.dd")

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentReportExerciseBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      binding.tvCalTitle.text = dateFormat(calendarDate)

      binding.clMenu1.setOnClickListener {
         replaceFragment1(requireActivity(), ReportBodyFragment())
      }

      binding.clMenu2.setOnClickListener {
         replaceFragment1(requireActivity(), ReportFoodFragment())
      }

      binding.clMenu4.setOnClickListener {
         replaceFragment1(requireActivity(), ReportDrugFragment())
      }

      binding.ivPrev.setOnClickListener {
         when(dateType) {
            0->{
               calendarDate = calendarDate!!.minusDays(1)
               binding.tvCalTitle.text = dateFormat(calendarDate)
               dailyView()
            }
            1->{
               calendarDate = calendarDate!!.minusWeeks(1)
               binding.tvCalTitle.text = weekFormat(calendarDate)
               weeklyView()
            }
            2->{
               calendarDate = calendarDate!!.minusMonths(1)
               binding.tvCalTitle.text = monthFormat(calendarDate)
               monthlyView()
            }
         }
      }

      binding.ivNext.setOnClickListener {
         when(dateType) {
            0->{
               calendarDate = calendarDate!!.plusDays(1)
               binding.tvCalTitle.text = dateFormat(calendarDate)
               dailyView()
            }
            1->{
               calendarDate = calendarDate!!.plusWeeks(1)
               binding.tvCalTitle.text = weekFormat(calendarDate)
               weeklyView()
            }
            2->{
               calendarDate = calendarDate!!.plusMonths(1)
               binding.tvCalTitle.text = monthFormat(calendarDate)
               monthlyView()
            }
         }
      }

      binding.tvDaily.setOnClickListener {
         binding.tvCalTitle.text = dateFormat(calendarDate)
         dailyView()
      }

      binding.tvWeekly.setOnClickListener {
         binding.tvCalTitle.text = weekFormat(calendarDate)
         weeklyView()
      }

      binding.tvMonthly.setOnClickListener {
         binding.tvCalTitle.text = monthFormat(calendarDate)
         monthlyView()
      }

      dailyView()

      return binding.root
   }

   private fun dailyView() {
      binding.tvDaily.setBackgroundResource(R.drawable.rec_5_purple)
      binding.tvDaily.setTextColor(Color.WHITE)
      binding.tvWeekly.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvWeekly.setTextColor(Color.BLACK)
      binding.tvMonthly.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvMonthly.setTextColor(Color.BLACK)
      dateType = 0

      val getDates = dataManager!!.getDates(TABLE_EXERCISE)
      if(getDates.size > 0) {
         binding.chart1.visibility = View.VISIBLE
         binding.tvEmpty1.visibility = View.GONE
         binding.chart2.visibility = View.VISIBLE
         binding.tvEmpty2.visibility = View.GONE
         settingChart1(binding.chart1, getDates)
         settingChart2(binding.chart2, getDates)
      }else {
         binding.chart1.visibility = View.GONE
         binding.tvEmpty1.visibility = View.VISIBLE
         binding.chart2.visibility = View.GONE
         binding.tvEmpty2.visibility = View.VISIBLE
      }

      countView(getDates)
   }

   private fun weeklyView() {
      binding.tvDaily.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvDaily.setTextColor(Color.BLACK)
      binding.tvWeekly.setBackgroundResource(R.drawable.rec_5_purple)
      binding.tvWeekly.setTextColor(Color.WHITE)
      binding.tvMonthly.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvMonthly.setTextColor(Color.BLACK)
      dateType = 1

      val weekArray = weekArray(calendarDate)
      val getDates = dataManager!!.getDates(TABLE_EXERCISE, weekArray[0].toString(), weekArray[6].toString())
      if(getDates.size > 0) {
         binding.chart1.visibility = View.VISIBLE
         binding.tvEmpty1.visibility = View.GONE
         binding.chart2.visibility = View.VISIBLE
         binding.tvEmpty2.visibility = View.GONE
         settingChart1(binding.chart1, getDates)
         settingChart2(binding.chart2, getDates)
      }else {
         binding.chart1.visibility = View.GONE
         binding.tvEmpty1.visibility = View.VISIBLE
         binding.chart2.visibility = View.GONE
         binding.tvEmpty2.visibility = View.VISIBLE
      }

      countView(getDates)
   }

   private fun monthlyView() {
      binding.tvDaily.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvDaily.setTextColor(Color.BLACK)
      binding.tvWeekly.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvWeekly.setTextColor(Color.BLACK)
      binding.tvMonthly.setBackgroundResource(R.drawable.rec_5_purple)
      binding.tvMonthly.setTextColor(Color.WHITE)
      dateType = 2

      val monthArray = monthArray2(calendarDate)
      val getDates = dataManager!!.getDates(TABLE_EXERCISE, monthArray[0].toString(), monthArray[monthArray.size-1].toString())
      if(getDates.size > 0) {
         binding.chart1.visibility = View.VISIBLE
         binding.tvEmpty1.visibility = View.GONE
         binding.chart2.visibility = View.VISIBLE
         binding.tvEmpty2.visibility = View.GONE
         settingChart1(binding.chart1, getDates)
         settingChart2(binding.chart2, getDates)
      }else {
         binding.chart1.visibility = View.GONE
         binding.tvEmpty1.visibility = View.VISIBLE
         binding.chart2.visibility = View.GONE
         binding.tvEmpty2.visibility = View.VISIBLE
      }

      countView(getDates)
   }

   private fun settingChart1(chart: CombinedChart, getData: ArrayList<String>) {
      chart.data = null
      chart.fitScreen()
      chart.xAxis.valueFormatter = null
      chart.clear()

      val data = CombinedData()
      val lineData = LineData()
      var xVal = arrayOf<String>()
      var lineList = floatArrayOf()
      val entries = ArrayList<Entry>()
      val barEntries = ArrayList<BarEntry>()

      for(i in 0 until getData.size){
         var total = 0f
         xVal += format2.format(format1.parse(getData[i])!!)
         val getExercise = dataManager!!.getExercise(getData[i])
         for(j in 0 until getExercise.size) {
            total += getExercise[j].workoutTime.toFloat()
         }
         lineList += total
         barEntries.add(BarEntry(i.toFloat(), total))
      }

      for (index in lineList.indices) {
         entries.add(Entry(index.toFloat(), lineList[index]))
      }

      val lineDataSet = LineDataSet(entries, "Line DataSet")
      lineDataSet.color = Color.parseColor("#BBBBBB")
      lineDataSet.lineWidth = 0.5f
      lineDataSet.setDrawCircles(false)
      lineDataSet.setDrawValues(true)
      lineDataSet.valueTextSize = 8f
      lineDataSet.valueTextColor = Color.parseColor("#BBBBBB")
      lineDataSet.axisDependency = YAxis.AxisDependency.RIGHT
      lineDataSet.valueFormatter = XValueFormatter()

      lineData.addDataSet(lineDataSet)
      data.setData(lineData)

      val barDataSet = BarDataSet(barEntries, "")
      barDataSet.color = Color.parseColor("#D3B479")
      barDataSet.valueTextSize = 0f

      val barData = BarData(barDataSet)
      barData.barWidth = 0.27f

      data.setData(barData)

      chart.data = data
      chart.invalidate()
      chart.setVisibleXRangeMaximum(7f)
      chart.isDragXEnabled = true

      chartCommon(chart, xVal, 1)
   }

   private fun settingChart2(chart: CombinedChart, getData: ArrayList<String>) {
      chart.data = null
      chart.fitScreen()
      chart.xAxis.valueFormatter = null
      chart.clear()

      val data = CombinedData()
      val lineData = LineData()
      var xVal = arrayOf<String>()
      var lineList = floatArrayOf()
      val entries = ArrayList<Entry>()
      val barEntries = ArrayList<BarEntry>()

      for(i in 0 until getData.size){
         var total = 0f
         xVal += format2.format(format1.parse(getData[i])!!)
         val getExercise = dataManager!!.getExercise(getData[i])
         for(j in 0 until getExercise.size) {
            total += getExercise[j].calories.toFloat()
         }
         lineList += total
         barEntries.add(BarEntry(i.toFloat(), total))
      }

      for (index in lineList.indices) {
         entries.add(Entry(index.toFloat(), lineList[index]))
      }

      val lineDataSet = LineDataSet(entries, "Line DataSet")
      lineDataSet.color = Color.parseColor("#BBBBBB")
      lineDataSet.lineWidth = 0.5f
      lineDataSet.setDrawCircles(false)
      lineDataSet.setDrawValues(true)
      lineDataSet.valueTextSize = 8f
      lineDataSet.valueTextColor = Color.parseColor("#BBBBBB")
      lineDataSet.axisDependency = YAxis.AxisDependency.RIGHT

      lineData.addDataSet(lineDataSet)
      data.setData(lineData)

      val barDataSet = BarDataSet(barEntries, "")
      barDataSet.color = Color.parseColor("#8F8C6E")
      barDataSet.valueTextSize = 0f

      val barData = BarData(barDataSet)
      barData.barWidth = 0.27f

      data.setData(barData)

      chart.data = data
      chart.invalidate()
      chart.setVisibleXRangeMaximum(7f)
      chart.isDragXEnabled = true

      chartCommon(chart, xVal, 2)
   }

   private fun chartCommon(chart: CombinedChart, xVal: Array<String>, type: Int) {
      chart.description.isEnabled = false
      chart.legend.isEnabled = false
      chart.setScaleEnabled(false)
      chart.isClickable = false
      chart.isHighlightPerDragEnabled = false
      chart.isHighlightPerTapEnabled = false
      chart.setExtraOffsets(12f, 15f, 15f, 10f)

      val xAxis = chart.xAxis
      xAxis.axisLineColor = Color.BLACK
      xAxis.axisLineWidth = 0.8f
      xAxis.position = XAxis.XAxisPosition.BOTTOM
      xAxis.spaceMax = 0.6f
      xAxis.spaceMin = 0.6f
      xAxis.valueFormatter = IndexAxisValueFormatter(xVal)
      xAxis.setDrawGridLines(false)
      xAxis.isGranularityEnabled = true

      val rightAxis = chart.axisRight
      rightAxis.axisMinimum = 0f
      rightAxis.isEnabled = false

      val leftAxis = chart.axisLeft
      leftAxis.axisLineColor = Color.BLACK
      leftAxis.axisLineWidth = 0.8f
      leftAxis.gridColor = Color.parseColor("#bbbbbb")
      leftAxis.enableGridDashedLine(10f, 15f, 0f)
      leftAxis.axisMinimum = 0f
      if(type == 1) {
         leftAxis.valueFormatter = LeftAxisFormatter()
      }
   }

   class XValueFormatter : IValueFormatter {
      override fun getFormattedValue(
         value: Float,
         entry: Entry,
         dataSetIndex: Int,
         viewPortHandler: ViewPortHandler
      ): String {
         val remain = value.toInt() % 60
         val result = if(value.toInt() < 60) {
            value.toInt().toString() + "분"
         }else {
            if(remain == 0) {
               "${value.toInt() / 60}시간"
            }else {
               "${value.toInt() / 60}시간 ${remain}분"
            }
         }
         return result
      }
   }

   class LeftAxisFormatter : IAxisValueFormatter {
      override fun getFormattedValue(value: Float, axis: AxisBase?): String {
         return "${value.toInt()}분"
      }
   }

   private fun countView(data: ArrayList<String>) {
      var running = 0
      var soccer = 0
      var yoga = 0
      var basketball = 0

      for(i in 0 until data.size){
         val getExercise = dataManager!!.getExercise(data[i])
         for(j in 0 until getExercise.size) {
            when(getExercise[j].name) {
               "달리기" -> running++
               "축구" -> soccer++
               "요가" -> yoga++
               "농구" -> basketball++
            }
         }
      }

      binding.tvRunning.text = "${running}회"
      binding.tvSoccer.text = "${soccer}회"
      binding.tvYoga.text = "${yoga}회"
      binding.tvBasketball.text = "${basketball}회"
   }
}
