package com.makebodywell.bodywell.view.home.food

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.makebodywell.bodywell.adapter.FoodRecord1Adapter
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentFoodRecord1Binding
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2

class FoodRecord1Fragment : Fragment() {
   private var _binding: FragmentFoodRecord1Binding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback

   private var bundle = Bundle()

   private var calendarDate = ""
   private var type = ""

   private var dataManager: DataManager? = null
   private var adapter: FoodRecord1Adapter? = null
   private var dataList = ArrayList<Food>()
   private var itemList = ArrayList<Food>()

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentFoodRecord1Binding.inflate(layoutInflater)

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

      binding.btnOut.setOnClickListener {
         when(type) {
            "breakfast" -> replaceFragment2(requireActivity(), FoodBreakfastFragment(), bundle)
            "lunch" -> replaceFragment2(requireActivity(), FoodLunchFragment(), bundle)
            "dinner" -> replaceFragment2(requireActivity(), FoodDinnerFragment(), bundle)
            "snack" -> replaceFragment2(requireActivity(), FoodSnackFragment(), bundle)
         }
      }
      binding.tvBtn2.setOnClickListener {
         replaceFragment2(requireActivity(), FoodRecord2Fragment(), bundle)
      }
      binding.tvBtn3.setOnClickListener {
         replaceFragment2(requireActivity(), FoodInputFragment(), bundle)
      }
   }

   private fun setupList() {
      when(type) {
         "breakfast" -> dataList = dataManager!!.getFood("breakfast", calendarDate)
         "lunch" -> dataList = dataManager!!.getFood("lunch", calendarDate)
         "dinner" -> dataList = dataManager!!.getFood("dinner", calendarDate)
         "snack" -> dataList = dataManager!!.getFood("snack", calendarDate)
      }

      if(dataList.size != 0) {
         binding.view.visibility = View.VISIBLE

         for (i in 0 until dataList.size) {
            itemList.add(Food(name = dataList[i].name, unit = dataList[i].unit, amount = dataList[i].amount, kcal = dataList[i].kcal,
               carbohydrate = dataList[i].carbohydrate, protein = dataList[i].protein, fat = dataList[i].fat, salt = dataList[i].salt,
               sugar = dataList[i].sugar))
         }

         adapter = FoodRecord1Adapter(itemList)
         binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
         binding.recyclerView.adapter = adapter
      }
   }

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            when(type) {
               "breakfast" -> replaceFragment2(requireActivity(), FoodBreakfastFragment(), bundle)
               "lunch" -> replaceFragment2(requireActivity(), FoodLunchFragment(), bundle)
               "dinner" -> replaceFragment2(requireActivity(), FoodDinnerFragment(), bundle)
               "snack" -> replaceFragment2(requireActivity(), FoodSnackFragment(), bundle)
            }
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}