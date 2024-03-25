package com.makebodywell.bodywell.view.report

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.makebodywell.bodywell.adapter.ReportAdapter
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DRUG_CHECK
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentReportDrugBinding
import com.makebodywell.bodywell.model.Item
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
import com.makebodywell.bodywell.util.CalendarUtil.Companion.monthArray2
import com.makebodywell.bodywell.util.CalendarUtil.Companion.monthFormat
import com.makebodywell.bodywell.util.CalendarUtil.Companion.weekArray
import com.makebodywell.bodywell.util.CalendarUtil.Companion.weekFormat
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.view.home.MainFragment
import java.text.SimpleDateFormat
import java.time.LocalDate

class ReportDrugFragment : Fragment() {
   private var _binding: FragmentReportDrugBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private var adapter: ReportAdapter? = null
   private var calendarDate = LocalDate.now()
   private var dateType = 1

   @SuppressLint("SimpleDateFormat")
   private val format1 = SimpleDateFormat("yyyy-MM-dd")
   @SuppressLint("SimpleDateFormat")
   private val format2 = SimpleDateFormat("M.dd")

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment1(requireActivity(), MainFragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   @SuppressLint("InternalInsetResource", "DiscouragedApi")
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
      dataManager.open()

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
      resetChart()
      binding.tvDaily.setBackgroundResource(R.drawable.rec_5_purple)
      binding.tvDaily.setTextColor(Color.WHITE)
      binding.tvWeekly.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvWeekly.setTextColor(Color.BLACK)
      binding.tvMonthly.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvMonthly.setTextColor(Color.BLACK)
      dateType = 1

      val getDrugCheckCount = dataManager.getDrugCheckCount(calendarDate.toString())
      if(getDrugCheckCount > 0) {
         val getDates = ArrayList<String>()
         getDates.add(calendarDate.toString())
         settingChart(binding.chart, getDates)
         rankView(dateType, "", "")
      }
   }

   private fun weeklyView() {
      resetChart()
      binding.tvDaily.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvDaily.setTextColor(Color.BLACK)
      binding.tvWeekly.setBackgroundResource(R.drawable.rec_5_purple)
      binding.tvWeekly.setTextColor(Color.WHITE)
      binding.tvMonthly.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvMonthly.setTextColor(Color.BLACK)
      dateType = 2

      val weekArray = weekArray(calendarDate)
      val getDates = dataManager.getDates(TABLE_DRUG_CHECK, weekArray[0].toString(), weekArray[6].toString())
      if(getDates.size > 0) {
         settingChart(binding.chart, getDates)
         rankView(dateType, weekArray[0].toString(), weekArray[6].toString())
      }
   }

   private fun monthlyView() {
      resetChart()
      binding.tvDaily.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvDaily.setTextColor(Color.BLACK)
      binding.tvWeekly.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvWeekly.setTextColor(Color.BLACK)
      binding.tvMonthly.setBackgroundResource(R.drawable.rec_5_purple)
      binding.tvMonthly.setTextColor(Color.WHITE)
      dateType = 3

      val monthArray = monthArray2(calendarDate)
      val getDates = dataManager.getDates(TABLE_DRUG_CHECK, monthArray[0].toString(), monthArray[monthArray.size-1].toString())
      if(getDates.size > 0) {
         settingChart(binding.chart, getDates)
         rankView(dateType, monthArray[0].toString(), monthArray[monthArray.size-1].toString())
      }
   }

   private fun resetChart() {
      binding.recyclerView.visibility = View.GONE
      binding.tvEmpty1.visibility = View.VISIBLE
      binding.chart.visibility = View.GONE
      binding.tvEmpty2.visibility = View.VISIBLE
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
      var count = 0

      for(i in 0 until getData.size){
         val getDailyGoal = dataManager.getDailyGoal(getData[i])
         val getDrugCheckCount = dataManager.getDrugCheckCount(getData[i])

         if(getDrugCheckCount > 0) {
            val pt = if(getDailyGoal.drugGoal == 0) 100f else (getDrugCheckCount.toFloat() / getDailyGoal.drugGoal.toFloat()) * 100

            xVal += format2.format(format1.parse(getData[i])!!)
            lineList += pt
            barEntries.add(BarEntry(count.toFloat(), pt))

            count++
         }
      }

      if(barEntries.size > 0) {
         binding.chart.visibility = View.VISIBLE
         binding.tvEmpty2.visibility = View.GONE

         for (index in lineList.indices) {
            entries.add(Entry(index.toFloat(), lineList[index]))
         }

         val lineDataSet = LineDataSet(entries, "Line DataSet")
         lineDataSet.color = Color.parseColor("#4E38A4")
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
         barDataSet.color = Color.parseColor("#4E38A4")
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
         chart.setExtraOffsets(10f, 20f, 15f, 10f)

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
         rightAxis.axisMaximum = 100f
         rightAxis.isEnabled = false

         val leftAxis = chart.axisLeft
         leftAxis.axisLineColor = Color.BLACK
         leftAxis.axisLineWidth = 0.8f
         leftAxis.gridColor = Color.parseColor("#bbbbbb")
         leftAxis.enableGridDashedLine(10f, 15f, 0f)
         leftAxis.axisMinimum = 0f
         leftAxis.axisMaximum = 100f
         leftAxis.valueFormatter = LeftAxisFormatter()
      }
   }

   class XValueFormatter : IValueFormatter {
      override fun getFormattedValue(value: Float, entry: Entry, dataSetIndex: Int, viewPortHandler: ViewPortHandler): String {
         return "${value.toInt()}%"
      }
   }

   class LeftAxisFormatter : IAxisValueFormatter {
      override fun getFormattedValue(value: Float, axis: AxisBase?): String {
         return if(value.toInt() == 0) "0" else "${value.toInt()}%"
      }
   }

   private fun rankView(type: Int, start: String, end: String) {
      val itemList = ArrayList<Item>()

      if(type == 1) {
         val getRanking = dataManager.getDrugRanking(calendarDate.toString())
         for(i in 0 until getRanking.size) {
            val getDrug = dataManager.getDrug(getRanking[i].int2)
            itemList.add(Item(string1 = getRanking[i].int1.toString(), string2 = getDrug.name))
         }
      }else {
         val getRanking = dataManager.getDrugRanking(start, end)
         for(i in 0 until getRanking.size) {
            val getDrug = dataManager.getDrug(getRanking[i].int2)
            itemList.add(Item(string1 = getRanking[i].int1.toString(), string2 = getDrug.name))
         }
      }

      if(itemList.size > 0) {
         binding.tvEmpty1.visibility = View.GONE
         binding.recyclerView.visibility = View.VISIBLE

         when(itemList.size) {
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