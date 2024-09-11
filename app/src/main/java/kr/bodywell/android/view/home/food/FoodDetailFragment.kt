package kr.bodywell.android.view.home.food

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import com.google.android.material.tabs.TabLayout
import kr.bodywell.android.R
import kr.bodywell.android.adapter.PagerAdapter
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentFoodDetailBinding
import kr.bodywell.android.model.Constant
import kr.bodywell.android.util.CustomUtil.replaceFragment2
import kr.bodywell.android.util.CustomUtil.replaceFragment3
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.view.home.DetailFragment

class FoodDetailFragment : Fragment() {
   private var _binding: FragmentFoodDetailBinding? = null
   val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private val bundle = Bundle()
   private var type = Constant.BREAKFAST.name

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment3(requireActivity(), DetailFragment())
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

      dataManager = DataManager(activity)
      dataManager.open()

      if(arguments?.getString("type") != null) type = arguments?.getString("type").toString()

      binding.clBack.setOnClickListener {
         replaceFragment3(requireActivity(), DetailFragment())
      }

      binding.cvInput.setOnClickListener {
         bundle.putString("type", type)
         replaceFragment2(requireActivity(), FoodRecord1Fragment(), bundle)
      }

      setTabView()

      return binding.root
   }

   private fun setTabView() {
      val pagerAdapter = PagerAdapter(requireActivity().supportFragmentManager)
      pagerAdapter.add(FoodBreakfastFragment(), "아침")
      pagerAdapter.add(FoodLunchFragment(), "점심")
      pagerAdapter.add(FoodDinnerFragment(), "저녁")
      pagerAdapter.add(FoodSnackFragment(), "간식")
      binding.viewPager.adapter = pagerAdapter

      binding.tabLayout.setupWithViewPager(binding.viewPager)

      for(i in 0 until 6) {
         val textView = LayoutInflater.from(requireActivity()).inflate(R.layout.item_tab, null) as TextView
         binding.tabLayout.getTabAt(i)?.customView = textView
      }

      when(type) {
         Constant.BREAKFAST.name -> {
            button1()
            binding.viewPager.currentItem = 0
         }
         Constant.LUNCH.name -> {
            button2()
            binding.viewPager.currentItem = 1
         }
         Constant.DINNER.name -> {
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
      type = Constant.BREAKFAST.name
      binding.tabLayout.getTabAt(0)?.customView?.setBackgroundResource(R.drawable.rec_25_food)
      binding.tabLayout.getTabAt(1)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(2)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(3)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
   }

   private fun button2() {
      type = Constant.LUNCH.name
      binding.tabLayout.getTabAt(0)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(1)?.customView?.setBackgroundResource(R.drawable.rec_25_food)
      binding.tabLayout.getTabAt(2)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(3)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
   }

   private fun button3() {
      type = Constant.DINNER.name
      binding.tabLayout.getTabAt(0)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(1)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(2)?.customView?.setBackgroundResource(R.drawable.rec_25_food)
      binding.tabLayout.getTabAt(3)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
   }

   private fun button4() {
      type = Constant.SNACK.name
      binding.tabLayout.getTabAt(0)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(1)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(2)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(3)?.customView?.setBackgroundResource(R.drawable.rec_25_food)
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}