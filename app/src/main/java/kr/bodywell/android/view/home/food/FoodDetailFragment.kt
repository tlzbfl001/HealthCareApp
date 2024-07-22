package kr.bodywell.android.view.home.food

import android.content.Context
import android.graphics.Color
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
import kr.bodywell.android.util.CustomUtil.Companion.replaceFragment2
import kr.bodywell.android.util.CustomUtil.Companion.replaceFragment3
import kr.bodywell.android.view.home.DetailFragment

class FoodDetailFragment : Fragment() {
   private var _binding: FragmentFoodDetailBinding? = null
   val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private val bundle = Bundle()
   private var type = "BREAKFAST"

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
         "BREAKFAST" -> {
            button1()
            binding.viewPager.currentItem = 0
         }
         "LUNCH" -> {
            button2()
            binding.viewPager.currentItem = 1
         }
         "DINNER" -> {
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
      type = "BREAKFAST"
      binding.tabLayout.getTabAt(0)?.customView?.setBackgroundResource(R.drawable.rec_25_food)
      binding.tabLayout.getTabAt(1)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(2)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(3)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
   }

   private fun button2() {
      type = "LUNCH"
      binding.tabLayout.getTabAt(0)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(1)?.customView?.setBackgroundResource(R.drawable.rec_25_food)
      binding.tabLayout.getTabAt(2)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(3)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
   }

   private fun button3() {
      type = "DINNER"
      binding.tabLayout.getTabAt(0)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(1)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tabLayout.getTabAt(2)?.customView?.setBackgroundResource(R.drawable.rec_25_food)
      binding.tabLayout.getTabAt(3)?.customView?.setBackgroundResource(R.drawable.rec_25_border_gray)
   }

   private fun button4() {
      type = "SNACK"
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