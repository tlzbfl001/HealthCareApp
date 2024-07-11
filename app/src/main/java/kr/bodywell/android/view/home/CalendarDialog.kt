package kr.bodywell.android.view.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import kr.bodywell.android.R
import kr.bodywell.android.adapter.CalendarAdapter1
import kr.bodywell.android.adapter.PhotoSlideAdapter
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.Image
import kr.bodywell.android.util.CalendarUtil.Companion.monthArray
import kr.bodywell.android.util.CalendarUtil.Companion.selectedDate
import kr.bodywell.android.util.MainViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.abs

class CalendarDialog(context: Context) : Dialog(context) {
   private var dataManager: DataManager? = null
   private var days: ArrayList<LocalDate?> = ArrayList()

   private var tvYear : TextView? = null
   private var tvMonth : TextView? = null
   private var ivPrev : ImageView? = null
   private var ivNext : ImageView? = null
   private var tvStatus : TextView? = null
   private var rv: RecyclerView? = null
   private var viewPager: ViewPager? = null

   @SuppressLint("ClickableViewAccessibility")
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.dialog_calendar1)

      dataManager = DataManager(context)
      dataManager!!.open()

      tvYear = findViewById(R.id.tvYear)
      tvMonth = findViewById(R.id.tvMonth)
      ivPrev = findViewById(R.id.ivPrev)
      ivNext = findViewById(R.id.ivNext)
      tvStatus = findViewById(R.id.tvStatus)
      rv = findViewById(R.id.rv)
      viewPager = findViewById(R.id.viewPager)

      ivPrev?.setOnClickListener {
         selectedDate = selectedDate.minusMonths(1)
         setMonthView()
      }

      ivNext?.setOnClickListener {
         selectedDate = selectedDate.plusMonths(1)
         setMonthView()
      }

      val gestureListener: SwipeGesture = SwipeGesture(rv!!)
      val gestureDetector = GestureDetector(context, gestureListener)

      rv!!.setOnTouchListener { _, event ->
         return@setOnTouchListener gestureDetector.onTouchEvent(event)
      }

      rv!!.addOnItemTouchListener(RecyclerItemClickListener(context, object: RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
               selectedDate = days[position]!!
               setMonthView()
            }
         })
      )

      setMonthView()
   }

   private fun setMonthView() {
      tvYear?.text = selectedDate.format(DateTimeFormatter.ofPattern("yyyy"))
      tvMonth?.text = selectedDate.format(DateTimeFormatter.ofPattern("M"))

      days = monthArray()
      val adapter = CalendarAdapter1(days, 2)
      val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(context, 7)

      rv?.layoutManager = layoutManager
      rv?.adapter = adapter

      setImageView()
   }

   inner class SwipeGesture(v: View) : GestureDetector.OnGestureListener {
      override fun onDown(p0: MotionEvent): Boolean {
         return false
      }
      override fun onShowPress(p0: MotionEvent) {}
      override fun onSingleTapUp(p0: MotionEvent): Boolean {
         return false
      }

      override fun onScroll(
         e1: MotionEvent?,
         e2: MotionEvent,
         distanceX: Float,
         distanceY: Float
      ): Boolean {
         return false
      }

      override fun onLongPress(p0: MotionEvent) {}
      override fun onFling(
         e1: MotionEvent?,
         e2: MotionEvent,
         velocityX: Float,
         velocityY: Float
      ): Boolean {
         var result = false
         try {
            val diffY = e2.y - e1!!.y
            val diffX = e2.x - e1.x
            if (abs(diffX) > abs(diffY)) {
               if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                  if (diffX > 0) {
                     selectedDate = selectedDate.minusMonths(1)
                     setMonthView()
                  } else {
                     selectedDate = selectedDate.plusMonths(1)
                     setMonthView()

                  }
               }
            }
            result = true
         } catch (exception: Exception) {
            exception.printStackTrace()
         }
         return result
      }
   }

   class RecyclerItemClickListener(context: Context, private val listener: OnItemClickListener?) : RecyclerView.OnItemTouchListener {
      private val mGestureDetector: GestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
         override fun onSingleTapUp(e: MotionEvent): Boolean {
            return true
         }
      })

      interface OnItemClickListener {
         fun onItemClick(view: View, position: Int)
      }

      override fun onInterceptTouchEvent(view: RecyclerView, e: MotionEvent): Boolean {
         val childView = view.findChildViewUnder(e.x, e.y)
         if (childView != null && listener != null && mGestureDetector.onTouchEvent(e)) {
            try {
               listener.onItemClick(childView, view.getChildAdapterPosition(childView))
            } catch (e: Exception) {
               e.printStackTrace()
            }
            return true
         }
         return false
      }

      override fun onTouchEvent(view: RecyclerView, motionEvent: MotionEvent) {}
      override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
   }

   private fun setImageView() {
      val itemList = ArrayList<Image>()

      // 데이터 가져오기
      val getImage1 = dataManager!!.getImage("1", selectedDate.toString())
      val getImage2 = dataManager!!.getImage("2", selectedDate.toString())
      val getImage3 = dataManager!!.getImage("3", selectedDate.toString())
      val getImage4 = dataManager!!.getImage("4", selectedDate.toString())

      // 리스트에 데이터 저장
      for (i in 0 until getImage1.size) {
         itemList.add(Image(id = getImage1[i].id, imageUri = getImage1[i].imageUri, created = selectedDate.toString()))
      }
      for (i in 0 until getImage2.size) {
         itemList.add(Image(id = getImage2[i].id, imageUri = getImage2[i].imageUri, created = selectedDate.toString()))
      }
      for (i in 0 until getImage3.size) {
         itemList.add(Image(id = getImage3[i].id, imageUri = getImage3[i].imageUri, created = selectedDate.toString()))
      }
      for (i in 0 until getImage4.size) {
         itemList.add(Image(id = getImage4[i].id, imageUri = getImage4[i].imageUri, created = selectedDate.toString()))
      }

      if (itemList.size > 0) {
         viewPager?.visibility = View.VISIBLE
         tvStatus?.visibility = View.GONE

         val adapter = PhotoSlideAdapter(context, itemList)
         viewPager?.adapter = adapter
         viewPager?.setPadding(0, 0, 210, 0)
      }else {
         viewPager?.visibility = View.GONE
         tvStatus?.visibility = View.VISIBLE
      }
   }

   companion object {
      private const val SWIPE_THRESHOLD = 100
      private const val SWIPE_VELOCITY_THRESHOLD = 100
   }
}