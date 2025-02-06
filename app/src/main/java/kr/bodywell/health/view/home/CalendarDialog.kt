package kr.bodywell.health.view.home

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Base64
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kr.bodywell.health.R
import kr.bodywell.health.adapter.CalendarAdapter1
import kr.bodywell.health.adapter.CalendarImageSlideAdapter
import kr.bodywell.health.model.FileItem
import kr.bodywell.health.util.CalendarUtil.monthArray
import kr.bodywell.health.util.CalendarUtil.selectedDate
import kr.bodywell.health.util.CustomUtil.getDietFiles
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.abs

class CalendarDialog(context: Context) : Dialog(context) {
   private var days: ArrayList<LocalDate?> = ArrayList()
   private var tvYear : TextView? = null
   private var tvMonth : TextView? = null
   private var ivPrev : ImageView? = null
   private var ivNext : ImageView? = null
   private var tvStatus : TextView? = null
   private var rv: RecyclerView? = null
   private var viewPager: ViewPager? = null
   private var images = ArrayList<FileItem>()

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.dialog_calendar1)

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

      val gestureListener = SwipeGesture(rv!!)
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
      images.clear()
      tvYear?.text = selectedDate.format(DateTimeFormatter.ofPattern("yyyy"))
      tvMonth?.text = selectedDate.format(DateTimeFormatter.ofPattern("M"))

      days = monthArray()
      val adapter = CalendarAdapter1(days, 1)
      val layoutManager = GridLayoutManager(context, 7)

      rv?.layoutManager = layoutManager
      rv?.adapter = adapter

      runBlocking {
         images = getDietFiles(selectedDate.toString())
         for(i in 0 until images.size) {
            val imgPath = context.filesDir.toString() + "/" + images[i].name
            val file = File(imgPath)

            if(!file.exists()){
               val base64Image = images[i].data.split(",")
               val imageBytes = Base64.decode(base64Image[1], Base64.DEFAULT)

               val deferred = async {
                  withContext(Dispatchers.IO) {
                     val fos = FileOutputStream(File(imgPath))
                     fos.use {
                        it.write(imageBytes)
                     }
                  }
               }
               deferred.await()
            }
         }

         if(images.size > 0) {
            viewPager?.visibility = View.VISIBLE
            tvStatus?.visibility = View.GONE

            val slideAdapter = CalendarImageSlideAdapter(context, images)
            viewPager?.adapter = slideAdapter
            viewPager?.setPadding(0, 0, 200, 0)
         }else {
            viewPager?.visibility = View.GONE
            tvStatus?.visibility = View.VISIBLE
         }
      }
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

   companion object {
      private const val SWIPE_THRESHOLD = 100
      private const val SWIPE_VELOCITY_THRESHOLD = 100
   }
}