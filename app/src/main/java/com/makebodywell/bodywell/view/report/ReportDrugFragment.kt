package com.makebodywell.bodywell.view.report

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DRUG
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentReportDrugBinding
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
import com.makebodywell.bodywell.util.CalendarUtil.Companion.monthArray2
import com.makebodywell.bodywell.util.CalendarUtil.Companion.monthFormat
import com.makebodywell.bodywell.util.CalendarUtil.Companion.weekArray
import com.makebodywell.bodywell.util.CalendarUtil.Companion.weekFormat
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import java.text.SimpleDateFormat
import java.time.LocalDate

class ReportDrugFragment : Fragment() {
   private var _binding: FragmentReportDrugBinding? = null
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
      _binding = FragmentReportDrugBinding.inflate(layoutInflater)

      requireActivity().window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK

         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.mainLayout.setPadding(0, statusBarHeight, 0, 0)
      }

      dataManager = DataManager(activity)
      dataManager!!.open()

      binding.tvCalTitle.text = dateFormat(calendarDate)

      binding.clMenu1.setOnClickListener {
         replaceFragment1(requireActivity(), ReportBodyFragment())
      }

      binding.clMenu2.setOnClickListener {
         replaceFragment1(requireActivity(), ReportFoodFragment())
      }

      binding.clMenu3.setOnClickListener {
         replaceFragment1(requireActivity(), ReportExerciseFragment())
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

      val getDates = dataManager!!.getDates(TABLE_DRUG)
      if(getDates.size > 0) {
         binding.chart.visibility = View.VISIBLE
         binding.tvEmpty.visibility = View.GONE
         settingChart(binding.chart, getDates)
      }else {
         binding.chart.visibility = View.GONE
         binding.tvEmpty.visibility = View.VISIBLE
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
      val getDates = dataManager!!.getDates(TABLE_DRUG, weekArray[0].toString(), weekArray[6].toString())
      if(getDates.size > 0) {
         binding.chart.visibility = View.VISIBLE
         binding.tvEmpty.visibility = View.GONE
         settingChart(binding.chart, getDates)
      }else {
         binding.chart.visibility = View.GONE
         binding.tvEmpty.visibility = View.VISIBLE
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
      val getDates = dataManager!!.getDates(TABLE_DRUG, monthArray[0].toString(), monthArray[monthArray.size-1].toString())
      if(getDates.size > 0) {
         binding.chart.visibility = View.VISIBLE
         binding.tvEmpty.visibility = View.GONE
         settingChart(binding.chart, getDates)
      }else {
         binding.chart.visibility = View.GONE
         binding.tvEmpty.visibility = View.VISIBLE
      }

      countView(getDates)
   }

   private fun settingChart(chart: CombinedChart, getData: ArrayList<String>) {
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
      var num = -1

      for(i in 0 until getData.size){
         val getDailyData = dataManager!!.getDailyData(getData[i])
         val count = dataManager!!.getDrugCheckCount(getData[i])

         if(getDailyData.regDate != "" && count != 0) {
            num++

            val pt = when(getDailyData.drugGoal) {
               0 -> 100f
               else -> (count.toFloat() / getDailyData.drugGoal.toFloat()) * 100
            }

            xVal += format2.format(format1.parse(getDailyData.regDate)!!)
            lineList += pt
            barEntries.add(BarEntry(num.toFloat(), pt))
         }
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
      barDataSet.color = Color.parseColor("#3C7A8A")
      barDataSet.valueTextSize = 0f

      val barData = BarData(barDataSet)
      barData.barWidth = 0.27f

      data.setData(barData)

      chart.data = data
      chart.invalidate()
      chart.setVisibleXRangeMaximum(7f)
      chart.isDragXEnabled = true

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
      leftAxis.valueFormatter = LeftAxisFormatter()
   }

   class XValueFormatter : IValueFormatter {
      override fun getFormattedValue(
         value: Float,
         entry: Entry,
         dataSetIndex: Int,
         viewPortHandler: ViewPortHandler
      ): String {
         return value.toInt().toString()
      }
   }

   class LeftAxisFormatter : IAxisValueFormatter {
      override fun getFormattedValue(value: Float, axis: AxisBase?): String {
         return if(value.toInt() == 0) {
            "0"
         }else {
            "${value.toInt()}%"
         }
      }
   }

   private fun countView(data: ArrayList<String>) {
      var drug1 = 0
      var drug2 = 0
      var drug3 = 0
      var drug4 = 0

      for(i in 0 until data.size){
         val getDrug = dataManager!!.getDrug(data[i])
         for(j in 0 until getDrug.size) {
            when(getDrug[j]) {
               "혈압약" -> drug1++
               "비타민" -> drug2++
               "감기약" -> drug3++
               "오메가" -> drug4++
            }
         }
      }

      binding.tvDrug1.text = "${drug1}회"
      binding.tvDrug2.text = "${drug2}회"
      binding.tvDrug3.text = "${drug3}회"
      binding.tvDrug4.text = "${drug4}회"
   }
}