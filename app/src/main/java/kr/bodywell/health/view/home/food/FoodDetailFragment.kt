package kr.bodywell.health.view.home.food

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kr.bodywell.health.R
import kr.bodywell.health.adapter.PagerAdapter
import kr.bodywell.health.databinding.FragmentFoodDetailBinding
import kr.bodywell.health.model.Constant.BREAKFAST
import kr.bodywell.health.model.Constant.DINNER
import kr.bodywell.health.model.Constant.LUNCH
import kr.bodywell.health.model.Constant.SNACK
import kr.bodywell.health.util.CustomUtil.replaceFragment2
import kr.bodywell.health.util.CustomUtil.replaceFragment3
import kr.bodywell.health.util.CustomUtil.setStatusBar
import kr.bodywell.health.view.home.DetailFragment

class FoodDetailFragment : Fragment() {
   private var _binding: FragmentFoodDetailBinding? = null
   val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private val bundle = Bundle()
   private var type = BREAKFAST

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment3(requireActivity().supportFragmentManager, DetailFragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentFoodDetailBinding.inflate(layoutInflater)

      setStatusBar(requireActivity(), binding.mainLayout)

      if(arguments?.getString("type") != null) type = arguments?.getString("type").toString()

      binding.clBack.setOnClickListener {
         replaceFragment3(requireActivity().supportFragmentManager, DetailFragment())
      }

      binding.tvInput.setOnClickListener {
         bundle.putString("type", type)
         replaceFragment2(requireActivity().supportFragmentManager, FoodRecord1Fragment(), bundle)
      }

      setTabView()

      return binding.root
   }

   private fun setTabView() {
      val fragmentList = ArrayList<Fragment>()
      fragmentList.add(FoodBreakfastFragment())
      fragmentList.add(FoodLunchFragment())
      fragmentList.add(FoodDinnerFragment())
      fragmentList.add(FoodSnackFragment())

      val pagerAdapter = PagerAdapter(fragmentList, requireActivity())
      binding.viewPager.adapter = pagerAdapter

      TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
         when(position) {
            0 -> tab.text = "아침"
            1 -> tab.text = "점심"
            2 -> tab.text = "저녁"
            3 -> tab.text = "간식"
         }
      }.attach()

      for(i in 0 until 6) {
         val textView = LayoutInflater.from(requireActivity()).inflate(R.layout.item_tab, null) as TextView
         binding.tabLayout.getTabAt(i)?.customView = textView
      }

      when(type) {
         BREAKFAST -> {
            button1()
            binding.viewPager.currentItem = 0
         }
         LUNCH -> {
            button2()
            binding.viewPager.currentItem = 1
         }
         DINNER -> {
            button3()
            binding.viewPager.currentItem = 2
         }
         else -> {
            button4()
            binding.viewPager.currentItem = 3
         }
      }

      binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
         override fun onTabSelected(tab: TabLayout.Tab?) {
            when(tab?.position) {
               0 -> button1()
               1 -> button2()
               2 -> button3()
               3 -> button4()
            }
         }

         override fun onTabUnselected(tab: TabLayout.Tab?) {}

         override fun onTabReselected(tab: TabLayout.Tab?) {}

      })
   }

   private fun button1() {
      type = BREAKFAST
      binding.tabLayout.getTabAt(0)?.customView?.setBackgroundResource(R.drawable.rec_25_purple)
      binding.tabLayout.getTabAt(1)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(2)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(3)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
   }

   private fun button2() {
      type = LUNCH
      binding.tabLayout.getTabAt(0)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(1)?.customView?.setBackgroundResource(R.drawable.rec_25_purple)
      binding.tabLayout.getTabAt(2)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(3)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
   }

   private fun button3() {
      type = DINNER
      binding.tabLayout.getTabAt(0)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(1)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(2)?.customView?.setBackgroundResource(R.drawable.rec_25_purple)
      binding.tabLayout.getTabAt(3)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
   }

   private fun button4() {
      type = SNACK
      binding.tabLayout.getTabAt(0)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(1)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(2)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(3)?.customView?.setBackgroundResource(R.drawable.rec_25_purple)
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}