package com.makebodywell.bodywell.view.home.food

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.adapter.CalendarAdapter2
import com.makebodywell.bodywell.adapter.CalendarAdapter2.OnItemListener
import com.makebodywell.bodywell.adapter.PhotoSlideAdapter
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.model.Image
import com.makebodywell.bodywell.util.CalendarUtil.Companion.calendarTitle
import com.makebodywell.bodywell.util.CalendarUtil.Companion.monthArray
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import java.time.LocalDate

class CalendarDialog(context: Context) : Dialog(context), OnItemListener {
   private var dataManager: DataManager? = null
   private val itemList = ArrayList<Image>()

   private var btnCalPrev : ImageView? = null
   private var tvCalTitle : TextView? = null
   private var btnCalNext : ImageView? = null
   private var clStatus : ConstraintLayout? = null
   private var recyclerView: RecyclerView? = null
   private var viewPager: ViewPager? = null

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.dialog_calendar)

      dataManager = DataManager(context)
      dataManager!!.open()

      btnCalPrev = findViewById(R.id.btnCalPrev)
      tvCalTitle = findViewById(R.id.tvCalTitle)
      btnCalNext = findViewById(R.id.btnCalNext)
      clStatus = findViewById(R.id.clStatus)
      recyclerView = findViewById(R.id.recyclerView)
      viewPager = findViewById(R.id.viewPager)

      selectedDate = LocalDate.now()

      // 캘린더 뷰
      setMonthView()

      // 이미지 뷰
      setImageView()

      btnCalPrev?.setOnClickListener {
         selectedDate = selectedDate.minusMonths(1)
         setMonthView()
         setImageView()
      }

      btnCalNext?.setOnClickListener {
         selectedDate = selectedDate.plusMonths(1)
         setMonthView()
         setImageView()
      }
   }

   private fun setMonthView() {
      tvCalTitle?.text = calendarTitle()
      val days = monthArray()
      val adapter = CalendarAdapter2(context, days, this)
      val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(context, 7)
      recyclerView?.layoutManager = layoutManager
      recyclerView?.adapter = adapter
   }

   private fun setImageView() {
      itemList.clear()

      // 데이터 가져오기
      val getImage1 = dataManager!!.getImage("breakfast", selectedDate.toString())
      val getImage2 = dataManager!!.getImage("lunch", selectedDate.toString())
      val getImage3 = dataManager!!.getImage("dinner", selectedDate.toString())
      val getImage4 = dataManager!!.getImage("snack", selectedDate.toString())

      // 리스트에 데이터 저장
      for (i in 0 until getImage1.size) {
         itemList.add(Image(id = getImage1[i].id, imageUri = getImage1[i].imageUri, regDate = selectedDate.toString()))
      }
      for (i in 0 until getImage2.size) {
         itemList.add(Image(id = getImage2[i].id, imageUri = getImage2[i].imageUri, regDate = selectedDate.toString()))
      }
      for (i in 0 until getImage3.size) {
         itemList.add(Image(id = getImage3[i].id, imageUri = getImage3[i].imageUri, regDate = selectedDate.toString()))
      }
      for (i in 0 until getImage4.size) {
         itemList.add(Image(id = getImage4[i].id, imageUri = getImage4[i].imageUri, regDate = selectedDate.toString()))
      }

      if (itemList.size > 0) {
         viewPager?.visibility = View.VISIBLE
         clStatus?.visibility = View.GONE

         val adapter = PhotoSlideAdapter(context, itemList)
         viewPager?.adapter = adapter
         viewPager?.setPadding(0, 0, 280, 0)
      }else {
         viewPager?.visibility = View.GONE
         clStatus?.visibility = View.VISIBLE
      }
   }

   override fun onItemClick(position: Int, date: LocalDate?) {
      selectedDate = date!!
      setMonthView()
      setImageView()
   }
}