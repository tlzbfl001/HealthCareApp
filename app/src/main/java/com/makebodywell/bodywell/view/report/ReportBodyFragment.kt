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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
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
import java.text.SimpleDateFormat
import java.time.LocalDate

class ReportBodyFragment : Fragment() {
   private var _binding: FragmentReportBodyBinding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null

   private var calendarDate = LocalDate.now()

   private var itemList = ArrayList<Body>()
   private var entries = ArrayList<Entry>()
   private var xValue = ArrayList<String>()
   private var dateType = 0

   private val format1 = SimpleDateFormat("yyyy-MM-dd")
   private val format2 = SimpleDateFormat("M.dd")

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

      val getData = dataManager!!.getBodyDaily()
      for(i in 0 until getData.size) {
         itemList.add(Body(weight = getData[i].weight, regDate = format2.format(format1.parse(getData[i].regDate)!!)))
      }

      if(itemList.size > 0) {
         binding.tvEmpty1.visibility = View.GONE
         binding.lineChart1.visibility = View.VISIBLE
         setupChart(binding.lineChart1)
      }else {
         binding.tvEmpty1.visibility = View.VISIBLE
         binding.lineChart1.visibility = View.GONE
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

      itemList.clear()

      val weekArray = weekArray(calendarDate)
      val getData = dataManager!!.getBody(weekArray[0].toString(), weekArray[6].toString())

      for(i in 0 until getData.size) {
         itemList.add(Body(weight = getData[i].weight, regDate = format2.format(format1.parse(getData[i].regDate)!!)))
      }

      if(itemList.size > 0) {
         binding.tvEmpty1.visibility = View.GONE
         binding.lineChart1.visibility = View.VISIBLE
         setupChart(binding.lineChart1)
      }else {
         binding.tvEmpty1.visibility = View.VISIBLE
         binding.lineChart1.visibility = View.GONE
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

      itemList.clear()

      val monthArray = monthArray2(calendarDate)
      val getData = dataManager!!.getBody(monthArray[0].toString(), monthArray[monthArray.size-1].toString())
      for(i in 0 until getData.size) {
         itemList.add(Body(weight = getData[i].weight, regDate = format2.format(format1.parse(getData[i].regDate)!!)))
      }

      if(itemList.size > 0) {
         binding.tvEmpty1.visibility = View.GONE
         binding.lineChart1.visibility = View.VISIBLE
         setupChart(binding.lineChart1)
      }else {
         binding.tvEmpty1.visibility = View.VISIBLE
         binding.lineChart1.visibility = View.GONE
      }
   }

   private fun setupChart(lineChart: LineChart) {
      entries.clear()
      xValue.clear()

      for(i in 0 until itemList.size) {
         entries.add(Entry(i.toFloat(), itemList[i].weight.toFloat()))
         xValue.add(itemList[i].regDate)
      }

      val legend = lineChart.legend
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

      lineChart.data = null
      lineChart.fitScreen()
      lineChart.xAxis.valueFormatter = null
      lineChart.clear()
      lineChart.data = lineData
      lineChart.notifyDataSetChanged()
      lineChart.invalidate()

      lineChart.description.isEnabled = false
      lineChart.axisRight.isEnabled = false
      lineChart.setVisibleXRangeMaximum(7f)
      lineChart.setScaleEnabled(false)
      lineChart.setPinchZoom(false)
      lineChart.moveViewToX(1f)
      lineChart.setDrawGridBackground(false)
      lineChart.axisLeft.isEnabled = false
      lineChart.isDragXEnabled = dateType==0 || dateType==2
      lineChart.setExtraOffsets(0f, 0f, 0f, 10f)

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
      yAxisLeft.granularity = 10f
      yAxisLeft.mAxisMaximum=60f
   }
}
