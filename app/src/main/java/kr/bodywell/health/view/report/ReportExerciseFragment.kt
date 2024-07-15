package kr.bodywell.health.view.report

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import kr.bodywell.health.R
import kr.bodywell.health.adapter.ReportAdapter
import kr.bodywell.health.database.DBHelper.Companion.TABLE_DAILY_EXERCISE
import kr.bodywell.health.database.DataManager
import kr.bodywell.health.databinding.FragmentReportExerciseBinding
import kr.bodywell.health.model.Item
import kr.bodywell.health.util.CalendarUtil.Companion.dateFormat
import kr.bodywell.health.util.CalendarUtil.Companion.monthArray2
import kr.bodywell.health.util.CalendarUtil.Companion.monthFormat
import kr.bodywell.health.util.CalendarUtil.Companion.weekArray
import kr.bodywell.health.util.CalendarUtil.Companion.weekFormat
import kr.bodywell.health.util.CustomUtil.Companion.replaceFragment1
import kr.bodywell.health.util.CustomUtil.Companion.replaceFragment3
import kr.bodywell.health.view.home.MainFragment
import java.text.SimpleDateFormat
import java.time.LocalDate

class ReportExerciseFragment : Fragment() {
   private var _binding: FragmentReportExerciseBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private val format1 = SimpleDateFormat("yyyy-MM-dd")
   private val format2 = SimpleDateFormat("M.dd")
   private var adapter: ReportAdapter? = null
   private var calendarDate = LocalDate.now()
   private var dateType = 1

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment1(requireActivity(), MainFragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentReportExerciseBinding.inflate(layoutInflater)

      requireActivity().window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK

         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.mainLayout.setPadding(0, statusBarHeight, 0, 0)
      }

      dataManager = DataManager(activity)
      dataManager.open()

      binding.tvCalTitle.text = dateFormat(calendarDate)

      binding.clMenu1.setOnClickListener {
         replaceFragment3(requireActivity(), ReportBodyFragment())
      }

      binding.clMenu2.setOnClickListener {
         replaceFragment3(requireActivity(), ReportFoodFragment())
      }

      binding.clMenu4.setOnClickListener {
         replaceFragment3(requireActivity(), ReportDrugFragment())
      }

      binding.clPrev.setOnClickListener {
         when(dateType) {
            1->{
               calendarDate = calendarDate!!.minusDays(1)
               binding.tvCalTitle.text = dateFormat(calendarDate)
               dailyView()
            }
            2->{
               calendarDate = calendarDate!!.minusWeeks(1)
               binding.tvCalTitle.text = weekFormat(calendarDate)
               weeklyView()
            }
            3->{
               calendarDate = calendarDate!!.minusMonths(1)
               binding.tvCalTitle.text = monthFormat(calendarDate)
               monthlyView()
            }
         }
      }

