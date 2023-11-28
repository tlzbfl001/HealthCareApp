package com.makebodywell.bodywell.view.report

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.databinding.FragmentReportFoodBinding
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.view.home.MainFragment

class ReportFoodFragment : Fragment() {
   private var _binding: FragmentReportFoodBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentReportFoodBinding.inflate(layoutInflater)

      setupView()

      settingChart1(binding.chart1)
      settingChart2(binding.chart2)

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

      binding.pbBody.setOnClickListener {
         replaceFragment1(requireActivity(), ReportBodyFragment())
      }

      binding.pbFood.setOnClickListener {
         binding.tvBody.setTextColor(resources.getColor(R.color.black))
         binding.tvFood.setTextColor(Color.WHITE)
         binding.tvExercise.setTextColor(resources.getColor(R.color.black))
         binding.tvDrug.setTextColor(resources.getColor(R.color.black))

         binding.clBody.setBackgroundResource(R.drawable.oval_border_gray)
         binding.clFood.setBackgroundResource(R.drawable.oval_report_food)
         binding.clExercise.setBackgroundResource(R.drawable.oval_border_gray)
         binding.clDrug.setBackgroundResource(R.drawable.oval_border_gray)
      }

      binding.pbExercise.setOnClickListener {
         replaceFragment1(requireActivity(), ReportExerciseFragment())
      }

      binding.pbDrug.setOnClickListener {
         replaceFragment1(requireActivity(), ReportDrugFragment())
      }
   }

   private fun settingChart1(chart: CombinedChart) {
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

      val data = CombinedData()

      data.setData(generateLineData())
      data.setData(generateBarData())

      chart.data = data
      chart.invalidate()
   }

   private fun settingChart2(chart: CombinedChart) {

   }

   private fun generateLineData(): LineData {
      val d = LineData()
      val entries = ArrayList<Entry>()

      val list = arrayListOf<Float>()
      list.add(7f)
      list.add(9f)
      list.add(5f)
      list.add(7f)
      list.add(8f)
      list.add(7f)
      list.add(6f)

      for (index in list.indices) {
         entries.add(Entry(index.toFloat(), list[index]))
      }

      val set = LineDataSet(entries, "Line DataSet")
      set.color = Color.parseColor("#BBBBBB")
      set.lineWidth = 0.5f
      set.setCircleColor(Color.parseColor("#BBBBBB"))
      set.circleRadius = 0.8f
      set.fillColor = Color.rgb(22, 145, 196)
      set.setDrawValues(true)
      set.valueTextSize = 6f
      set.valueTextColor = Color.parseColor("#BBBBBB")
      set.axisDependency = YAxis.AxisDependency.RIGHT

      d.addDataSet(set)

      return d
   }

   private fun generateBarData(): BarData {
      val entries = java.util.ArrayList<BarEntry>()
      entries.add(BarEntry(0f, floatArrayOf(1f, 2f, 1f, 3f)))
      entries.add(BarEntry(1f, floatArrayOf(1f, 3f, 2f, 3f)))
      entries.add(BarEntry(2f, floatArrayOf(0.5f, 1f, 0.5f, 3f)))
      entries.add(BarEntry(3f, floatArrayOf(2f, 1f, 1f, 3f)))
      entries.add(BarEntry(4f, floatArrayOf(1f, 3f, 1f, 3f)))
      entries.add(BarEntry(5f, floatArrayOf(1f, 1f, 2f, 3f)))
      entries.add(BarEntry(6f, floatArrayOf(0.5f, 2f, 0.5f, 3f)))

      val colors = ArrayList<Int>()
      colors.add(Color.parseColor("#FFC6D7"))
      colors.add(Color.parseColor("#BFA1AC"))
      colors.add(Color.parseColor("#FE9A9A"))
      colors.add(Color.parseColor("#FFAD0D"))

      val set = BarDataSet(entries, "")
      set.colors = colors
      set.valueTextSize = 0f

      val d = BarData(set)
      d.barWidth = 0.27f

      return d
   }

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment1(requireActivity(), MainFragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}