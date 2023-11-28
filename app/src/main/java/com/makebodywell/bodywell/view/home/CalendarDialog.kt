package com.makebodywell.bodywell.view.home

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.adapter.CalendarAdapter2
import com.makebodywell.bodywell.adapter.CalendarAdapter2.OnItemListener
import com.makebodywell.bodywell.adapter.CalendarPhotoAdapter
import com.makebodywell.bodywell.util.CalendarUtil
import com.makebodywell.bodywell.util.CalendarUtil.Companion.monthArray
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import java.time.LocalDate

class CalendarDialog(context: Context) : Dialog(context), OnItemListener {
   private var btnCalPrev : ImageView? = null
   private var tvCalTitle : TextView? = null
   private var btnCalNext : ImageView? = null
   private var recyclerView: RecyclerView? = null

   private var viewPager: ViewPager? = null
   private var photoList: ArrayList<Int>? = ArrayList()

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.dialog_calendar)

      btnCalPrev = findViewById(R.id.btnCalPrev)
      tvCalTitle = findViewById(R.id.tvCalTitle)
      btnCalNext = findViewById(R.id.btnCalNext)
      recyclerView = findViewById(R.id.recyclerView)
      viewPager = findViewById(R.id.viewPager)

      selectedDate = LocalDate.now()
      setMonthView()

      btnCalPrev?.setOnClickListener {
         selectedDate = selectedDate!!.minusMonths(1)
         setMonthView()
      }

      btnCalNext?.setOnClickListener {
         selectedDate = selectedDate!!.plusMonths(1)
         setMonthView()
      }

      // 캘린더 사진첩
      setupPhotoList()
   }

   private fun setMonthView() {
      tvCalTitle?.text = CalendarUtil.dateTitle(selectedDate!!)
      val days = monthArray(selectedDate!!)
      val adapter = CalendarAdapter2(days, this)
      val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(context, 7)
      recyclerView?.layoutManager = layoutManager
      recyclerView?.adapter = adapter
   }

   private fun setupPhotoList() {
      var adapter = CalendarPhotoAdapter(photoList, context)

      viewPager?.adapter = adapter
      viewPager?.setPadding(0, 0, 250, 0)
   }

   override fun onItemClick(position: Int, date: LocalDate?) {
      selectedDate = date
      setMonthView()
   }
}