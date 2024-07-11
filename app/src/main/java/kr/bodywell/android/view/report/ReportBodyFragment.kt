package kr.bodywell.android.view.report

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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
import kr.bodywell.android.R
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentReportBodyBinding
import kr.bodywell.android.model.Body
import kr.bodywell.android.util.CalendarUtil.Companion.dateFormat
import kr.bodywell.android.util.CalendarUtil.Companion.monthArray2
import kr.bodywell.android.util.CalendarUtil.Companion.monthFormat
import kr.bodywell.android.util.CalendarUtil.Companion.weekArray
import kr.bodywell.android.util.CalendarUtil.Companion.weekFormat
import kr.bodywell.android.util.CustomUtil.Companion.replaceFragment1
import kr.bodywell.android.view.home.MainFragment
import java.text.SimpleDateFormat
import java.time.LocalDate

class ReportBodyFragment : Fragment() {
   private var _binding: FragmentReportBodyBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private val format1 = SimpleDateFormat("yyyy-MM-dd")
   private val format2 = SimpleDateFormat("M.dd")
   private var calendarDate = LocalDate.now()
   private var dateType = 0

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
      dataManager.open()

      binding.tvDate.text = dateFormat(calendarDate)

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
               binding.tvDate.text = dateFormat(calendarDate)
               dailyView()
            }
            1->{
               calendarDate = calendarDate!!.minusWeeks(1)
               binding.tvDate.text = weekFormat(calendarDate)
               weeklyView()
            }
            2->{
               calendarDate = calendarDate!!.minusMonths(1)
               binding.tvDate.text = monthFormat(calendarDate)
               monthlyView()
            }
         }
      }

      binding.clNext.setOnClickListener {
         when(dateType) {
            0->{
               calendarDate = calendarDate!!.plusDays(1)
               binding.tvDate.text = dateFormat(calendarDate)
               dailyView()
            }
            1->{
               calendarDate = calendarDate!!.plusWeeks(1)
               binding.tvDate.text = weekFormat(calendarDate)
               weeklyView()
            }
            2->{
               calendarDate = calendarDate!!.plusMonths(1)
               binding.tvDate.text = monthFormat(calendarDate)
               monthlyView()
            }
         }
      }

      binding.tvDaily.setOnClickListener {
         binding.tvDate.text = dateFormat(calendarDate)
         dailyView()
      }

      binding.tvWeekly.setOnClickListener {
         binding.tvDate.text = weekFormat(calendarDate)
         weeklyView()
      }

      binding.tvMonthly.setOnClickListener {
         binding.tvDate.text = monthFormat(calendarDate)
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
      dateType = 0

      val getBody = dataManager.getBody(calendarDate.toString())

      if (getBody.weight > 0) {
         binding.tvEmpty1.visibility = View.GONE
         binding.lineChart1.visibility = View.VISIBLE

         val entries = ArrayList<Entry>()
         val xValue = ArrayList<String>()

         val date = format2.format(format1.parse(getBody.created)!!)
         entries.add(Entry(0f, getBody.weight.toFloat()))
         xValue.add(date)

         setupChart(binding.lineChart1, entries, xValue)
      }

      if (getBody.bmi > 0) {
         binding.tvEmpty2.visibility = View.GONE
         binding.lineChart2.visibility = View.VISIBLE

         val entries = ArrayList<Entry>()
         val xValue = ArrayList<String>()

         val date = format2.format(format1.parse(getBody.created)!!)
         entries.add(Entry(0f, getBody.bmi.toFloat()))
         xValue.add(date)

         setupChart(binding.lineChart2, entries, xValue)
      }

      if (getBody.fat > 0) {
         binding.tvEmpty3.visibility = View.GONE
         binding.lineChart3.visibility = View.VISIBLE

         val entries = ArrayList<Entry>()
         val xValue = ArrayList<String>()

         val date = format2.format(format1.parse(getBody.created)!!)
         entries.add(Entry(0f, getBody.fat.toFloat()))
         xValue.add(date)

         setupChart(binding.lineChart3, entries, xValue)
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
      dateType = 1

      val itemList1 = ArrayList<Body>()
      val itemList2 = ArrayList<Body>()
      val itemList3 = ArrayList<Body>()

      val weekArray = weekArray(calendarDate)
      val getData = dataManager.getBody(weekArray[0].toString(), weekArray[6].toString())
      for(i in 0 until getData.size) {
         if (getData[i].weight > 0) {
            itemList1.add(Body(weight = getData[i].weight, created = format2.format(format1.parse(getData[i].created)!!)))
         }
         if (getData[i].bmi > 0) {
            itemList2.add(Body(bmi = getData[i].bmi, created = format2.format(format1.parse(getData[i].created)!!)))
         }
         if (getData[i].fat > 0) {
            itemList3.add(Body(fat = getData[i].fat, created = format2.format(format1.parse(getData[i].created)!!)))
         }
      }

      if(itemList1.size > 0) {
         binding.tvEmpty1.visibility = View.GONE
         binding.lineChart1.visibility = View.VISIBLE

         val entries = ArrayList<Entry>()
         val xValue = ArrayList<String>()

         for(i in 0 until itemList1.size) {
            entries.add(Entry(i.toFloat(), itemList1[i].weight.toFloat()))
            xValue.add(itemList1[i].created)
         }

         setupChart(binding.lineChart1, entries, xValue)
      }

      if(itemList2.size > 0) {
         binding.tvEmpty2.visibility = View.GONE
         binding.lineChart2.visibility = View.VISIBLE

         val entries = ArrayList<Entry>()
         val xValue = ArrayList<String>()

         for(i in 0 until itemList2.size) {
            entries.add(Entry(i.toFloat(), itemList2[i].bmi.toFloat()))
            xValue.add(itemList2[i].created)
         }

         setupChart(binding.lineChart2, entries, xValue)
      }

      if(itemList3.size > 0) {
         binding.tvEmpty3.visibility = View.GONE
         binding.lineChart3.visibility = View.VISIBLE

         val entries = ArrayList<Entry>()
         val xValue = ArrayList<String>()

         for(i in 0 until itemList3.size) {
            entries.add(Entry(i.toFloat(), itemList3[i].fat.toFloat()))
            xValue.add(itemList3[i].created)
         }

         setupChart(binding.lineChart3, entries, xValue)
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
      dateType = 2

      val itemList1 = ArrayList<Body>()
      val itemList2 = ArrayList<Body>()
      val itemList3 = ArrayList<Body>()

      val monthArray = monthArray2(calendarDate)
      val getData = dataManager.getBody(monthArray[0].toString(), monthArray[monthArray.size-1].toString())
      for(i in 0 until getData.size) {
         if (getData[i].weight > 0) {
            itemList1.add(Body(weight = getData[i].weight, created = format2.format(format1.parse(getData[i].created)!!)))
         }
         if (getData[i].bmi > 0) {
            itemList2.add(Body(bmi = getData[i].bmi, created = format2.format(format1.parse(getData[i].created)!!)))
         }
         if (getData[i].fat > 0) {
            itemList3.add(Body(fat = getData[i].fat, created = format2.format(format1.parse(getData[i].created)!!)))
         }
      }

      if(itemList1.size > 0) {
         binding.tvEmpty1.visibility = View.GONE
         binding.lineChart1.visibility = View.VISIBLE

         val entries = ArrayList<Entry>()
         val xValue = ArrayList<String>()

         for(i in 0 until itemList1.size) {
            entries.add(Entry(i.toFloat(), itemList1[i].weight.toFloat()))
            xValue.add(itemList1[i].created)
         }

         setupChart(binding.lineChart1, entries, xValue)
      }

      if(itemList2.size > 0) {
         binding.tvEmpty2.visibility = View.GONE
         binding.lineChart2.visibility = View.VISIBLE

         val entries = ArrayList<Entry>()
         val xValue = ArrayList<String>()

         for(i in 0 until itemList2.size) {
            entries.add(Entry(i.toFloat(), itemList2[i].bmi.toFloat()))
            xValue.add(itemList2[i].created)
         }

         setupChart(binding.lineChart2, entries, xValue)
      }

      if(itemList3.size > 0) {
         binding.tvEmpty3.visibility = View.GONE
         binding.lineChart3.visibility = View.VISIBLE

         val entries = ArrayList<Entry>()
         val xValue = ArrayList<String>()

         for(i in 0 until itemList3.size) {
            entries.add(Entry(i.toFloat(), itemList3[i].fat.toFloat()))
            xValue.add(itemList3[i].created)
         }

         setupChart(binding.lineChart3, entries, xValue)
      }
   }

   private fun resetChart() {
      binding.tvEmpty1.visibility = View.VISIBLE
      binding.lineChart1.visibility = View.GONE
      binding.tvEmpty2.visibility = View.VISIBLE
      binding.lineChart2.visibility = View.GONE
      binding.tvEmpty3.visibility = View.VISIBLE
      binding.lineChart3.visibility = View.GONE
   }

   private fun setupChart(chart: LineChart, entries: ArrayList<Entry>, xValue: ArrayList<String>) {
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
      chart.animateY(5000)
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

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}
