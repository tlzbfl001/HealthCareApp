package com.makebodywell.bodywell.view.report

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.apollographql.apollo3.api.toInput
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.databinding.FragmentReportBodyBinding
import com.makebodywell.bodywell.model.Body
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.util.CalendarUtil
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
import com.makebodywell.bodywell.util.CalendarUtil.Companion.sundayForDate
import com.makebodywell.bodywell.util.CalendarUtil.Companion.weekArray
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class ReportBodyFragment : Fragment() {
   private var _binding: FragmentReportBodyBinding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null

   private var calendarDate = LocalDate.now()

   private var itemList = ArrayList<Body>()
   private var entries = ArrayList<Entry>()
   private var xValue = ArrayList<String>()
   private var dateType = 0

   private val formatter1 = SimpleDateFormat("yyyy-MM-dd")
   private val formatter2 = SimpleDateFormat("M.dd")

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentReportBodyBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      setupView()
      dailyView()

      return binding.root
   }

   private fun setupView() {
      binding.pbBody.max = 100
      binding.pbBody.progress = 50
      binding.pbFood.max = 100
      binding.pbFood.progress = 50
      binding.pbExercise.max = 100
      binding.pbExercise.progress = 50
      binding.pbDrug.max = 100
      binding.pbDrug.progress = 50

      binding.tvCalTitle.text = dateFormat(calendarDate)

      binding.pbFood.setOnClickListener {
         replaceFragment1(requireActivity(), ReportFoodFragment())
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
               val calendar = Calendar.getInstance()
               val split = calendarDate.toString().split("-")
               calendar.set(split[0].toInt(), split[1].toInt()-1, split[2].toInt())
               binding.tvCalTitle.text = "${split[0]}년 ${split[1]}월 ${calendar.get(Calendar.WEEK_OF_MONTH)}주차"
               weeklyView()
            }
            2->{
               calendarDate = calendarDate!!.minusDays(1)
               binding.tvCalTitle.text = dateFormat(calendarDate)
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
               val calendar = Calendar.getInstance()
               val split = calendarDate.toString().split("-")
               calendar.set(split[0].toInt(), split[1].toInt()-1, split[2].toInt())
               binding.tvCalTitle.text = "${split[0]}년 ${split[1]}월 ${calendar.get(Calendar.WEEK_OF_MONTH)}주차"
               weeklyView()
            }
            2->{
               calendarDate = calendarDate!!.plusDays(1)
               binding.tvCalTitle.text = dateFormat(calendarDate)
               monthlyView()
            }
         }
      }

      binding.tvDaily.setOnClickListener {
         binding.tvCalTitle.text = dateFormat(calendarDate)
         dailyView()
      }

      binding.tvWeekly.setOnClickListener {
         weeklyView()
         val calendar = Calendar.getInstance()
         val split = calendarDate.toString().split("-")
         calendar.set(split[0].toInt(), split[1].toInt()-1, split[2].toInt())
         binding.tvCalTitle.text = "${split[0]}년 ${split[1]}월 ${calendar.get(Calendar.WEEK_OF_MONTH)}주차"
      }

      binding.tvMonthly.setOnClickListener {
         binding.tvCalTitle.text = dateFormat(calendarDate)
         monthlyView()
      }
   }

   private fun dailyView() {
      binding.tvDaily.setBackgroundResource(R.drawable.rec_12_blue)
      binding.tvDaily.setTextColor(Color.WHITE)
      binding.tvWeekly.setBackgroundResource(R.drawable.rec_12_border_gray)
      binding.tvWeekly.setTextColor(Color.BLACK)
      binding.tvMonthly.setBackgroundResource(R.drawable.rec_12_border_gray)
      binding.tvMonthly.setTextColor(Color.BLACK)
      dateType = 0

      itemList.clear()

      for(i in 6 downTo 0) {
         val date = calendarDate.minusDays(i.toLong())
         val getBodyDaily = dataManager!!.getBodyDaily(date.toString())
         itemList.add(Body(weight = getBodyDaily.weight, regDate = formatter2.format(formatter1.parse(date.toString())!!)))
      }

      setupChart(binding.lineChart1)
      setupChart(binding.lineChart2)
      setupChart(binding.lineChart3)
   }

   private fun weeklyView() {
      binding.tvDaily.setBackgroundResource(R.drawable.rec_12_border_gray)
      binding.tvDaily.setTextColor(Color.BLACK)
      binding.tvWeekly.setBackgroundResource(R.drawable.rec_12_blue)
      binding.tvWeekly.setTextColor(Color.WHITE)
      binding.tvMonthly.setBackgroundResource(R.drawable.rec_12_border_gray)
      binding.tvMonthly.setTextColor(Color.BLACK)
      dateType = 1

      itemList.clear()

      val weekArray = weekArray(calendarDate!!)
      val startDate = sundayForDate(calendarDate!!)
      val endDate = startDate!!.plusWeeks(1).minusDays(1)
      val getBodyWeekly = dataManager!!.getBodyWeekly(startDate.toString(), endDate.toString())

      for(i in 0 until weekArray.size) {
         var check = false
         for(j in 0 until getBodyWeekly.size) {
            if(weekArray[i].toString() == getBodyWeekly[j].regDate) {
               itemList.add(Body(weight = getBodyWeekly[j].weight, regDate = formatter2.format(formatter1.parse("${weekArray[i]}")!!)))
               check = true
            }
            if(j == (getBodyWeekly.size -1) && !check) {
               itemList.add(Body(weight = 0.0, regDate = formatter2.format(formatter1.parse("${weekArray[i]}")!!)))
            }
         }
      }

      if(itemList.size == 0) {
         for(i in 0 until weekArray.size) {
            itemList.add(Body(weight = 0.0, regDate = formatter2.format(formatter1.parse("${weekArray[i]}")!!)))
         }
      }

      setupChart(binding.lineChart1)
      setupChart(binding.lineChart2)
      setupChart(binding.lineChart3)
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

   private fun setupChart(lineChart: LineChart) {
      entries.clear()
      xValue.clear()

      val colors = intArrayOf(R.color.black, R.color.black, R.color.black, R.color.black, R.color.black, R.color.black, R.color.red)

      for(i in 0 until itemList.size) {
         entries.add(Entry(i.toFloat(), itemList[i].weight.toFloat()))
         xValue.add(itemList[i].regDate)
      }

      val legend = lineChart.legend
      legend.isEnabled = false

      lineChart.description.isEnabled = false
      lineChart.axisRight.isEnabled = false
      lineChart.setVisibleXRangeMaximum(7f)
      lineChart.setScaleEnabled(false)
      lineChart.setPinchZoom(false)
      lineChart.moveViewToX(1f)
      lineChart.isScrollContainer = true
      lineChart.setDrawGridBackground(false)
      lineChart.axisLeft.isEnabled = false
      lineChart.setExtraOffsets(0f, 0f, 0f, 10f)

      val lineDataSet = LineDataSet(entries, "data")
      lineDataSet.lineWidth = 3f
      lineDataSet.circleRadius = 3f
      lineDataSet.color = Color.parseColor("#EAEAEA")
      lineDataSet.setCircleColor(resources.getColor(R.color.black))
      lineDataSet.setDrawCircles(true)
      lineDataSet.setDrawCircleHole(false)
      lineDataSet.setDrawHorizontalHighlightIndicator(false)
      lineDataSet.setDrawHighlightIndicators(false)

      val dataSets = ArrayList<ILineDataSet>()
      dataSets.add(lineDataSet)

      val lineData = LineData(dataSets)
      lineChart.data = lineData
      lineData.setValueTextSize(8f)

      val xAxis = lineChart.xAxis
      xAxis.setDrawLabels(true)
      xAxis.position = XAxis.XAxisPosition.BOTTOM
      xAxis.textColor = Color.BLACK
      xAxis.axisLineColor = Color.BLACK
      xAxis.axisLineWidth = 1.1f
      xAxis.valueFormatter = IndexAxisValueFormatter(xValue)
      xAxis.granularity = 1f
      xAxis.spaceMax = 0.7f
      xAxis.spaceMin = 0.7f
      xAxis.gridColor = Color.parseColor("#EAEAEA")

      val yAxisLeft = lineChart.axisLeft
      yAxisLeft.textSize = 7f

      lineChart.invalidate()
   }
}
