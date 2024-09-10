package kr.bodywell.test.view.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import kr.bodywell.test.R
import kr.bodywell.test.adapter.PagerAdapter
import kr.bodywell.test.databinding.FragmentDetailBinding
import kr.bodywell.test.util.CalendarUtil.dateFormat
import kr.bodywell.test.util.CalendarUtil.selectedDate
import kr.bodywell.test.util.CustomUtil.layoutType
import kr.bodywell.test.util.CustomUtil.replaceFragment3
import kr.bodywell.test.util.CustomUtil.setStatusBar
import kr.bodywell.test.view.MainViewModel
import kr.bodywell.test.view.home.body.BodyFragment
import kr.bodywell.test.view.home.drug.DrugFragment
import kr.bodywell.test.view.home.exercise.ExerciseFragment
import kr.bodywell.test.view.home.food.FoodFragment
import kr.bodywell.test.view.home.sleep.SleepFragment
import kr.bodywell.test.view.home.water.WaterFragment

class DetailFragment : Fragment() {
   private var _binding: FragmentDetailBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private val viewModel: MainViewModel by activityViewModels()

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment3(requireActivity(), MainFragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentDetailBinding.inflate(layoutInflater)

      setStatusBar(requireActivity(), binding.mainLayout)

      binding.tvDate.text = dateFormat(selectedDate)

      binding.clBack.setOnClickListener {
         replaceFragment3(requireActivity(), MainFragment())
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

      setTabView()

      return binding.root
   }

   private fun setTabView() {
      val pagerAdapter = PagerAdapter(requireActivity().supportFragmentManager)
      pagerAdapter.add(FoodFragment(), "식단")
      pagerAdapter.add(WaterFragment(), "물")
      pagerAdapter.add(ExerciseFragment(), "운동")
      pagerAdapter.add(BodyFragment(), "신체")
      pagerAdapter.add(SleepFragment(), "수면")
      pagerAdapter.add(DrugFragment(), "약복용")
      binding.viewPager.adapter = pagerAdapter

      binding.tabLayout.setupWithViewPager(binding.viewPager)

      for(i in 0 until 6) {
         val textView = LayoutInflater.from(requireActivity()).inflate(R.layout.item_tab, null) as TextView
         binding.tabLayout.getTabAt(i)?.customView = textView
      }

      binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
         override fun onTabSelected(tab: Tab?) {
            when(tab?.position) {
               0 -> button1()
               1 -> button2()
               2 -> button3()
               3 -> button4()
               4 -> button5()
               5 -> button6()
            }
         }

         override fun onTabUnselected(tab: Tab?) {}

         override fun onTabReselected(tab: Tab?) {}

      })

      when(layoutType) {
         1 -> {
            button1()
            binding.viewPager.currentItem = 0
         }
         2 -> {
            button2()
            binding.viewPager.currentItem = 1
         }
         3 -> {
            button3()
            binding.viewPager.currentItem = 2
         }
         4 -> {
            button4()
            binding.viewPager.currentItem = 3
         }
         5 -> {
            button5()
            binding.viewPager.currentItem = 4
         }
         6 -> {
            button6()
            binding.viewPager.currentItem = 5
         }
      }
   }

   private fun button1() {
      layoutType = 1
      binding.tabLayout.getTabAt(0)?.customView?.setBackgroundResource(R.drawable.rec_25_food)
      binding.tabLayout.getTabAt(1)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(2)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(3)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(4)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(5)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
   }

   private fun button2() {
      layoutType = 2
      binding.tabLayout.getTabAt(0)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(1)?.customView?.setBackgroundResource(R.drawable.rec_25_water)
      binding.tabLayout.getTabAt(2)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(3)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(4)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(5)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
   }

   private fun button3() {
      layoutType = 3
      binding.tabLayout.getTabAt(0)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(1)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(2)?.customView?.setBackgroundResource(R.drawable.rec_25_exercise)
      binding.tabLayout.getTabAt(3)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(4)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(5)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
   }

   private fun button4() {
      layoutType = 4
      binding.tabLayout.getTabAt(0)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(1)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(2)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(3)?.customView?.setBackgroundResource(R.drawable.rec_25_body)
      binding.tabLayout.getTabAt(4)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(5)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
   }

   private fun button5() {
      layoutType = 5
      binding.tabLayout.getTabAt(0)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(1)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(2)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(3)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(4)?.customView?.setBackgroundResource(R.drawable.rec_25_sleep)
      binding.tabLayout.getTabAt(5)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
   }

   private fun button6() {
      layoutType = 6
      binding.tabLayout.getTabAt(0)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(1)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(2)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(3)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(4)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(5)?.customView?.setBackgroundResource(R.drawable.rec_25_drug)
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}