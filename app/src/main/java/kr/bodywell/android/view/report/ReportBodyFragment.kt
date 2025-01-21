package kr.bodywell.android.view.report

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.databinding.FragmentReportBodyBinding
import kr.bodywell.android.model.Body
import kr.bodywell.android.util.CalendarUtil.dateFormat
import kr.bodywell.android.util.CalendarUtil.monthArray2
import kr.bodywell.android.util.CalendarUtil.monthFormat
import kr.bodywell.android.util.CalendarUtil.weekArray
import kr.bodywell.android.util.CalendarUtil.weekFormat
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.util.CustomUtil.replaceFragment3
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.util.MyApp.Companion.powerSync
import kr.bodywell.android.view.MainFragment
import java.text.SimpleDateFormat
import java.time.LocalDate

class ReportBodyFragment : Fragment() {
   private var _binding: FragmentReportBodyBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private val format1 = SimpleDateFormat("yyyy-MM-dd")
   private val format2 = SimpleDateFormat("M.dd")
   private var calendarDate = LocalDate.now()
   private var dateType = 0

   override fun onAttach(context: Context) {
      super.onCreate(null)
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment1(requireActivity().supportFragmentManager, MainFragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentReportBodyBinding.inflate(layoutInflater)

      setStatusBar(requireActivity(), binding.mainLayout)

      binding.tvDate.text = dateFormat(calendarDate)

      binding.clMenu2.setOnClickListener {
         replaceFragment3(requireActivity().supportFragmentManager, ReportFoodFragment())
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
      binding.tvDaily.setBackgroundResource(R.drawable.rec_5_purple)
      binding.tvDaily.setTextColor(Color.WHITE)
      binding.tvWeekly.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvWeekly.setTextColor(Color.BLACK)
      binding.tvMonthly.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvMonthly.setTextColor(Color.BLACK)
      dateType = 0
      resetChart()

      lifecycleScope.launch {
         val getBody = powerSync.getBody(calendarDate.toString())

         if(getBody.weight != null && getBody.weight!! > 0) {
            binding.tvEmpty1.visibility = View.GONE
            binding.lineChart1.visibility = View.VISIBLE

            val entries = ArrayList<Entry>()
            val xValue = ArrayList<String>()

            val date = format2.format(format1.parse(getBody.createdAt)!!)
            entries.add(Entry(0f, getBody.weight!!.toFloat()))
            xValue.add(date)

            setupChart(binding.lineChart1, entries, xValue)
         }

         if (getBody.bodyMassIndex != null && getBody.bodyMassIndex!! > 0) {
            binding.tvEmpty2.visibility = View.GONE
            binding.lineChart2.visibility = View.VISIBLE

            val entries = ArrayList<Entry>()
            val xValue = ArrayList<String>()

            val date = format2.format(format1.parse(getBody.createdAt)!!)
            entries.add(Entry(0f, getBody.bodyMassIndex!!.toFloat()))
            xValue.add(date)

            setupChart(binding.lineChart2, entries, xValue)
         }

         if (getBody.bodyFatPercentage != null && getBody.bodyFatPercentage!! > 0) {
            binding.tvEmpty3.visibility = View.GONE
            binding.lineChart3.visibility = View.VISIBLE

            val entries = ArrayList<Entry>()
            val xValue = ArrayList<String>()

            val date = format2.format(format1.parse(getBody.createdAt)!!)
            entries.add(Entry(0f, getBody.bodyFatPercentage!!.toFloat()))
            xValue.add(date)

            setupChart(binding.lineChart3, entries, xValue)
         }
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
      resetChart()

      val itemList1 = ArrayList<Body>()
      val itemList2 = ArrayList<Body>()
      val itemList3 = ArrayList<Body>()

      val weekArray = weekArray(calendarDate)

      lifecycleScope.launch {
         val getBody = powerSync.getBodies(weekArray[0].toString(), weekArray[6].toString())

         for(i in getBody.indices) {
            if(getBody[i].weight != null && getBody[i].weight!! > 0) {
               itemList1.add(Body(weight = getBody[i].weight, time = format2.format(format1.parse(getBody[i].time!!)!!)))
            }
            if(getBody[i].bodyMassIndex != null && getBody[i].bodyMassIndex!! > 0) {
               itemList2.add(Body(bodyMassIndex = getBody[i].bodyMassIndex, time = format2.format(format1.parse(getBody[i].time!!)!!)))
            }
            if(getBody[i].bodyFatPercentage != null && getBody[i].bodyFatPercentage!! > 0) {
               itemList3.add(Body(bodyFatPercentage = getBody[i].bodyFatPercentage, time = format2.format(format1.parse(getBody[i].time!!)!!)))
            }
         }
      }

      if(itemList1.size > 0) {
         binding.tvEmpty1.visibility = View.GONE
         binding.lineChart1.visibility = View.VISIBLE

         val entries = ArrayList<Entry>()
         val xValue = ArrayList<String>()

         for(i in 0 until itemList1.size) {
            entries.add(Entry(i.toFloat(), itemList1[i].weight!!.toFloat()))
            xValue.add(itemList1[i].time!!)
         }

         setupChart(binding.lineChart1, entries, xValue)
      }

      if(itemList2.size > 0) {
         binding.tvEmpty2.visibility = View.GONE
         binding.lineChart2.visibility = View.VISIBLE

         val entries = ArrayList<Entry>()
         val xValue = ArrayList<String>()

         for(i in 0 until itemList2.size) {
            entries.add(Entry(i.toFloat(), itemList2[i].bodyMassIndex!!.toFloat()))
            xValue.add(itemList2[i].time!!)
         }

         setupChart(binding.lineChart2, entries, xValue)
      }

      if(itemList3.size > 0) {
         binding.tvEmpty3.visibility = View.GONE
         binding.lineChart3.visibility = View.VISIBLE

         val entries = ArrayList<Entry>()
         val xValue = ArrayList<String>()

         for(i in 0 until itemList3.size) {
            entries.add(Entry(i.toFloat(), itemList3[i].bodyFatPercentage!!.toFloat()))
            xValue.add(itemList3[i].time!!)
         }

         setupChart(binding.lineChart3, entries, xValue)
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
      resetChart()

      val itemList1 = ArrayList<Body>()
      val itemList2 = ArrayList<Body>()
      val itemList3 = ArrayList<Body>()

      val monthArray = monthArray2(calendarDate)

      lifecycleScope.launch {
         val getBody = powerSync.getBodies(monthArray[0].toString(), monthArray[monthArray.size-1].toString())

         for(i in getBody.indices) {
            if(getBody[i].weight != null && getBody[i].weight!! > 0) {
               itemList1.add(Body(weight = getBody[i].weight, time = format2.format(format1.parse(getBody[i].time!!)!!)))
            }
            if(getBody[i].bodyMassIndex != null && getBody[i].bodyMassIndex!! > 0) {
               itemList2.add(Body(bodyMassIndex = getBody[i].bodyMassIndex, time = format2.format(format1.parse(getBody[i].time!!)!!)))
            }
            if(getBody[i].bodyFatPercentage != null && getBody[i].bodyFatPercentage!! > 0) {
               itemList3.add(Body(bodyFatPercentage = getBody[i].bodyFatPercentage, time = format2.format(format1.parse(getBody[i].time!!)!!)))
            }
         }
      }

      if(itemList1.size > 0) {
         binding.tvEmpty1.visibility = View.GONE
         binding.lineChart1.visibility = View.VISIBLE

         val entries = ArrayList<Entry>()
         val xValue = ArrayList<String>()

         for(i in 0 until itemList1.size) {
            entries.add(Entry(i.toFloat(), itemList1[i].weight!!.toFloat()))
            xValue.add(itemList1[i].time!!)
         }

         setupChart(binding.lineChart1, entries, xValue)
      }

      if(itemList2.size > 0) {
         binding.tvEmpty2.visibility = View.GONE
         binding.lineChart2.visibility = View.VISIBLE

         val entries = ArrayList<Entry>()
         val xValue = ArrayList<String>()

         for(i in 0 until itemList2.size) {
            entries.add(Entry(i.toFloat(), itemList2[i].bodyMassIndex!!.toFloat()))
            xValue.add(itemList2[i].time!!)
         }

         setupChart(binding.lineChart2, entries, xValue)
      }

      if(itemList3.size > 0) {
         binding.tvEmpty3.visibility = View.GONE
         binding.lineChart3.visibility = View.VISIBLE

         val entries = ArrayList<Entry>()
         val xValue = ArrayList<String>()

         for(i in 0 until itemList3.size) {
            entries.add(Entry(i.toFloat(), itemList3[i].bodyFatPercentage!!.toFloat()))
            xValue.add(itemList3[i].time!!)
         }

         setupChart(binding.lineChart3, entries, xValue)
      }
   }

   private fun setupChart(chart: LineChart, entries: ArrayList<Entry>, xValue: ArrayList<String>) {
      chart.data = null
      chart.fitScreen()
      chart.xAxis.valueFormatter = null
      chart.clear()

      val legend = chart.legend
      legend.isEnabled = false

      val xAxis = chart.xAxis
      xAxis.setDrawLabels(true)
      xAxis.isGranularityEnabled = true
      xAxis.position = XAxis.XAxisPosition.BOTTOM
      xAxis.textColor = resources.getColor(R.color.black_white)
      xAxis.axisLineColor = resources.getColor(R.color.black_white)
      xAxis.axisLineWidth = 1.1f
      xAxis.valueFormatter = IndexAxisValueFormatter(xValue)
      xAxis.granularity = 1f
      xAxis.spaceMax = 0.7f
      xAxis.spaceMin = 0.7f
      xAxis.gridColor = Color.parseColor("#EAEAEA")

      val yAxisLeft = chart.axisLeft
      yAxisLeft.textSize = 8f

      val lineDataSet = LineDataSet(entries, "data")
      lineDataSet.lineWidth = 3.4f
      lineDataSet.circleRadius = 3.3f
      lineDataSet.color = Color.parseColor("#EAEAEA")
      lineDataSet.setCircleColor(resources.getColor(R.color.black_white))
      lineDataSet.setDrawCircles(true)
      lineDataSet.setDrawCircleHole(false)
      lineDataSet.setDrawHorizontalHighlightIndicator(false)
      lineDataSet.setDrawHighlightIndicators(false)

      val dataSets = ArrayList<ILineDataSet>()
      dataSets.add(lineDataSet)

      val lineData = LineData(dataSets)
      lineData.setValueTextSize(9f)
      lineData.setValueFormatter(MyValueFormatter())
      lineData.setValueTextColor(resources.getColor(R.color.black_white))

      chart.data = lineData
      chart.description.isEnabled = false
      chart.axisRight.isEnabled = false
      chart.setScaleEnabled(false)
      chart.setPinchZoom(false)
      chart.setDrawGridBackground(false)
      chart.axisLeft.isEnabled = false
      chart.setVisibleXRangeMaximum(7f)
      chart.isDragXEnabled = true
      chart.setExtraOffsets(12f, 15f, 15f, 10f)
      chart.animateY(1000, Easing.EasingOption.EaseInOutExpo)
      chart.notifyDataSetChanged()
      chart.invalidate()
   }

   private fun resetChart() {
      binding.tvEmpty1.visibility = View.VISIBLE
      binding.lineChart1.visibility = View.GONE
      binding.tvEmpty2.visibility = View.VISIBLE
      binding.lineChart2.visibility = View.GONE
      binding.tvEmpty3.visibility = View.VISIBLE
      binding.lineChart3.visibility = View.GONE
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
