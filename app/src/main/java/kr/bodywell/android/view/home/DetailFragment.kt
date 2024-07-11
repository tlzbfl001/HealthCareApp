package kr.bodywell.android.view.home

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import kr.bodywell.android.R
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentDetailBinding
import kr.bodywell.android.util.CalendarUtil.Companion.dateFormat
import kr.bodywell.android.util.CalendarUtil.Companion.selectedDate
import kr.bodywell.android.util.CustomUtil
import kr.bodywell.android.util.CustomUtil.Companion.replaceFragment1
import kr.bodywell.android.util.CustomUtil.Companion.replaceDetailFragment2
import kr.bodywell.android.util.CustomUtil.Companion.replaceDetailFragment1
import kr.bodywell.android.util.MainViewModel
import kr.bodywell.android.view.home.body.BodyFragment
import kr.bodywell.android.view.home.drug.DrugFragment
import kr.bodywell.android.view.home.exercise.ExerciseFragment
import kr.bodywell.android.view.home.food.FoodFragment
import kr.bodywell.android.view.home.sleep.SleepFragment
import kr.bodywell.android.view.home.water.WaterFragment

class DetailFragment : Fragment() {
   private var _binding: FragmentDetailBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private val viewModel: MainViewModel by activityViewModels()

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment1(requireActivity(), MainFragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View? {
      _binding = FragmentDetailBinding.inflate(layoutInflater)

      requireActivity().window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK

         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.mainLayout.setPadding(0, statusBarHeight, 0, 0)
      }

      dataManager = DataManager(activity)
      dataManager.open()

      binding.tvDate.text = dateFormat(selectedDate)

      binding.clBack.setOnClickListener {
         replaceFragment1(requireActivity(), MainFragment())
      }

      binding.clPrev.setOnClickListener {
         selectedDate = selectedDate.minusDays(1)
         binding.tvDate.text = dateFormat(selectedDate)
         viewModel.setDate()
      }

      binding.clNext.setOnClickListener {
         selectedDate = selectedDate.plusDays(1)
         binding.tvDate.text = dateFormat(selectedDate)
         viewModel.setDate()
      }

      binding.tvFood.setOnClickListener {
         setMenu1()
         replaceDetailFragment2(requireActivity(), FoodFragment())
      }

      binding.tvWater.setOnClickListener {
         setMenu2()
         replaceDetailFragment2(requireActivity(), WaterFragment())
      }

      binding.tvExercise.setOnClickListener {
         setMenu3()
         replaceDetailFragment2(requireActivity(), ExerciseFragment())
      }

      binding.tvBody.setOnClickListener {
         setMenu4()
         replaceDetailFragment2(requireActivity(), BodyFragment())
      }

      binding.tvSleep.setOnClickListener {
         setMenu5()
         replaceDetailFragment2(requireActivity(), SleepFragment())
      }

      binding.tvDrug.setOnClickListener {
         setMenu6()
         replaceDetailFragment2(requireActivity(), DrugFragment())
      }

      return binding.root
   }

   private fun setMenu1() {
      binding.tvFood.setBackgroundResource(R.drawable.rec_25_food)
      binding.tvFood.setTextColor(Color.WHITE)
      binding.tvWater.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvWater.setTextColor(Color.BLACK)
      binding.tvExercise.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvExercise.setTextColor(Color.BLACK)
      binding.tvBody.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvBody.setTextColor(Color.BLACK)
      binding.tvSleep.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvSleep.setTextColor(Color.BLACK)
      binding.tvDrug.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvDrug.setTextColor(Color.BLACK)
   }

   private fun setMenu2() {
      binding.tvFood.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvFood.setTextColor(Color.BLACK)
      binding.tvWater.setBackgroundResource(R.drawable.rec_25_water)
      binding.tvWater.setTextColor(Color.WHITE)
      binding.tvExercise.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvExercise.setTextColor(Color.BLACK)
      binding.tvBody.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvBody.setTextColor(Color.BLACK)
      binding.tvSleep.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvSleep.setTextColor(Color.BLACK)
      binding.tvDrug.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvDrug.setTextColor(Color.BLACK)
   }

   private fun setMenu3() {
      binding.tvFood.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvFood.setTextColor(Color.BLACK)
      binding.tvWater.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvWater.setTextColor(Color.BLACK)
      binding.tvExercise.setBackgroundResource(R.drawable.rec_25_exercise)
      binding.tvExercise.setTextColor(Color.WHITE)
      binding.tvBody.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvBody.setTextColor(Color.BLACK)
      binding.tvSleep.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvSleep.setTextColor(Color.BLACK)
      binding.tvDrug.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvDrug.setTextColor(Color.BLACK)
   }

   private fun setMenu4() {
      binding.tvFood.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvFood.setTextColor(Color.BLACK)
      binding.tvWater.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvWater.setTextColor(Color.BLACK)
      binding.tvExercise.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvExercise.setTextColor(Color.BLACK)
      binding.tvBody.setBackgroundResource(R.drawable.rec_25_body)
      binding.tvBody.setTextColor(Color.WHITE)
      binding.tvSleep.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvSleep.setTextColor(Color.BLACK)
      binding.tvDrug.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvDrug.setTextColor(Color.BLACK)
   }

   private fun setMenu5() {
      binding.tvFood.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvFood.setTextColor(Color.BLACK)
      binding.tvWater.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvWater.setTextColor(Color.BLACK)
      binding.tvExercise.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvExercise.setTextColor(Color.BLACK)
      binding.tvBody.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvBody.setTextColor(Color.BLACK)
      binding.tvSleep.setBackgroundResource(R.drawable.rec_25_sleep)
      binding.tvSleep.setTextColor(Color.WHITE)
      binding.tvDrug.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvDrug.setTextColor(Color.BLACK)
   }

   private fun setMenu6() {
      binding.tvFood.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvFood.setTextColor(Color.BLACK)
      binding.tvWater.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvWater.setTextColor(Color.BLACK)
      binding.tvExercise.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvExercise.setTextColor(Color.BLACK)
      binding.tvBody.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvBody.setTextColor(Color.BLACK)
      binding.tvSleep.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvSleep.setTextColor(Color.BLACK)
      binding.tvDrug.setBackgroundResource(R.drawable.rec_25_drug)
      binding.tvDrug.setTextColor(Color.WHITE)
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}