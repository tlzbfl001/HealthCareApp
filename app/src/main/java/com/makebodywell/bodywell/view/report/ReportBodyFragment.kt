package com.makebodywell.bodywell.view.report

<<<<<<< HEAD
=======
import android.content.Context
>>>>>>> e5f18d1dfc2f1449657445a53cc6b46714d681ac
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
<<<<<<< HEAD
=======
import androidx.activity.OnBackPressedCallback
>>>>>>> e5f18d1dfc2f1449657445a53cc6b46714d681ac
import androidx.fragment.app.Fragment
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.databinding.FragmentReportBodyBinding
import com.makebodywell.bodywell.model.Body
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
<<<<<<< HEAD
=======
import com.makebodywell.bodywell.view.home.MainFragment
>>>>>>> e5f18d1dfc2f1449657445a53cc6b46714d681ac
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
import java.time.LocalDate

class ReportBodyFragment : Fragment() {
   private var _binding: FragmentReportBodyBinding? = null
   private val binding get() = _binding!!

   private var calendarDate = LocalDate.now()

   private var itemList = ArrayList<Body>()
   private var entries = ArrayList<Entry>()
   private var xValue = ArrayList<String>()

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentReportBodyBinding.inflate(layoutInflater)

      setupView()

      // 차트세팅
      setupChart(binding.lineChart1)
      setupChart(binding.lineChart2)
      setupChart(binding.lineChart3)

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
         calendarDate = calendarDate!!.minusDays(1)
         binding.tvCalTitle.text = dateFormat(calendarDate)
      }

      binding.ivNext.setOnClickListener {
         calendarDate = calendarDate!!.plusDays(1)
         binding.tvCalTitle.text = dateFormat(calendarDate)
      }
   }

   private fun setupChart(lineChart: LineChart) {
      itemList.clear()
      entries.clear()
      xValue.clear()

      itemList.add(Body(weight = 68.5, regDate = "8.16"))
      itemList.add(Body(weight = 67.8, regDate = "8.17"))
      itemList.add(Body(weight = 67.5, regDate = "8.18"))
      itemList.add(Body(weight = 67.8, regDate = "8.19"))
      itemList.add(Body(weight = 67.2, regDate = "8.20"))
      itemList.add(Body(weight = 67.5, regDate = "8.21"))
      itemList.add(Body(weight = 68.0, regDate = "오늘"))

      var colors = intArrayOf(R.color.black, R.color.black, R.color.black, R.color.black, R.color.black, R.color.black, R.color.red)

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
      lineDataSet.lineWidth = 2.7f
      lineDataSet.circleRadius = 3f
      lineDataSet.color = Color.parseColor("#EAEAEA")
      lineDataSet.setCircleColor(resources.getColor(R.color.black))
      lineDataSet.setDrawCircles(true)
      lineDataSet.setDrawCircleHole(false)
      lineDataSet.setDrawHorizontalHighlightIndicator(false)
      lineDataSet.setDrawHighlightIndicators(false)
      lineDataSet.setCircleColors(colors, activity)

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
      xAxis.gridColor = Color.parseColor("#E8E8E8")

      val yAxisLeft = lineChart.axisLeft
      yAxisLeft.textSize = 7f

      lineChart.invalidate()
   }
}
