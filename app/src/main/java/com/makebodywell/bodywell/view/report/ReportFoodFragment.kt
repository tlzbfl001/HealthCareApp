package com.makebodywell.bodywell.view.report

import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentReportFoodBinding
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.model.Water
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
import com.makebodywell.bodywell.util.CalendarUtil.Companion.monthFormat
import com.makebodywell.bodywell.util.CalendarUtil.Companion.weekArray
import com.makebodywell.bodywell.util.CalendarUtil.Companion.weekFormat
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.getFoodKcal
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

   private var xVal = arrayOf<String>()
   private var lineList = floatArrayOf()
   private val barEntries = ArrayList<BarEntry>()

   private val format1 = SimpleDateFormat("yyyy-MM-dd")
   private val format2 = SimpleDateFormat("M.dd")

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentReportFoodBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      binding.tvCalTitle.text = dateFormat(calendarDate)

      binding.pbBody.setOnClickListener {
         replaceFragment1(requireActivity(), ReportBodyFragment())
      }

      binding.pbExercise.setOnClickListener {
         replaceFragment1(requireActivity(), ReportExerciseFragment())
      }

      binding.pbDrug.setOnClickListener {
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

      buttonUI()
      dailyView()

      return binding.root
   }

   private fun dailyView() {
      binding.tvDaily.setBackgroundResource(R.drawable.rec_12_blue)
      binding.tvDaily.setTextColor(Color.WHITE)
      binding.tvWeekly.setBackgroundResource(R.drawable.rec_12_border_gray)
      binding.tvWeekly.setTextColor(Color.BLACK)
      binding.tvMonthly.setBackgroundResource(R.drawable.rec_12_border_gray)
      binding.tvMonthly.setTextColor(Color.BLACK)
      dateType = 0

      val getFoodDates = dataManager!!.getFoodDates()
      if(getFoodDates.size > 0) {
         binding.tvEmpty1.visibility = View.GONE
         binding.chart1.visibility = View.VISIBLE
         binding.tvEmpty2.visibility = View.GONE
         binding.chart2.visibility = View.VISIBLE

         xVal = arrayOf()
         lineList = floatArrayOf()
         barEntries.clear()

         var xVal = arrayOf<String>()
         var lineList = floatArrayOf()
         val barEntries = ArrayList<BarEntry>()

         for(i in 0 until getFoodDates.size){
            xVal += format2.format(format1.parse(getFoodDates[i].regDate))
            lineList += getFoodKcal(requireActivity(), getFoodDates[i].regDate!!).int5!!.toFloat()
            barEntries.add(BarEntry(i.toFloat(), floatArrayOf(
               getFoodKcal(requireActivity(), getFoodDates[i].regDate!!).int1!!.toFloat(),
               getFoodKcal(requireActivity(), getFoodDates[i].regDate!!).int2!!.toFloat(),
               getFoodKcal(requireActivity(), getFoodDates[i].regDate!!).int3!!.toFloat(),
               getFoodKcal(requireActivity(), getFoodDates[i].regDate!!).int4!!.toFloat()
            )))
         }

         settingChart1(binding.chart1, xVal, lineList, barEntries)
         settingChart2(binding.chart2, xVal, lineList, barEntries)
      }else {
         binding.tvEmpty1.visibility = View.VISIBLE
         binding.chart1.visibility = View.GONE
         binding.tvEmpty2.visibility = View.VISIBLE
         binding.chart2.visibility = View.GONE
      }

      val getWater = dataManager!!.getWater()
      if(getWater.size > 0) {
         var xVal = arrayOf<String>()
         var lineList = floatArrayOf()
         val barEntries = ArrayList<BarEntry>()

         for(i in 0 until getWater.size){
            xVal += format2.format(format1.parse(getWater[i].regDate))
            lineList += getDailyData(getWater[i].regDate).unit!!.toFloat()
            barEntries.add(BarEntry(i.toFloat(), floatArrayOf(
               getDailyData(getWater[i].regDate).carbohydrate!!.toFloat(),
               getDailyData(getWater[i].regDate).protein!!.toFloat(),
               getDailyData(getWater[i].regDate).fat!!.toFloat(),
               getDailyData(getWater[i].regDate).sugar!!.toFloat()
            )))
         }

         settingChart3(binding.chart3, getWater)
      }
   }

   private fun weeklyView() {
      binding.tvDaily.setBackgroundResource(R.drawable.rec_12_border_gray)
      binding.tvDaily.setTextColor(Color.BLACK)
      binding.tvWeekly.setBackgroundResource(R.drawable.rec_12_blue)
      binding.tvWeekly.setTextColor(Color.WHITE)
      binding.tvMonthly.setBackgroundResource(R.drawable.rec_12_border_gray)
      binding.tvMonthly.setTextColor(Color.BLACK)
      dateType = 1

      val weekArray = weekArray(calendarDate)
      val getFoodDates = dataManager!!.getFoodDa(weekArray[0].toString(), weekArray[6].toString())

      if(getFoodDates.size > 0) {
         binding.tvEmpty1.visibility = View.GONE
         binding.chart1.visibility = View.VISIBLE
         binding.tvEmpty2.visibility = View.GONE
         binding.chart2.visibility = View.VISIBLE

         xVal = arrayOf()
         lineList = floatArrayOf()
         barEntries.clear()

         for(i in 0 until getFoodDates.size){
            xVal += format2.format(format1.parse(getFoodDates[i].regDate))
            lineList += getFoodKcal(requireActivity(), getFoodDates[i].regDate!!).int5!!.toFloat()
            barEntries.add(BarEntry(i.toFloat(), floatArrayOf(
               getFoodKcal(requireActivity(), getFoodDates[i].regDate!!).int1!!.toFloat(),
               getFoodKcal(requireActivity(), getFoodDates[i].regDate!!).int2!!.toFloat(),
               getFoodKcal(requireActivity(), getFoodDates[i].regDate!!).int3!!.toFloat(),
               getFoodKcal(requireActivity(), getFoodDates[i].regDate!!).int4!!.toFloat()
            )))
         }

         settingChart1(binding.chart1, xVal, lineList, barEntries)
         settingChart2(binding.chart2, xVal, lineList, barEntries)
      }else {
         binding.tvEmpty1.visibility = View.VISIBLE
         binding.chart1.visibility = View.GONE
         binding.tvEmpty2.visibility = View.VISIBLE
         binding.chart2.visibility = View.GONE
      }

      val getWater = dataManager!!.getWater()
      if(getWater.size > 0) {

      }
   }

   private fun monthlyView() {
      binding.tvDaily.setBackgroundResource(R.drawable.rec_12_border_gray)
      binding.tvDaily.setTextColor(Color.BLACK)
      binding.tvWeekly.setBackgroundResource(R.drawable.rec_12_border_gray)
      binding.tvWeekly.setTextColor(Color.BLACK)
      binding.tvMonthly.setBackgroundResource(R.drawable.rec_12_blue)
      binding.tvMonthly.setTextColor(Color.WHITE)
      dateType = 2
   }

   private fun settingChart1(
      chart: CombinedChart,
      xVal: Array<String>,
      lineList: FloatArray,
      barEntries: ArrayList<BarEntry>
   ) {
      chart.data = null
      chart.fitScreen()
      chart.xAxis.valueFormatter = null
      chart.clear()

      val data = CombinedData()
      val lineData = LineData()
      val entries = ArrayList<Entry>()

      for (index in lineList.indices) {
         entries.add(Entry(index.toFloat(), lineList[index]))
      }

      chartCommon(chart, xVal)

      val lineDataSet = LineDataSet(entries, "Line DataSet")
      lineDataSet.color = Color.parseColor("#BBBBBB")
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
      barColor.add(Color.parseColor("#BFA1AC"))
      barColor.add(Color.parseColor("#FE9A9A"))
      barColor.add(Color.parseColor("#FFAD0D"))

      val barDataSet = BarDataSet(barEntries, "")
      barDataSet.colors = barColor
      barDataSet.valueTextSize = 0f

      val barData = BarData(barDataSet)
      barData.barWidth = 0.27f

      data.setData(barData)

      chart.data = data
      chart.invalidate()
      chart.setVisibleXRangeMaximum(7f)
      chart.isDragXEnabled = dateType==0 || dateType==2
   }

   private fun settingChart2(
      chart: CombinedChart,
      xVal: Array<String>,
      lineList: FloatArray,
      barEntries: ArrayList<BarEntry>
   ) {
      chart.data = null
      chart.fitScreen()
      chart.xAxis.valueFormatter = null
      chart.clear()

      val data = CombinedData()
      val lineData = LineData()
      val entries = ArrayList<Entry>()

      for (index in lineList.indices) {
         entries.add(Entry(index.toFloat(), lineList[index]))
      }

      chartCommon(chart, xVal)

      val lineDataSet = LineDataSet(entries, "Line DataSet")
      lineDataSet.color = Color.parseColor("#BBBBBB")
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
      barColor.add(Color.parseColor("#FFE380"))
      barColor.add(Color.parseColor("#FFAD0D"))
      barColor.add(Color.parseColor("#ECBA59"))
      barColor.add(Color.parseColor("#DE7453"))

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
   }

   private fun settingChart3(chart: CombinedChart, getData: ArrayList<Water>) {
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

      chartCommon(chart, xVal)

      val lineDataSet = LineDataSet(entries, "Line DataSet")
      lineDataSet.color = Color.parseColor("#BBBBBB")
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
      barDataSet.color = Color.parseColor("#4477E6")
      barDataSet.valueTextSize = 0f

      val barData = BarData(barDataSet)
      barData.barWidth = 0.27f

      data.setData(barData)

      chart.data = data
      chart.invalidate()
      chart.setVisibleXRangeMaximum(7f)
      chart.isDragXEnabled = true
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

   private fun getDailyData(date:String) : Food {
      val getFood1 = dataManager!!.getFood(1, date)
      val getFood2 = dataManager!!.getFood(2, date)
      val getFood3 = dataManager!!.getFood(3, date)
      val getFood4 = dataManager!!.getFood(4, date)

      var carbohydrate = 0.0
      var protein = 0.0
      var fat = 0.0
      var sugar = 0.0

      for(i in 0 until getFood1.size) {
         carbohydrate += getFood1[i].carbohydrate!!.toDouble() * getFood1[i].amount
         protein += getFood1[i].protein!!.toDouble() * getFood1[i].amount
         fat += getFood1[i].fat!!.toDouble() * getFood1[i].amount
         sugar += getFood1[i].sugar!!.toDouble() * getFood1[i].amount
      }
      for(i in 0 until getFood2.size) {
         carbohydrate += getFood2[i].carbohydrate!!.toDouble() * getFood2[i].amount
         protein += getFood2[i].protein!!.toDouble() * getFood2[i].amount
         fat += getFood2[i].fat!!.toDouble() * getFood2[i].amount
         sugar += getFood2[i].sugar!!.toDouble() * getFood2[i].amount
      }
      for(i in 0 until getFood3.size) {
         carbohydrate += getFood3[i].carbohydrate!!.toDouble() * getFood3[i].amount
         protein += getFood3[i].protein!!.toDouble() * getFood3[i].amount
         fat += getFood3[i].fat!!.toDouble() * getFood3[i].amount
         sugar += getFood3[i].sugar!!.toDouble() * getFood3[i].amount
      }
      for(i in 0 until getFood4.size) {
         carbohydrate += getFood4[i].carbohydrate!!.toDouble() * getFood4[i].amount
         protein += getFood4[i].protein!!.toDouble() * getFood4[i].amount
         fat += getFood4[i].fat!!.toDouble() * getFood4[i].amount
         sugar += getFood4[i].sugar!!.toDouble() * getFood4[i].amount
      }

      return Food(unit = (carbohydrate+protein+fat+sugar).toString(), carbohydrate = carbohydrate.toString(),
         protein = protein.toString(), fat = fat.toString(), sugar = sugar.toString())
   }

   private fun getWeeklyData(date: String) : Food {
      val getFood1 = dataManager!!.getFood(1, date)
      val getFood2 = dataManager!!.getFood(2, date)
      val getFood3 = dataManager!!.getFood(3, date)
      val getFood4 = dataManager!!.getFood(4, date)

      var carbohydrate = 0.0
      var protein = 0.0
      var fat = 0.0
      var sugar = 0.0

      for(i in 0 until getFood1.size) {
         carbohydrate += getFood1[i].carbohydrate!!.toDouble() * getFood1[i].amount
         protein += getFood1[i].protein!!.toDouble() * getFood1[i].amount
         fat += getFood1[i].fat!!.toDouble() * getFood1[i].amount
         sugar += getFood1[i].sugar!!.toDouble() * getFood1[i].amount
      }
      for(i in 0 until getFood2.size) {
         carbohydrate += getFood2[i].carbohydrate!!.toDouble() * getFood2[i].amount
         protein += getFood2[i].protein!!.toDouble() * getFood2[i].amount
         fat += getFood2[i].fat!!.toDouble() * getFood2[i].amount
         sugar += getFood2[i].sugar!!.toDouble() * getFood2[i].amount
      }
      for(i in 0 until getFood3.size) {
         carbohydrate += getFood3[i].carbohydrate!!.toDouble() * getFood3[i].amount
         protein += getFood3[i].protein!!.toDouble() * getFood3[i].amount
         fat += getFood3[i].fat!!.toDouble() * getFood3[i].amount
         sugar += getFood3[i].sugar!!.toDouble() * getFood3[i].amount
      }
      for(i in 0 until getFood4.size) {
         carbohydrate += getFood4[i].carbohydrate!!.toDouble() * getFood4[i].amount
         protein += getFood4[i].protein!!.toDouble() * getFood4[i].amount
         fat += getFood4[i].fat!!.toDouble() * getFood4[i].amount
         sugar += getFood4[i].sugar!!.toDouble() * getFood4[i].amount
      }

      return Food(unit = (carbohydrate+protein+fat+sugar).toString(), carbohydrate = carbohydrate.toString(),
         protein = protein.toString(), fat = fat.toString(), sugar = sugar.toString())
   }

   private fun buttonUI() {
      binding.pbBody.max = 100
      binding.pbBody.progress = 50
      binding.pbFood.max = 100
      binding.pbFood.progress = 50
      binding.pbExercise.max = 100
      binding.pbExercise.progress = 50
      binding.pbDrug.max = 100
      binding.pbDrug.progress = 50
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