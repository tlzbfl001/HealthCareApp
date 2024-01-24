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
import java.time.format.DateTimeFormatter

class CalendarDialog(context: Context) : Dialog(context), OnItemListener {
   private var dataManager: DataManager? = null

   private var tvYear : TextView? = null
   private var tvMonth : TextView? = null
   private var ivPrev : ImageView? = null
   private var ivNext : ImageView? = null
   private var tvStatus : TextView? = null
   private var rv: RecyclerView? = null
   private var viewPager: ViewPager? = null

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.dialog_calendar)

      dataManager = DataManager(context)
      dataManager!!.open()

      tvYear = findViewById(R.id.tvYear)
      tvMonth = findViewById(R.id.tvMonth)
      ivPrev = findViewById(R.id.ivPrev)
      ivNext = findViewById(R.id.ivNext)
      tvStatus = findViewById(R.id.tvStatus)
      rv = findViewById(R.id.rv)
      viewPager = findViewById(R.id.viewPager)

      selectedDate = LocalDate.now()

      ivPrev?.setOnClickListener {
         selectedDate = selectedDate.minusMonths(1)
         setMonthView()
         setImageView()
      }

      ivNext?.setOnClickListener {
         selectedDate = selectedDate.plusMonths(1)
         setMonthView()
         setImageView()
      }

      // 캘린더 뷰
      setMonthView()

      // 이미지 뷰
      setImageView()
   }

   private fun setMonthView() {
      tvYear?.text = selectedDate.format(DateTimeFormatter.ofPattern("yyyy"))
      tvMonth?.text = selectedDate.format(DateTimeFormatter.ofPattern("M"))
      val days = monthArray()
      val adapter = CalendarAdapter2(context, days, this)
      val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(context, 7)
      rv?.layoutManager = layoutManager
      rv?.adapter = adapter
   }

   private fun setImageView() {
      val itemList = ArrayList<Image>()

      // 데이터 가져오기
      val getImage1 = dataManager!!.getImage(1, selectedDate.toString())
      val getImage2 = dataManager!!.getImage(2, selectedDate.toString())
      val getImage3 = dataManager!!.getImage(3, selectedDate.toString())
      val getImage4 = dataManager!!.getImage(4, selectedDate.toString())

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
         tvStatus?.visibility = View.GONE

         val adapter = PhotoSlideAdapter(context, itemList)
         viewPager?.adapter = adapter
         viewPager?.setPadding(0, 0, 280, 0)
      }else {
         viewPager?.visibility = View.GONE
         tvStatus?.visibility = View.VISIBLE
      }
   }

   override fun onItemClick(position: Int, date: LocalDate?) {
      selectedDate = date!!
      setMonthView()
      setImageView()
   }
}