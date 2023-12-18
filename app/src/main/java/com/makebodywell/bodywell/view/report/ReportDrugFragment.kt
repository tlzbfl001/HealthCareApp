package com.makebodywell.bodywell.view.report

<<<<<<< HEAD
=======
import android.content.Context
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
<<<<<<< HEAD
=======
import androidx.activity.OnBackPressedCallback
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.makebodywell.bodywell.databinding.FragmentReportDrugBinding
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
<<<<<<< HEAD
=======
import com.makebodywell.bodywell.view.home.MainFragment
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
import java.time.LocalDate

class ReportDrugFragment : Fragment() {
   private var _binding: FragmentReportDrugBinding? = null
   private val binding get() = _binding!!

   private var calendarDate = LocalDate.now()

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentReportDrugBinding.inflate(layoutInflater)

      setupView()

      settingChart(binding.chart1)

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

      binding.pbFood.setOnClickListener {
         replaceFragment1(requireActivity(), ReportFoodFragment())
      }

      binding.pbExercise.setOnClickListener {
         replaceFragment1(requireActivity(), ReportExerciseFragment())
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

   private fun settingChart(chart: CombinedChart) {
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
      leftAxis.setValueFormatter { value, axis ->
         if(value.toInt() == 0) {
            return@setValueFormatter "0"
         }else {
            return@setValueFormatter value.toInt().toString() + "%"
         }
      }

      val data = CombinedData()

      // lineChart 설정
      val lineData = LineData()
      val entries = ArrayList<Entry>()
      val lineList = floatArrayOf(40f, 62f, 40f, 80f, 100f, 40f, 65f)
      for (index in lineList.indices) {
         entries.add(Entry(index.toFloat(), lineList[index]))
      }

      val lineDataSet = LineDataSet(entries, "Line DataSet")
      lineDataSet.color = Color.parseColor("#BBBBBB")
      lineDataSet.lineWidth = 0.5f
      lineDataSet.setCircleColor(Color.parseColor("#3C7A8A"))
      lineDataSet.setDrawValues(true)
      lineDataSet.valueTextSize = 8f
      lineDataSet.valueTextColor = Color.parseColor("#BBBBBB")
      lineDataSet.axisDependency = YAxis.AxisDependency.RIGHT
      lineDataSet.setValueFormatter { value, entry, dataSetIndex, viewPortHandler ->
         return@setValueFormatter value.toInt().toString() + "%"
      }

      lineData.addDataSet(lineDataSet)
      data.setData(lineData)

      // barChart 설정
      val barEntries = ArrayList<BarEntry>()
      barEntries.add(BarEntry(0f, 40f))
      barEntries.add(BarEntry(1f, 62f))
      barEntries.add(BarEntry(2f, 40f))
      barEntries.add(BarEntry(3f, 80f))
      barEntries.add(BarEntry(4f, 100f))
      barEntries.add(BarEntry(5f, 40f))
      barEntries.add(BarEntry(6f, 65f))

      val barDataSet = BarDataSet(barEntries, "")
      barDataSet.color = Color.parseColor ("#3C7A8A")
      barDataSet.valueTextSize = 0f

      val barData = BarData(barDataSet)
      barData.barWidth = 0.3f

      data.setData(barData)

      chart.data = data
      chart.invalidate()
   }
}