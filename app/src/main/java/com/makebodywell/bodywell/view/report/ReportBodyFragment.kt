package com.makebodywell.bodywell.view.report

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ViewPortHandler
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentReportBodyBinding
import com.makebodywell.bodywell.model.Body
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
import com.makebodywell.bodywell.util.CalendarUtil.Companion.monthArray2
import com.makebodywell.bodywell.util.CalendarUtil.Companion.monthFormat
import com.makebodywell.bodywell.util.CalendarUtil.Companion.weekArray
import com.makebodywell.bodywell.util.CalendarUtil.Companion.weekFormat
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.MyApp
import java.text.SimpleDateFormat
import java.time.LocalDate

class ReportBodyFragment : Fragment() {
   private var _binding: FragmentReportBodyBinding? = null
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
      _binding = FragmentReportBodyBinding.inflate(layoutInflater)

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

      binding.clMenu2.setOnClickListener {
         replaceFragment1(requireActivity(), ReportFoodFragment())
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

      val itemList1 = ArrayList<Body>()
      val itemList2 = ArrayList<Body>()
      val itemList3 = ArrayList<Body>()

      val getBody = dataManager!!.getBody(MyApp.prefs.getId())
      for(i in 0 until getBody.size) {
         if (getBody[i].weight > 0) {
            itemList1.add(Body(weight = getBody[i].weight, regDate = format2.format(format1.parse(getBody[i].regDate)!!)))
         }
         if (getBody[i].bmi > 0) {
            itemList2.add(Body(bmi = getBody[i].bmi, regDate = format2.format(format1.parse(getBody[i].regDate)!!)))
         }
         if (getBody[i].fat > 0) {
            itemList3.add(Body(fat = getBody[i].fat, regDate = format2.format(format1.parse(getBody[i].regDate)!!)))
         }
      }

      if(itemList1.size > 0) {
         binding.tvEmpty1.visibility = View.GONE
         binding.lineChart1.visibility = View.VISIBLE

         val entries = ArrayList<Entry>()
         val xValue = ArrayList<String>()

         for(i in 0 until itemList1.size) {
            entries.add(Entry(i.toFloat(), itemList1[i].weight.toFloat()))
            xValue.add(itemList1[i].regDate)
         }

         setupChart(binding.lineChart1, entries, xValue)
      }else {
         binding.tvEmpty1.visibility = View.VISIBLE
         binding.lineChart1.visibility = View.GONE
      }

      if(itemList2.size > 0) {
         binding.tvEmpty2.visibility = View.GONE
         binding.lineChart2.visibility = View.VISIBLE

         val entries = ArrayList<Entry>()
         val xValue = ArrayList<String>()

         for(i in 0 until itemList2.size) {
            entries.add(Entry(i.toFloat(), itemList2[i].bmi.toFloat()))
            xValue.add(itemList2[i].regDate)
         }

         setupChart(binding.lineChart2, entries, xValue)
      }else {
         binding.tvEmpty2.visibility = View.VISIBLE
         binding.lineChart2.visibility = View.GONE
      }

      if(itemList3.size > 0) {
         binding.tvEmpty3.visibility = View.GONE
         binding.lineChart3.visibility = View.VISIBLE

         val entries = ArrayList<Entry>()
         val xValue = ArrayList<String>()

         for(i in 0 until itemList3.size) {
            entries.add(Entry(i.toFloat(), itemList3[i].fat.toFloat()))
            xValue.add(itemList3[i].regDate)
         }

         setupChart(binding.lineChart3, entries, xValue)
      }else {
         binding.tvEmpty3.visibility = View.VISIBLE
         binding.lineChart3.visibility = View.GONE
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

      val itemList1 = ArrayList<Body>()
      val itemList2 = ArrayList<Body>()
      val itemList3 = ArrayList<Body>()

      val weekArray = weekArray(calendarDate)
      val getData = dataManager!!.getBody(MyApp.prefs.getId(), weekArray[0].toString(), weekArray[6].toString())
      for(i in 0 until getData.size) {
         if (getData[i].weight > 0) {
            itemList1.add(Body(weight = getData[i].weight, regDate = format2.format(format1.parse(getData[i].regDate)!!)))
         }
         if (getData[i].bmi > 0) {
            itemList2.add(Body(bmi = getData[i].bmi, regDate = format2.format(format1.parse(getData[i].regDate)!!)))
         }
         if (getData[i].fat > 0) {
            itemList3.add(Body(fat = getData[i].fat, regDate = format2.format(format1.parse(getData[i].regDate)!!)))
         }
      }

      if(itemList1.size > 0) {
         binding.tvEmpty1.visibility = View.GONE
         binding.lineChart1.visibility = View.VISIBLE

         val entries = ArrayList<Entry>()
         val xValue = ArrayList<String>()

         for(i in 0 until itemList1.size) {
            entries.add(Entry(i.toFloat(), itemList1[i].weight.toFloat()))
            xValue.add(itemList1[i].regDate)
         }

         setupChart(binding.lineChart1, entries, xValue)
      }else {
         binding.tvEmpty1.visibility = View.VISIBLE
         binding.lineChart1.visibility = View.GONE
      }

      if(itemList2.size > 0) {
         binding.tvEmpty2.visibility = View.GONE
         binding.lineChart2.visibility = View.VISIBLE

         val entries = ArrayList<Entry>()
         val xValue = ArrayList<String>()

         for(i in 0 until itemList2.size) {
            entries.add(Entry(i.toFloat(), itemList2[i].bmi.toFloat()))
            xValue.add(itemList2[i].regDate)
         }

         setupChart(binding.lineChart2, entries, xValue)
      }else {
         binding.tvEmpty2.visibility = View.VISIBLE
         binding.lineChart2.visibility = View.GONE
      }

      if(itemList3.size > 0) {
         binding.tvEmpty3.visibility = View.GONE
         binding.lineChart3.visibility = View.VISIBLE

         val entries = ArrayList<Entry>()
         val xValue = ArrayList<String>()

         for(i in 0 until itemList3.size) {
            entries.add(Entry(i.toFloat(), itemList3[i].fat.toFloat()))
            xValue.add(itemList3[i].regDate)
         }

         setupChart(binding.lineChart3, entries, xValue)
      }else {
         binding.tvEmpty3.visibility = View.VISIBLE
         binding.lineChart3.visibility = View.GONE
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

      val itemList1 = ArrayList<Body>()
      val itemList2 = ArrayList<Body>()
      val itemList3 = ArrayList<Body>()

      val monthArray = monthArray2(calendarDate)
      val getData = dataManager!!.getBody(MyApp.prefs.getId(), monthArray[0].toString(), monthArray[monthArray.size-1].toString())
      for(i in 0 until getData.size) {
         if (getData[i].weight > 0) {
            itemList1.add(Body(weight = getData[i].weight, regDate = format2.format(format1.parse(getData[i].regDate)!!)))
         }
         if (getData[i].bmi > 0) {
            itemList2.add(Body(bmi = getData[i].bmi, regDate = format2.format(format1.parse(getData[i].regDate)!!)))
         }
         if (getData[i].fat > 0) {
            itemList3.add(Body(fat = getData[i].fat, regDate = format2.format(format1.parse(getData[i].regDate)!!)))
         }
      }

      if(itemList1.size > 0) {
         binding.tvEmpty1.visibility = View.GONE
         binding.lineChart1.visibility = View.VISIBLE

         val entries = ArrayList<Entry>()
         val xValue = ArrayList<String>()

         for(i in 0 until itemList1.size) {
            entries.add(Entry(i.toFloat(), itemList1[i].weight.toFloat()))
            xValue.add(itemList1[i].regDate)
         }

         setupChart(binding.lineChart1, entries, xValue)
      }else {
         binding.tvEmpty1.visibility = View.VISIBLE
         binding.lineChart1.visibility = View.GONE
      }

      if(itemList2.size > 0) {
         binding.tvEmpty2.visibility = View.GONE
         binding.lineChart2.visibility = View.VISIBLE

         val entries = ArrayList<Entry>()
         val xValue = ArrayList<String>()

         for(i in 0 until itemList2.size) {
            entries.add(Entry(i.toFloat(), itemList2[i].bmi.toFloat()))
            xValue.add(itemList2[i].regDate)
         }

         setupChart(binding.lineChart2, entries, xValue)
      }else {
         binding.tvEmpty2.visibility = View.VISIBLE
         binding.lineChart2.visibility = View.GONE
      }

      if(itemList3.size > 0) {
         binding.tvEmpty3.visibility = View.GONE
         binding.lineChart3.visibility = View.VISIBLE

         val entries = ArrayList<Entry>()
         val xValue = ArrayList<String>()

         for(i in 0 until itemList3.size) {
            entries.add(Entry(i.toFloat(), itemList3[i].fat.toFloat()))
            xValue.add(itemList3[i].regDate)
         }

         setupChart(binding.lineChart3, entries, xValue)
      }else {
         binding.tvEmpty3.visibility = View.VISIBLE
         binding.lineChart3.visibility = View.GONE
      }
   }

   private fun setupChart(
      chart: LineChart,
      entries: ArrayList<Entry>,
      xValue: ArrayList<String>
   ) {
      chart.data = null
      chart.fitScreen()
      chart.xAxis.valueFormatter = null
      chart.clear()

      val legend = chart.legend
      legend.isEnabled = false

      val lineDataSet = LineDataSet(entries, "data")
      lineDataSet.lineWidth = 3.4f
      lineDataSet.circleRadius = 3.3f
      lineDataSet.color = Color.parseColor("#EAEAEA")
      lineDataSet.setCircleColor(resources.getColor(R.color.black))
      lineDataSet.setDrawCircles(true)
      lineDataSet.setDrawCircleHole(false)
      lineDataSet.setDrawHorizontalHighlightIndicator(false)
      lineDataSet.setDrawHighlightIndicators(false)

      val dataSets = ArrayList<ILineDataSet>()
      dataSets.add(lineDataSet)

      val lineData = LineData(dataSets)
      lineData.setValueTextSize(8f)
      lineData.setValueFormatter(MyValueFormatter())

      chart.data = lineData
      chart.notifyDataSetChanged()
      chart.invalidate()

      chart.description.isEnabled = false
      chart.axisRight.isEnabled = false
      chart.setScaleEnabled(false)
      chart.setPinchZoom(false)
      chart.setDrawGridBackground(false)
      chart.axisLeft.isEnabled = false
      chart.moveViewToX(1f)
      chart.setVisibleXRangeMaximum(7f)
      chart.isDragXEnabled = true
      chart.setExtraOffsets(0f, 0f, 0f, 10f)

      val xAxis = chart.xAxis
      xAxis.setDrawLabels(true)
      xAxis.isGranularityEnabled = true
      xAxis.position = XAxis.XAxisPosition.BOTTOM
      xAxis.textColor = Color.BLACK
      xAxis.axisLineColor = Color.BLACK
      xAxis.axisLineWidth = 1.1f
      xAxis.valueFormatter = IndexAxisValueFormatter(xValue)
      xAxis.granularity = 1f
      xAxis.spaceMax = 0.7f
      xAxis.spaceMin = 0.7f
      xAxis.gridColor = Color.parseColor("#EAEAEA")

      val yAxisLeft = chart.axisLeft
      yAxisLeft.textSize = 7f
   }

   class MyValueFormatter : IValueFormatter {
      override fun getFormattedValue(
         value: Float,
         entry: Entry,
         dataSetIndex: Int,
         viewPortHandler: ViewPortHandler
      ): String {
         return value.toString()
      }
   }
}
