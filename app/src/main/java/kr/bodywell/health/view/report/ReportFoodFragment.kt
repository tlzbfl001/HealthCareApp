package kr.bodywell.health.view.report

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.launch
import kr.bodywell.health.R
import kr.bodywell.health.databinding.FragmentReportFoodBinding
import kr.bodywell.health.model.Constant.DIETS
import kr.bodywell.health.model.Water
import kr.bodywell.health.util.CalendarUtil.dateFormat
import kr.bodywell.health.util.CalendarUtil.monthArray2
import kr.bodywell.health.util.CalendarUtil.monthFormat
import kr.bodywell.health.util.CalendarUtil.selectedDate
import kr.bodywell.health.util.CalendarUtil.weekArray
import kr.bodywell.health.util.CalendarUtil.weekFormat
import kr.bodywell.health.util.CustomUtil.getFoodCalories
import kr.bodywell.health.util.CustomUtil.getNutrition
import kr.bodywell.health.util.CustomUtil.replaceFragment3
import kr.bodywell.health.util.CustomUtil.setStatusBar
import kr.bodywell.health.util.MyApp.Companion.powerSync
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import kotlin.system.exitProcess

class ReportFoodFragment : Fragment() {
   private var _binding: FragmentReportFoodBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private val format1 = SimpleDateFormat("yyyy-MM-dd")
   private val format2 = SimpleDateFormat("M.dd")
   private var calendarDate = LocalDate.now()
   private var dateType = 0
   private var pressedTime: Long = 0

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            pressedTime = if(pressedTime == 0L) {
               Toast.makeText(requireActivity(), "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
               System.currentTimeMillis()
            }else {
               val seconds = (System.currentTimeMillis() - pressedTime).toInt()
               if(seconds > 2000) {
                  Toast.makeText(requireActivity(), "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                  0
               }else {
                  requireActivity().finishAffinity()
                  System.runFinalization()
                  exitProcess(0)
               }
            }
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentReportFoodBinding.inflate(layoutInflater)

      setStatusBar(requireActivity(), binding.mainLayout)

      binding.tvCalTitle.text = dateFormat(calendarDate)

      binding.clMenu1.setOnClickListener {
         replaceFragment3(requireActivity().supportFragmentManager, ReportBodyFragment())
      }

      binding.clMenu3.setOnClickListener {
         replaceFragment3(requireActivity().supportFragmentManager, ReportExerciseFragment())
      }

      binding.clMenu4.setOnClickListener {
         replaceFragment3(requireActivity().supportFragmentManager, ReportMedicineFragment())
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
      binding.tvDaily.setBackgroundResource(R.drawable.rec_5_green)
      binding.tvDaily.setTextColor(Color.WHITE)
      binding.tvWeekly.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvWeekly.setTextColor(Color.BLACK)
      binding.tvMonthly.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvMonthly.setTextColor(Color.BLACK)
      dateType = 0
      resetChart()

      lifecycleScope.launch {
         val getDates = powerSync.getDates(DIETS, "date", calendarDate.toString(), calendarDate.toString())
         if(getDates.isNotEmpty()) {
            settingChart1(binding.chart1, getDates)
            settingChart2(binding.chart2, getDates)
         }

         val getWater = ArrayList<Water>()
         val data = powerSync.getWater(selectedDate.toString())

         if(data.count > 0) {
            binding.chart3.visibility = View.VISIBLE
            binding.tvEmpty3.visibility = View.GONE
            getWater.add(data)
            settingChart3(binding.chart3, getWater)
         }else {
            binding.chart3.visibility = View.GONE
            binding.tvEmpty3.visibility = View.VISIBLE
         }
      }
   }

   private fun weeklyView() {
      binding.tvDaily.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvDaily.setTextColor(Color.BLACK)
      binding.tvWeekly.setBackgroundResource(R.drawable.rec_5_green)
      binding.tvWeekly.setTextColor(Color.WHITE)
      binding.tvMonthly.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvMonthly.setTextColor(Color.BLACK)
      dateType = 1
      resetChart()

      lifecycleScope.launch {
         val weekArray = weekArray(calendarDate)

         val getDates = powerSync.getDates(DIETS, "date", weekArray[0].toString(), weekArray[6].toString())
         if(getDates.isNotEmpty()) {
            settingChart1(binding.chart1, getDates)
            settingChart2(binding.chart2, getDates)
         }

         val getWater = powerSync.getAllWater(weekArray[0].toString(), weekArray[6].toString())
         if(getWater.isNotEmpty()) {
            binding.chart3.visibility = View.VISIBLE
            binding.tvEmpty3.visibility = View.GONE
            settingChart3(binding.chart3, getWater)
         }else {
            binding.chart3.visibility = View.GONE
            binding.tvEmpty3.visibility = View.VISIBLE
         }
      }
   }

   private fun monthlyView() {
      binding.tvDaily.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvDaily.setTextColor(Color.BLACK)
      binding.tvWeekly.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvWeekly.setTextColor(Color.BLACK)
      binding.tvMonthly.setBackgroundResource(R.drawable.rec_5_green)
      binding.tvMonthly.setTextColor(Color.WHITE)
      dateType = 2
      resetChart()

      lifecycleScope.launch {
         val monthArray = monthArray2(calendarDate)
         val getDates = powerSync.getDates(DIETS, "date", monthArray[0].toString(), monthArray[monthArray.size-1].toString())
         if(getDates.isNotEmpty()) {
            settingChart1(binding.chart1, getDates)
            settingChart2(binding.chart2, getDates)
         }

         val getWater = powerSync.getAllWater(monthArray[0].toString(), monthArray[monthArray.size-1].toString())
         if(getWater.isNotEmpty()) {
            binding.chart3.visibility = View.VISIBLE
            binding.tvEmpty3.visibility = View.GONE
            settingChart3(binding.chart3, getWater)
         }else {
            binding.chart3.visibility = View.GONE
            binding.tvEmpty3.visibility = View.VISIBLE
         }
      }
   }

   private fun resetChart() {
      binding.chart1.visibility = View.GONE
      binding.tvEmpty1.visibility = View.VISIBLE
      binding.chart2.visibility = View.GONE
      binding.tvEmpty2.visibility = View.VISIBLE
   }

   private fun settingChart1(chart: CombinedChart, getData: List<String>) {
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
      var count = 0

      lifecycleScope.launch {
         for(i in getData.indices){
            val foodKcal = getFoodCalories(getData[i])

            if(foodKcal.int5 > 0) {
               xVal += format2.format(format1.parse(getData[i])!!)
               lineList += foodKcal.int5.toFloat()
               barEntries.add(BarEntry(count.toFloat(), floatArrayOf(foodKcal.int4.toFloat(), foodKcal.int3.toFloat(), foodKcal.int2.toFloat(), foodKcal.int1.toFloat())))
               count++
            }
         }

         if(barEntries.size > 0) {
            binding.chart1.visibility = View.VISIBLE
            binding.tvEmpty1.visibility = View.GONE

            for(index in lineList.indices) entries.add(Entry(index.toFloat(), lineList[index]))

            val lineDataSet = LineDataSet(entries, "Line DataSet")
            lineDataSet.color = Color.parseColor("#F0727A")
            lineDataSet.lineWidth = 1f
            lineDataSet.setDrawCircles(false)
            lineDataSet.setDrawValues(true)
            lineDataSet.valueTextSize = 9f
            lineDataSet.valueTextColor = resources.getColor(R.color.black_white)
            lineDataSet.axisDependency = YAxis.AxisDependency.RIGHT
            lineDataSet.valueFormatter = DefaultValueFormatter(0)

            lineData.addDataSet(lineDataSet)
            data.setData(lineData)

            val barColor = ArrayList<Int>()
            barColor.add(Color.parseColor("#FFE1EB"))
            barColor.add(Color.parseColor("#D8FFAA"))
            barColor.add(Color.parseColor("#FFEDBE"))
            barColor.add(Color.parseColor("#F0727A"))

            val barDataSet = BarDataSet(barEntries, "")
            barDataSet.colors = barColor
            barDataSet.valueTextSize = 0f

            val barData = BarData(barDataSet)
            barData.barWidth = 0.27f

            data.setData(barData)

            chart.data = data

            chartCommon(chart, xVal)
         }
      }
   }

   private fun settingChart2(chart: CombinedChart, getData: List<String>) {
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
      var count = 0

      lifecycleScope.launch {
         for(i in getData.indices){
            val nutrition = getNutrition(getData[i])
            if(nutrition.volumeUnit.toDouble() > 0) {
               xVal += format2.format(format1.parse(getData[i])!!)
               lineList += nutrition.volumeUnit.toFloat()
               barEntries.add(BarEntry(count.toFloat(), floatArrayOf(
                  nutrition.fat.toFloat(), nutrition.protein.toFloat(), nutrition.carbohydrate.toFloat()
               )))
               count++
            }
         }

         if(barEntries.size > 0) {
            binding.chart2.visibility = View.VISIBLE
            binding.tvEmpty2.visibility = View.GONE

            for (index in lineList.indices) {
               entries.add(Entry(index.toFloat(), lineList[index]))
            }

            val lineDataSet = LineDataSet(entries, "Line DataSet")
            lineDataSet.color = Color.parseColor("#9B7CE4")
            lineDataSet.lineWidth = 1f
            lineDataSet.setDrawCircles(false)
            lineDataSet.setDrawValues(true)
            lineDataSet.valueTextSize = 9f
            lineDataSet.valueTextColor = resources.getColor(R.color.black_white)
            lineDataSet.axisDependency = YAxis.AxisDependency.RIGHT
            lineDataSet.valueFormatter = MyValueFormatter()

            lineData.addDataSet(lineDataSet)
            data.setData(lineData)

            val barColor = ArrayList<Int>()
            barColor.add(Color.parseColor("#DED8EB"))
            barColor.add(Color.parseColor("#BBAAE4"))
            barColor.add(Color.parseColor("#9B7CE4"))

            val barDataSet = BarDataSet(barEntries, "")
            barDataSet.colors = barColor
            barDataSet.valueTextSize = 0f

            val barData = BarData(barDataSet)
            barData.barWidth = 0.27f

            data.setData(barData)

            chart.data = data

            chartCommon(chart, xVal)
         }
      }
   }

   private fun settingChart3(chart: CombinedChart, getData: List<Water>) {
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

      for(i in getData.indices){
         xVal += format2.format(format1.parse(getData[i].createdAt)!!)
         lineList += (getData[i].count * getData[i].mL).toFloat()
         barEntries.add(BarEntry(i.toFloat(), (getData[i].count * getData[i].mL).toFloat()))
      }

      for (index in lineList.indices) {
         entries.add(Entry(index.toFloat(), lineList[index]))
      }

      val lineDataSet = LineDataSet(entries, "Line DataSet")
      lineDataSet.color = Color.parseColor("#7FB8E6")
      lineDataSet.lineWidth = 1f
      lineDataSet.setDrawCircles(false)
      lineDataSet.setDrawValues(true)
      lineDataSet.valueTextSize = 9f
      lineDataSet.valueTextColor = resources.getColor(R.color.black_white)
      lineDataSet.axisDependency = YAxis.AxisDependency.RIGHT
      lineDataSet.valueFormatter = DefaultValueFormatter(0)

      lineData.addDataSet(lineDataSet)
      lineData.setValueTextColor(resources.getColor(R.color.black_white))
      data.setData(lineData)

      val barDataSet = BarDataSet(barEntries, "")
      barDataSet.color = Color.parseColor("#7FB8E6")
      barDataSet.valueTextSize = 0f

      val barData = BarData(barDataSet)
      barData.barWidth = 0.27f

      data.setData(barData)

      chart.data = data

      chartCommon(chart, xVal)
   }

   private fun chartCommon(chart: CombinedChart, xVal: Array<String>) {
      chart.setExtraOffsets(8f, 12f, 15f, 10f)
      chart.description.isEnabled = false
      chart.legend.isEnabled = false
      chart.setScaleEnabled(false)
      chart.isClickable = false
      chart.isHighlightPerDragEnabled = false
      chart.isHighlightPerTapEnabled = false
      chart.setVisibleXRangeMaximum(7f)
      chart.animateY(1000)
      chart.notifyDataSetChanged()
      chart.invalidate()
      chart.isDragXEnabled = true

      val xAxis = chart.xAxis
      xAxis.axisLineColor = resources.getColor(R.color.black_white)
      xAxis.textColor = resources.getColor(R.color.black_white)
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
      leftAxis.axisLineColor = resources.getColor(R.color.black_white)
      leftAxis.textColor = resources.getColor(R.color.black_white)
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

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}