package com.makebodywell.bodywell.view.report

<<<<<<< HEAD
=======
import android.content.Context
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
<<<<<<< HEAD
=======
import androidx.activity.OnBackPressedCallback
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.makebodywell.bodywell.databinding.FragmentReportFoodBinding
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.view.home.MainFragment
import java.time.LocalDate

class ReportFoodFragment : Fragment() {
   private var _binding: FragmentReportFoodBinding? = null
   private val binding get() = _binding!!

   private var calendarDate = LocalDate.now()

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentReportFoodBinding.inflate(layoutInflater)

      setupView()

      settingChart1(binding.chart1)
      settingChart2(binding.chart2)
      settingChart3(binding.chart3)

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
         calendarDate = calendarDate!!.minusDays(1)
         binding.tvCalTitle.text = dateFormat(calendarDate)
      }

      binding.ivNext.setOnClickListener {
         calendarDate = calendarDate!!.plusDays(1)
         binding.tvCalTitle.text = dateFormat(calendarDate)
      }
   }

   private fun settingChart1(chart: CombinedChart) {
      chartCommon(chart)

      val data = CombinedData()

      // lineChart 설정
      val lineData = LineData()
      val entries = ArrayList<Entry>()

      val lineList = floatArrayOf(1350f, 1089f, 870f, 1135f, 1485f, 487f, 1201f)
      for (index in lineList.indices) {
         entries.add(Entry(index.toFloat(), lineList[index]))
      }

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

      // barChart 설정
      val barEntries = java.util.ArrayList<BarEntry>()
      barEntries.add(BarEntry(0f, floatArrayOf(350f, 250f, 500f, 250f)))
      barEntries.add(BarEntry(1f, floatArrayOf(200f, 489f, 100f, 300f)))
      barEntries.add(BarEntry(2f, floatArrayOf(150f, 450f, 170f, 100f)))
      barEntries.add(BarEntry(3f, floatArrayOf(400f, 100f, 300f, 335f)))
      barEntries.add(BarEntry(4f, floatArrayOf(500f, 705f, 140f, 140f)))
      barEntries.add(BarEntry(5f, floatArrayOf(100f, 100f, 237f, 50f)))
      barEntries.add(BarEntry(6f, floatArrayOf(450f, 100f, 50f, 601f)))

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
   }

   private fun settingChart2(chart: CombinedChart) {
      chartCommon(chart)

      val data = CombinedData()

      // lineChart 설정
      val lineData = LineData()
      val entries = ArrayList<Entry>()

      val lineList = floatArrayOf(1350f, 1089f, 870f, 1135f, 1485f, 487f, 1201f)
      for (index in lineList.indices) {
         entries.add(Entry(index.toFloat(), lineList[index]))
      }

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

      // barChart 설정
      val barEntries = java.util.ArrayList<BarEntry>()
      barEntries.add(BarEntry(0f, floatArrayOf(350f, 250f, 500f, 250f)))
      barEntries.add(BarEntry(1f, floatArrayOf(200f, 489f, 100f, 300f)))
      barEntries.add(BarEntry(2f, floatArrayOf(150f, 450f, 170f, 100f)))
      barEntries.add(BarEntry(3f, floatArrayOf(400f, 100f, 300f, 335f)))
      barEntries.add(BarEntry(4f, floatArrayOf(500f, 705f, 140f, 140f)))
      barEntries.add(BarEntry(5f, floatArrayOf(100f, 100f, 237f, 50f)))
      barEntries.add(BarEntry(6f, floatArrayOf(450f, 100f, 50f, 601f)))

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
   }

   private fun settingChart3(chart: CombinedChart) {
      chartCommon(chart)

      val data = CombinedData()

      // lineChart 설정
      val lineData = LineData()
      val entries = ArrayList<Entry>()
      val lineList = floatArrayOf(600f, 900f, 800f, 1200f, 1500f, 600f, 1200f)
      for (index in lineList.indices) {
         entries.add(Entry(index.toFloat(), lineList[index]))
      }

      val lineDataSet = LineDataSet(entries, "Line DataSet")
      lineDataSet.color = Color.parseColor("#BBBBBB")
      lineDataSet.lineWidth = 0.5f
      lineDataSet.setCircleColor(Color.parseColor("#4477E6"))
      lineDataSet.circleRadius = 0.3f
      lineDataSet.setDrawValues(true)
      lineDataSet.valueTextSize = 8f
      lineDataSet.valueTextColor = Color.parseColor("#BBBBBB")
      lineDataSet.axisDependency = YAxis.AxisDependency.RIGHT
      lineDataSet.valueFormatter = DefaultValueFormatter(0)

      lineData.addDataSet(lineDataSet)
      data.setData(lineData)

      // barChart 설정
      val barEntries = java.util.ArrayList<BarEntry>()
      barEntries.add(BarEntry(0f, 600f))
      barEntries.add(BarEntry(1f, 900f))
      barEntries.add(BarEntry(2f, 800f))
      barEntries.add(BarEntry(3f, 1200f))
      barEntries.add(BarEntry(4f, 1500f))
      barEntries.add(BarEntry(5f, 600f))
      barEntries.add(BarEntry(6f, 1200f))

      val barDataSet = BarDataSet(barEntries, "")
      barDataSet.color = Color.parseColor("#4477E6")
      barDataSet.valueTextSize = 0f

      val barData = BarData(barDataSet)
      barData.barWidth = 0.27f

      data.setData(barData)

      chart.data = data
      chart.invalidate()
   }

   private fun chartCommon(chart: CombinedChart) {
      chart.description.isEnabled = false
      chart.legend.isEnabled = false
      chart.setScaleEnabled(false)
      chart.isClickable = false
      chart.isHighlightPerDragEnabled = false
      chart.isHighlightPerTapEnabled = false
      chart.setExtraOffsets(15f, 15f, 15f, 10f)

      val xAxis = chart.xAxis
      xAxis.axisLineColor = Color.BLACK
      xAxis.axisLineWidth = 0.8f
      xAxis.position = XAxis.XAxisPosition.BOTTOM
      xAxis.spaceMax = 0.6f
      xAxis.spaceMin = 0.6f
      xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("7.10", "7.11","7.12", "7.13", "7.14", "7.15", "오늘"))
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
}