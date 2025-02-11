package kr.bodywell.health.view.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import com.google.android.material.tabs.TabLayoutMediator
import kr.bodywell.health.R
import kr.bodywell.health.adapter.PagerAdapter
import kr.bodywell.health.databinding.FragmentDetailBinding
import kr.bodywell.health.util.CalendarUtil.dateFormat
import kr.bodywell.health.util.CalendarUtil.selectedDate
import kr.bodywell.health.util.CustomUtil.layoutType
import kr.bodywell.health.util.CustomUtil.replaceFragment3
import kr.bodywell.health.util.CustomUtil.setStatusBar
import kr.bodywell.health.view.MainFragment
import kr.bodywell.health.view.MainViewModel
import kr.bodywell.health.view.home.body.BodyFragment
import kr.bodywell.health.view.home.exercise.ExerciseFragment
import kr.bodywell.health.view.home.food.FoodFragment
import kr.bodywell.health.view.home.medicine.MedicineFragment
import kr.bodywell.health.view.home.sleep.SleepFragment
import kr.bodywell.health.view.home.water.WaterFragment

class DetailFragment : Fragment() {
   private var _binding: FragmentDetailBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private val viewModel: MainViewModel by activityViewModels()

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment3(requireActivity().supportFragmentManager, MainFragment())
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
         replaceFragment3(requireActivity().supportFragmentManager, MainFragment())
      }

      binding.clPrev.setOnClickListener {
         selectedDate = selectedDate.minusDays(1)
         binding.tvDate.text = dateFormat(selectedDate)
         viewModel.setDateState()
      }

      binding.clNext.setOnClickListener {
         selectedDate = selectedDate.plusDays(1)
         binding.tvDate.text = dateFormat(selectedDate)
         viewModel.setDateState()
      }

      setTabView()

      return binding.root
   }

   private fun setTabView() {
      val fragmentList = ArrayList<Fragment>()
      fragmentList.add(FoodFragment())
      fragmentList.add(WaterFragment())
      fragmentList.add(ExerciseFragment())
      fragmentList.add(BodyFragment())
      fragmentList.add(SleepFragment())
      fragmentList.add(MedicineFragment())

      val pagerAdapter = PagerAdapter(fragmentList, requireActivity())
      binding.viewPager.adapter = pagerAdapter

      TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
         when(position) {
            0 -> tab.text = "식단"
            1 -> tab.text = "물"
            2 -> tab.text = "운동"
            3 -> tab.text = "신체"
            4 -> tab.text = "수면"
            5 -> tab.text = "약복용"
         }
      }.attach()

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
      binding.tabLayout.getTabAt(0)?.customView?.setBackgroundResource(R.drawable.rec_25_purple)
      binding.tabLayout.getTabAt(1)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(2)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(3)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(4)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(5)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
   }

   private fun button2() {
      layoutType = 2
      binding.tabLayout.getTabAt(0)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(1)?.customView?.setBackgroundResource(R.drawable.rec_25_purple)
      binding.tabLayout.getTabAt(2)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(3)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(4)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(5)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
   }

   private fun button3() {
      layoutType = 3
      binding.tabLayout.getTabAt(0)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(1)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(2)?.customView?.setBackgroundResource(R.drawable.rec_25_purple)
      binding.tabLayout.getTabAt(3)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(4)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(5)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
   }

   private fun button4() {
      layoutType = 4
      binding.tabLayout.getTabAt(0)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(1)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(2)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(3)?.customView?.setBackgroundResource(R.drawable.rec_25_purple)
      binding.tabLayout.getTabAt(4)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(5)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
   }

   private fun button5() {
      layoutType = 5
      binding.tabLayout.getTabAt(0)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(1)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(2)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(3)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(4)?.customView?.setBackgroundResource(R.drawable.rec_25_purple)
      binding.tabLayout.getTabAt(5)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
   }

   private fun button6() {
      layoutType = 6
      binding.tabLayout.getTabAt(0)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(1)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(2)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(3)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(4)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(5)?.customView?.setBackgroundResource(R.drawable.rec_25_purple)
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}