package com.makebodywell.bodywell.view.home.food

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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

   private lateinit var callback: OnBackPressedCallback

   private var bundle = Bundle()

   private var dataManager: DataManager? = null
   private var adapter1 = FoodRecord1Adapter()
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

      initView()
      setupList()
      searchView()

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
         binding.cv2.visibility = View.VISIBLE

         for (i in 0 until dataList.size) {
            itemList.add(Food(name = dataList[i].name, unit = dataList[i].unit, amount = dataList[i].amount, kcal = dataList[i].kcal,
               carbohydrate = dataList[i].carbohydrate, protein = dataList[i].protein, fat = dataList[i].fat, salt = dataList[i].salt,
               sugar = dataList[i].sugar))
         }

         adapter1 = FoodRecord1Adapter(itemList)
         binding.recyclerView1.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
         binding.recyclerView1.adapter = adapter1
      }
   }

   private fun searchView() {
      val strings = arrayListOf<String>(
         "Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "가나", "한국", "가나다", "가다", "중국", "미국", "유럽"
      )

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
      binding.recyclerView2.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
      binding.recyclerView2.adapter = adapter2

      adapter2.setItemClickListener(object: SearchAdapter.OnItemClickListener{
         override fun onClick(v: View, position: Int) {
            Log.d(TAG, "position: ${searchList[position]}")
         }
      })
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