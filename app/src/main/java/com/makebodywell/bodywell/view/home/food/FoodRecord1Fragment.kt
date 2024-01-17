package com.makebodywell.bodywell.view.home.food

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.makebodywell.bodywell.adapter.FoodRecord1Adapter
import com.makebodywell.bodywell.adapter.SearchAdapter
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentFoodRecord1Binding
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2

class FoodRecord1Fragment : Fragment() {
   private var _binding: FragmentFoodRecord1Binding? = null
   private val binding get() = _binding!!

   private var bundle = Bundle()

   private var dataManager: DataManager? = null
   private var adapter2 = SearchAdapter()
   private var dataList = ArrayList<Food>()
   private val itemList = ArrayList<Food>()
   private val searchList = ArrayList<String>()
   private val originalList = ArrayList<String>()

   private var calendarDate = ""
   private var type = ""

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentFoodRecord1Binding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      calendarDate = arguments?.getString("calendarDate").toString()
      type = arguments?.getString("type").toString()
      bundle.putString("calendarDate", calendarDate)

      binding.clBack.setOnClickListener {
         when(type) {
            "1" -> replaceFragment2(requireActivity(), FoodBreakfastFragment(), bundle)
            "2" -> replaceFragment2(requireActivity(), FoodLunchFragment(), bundle)
            "3" -> replaceFragment2(requireActivity(), FoodDinnerFragment(), bundle)
            "4" -> replaceFragment2(requireActivity(), FoodSnackFragment(), bundle)
         }
      }

      binding.tvBtn2.setOnClickListener {
         replaceFragment2(requireActivity(), FoodRecord2Fragment(), bundle)
      }

      binding.tvBtn3.setOnClickListener {
         replaceFragment2(requireActivity(), FoodInputFragment(), bundle)
      }

      listView()
      searchView()

      return binding.root
   }

   private fun listView() {
      when(type) {
         "1" -> dataList = dataManager!!.getFood(1, calendarDate)
         "2" -> dataList = dataManager!!.getFood(2, calendarDate)
         "3" -> dataList = dataManager!!.getFood(3, calendarDate)
         "4" -> dataList = dataManager!!.getFood(4, calendarDate)
      }

      if(dataList.size != 0) {
         binding.rv1.visibility = View.VISIBLE

         for (i in 0 until dataList.size) {
            itemList.add(Food(id = dataList[i].id, name = dataList[i].name, unit = dataList[i].unit, amount = dataList[i].amount, kcal = dataList[i].kcal,
               carbohydrate = dataList[i].carbohydrate, protein = dataList[i].protein, fat = dataList[i].fat, salt = dataList[i].salt, sugar = dataList[i].sugar))
         }

         val adapter1 = FoodRecord1Adapter(requireActivity(), itemList)
         binding.rv1.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
         binding.rv1.adapter = adapter1
      }
   }

   private fun searchView() {
      val strings = arrayListOf("Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "가나", "한국", "가나다", "가다", "중국", "미국", "유럽")

      for(i in 0 until strings.size) {
         originalList.add(strings[i])
      }

      binding.etSearch.addTextChangedListener(object: TextWatcher{
         override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
         override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
         override fun afterTextChanged(p0: Editable?) {
            searchList.clear()
            if(binding.etSearch.text.toString() == "") {
               adapter2.clearItems()
            }else {
               // 검색 단어를 포함하는지 확인
               for(i in 0 until strings.size) {
                  if(originalList[i].lowercase().contains(binding.etSearch.text.toString().lowercase())) {
                     searchList.add(originalList[i])
                  }
                  adapter2.setItems(searchList)
               }
            }
         }
      })

      adapter2 = SearchAdapter()
      binding.rv2.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
      binding.rv2.adapter = adapter2

      adapter2.setItemClickListener(object: SearchAdapter.OnItemClickListener{
         override fun onClick(v: View, position: Int) {
            Log.d(TAG, "position: ${searchList[position]}")
         }
      })
   }
}