package kr.bodywell.android.view.note

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.GestureDetector
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.f4b6a3.uuid.UuidCreator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.bodywell.android.R
import kr.bodywell.android.adapter.CalendarAdapter2
import kr.bodywell.android.adapter.NoteImageSlideAdapter
import kr.bodywell.android.databinding.FragmentNoteBinding
import kr.bodywell.android.model.Constant.ANGRY
import kr.bodywell.android.model.Constant.EXCITED
import kr.bodywell.android.model.Constant.HAPPY
import kr.bodywell.android.model.Constant.NOTES
import kr.bodywell.android.model.Constant.PEACEFUL
import kr.bodywell.android.model.Constant.SAD
import kr.bodywell.android.model.FileItem
import kr.bodywell.android.model.Note
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CalendarUtil.weekArray
import kr.bodywell.android.util.CustomUtil.dateTimeToIso
import kr.bodywell.android.util.CustomUtil.getFoodCalories
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.util.CustomUtil.replaceFragment2
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.util.MyApp.Companion.powerSync
import kr.bodywell.android.util.PermissionUtil.MEDIA_PERMISSION_1
import kr.bodywell.android.util.PermissionUtil.MEDIA_PERMISSION_2
import kr.bodywell.android.util.PermissionUtil.checkMediaPermission
import kr.bodywell.android.view.MainFragment
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import kotlin.math.abs

class NoteFragment : Fragment() {
   private var _binding: FragmentNoteBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
   private var days = ArrayList<LocalDate?>()
   private var getNote = Note()
   private var images = ArrayList<FileItem>()
   private var noteId = ""

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
      _binding = FragmentNoteBinding.inflate(layoutInflater)

      setStatusBar(requireActivity(), binding.mainLayout)

      pLauncher = registerForActivityResult(
         ActivityResultContracts.RequestMultiplePermissions()
      ){}

      val layoutManager = GridLayoutManager(activity, 7)
      binding.recyclerView.layoutManager = layoutManager
      binding.viewPager.setPadding(140, 0, 140, 0)

      val gestureListener = SwipeGesture(binding.recyclerView)
      val gestureDetector = GestureDetector(requireActivity(), gestureListener)

      if(arguments?.getString("data").toString() != NOTES) selectedDate = LocalDate.now() // 날짜 초기화

      setDailyView() // 일일 데이터 초기화

      binding.clExpand.setOnClickListener {
         val dialog = CalendarDialog(requireActivity())
         dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
         dialog.window?.setGravity(Gravity.TOP)

         dialog.setOnDismissListener {
            setDailyView()
         }

         dialog.show()

         val window = dialog.window
         window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
      }

      binding.recyclerView.setOnTouchListener { _, event ->
         return@setOnTouchListener gestureDetector.onTouchEvent(event)
      }

      binding.recyclerView.addOnItemTouchListener(RecyclerItemClickListener(requireActivity(), object : OnItemClickListener {
         override fun onItemClick(view: View, position: Int) {
            selectedDate = days[position]!!
            setDailyView()
         }
      }))

      binding.clGallery.setOnClickListener {
         if(checkMediaPermission(requireActivity())) {
            val bundle = Bundle()
            if(noteId == "") {
               lifecycleScope.launch {
                  noteId = UuidCreator.getTimeOrderedEpoch().toString()
                  powerSync.insertNote(Note(id = noteId, title = "", content = "", emotion = HAPPY, date = selectedDate.toString(),
                     createdAt = dateTimeToIso(Calendar.getInstance()), updatedAt = dateTimeToIso(Calendar.getInstance())))
               }
            }

            bundle.putString("noteId", noteId)
            replaceFragment2(requireActivity().supportFragmentManager, GalleryFragment(), bundle)
         }else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
               pLauncher.launch(MEDIA_PERMISSION_2)
            }else {
               pLauncher.launch(MEDIA_PERMISSION_1)
            }
         }
      }

      binding.clWrite.setOnClickListener {
         replaceFragment1(requireActivity().supportFragmentManager, NoteWriteFragment())
      }

      binding.cvWrite.setOnClickListener {
         replaceFragment1(requireActivity().supportFragmentManager, NoteWriteFragment())
      }

      return binding.root
   }

   private fun setDailyView() {
      images.clear()
      binding.tvYear.text = selectedDate.format(DateTimeFormatter.ofPattern("yyyy"))
      binding.tvYearText.text = selectedDate.month.toString()
      days = weekArray(selectedDate)
      val adapter = CalendarAdapter2(days)
      binding.recyclerView.adapter = adapter

      lifecycleScope.launch {
         getNote = powerSync.getNote(selectedDate.toString())
         noteId = getNote.id

         var total = 0
         val foodKcal = getFoodCalories(selectedDate.toString())
         binding.tvKcal1.text = "${foodKcal.int5}kcal"
         val getAllWorkout = powerSync.getWorkouts(selectedDate.toString())
         for(i in getAllWorkout.indices) total += getAllWorkout[i].calorie
         binding.tvKcal2.text = "${total}kcal"

         if(checkMediaPermission(requireActivity())) {
            images = powerSync.getFiles("note_id", noteId) as ArrayList<FileItem>

            for(i in images.indices) {
               val imgPath = requireActivity().filesDir.toString() + "/" + images[i].name
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
               binding.view2.visibility = View.VISIBLE
               val photoAdapter = NoteImageSlideAdapter(requireActivity(), noteId, images)
               binding.viewPager.adapter = photoAdapter
            }else {
               binding.view2.visibility = View.GONE
               binding.viewPager.adapter = null
            }
         }

         if(getNote.title != "") {
            binding.cvWrite.visibility = View.VISIBLE
            binding.tvTitle.text = getNote.title
            binding.tvContent.text = getNote.content
            when(getNote.emotion) {
               HAPPY -> binding.ivFace.setImageResource(R.drawable.face1)
               PEACEFUL -> binding.ivFace.setImageResource(R.drawable.face2)
               EXCITED -> binding.ivFace.setImageResource(R.drawable.face3)
               SAD -> binding.ivFace.setImageResource(R.drawable.face4)
               ANGRY -> binding.ivFace.setImageResource(R.drawable.face5)
            }
         }else {
            binding.cvWrite.visibility = View.GONE
            binding.ivFace.setImageResource(R.drawable.face1)
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
      ): Boolean {var result = false
         try {
            val diffY = e2.y - e1!!.y
            val diffX = e2.x - e1.x
            if (abs(diffX) > abs(diffY)) {
               if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                  if (diffX > 0) {
                     selectedDate = selectedDate.minusWeeks(1)
                     setDailyView()
                  } else {
                     selectedDate = selectedDate.plusWeeks(1)
                     setDailyView()
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

   interface OnItemClickListener {
      fun onItemClick(view: View, position: Int)
   }

   inner class RecyclerItemClickListener(context: Context, private val listener: OnItemClickListener?) : RecyclerView.OnItemTouchListener {
      private val mGestureDetector: GestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
         override fun onSingleTapUp(e: MotionEvent): Boolean {
            return true
         }
      })

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

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }

   companion object {
      private const val SWIPE_THRESHOLD = 100
      private const val SWIPE_VELOCITY_THRESHOLD = 100
   }
}