      binding.clNext.setOnClickListener {
         when(dateType) {
            1->{
               calendarDate = calendarDate!!.plusDays(1)
               binding.tvCalTitle.text = dateFormat(calendarDate)
               dailyView()
            }
            2->{
               calendarDate = calendarDate!!.plusWeeks(1)
               binding.tvCalTitle.text = weekFormat(calendarDate)
               weeklyView()
            }
            3->{
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
      chartReset()
      binding.tvDaily.setBackgroundResource(R.drawable.rec_5_purple)
      binding.tvDaily.setTextColor(Color.WHITE)
      binding.tvWeekly.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvWeekly.setTextColor(Color.BLACK)
      binding.tvMonthly.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvMonthly.setTextColor(Color.BLACK)
      dateType = 1

      val dates = ArrayList<String>()
      val getExercise = dataManager.getDailyExercise("created", calendarDate.toString())

      if(getExercise.size > 0) {
         dates.add(calendarDate.toString())
         settingChart1(binding.chart1, dates)
         settingChart2(binding.chart2, dates)
         rankView(dateType, "", "")
      }
   }

   private fun weeklyView() {
      chartReset()
      binding.tvDaily.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvDaily.setTextColor(Color.BLACK)
      binding.tvWeekly.setBackgroundResource(R.drawable.rec_5_purple)
      binding.tvWeekly.setTextColor(Color.WHITE)
      binding.tvMonthly.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvMonthly.setTextColor(Color.BLACK)
      dateType = 2

      val weekArray = weekArray(calendarDate)
      val getDates = dataManager.getDates(TABLE_DAILY_EXERCISE, weekArray[0].toString(), weekArray[6].toString())

      if(getDates.size > 0) {
         settingChart1(binding.chart1, getDates)
         settingChart2(binding.chart2, getDates)
         rankView(dateType, weekArray[0].toString(), weekArray[6].toString())
      }
   }

   private fun monthlyView() {
      chartReset()
      binding.tvDaily.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvDaily.setTextColor(Color.BLACK)
      binding.tvWeekly.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvWeekly.setTextColor(Color.BLACK)
      binding.tvMonthly.setBackgroundResource(R.drawable.rec_5_purple)
      binding.tvMonthly.setTextColor(Color.WHITE)
      dateType = 3

      val monthArray = monthArray2(calendarDate)
      val getDates = dataManager.getDates(TABLE_DAILY_EXERCISE, monthArray[0].toString(), monthArray[monthArray.size-1].toString())

      if(getDates.size > 0) {
         settingChart1(binding.chart1, getDates)
         settingChart2(binding.chart2, getDates)

         rankView(dateType, monthArray[0].toString(), monthArray[monthArray.size-1].toString())
      }
   }

   private fun chartReset() {
      binding.recyclerView.visibility = View.GONE
      binding.tvEmpty.visibility = View.VISIBLE
      binding.chart1.visibility = View.GONE
      binding.tvEmpty1.visibility = View.VISIBLE
      binding.chart2.visibility = View.GONE
      binding.tvEmpty2.visibility = View.VISIBLE
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
      var count = 0

      for(i in 0 until getData.size){
         var total = 0f

         val getDailyExercise = dataManager.getDailyExercise("created", getData[i])
         for(j in 0 until getDailyExercise.size) {
            if(getDailyExercise[j].workoutTime > 0) {
               total += getDailyExercise[j].workoutTime.toFloat()
            }
         }

         if(total > 0) {
            lineList += total
            barEntries.add(BarEntry(count.toFloat(), total))
            xVal += format2.format(format1.parse(getData[i])!!)
            count += 1
         }
      }

      if(lineList.isNotEmpty()) {
         binding.chart1.visibility = View.VISIBLE
         binding.tvEmpty1.visibility = View.GONE

         for (index in lineList.indices) entries.add(Entry(index.toFloat(), lineList[index]))

         val lineDataSet = LineDataSet(entries, "Line DataSet")
         lineDataSet.color = Color.parseColor("#D3B479")
         lineDataSet.lineWidth = 1f
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

         chartCommon(chart, xVal)
      }
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
      var count = 0

      for(i in 0 until getData.size){
         var total = 0f

         val getDailyExercise = dataManager.getDailyExercise("created", getData[i])
         for(j in 0 until getDailyExercise.size) {
            if(getDailyExercise[j].kcal > 0) {
               total += getDailyExercise[j].kcal.toFloat()
            }
         }

         if(total > 0) {
            lineList += total
            barEntries.add(BarEntry(count.toFloat(), total))
            xVal += format2.format(format1.parse(getData[i])!!)
            count++
         }
      }

      if(lineList.isNotEmpty()) {
         binding.chart2.visibility = View.VISIBLE
         binding.tvEmpty2.visibility = View.GONE

         for (index in lineList.indices) entries.add(Entry(index.toFloat(), lineList[index]))

         val lineDataSet = LineDataSet(entries, "Line DataSet")
         lineDataSet.color = Color.parseColor("#FFC6D7")
         lineDataSet.lineWidth = 1f
         lineDataSet.setDrawCircles(false)
         lineDataSet.setDrawValues(true)
         lineDataSet.valueTextSize = 8f
         lineDataSet.valueTextColor = Color.parseColor("#BBBBBB")
         lineDataSet.axisDependency = YAxis.AxisDependency.RIGHT

         lineData.addDataSet(lineDataSet)
         data.setData(lineData)

         val barDataSet = BarDataSet(barEntries, "")
         barDataSet.color = Color.parseColor("#FFC6D7")
         barDataSet.valueTextSize = 0f

         val barData = BarData(barDataSet)
         barData.barWidth = 0.27f

         data.setData(barData)

         chart.data = data

         chartCommon(chart, xVal)
      }
   }

   private fun chartCommon(chart: CombinedChart, xVal: Array<String>) {
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

      chart.description.isEnabled = false
      chart.legend.isEnabled = false
      chart.setScaleEnabled(false)
      chart.isClickable = false
      chart.isHighlightPerDragEnabled = false
      chart.isHighlightPerTapEnabled = false
      chart.setVisibleXRangeMaximum(7f)
      chart.isDragXEnabled = true
      chart.animateY(1000)
      chart.invalidate()
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
            if(remain == 0) "${value.toInt() / 60}시간" else "${value.toInt() / 60}시간 ${remain}분"
         }
         return result
      }
   }

   private fun rankView(type: Int, start: String, end: String) {
      val itemList = ArrayList<Item>()

      val getData = when(type) {
         1 -> dataManager.getExerciseRanking(calendarDate.toString())
         else -> dataManager.getExerciseRanking(start, end)
      }

      if(getData.size > 0) {
         binding.tvEmpty.visibility = View.GONE
         binding.recyclerView.visibility = View.VISIBLE

         for(i in 0 until getData.size) itemList.add(Item(string1 = getData[i].string1, string2 = getData[i].string2))

         when(getData.size) {
            1 -> {
               val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 1)
               binding.recyclerView.layoutManager = layoutManager
            }
            2 -> {
               val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 2)
               binding.recyclerView.layoutManager = layoutManager
            }
            3 -> {
               val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 3)
               binding.recyclerView.layoutManager = layoutManager
            }
            4 -> {
               val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 4)
               binding.recyclerView.layoutManager = layoutManager
            }
         }

         adapter = ReportAdapter(itemList)
         binding.recyclerView.adapter = adapter
      }
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}
