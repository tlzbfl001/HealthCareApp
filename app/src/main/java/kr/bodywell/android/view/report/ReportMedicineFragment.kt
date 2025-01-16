package kr.bodywell.android.view.report

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.adapter.ReportAdapter
import kr.bodywell.android.databinding.FragmentReportMedicineBinding
import kr.bodywell.android.model.Constant.MEDICINE_INTAKES
import kr.bodywell.android.model.Item
import kr.bodywell.android.util.CalendarUtil.dateFormat
import kr.bodywell.android.util.CalendarUtil.monthArray2
import kr.bodywell.android.util.CalendarUtil.monthFormat
import kr.bodywell.android.util.CalendarUtil.weekArray
import kr.bodywell.android.util.CalendarUtil.weekFormat
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.util.CustomUtil.replaceFragment3
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.view.home.MainFragment
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate

class ReportMedicineFragment : Fragment() {
   private var _binding: FragmentReportMedicineBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private val format1 = SimpleDateFormat("yyyy-MM-dd")
   private val format2 = SimpleDateFormat("M.dd")
   private var adapter: ReportAdapter? = null
   private var calendarDate = LocalDate.now()
   private var dateType = 1

   override fun onAttach(context: Context) {
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
      _binding = FragmentReportMedicineBinding.inflate(layoutInflater)

      setStatusBar(requireActivity(), binding.mainLayout)

      binding.tvCalTitle.text = dateFormat(calendarDate)

      binding.clMenu1.setOnClickListener {
         replaceFragment3(requireActivity().supportFragmentManager, ReportBodyFragment())
      }

      binding.clMenu2.setOnClickListener {
         replaceFragment3(requireActivity().supportFragmentManager, ReportFoodFragment())
      }

      binding.clMenu3.setOnClickListener {
         replaceFragment3(requireActivity().supportFragmentManager, ReportExerciseFragment())
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
      binding.tvDaily.setBackgroundResource(R.drawable.rec_5_purple)
      binding.tvDaily.setTextColor(Color.WHITE)
      binding.tvWeekly.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvWeekly.setTextColor(Color.BLACK)
      binding.tvMonthly.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvMonthly.setTextColor(Color.BLACK)
      dateType = 1
      resetChart()

      lifecycleScope.launch {
         val getRecentlyIntakes = powerSync.getRecentlyIntakes(calendarDate.toString())
         if(getRecentlyIntakes.isNotEmpty()) {
            val getDates = ArrayList<String>()
            getDates.add(calendarDate.toString())
            settingChart(binding.chart, getDates, dateType, "", "")
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
      dateType = 2
      resetChart()

      val weekArray = weekArray(calendarDate)

      lifecycleScope.launch {
         val getDates = powerSync.getDates(MEDICINE_INTAKES, "intaked_at", weekArray[0].toString(), weekArray[6].toString())
         if(getDates.isNotEmpty()) {
            settingChart(binding.chart, getDates, dateType, weekArray[0].toString(), weekArray[6].toString())
         }
      }
   }

   private fun monthlyView() {
      binding.tvDaily.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvDaily.setTextColor(Color.BLACK)
      binding.tvWeekly.setBackgroundResource(R.drawable.rec_5_border_gray)
      binding.tvWeekly.setTextColor(Color.BLACK)
      binding.tvMonthly.setBackgroundResource(R.drawable.rec_5_purple)
      binding.tvMonthly.setTextColor(Color.WHITE)
      dateType = 3
      resetChart()

      val monthArray = monthArray2(calendarDate)

      lifecycleScope.launch {
         val getDates = powerSync.getDates(MEDICINE_INTAKES, "intaked_at", monthArray[0].toString(), monthArray[monthArray.size-1].toString())
         if(getDates.isNotEmpty()) {
            settingChart(binding.chart, getDates, dateType, monthArray[0].toString(), monthArray[monthArray.size-1].toString())
         }
      }
   }

   private fun resetChart() {
      binding.recyclerView.visibility = View.GONE
      binding.tvEmpty1.visibility = View.VISIBLE
      binding.chart.visibility = View.GONE
      binding.tvEmpty2.visibility = View.VISIBLE
   }

   private fun settingChart(chart: CombinedChart, getData: List<String>, type: Int, start: String, end: String) {
      chart.data = null
      chart.fitScreen()
      chart.xAxis.valueFormatter = null
      chart.clear()

      val itemList = ArrayList<Item>()
      val data = CombinedData()
      val lineData = LineData()
      var xVal = arrayOf<String>()
      var lineList = floatArrayOf()
      val entries = ArrayList<Entry>()
      val barEntries = ArrayList<BarEntry>()
      var count = 0

      lifecycleScope.launch {
         for(i in getData.indices){
            val getGoal = powerSync.getGoal(getData[i])
            val getIntake = powerSync.getRecentlyIntakes(getData[i])

            if(getIntake.isNotEmpty()) {
               val pt = if(getGoal.medicineIntake == 0) 100f else (getIntake.size.toFloat() / getGoal.medicineIntake.toFloat()) * 100
               xVal += format2.format(format1.parse(getData[i])!!)
               lineList += pt
               barEntries.add(BarEntry(count.toFloat(), pt))
               count += 1
            }
         }

         if(barEntries.size > 0) {
            binding.chart.visibility = View.VISIBLE
            binding.tvEmpty2.visibility = View.GONE

            for (index in lineList.indices) entries.add(Entry(index.toFloat(), lineList[index]))

            val lineDataSet = LineDataSet(entries, "Line DataSet")
            lineDataSet.color = Color.parseColor("#B499F1")
            lineDataSet.lineWidth = 1f
            lineDataSet.setDrawCircles(false)
            lineDataSet.setDrawValues(true)
            lineDataSet.valueTextSize = 9f
            lineDataSet.valueTextColor = Color.parseColor("#BBBBBB")
            lineDataSet.axisDependency = YAxis.AxisDependency.RIGHT
            lineDataSet.valueFormatter = MyValueFormatter()

            lineData.addDataSet(lineDataSet)
            lineData.setValueTextColor(resources.getColor(R.color.black_white))
            data.setData(lineData)

            val barDataSet = BarDataSet(barEntries, "")
            barDataSet.color = Color.parseColor("#B499F1")
            barDataSet.valueTextSize = 0f

            val barData = BarData(barDataSet)
            barData.barWidth = 0.27f

            data.setData(barData)

            chart.data = data

            val xAxis = chart.xAxis
            xAxis.textColor = resources.getColor(R.color.black_white)
            xAxis.axisLineColor = resources.getColor(R.color.black_white)
            xAxis.axisLineWidth = 0.8f
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.spaceMax = 0.6f
            xAxis.spaceMin = 0.6f
            xAxis.valueFormatter = IndexAxisValueFormatter(xVal)
            xAxis.setDrawGridLines(false)
            xAxis.isGranularityEnabled = true

            val rightAxis = chart.axisRight
            rightAxis.axisMinimum = 0f
            rightAxis.isEnabled = false

            val leftAxis = chart.axisLeft
            leftAxis.axisLineColor = resources.getColor(R.color.black_white)
            leftAxis.textColor = resources.getColor(R.color.black_white)
            leftAxis.axisLineWidth = 0.8f
            leftAxis.gridColor = Color.parseColor("#bbbbbb")
            leftAxis.enableGridDashedLine(10f, 15f, 0f)
            leftAxis.axisMinimum = 0f

            chart.setExtraOffsets(8f, 12f, 15f, 10f)
            chart.description.isEnabled = false
            chart.legend.isEnabled = false
            chart.setScaleEnabled(false)
            chart.isClickable = false
            chart.isHighlightPerDragEnabled = false
            chart.isHighlightPerTapEnabled = false
            chart.setVisibleXRangeMaximum(7f)
            chart.isDragXEnabled = true
            chart.animateY(1000)
            chart.invalidate()
         }

         if(type == 1) {
            val getRanking = powerSync.getMedicineRanking(calendarDate.toString())
            for(i in getRanking.indices) {
               itemList.add(Item(string1 = getRanking[i].id, string2 = getRanking[i].name))
            }
         }else {
            val getRanking = powerSync.getMedicineRanking(start, end)
            for(i in getRanking.indices) {
               itemList.add(Item(string1 = getRanking[i].id, string2 = getRanking[i].name))
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
   }

   class MyValueFormatter : IValueFormatter {
      override fun getFormattedValue(
         value: Float,
         entry: Entry,
         dataSetIndex: Int,
         viewPortHandler: ViewPortHandler
      ): String {
         val formatter = DecimalFormat("#")
         return formatter.format(value) + "%"
      }
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}