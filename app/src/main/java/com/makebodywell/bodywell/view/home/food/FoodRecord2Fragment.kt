package com.makebodywell.bodywell.view.home.food

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.makebodywell.bodywell.adapter.FoodRecordAdapter
import com.makebodywell.bodywell.adapter.SearchAdapter
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentFoodRecord2Binding
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.model.Item
import com.makebodywell.bodywell.util.CustomUtil.Companion.hideKeyboard
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import com.makebodywell.bodywell.view.home.MainActivity

class FoodRecord2Fragment : Fragment(), MainActivity.OnBackPressedListener {
   private var _binding: FragmentFoodRecord2Binding? = null
   private val binding get() = _binding!!

   private var bundle = Bundle()
   private var dataManager: DataManager? = null
   private var itemList = ArrayList<Food>()
   private val originalList = ArrayList<Food>()
   private val searchList = ArrayList<Item>()
   private var type = ""

   override fun onAttach(context: Context) {
      super.onAttach(context)
      (context as MainActivity).setOnBackPressedListener(this)
   }

   @SuppressLint("DiscouragedApi", "InternalInsetResource", "ClickableViewAccessibility")
   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentFoodRecord2Binding.inflate(layoutInflater)

      requireActivity().window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK

         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.constraint.setPadding(0, statusBarHeight, 0, 0)
      }

      dataManager = DataManager(activity)
      dataManager!!.open()

      type = arguments?.getString("type").toString()
      bundle.putString("type", type)

      binding.constraint.setOnTouchListener { view, motionEvent ->
         hideKeyboard(requireActivity())
         true
      }

      binding.linear.setOnTouchListener { view, motionEvent ->
         hideKeyboard(requireActivity())
         true
      }

      binding.rv1.setOnTouchListener { view, motionEvent ->
         hideKeyboard(requireActivity())
         true
      }

      binding.rv2.setOnTouchListener { view, motionEvent ->
         hideKeyboard(requireActivity())
         true
      }

      binding.clBack.setOnClickListener {
         when(type) {
            "1" -> replaceFragment1(requireActivity(), FoodBreakfastFragment())
            "2" -> replaceFragment1(requireActivity(), FoodLunchFragment())
            "3" -> replaceFragment1(requireActivity(), FoodDinnerFragment())
            "4" -> replaceFragment1(requireActivity(), FoodSnackFragment())
         }
      }

      binding.tvBtn1.setOnClickListener {
         replaceFragment2(requireActivity(), FoodRecord1Fragment(), bundle)
      }

      binding.tvBtn3.setOnClickListener {
         replaceFragment2(requireActivity(), FoodInputFragment(), bundle)
      }

      listView()
      searchView()

      return binding.root
   }

   private fun listView() {
      itemList.clear()

      itemList = dataManager!!.getSearchFood("useDate")

      if(itemList.size > 0) {
         binding.tvEmpty.visibility = View.GONE
         binding.rv1.visibility = View.VISIBLE

         val adapter = FoodRecordAdapter(itemList)
         binding.rv1.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

         adapter.setOnItemClickListener(object : FoodRecordAdapter.OnItemClickListener {
            override fun onItemClick(pos: Int) {
               bundle.putString("dataId", itemList[pos].id.toString())
               replaceFragment2(requireActivity(), FoodSearchFragment(), bundle)
            }
         })

         binding.rv1.adapter = adapter
      }
   }

   private fun searchView() {
      var adapter = SearchAdapter()

      for(i in 0 until itemList.size) {
         originalList.add(itemList[i])
      }

      binding.etSearch.addTextChangedListener(object: TextWatcher {
         override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
         override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
         override fun afterTextChanged(p0: Editable?) {
            binding.rv1.visibility = View.GONE
            binding.rv2.visibility = View.VISIBLE
            searchList.clear()
            if(binding.etSearch.text.toString() == "") {
               binding.rv1.visibility = View.VISIBLE
               binding.rv2.visibility = View.GONE
               adapter.clearItems()
            }else {
               // 검색 단어를 포함하는지 확인
               for(i in 0 until itemList.size) {
                  if(originalList[i].name.lowercase().contains(binding.etSearch.text.toString().lowercase())) {
                     searchList.add(Item(int1 = originalList[i].id, string1 = originalList[i].name))
                  }
                  adapter.setItems(searchList)
               }
            }
         }
      })

      adapter = SearchAdapter()
      binding.rv2.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
      binding.rv2.adapter = adapter

      adapter.setItemClickListener(object: SearchAdapter.OnItemClickListener{
         override fun onClick(v: View, pos: Int) {
            bundle.putString("dataId", searchList[pos].int1.toString())
            replaceFragment2(requireActivity(), FoodSearchFragment(), bundle)
         }
      })
   }

   override fun onBackPressed() {
      val activity = activity as MainActivity?
      activity!!.setOnBackPressedListener(null)

      when(type) {
         "1" -> replaceFragment1(requireActivity(), FoodBreakfastFragment())
         "2" -> replaceFragment1(requireActivity(), FoodLunchFragment())
         "3" -> replaceFragment1(requireActivity(), FoodDinnerFragment())
         "4" -> replaceFragment1(requireActivity(), FoodSnackFragment())
      }
   }
}