package com.makebodywell.bodywell.view.report

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_FOOD
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentReportFoodBinding
import com.makebodywell.bodywell.model.Water
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
import com.makebodywell.bodywell.util.CalendarUtil.Companion.monthArray2
import com.makebodywell.bodywell.util.CalendarUtil.Companion.monthFormat
import com.makebodywell.bodywell.util.CalendarUtil.Companion.weekArray
import com.makebodywell.bodywell.util.CalendarUtil.Companion.weekFormat
import com.makebodywell.bodywell.util.CustomUtil.Companion.getFoodKcal
import com.makebodywell.bodywell.util.CustomUtil.Companion.getNutrition
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate

class ReportFoodFragment : Fragment() {
   private var _binding: FragmentReportFoodBinding? = null
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
      _binding = FragmentReportFoodBinding.inflate(layoutInflater)

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

      binding.clMenu3.setOnClickListener {
         replaceFragment1(requireActivity(), ReportExerciseFragment())
      }

      binding.clMenu4.setOnClickListener {
         replaceFragment1(requireActivity(), ReportDrugFragment())
      }

      binding.clPrev.setOnClickListener {
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

      binding.clNext.setOnClickListener {
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

      val getDates = dataManager!!.getDates(TABLE_FOOD)
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

      val getWater = dataManager!!.getWater()
      if(getWater.size > 0) {
         binding.chart3.visibility = View.VISIBLE
         binding.tvEmpty3.visibility = View.GONE
         settingChart3(binding.chart3, getWater)
      }else {
         binding.chart3.visibility = View.GONE
         binding.tvEmpty3.visibility = View.VISIBLE
      }
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
      val getDates = dataManager!!.getDates(TABLE_FOOD, weekArray[0].toString(), weekArray[6].toString())
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

      val getWater = dataManager!!.getWater(weekArray[0].toString(), weekArray[6].toString())
      if(getWater.size > 0) {
         binding.chart3.visibility = View.VISIBLE
         binding.tvEmpty3.visibility = View.GONE
         settingChart3(binding.chart3, getWater)
      }else {
         binding.chart3.visibility = View.GONE
         binding.tvEmpty3.visibility = View.VISIBLE
      }
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
      val getDates = dataManager!!.getDates(TABLE_FOOD, monthArray[0].toString(), monthArray[monthArray.size-1].toString())
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

      val getWater = dataManager!!.getWater(monthArray[0].toString(), monthArray[monthArray.size-1].toString())
      if(getWater.size > 0) {
         binding.chart3.visibility = View.VISIBLE
         binding.tvEmpty3.visibility = View.GONE

         settingChart3(binding.chart3, getWater)
      }else {
         binding.chart3.visibility = View.GONE
         binding.tvEmpty3.visibility = View.VISIBLE
      }
   }

   private fun settingChart1(chart: CombinedChart, getData: ArrayList<String>) {
      chart.data = null
      chart.fitScreen()
      chart.xAxis.valueFormatter = null
      chart.clear()

      val data = CombinedData()
      var xVal = arrayOf<String>()
      val lineData = LineData()
      var lineList = floatArrayOf()
      val entries = ArrayList<Entry>()
      val barEntries = ArrayList<BarEntry>()

      for(i in 0 until getData.size){
         val foodKcal = getFoodKcal(requireActivity(), getData[i])
         xVal += format2.format(format1.parse(getData[i])!!)
         lineList += foodKcal.int5.toFloat()
         barEntries.add(BarEntry(i.toFloat(), floatArrayOf(
            foodKcal.int4.toFloat(), foodKcal.int3.toFloat(), foodKcal.int2.toFloat(), foodKcal.int1.toFloat()
         )))
      }

      for (index in lineList.indices) {
         entries.add(Entry(index.toFloat(), lineList[index]))
      }

      val lineDataSet = LineDataSet(entries, "Line DataSet")
      lineDataSet.color = Color.parseColor("#FB9797")
      lineDataSet.lineWidth = 0.5f
      lineDataSet.setDrawCircles(false)
      lineDataSet.setDrawValues(true)
      lineDataSet.valueTextSize = 8f
      lineDataSet.valueTextColor = Color.parseColor("#BBBBBB")
      lineDataSet.axisDependency = YAxis.AxisDependency.RIGHT
      lineDataSet.valueFormatter = DefaultValueFormatter(0)

      lineData.addDataSet(lineDataSet)
      data.setData(lineData)

      val barColor = ArrayList<Int>()
      barColor.add(Color.parseColor("#FFC6D7"))
      barColor.add(Color.parseColor("#ABE764"))
      barColor.add(Color.parseColor("#FFE500"))
      barColor.add(Color.parseColor("#FE9A9A"))

      val barDataSet = BarDataSet(barEntries, "")
      barDataSet.colors = barColor
      barDataSet.valueTextSize = 0f

      val barData = BarData(barDataSet)
      barData.barWidth = 0.27f

      data.setData(barData)

      chart.data = data
      chart.notifyDataSetChanged()
      chart.invalidate()
      chart.setVisibleXRangeMaximum(7f)
      chart.isDragXEnabled = true

      chartCommon(chart, xVal)
   }

   private fun settingChart2(chart: CombinedChart, getData: ArrayList<String>) {
      chart.data = null
      chart.fitScreen()
      chart.xAxis.valueFormatter = null
      chart.clear()

      val data = CombinedData()
      var xVal = arrayOf<String>()
      val lineData = LineData()
      var lineList = floatArrayOf()
      val entries = ArrayList<Entry>()
      val barEntries = ArrayList<BarEntry>()

      for(i in 0 until getData.size){
         val nutrition = getNutrition(requireActivity(), getData[i])
         xVal += format2.format(format1.parse(getData[i])!!)
         lineList += nutrition.name.toFloat()
         barEntries.add(BarEntry(i.toFloat(), floatArrayOf(
            nutrition.carbohydrate.toFloat(), nutrition.protein.toFloat(), nutrition.fat.toFloat(), nutrition.sugar.toFloat()
         )))
      }

      for (index in lineList.indices) {
         entries.add(Entry(index.toFloat(), lineList[index]))
      }

      val lineDataSet = LineDataSet(entries, "Line DataSet")
      lineDataSet.color = Color.parseColor("#FFAD0D")
      lineDataSet.lineWidth = 0.5f
      lineDataSet.setDrawCircles(false)
      lineDataSet.setDrawValues(true)
      lineDataSet.valueTextSize = 8f
      lineDataSet.valueTextColor = Color.parseColor("#BBBBBB")
      lineDataSet.axisDependency = YAxis.AxisDependency.RIGHT
      lineDataSet.valueFormatter = MyValueFormatter()

      lineData.addDataSet(lineDataSet)
      data.setData(lineData)

      val barColor = ArrayList<Int>()
      barColor.add(Color.parseColor("#FAE498"))
      barColor.add(Color.parseColor("#FBCE59"))
      barColor.add(Color.parseColor("#FFAD0D"))
      barColor.add(Color.parseColor("#DBA00A"))

      val barDataSet = BarDataSet(barEntries, "")
      barDataSet.colors = barColor
      barDataSet.valueTextSize = 0f

      val barData = BarData(barDataSet)
      barData.barWidth = 0.27f

      data.setData(barData)

      chart.data = data
      chart.invalidate()
      chart.setVisibleXRangeMaximum(7f)
      chart.isDragXEnabled = true

      chartCommon(chart, xVal)
   }

   private fun settingChart3(chart: CombinedChart, getData: ArrayList<Water>) {
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
         xVal += format2.format(format1.parse(getData[i].regDate))
         lineList += (getData[i].water * getData[i].volume).toFloat()
         barEntries.add(BarEntry(i.toFloat(), (getData[i].water * getData[i].volume).toFloat()))
      }

      for (index in lineList.indices) {
         entries.add(Entry(index.toFloat(), lineList[index]))
      }

      val lineDataSet = LineDataSet(entries, "Line DataSet")
      lineDataSet.color = Color.parseColor("#0F7EF6")
      lineDataSet.lineWidth = 0.5f
      lineDataSet.setDrawCircles(false)
      lineDataSet.setDrawValues(true)
      lineDataSet.valueTextSize = 8f
      lineDataSet.valueTextColor = Color.parseColor("#BBBBBB")
      lineDataSet.axisDependency = YAxis.AxisDependency.RIGHT
      lineDataSet.valueFormatter = DefaultValueFormatter(0)

      lineData.addDataSet(lineDataSet)
      data.setData(lineData)

      val barDataSet = BarDataSet(barEntries, "")
      barDataSet.color = Color.parseColor("#6F89BF")
      barDataSet.valueTextSize = 0f

      val barData = BarData(barDataSet)
      barData.barWidth = 0.27f

      data.setData(barData)

      chart.data = data
      chart.invalidate()
      chart.setVisibleXRangeMaximum(7f)
      chart.isDragXEnabled = true

      chartCommon(chart, xVal)
   }

   private fun chartCommon(chart: CombinedChart, xVal: Array<String>) {
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
   }

   class MyValueFormatter : IValueFormatter {
      override fun getFormattedValue(
         value: Float,
         entry: Entry,
         dataSetIndex: Int,
         viewPortHandler: ViewPortHandler
      ): String {
         val formatter= DecimalFormat("#.#");
         return formatter.format(value)
      }
   }
}