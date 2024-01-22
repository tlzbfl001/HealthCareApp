package com.makebodywell.bodywell.view.home.food

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.LinearLayoutManager
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.adapter.FoodRecord2Adapter
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentFoodRecord2Binding
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2

class FoodRecord2Fragment : Fragment() {
   private var _binding: FragmentFoodRecord2Binding? = null
   private val binding get() = _binding!!

   private var bundle = Bundle()

   private var calendarDate = ""
   private var type = ""

   private var dataManager: DataManager? = null
   private var adapter: FoodRecord2Adapter? = null
   private var dataList = ArrayList<Food>()
   private var itemList = ArrayList<Food>()

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentFoodRecord2Binding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      initView()
      setupList()

      return binding.root
   }

   private fun initView() {
      calendarDate = arguments?.getString("calendarDate").toString()
      type = arguments?.getString("type").toString()
      bundle.putString("calendarDate", calendarDate)
      bundle.putString("type", type)

      binding.clBack.setOnClickListener {
         when(type) {
            "1" -> replaceFragment2(requireActivity(), FoodBreakfastFragment(), bundle)
            "2" -> replaceFragment2(requireActivity(), FoodLunchFragment(), bundle)
            "3" -> replaceFragment2(requireActivity(), FoodDinnerFragment(), bundle)
            "4" -> replaceFragment2(requireActivity(), FoodSnackFragment(), bundle)
         }
      }
      binding.tvBtn1.setOnClickListener {
         replaceFragment2(requireActivity(), FoodRecord1Fragment(), bundle)
      }
      binding.tvBtn3.setOnClickListener {
         replaceFragment2(requireActivity(), FoodInputFragment(), bundle)
      }
   }

   private fun setupList() {
      when(type) {
         "1" -> dataList = dataManager!!.getFood(1 ,calendarDate)
         "2" -> dataList = dataManager!!.getFood(2, calendarDate)
         "3" -> dataList = dataManager!!.getFood(3, calendarDate)
         "4" -> dataList = dataManager!!.getFood(4 ,calendarDate)
      }

      if(dataList.size != 0) {
         for (i in 0 until dataList.size) {
            itemList.add(Food(id = dataList[i].id, name = dataList[i].name, star = R.drawable.ic_star_rate))
         }

         adapter = FoodRecord2Adapter(itemList)
         binding.rv.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
         binding.rv.adapter = adapter
      }
   }
}