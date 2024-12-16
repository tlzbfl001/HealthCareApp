package kr.bodywell.android.view.home.food

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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import kr.bodywell.android.adapter.FoodRecordAdapter
import kr.bodywell.android.adapter.SearchAdapter
import kr.bodywell.android.databinding.FragmentFoodRecord1Binding
import kr.bodywell.android.model.Constants.FOODS
import kr.bodywell.android.model.Food
import kr.bodywell.android.model.Item
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.CustomUtil.hideKeyboard
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment2
import kr.bodywell.android.util.CustomUtil.replaceFragment4
import kr.bodywell.android.util.CustomUtil.setStatusBar

class FoodRecord1Fragment : Fragment() {
   private var _binding: FragmentFoodRecord1Binding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private var bundle = Bundle()
   private val searchList = ArrayList<Item>()

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment4(parentFragmentManager, FoodDetailFragment(), bundle)
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentFoodRecord1Binding.inflate(layoutInflater)

      setStatusBar(requireActivity(), binding.constraint)

      val type = arguments?.getString("type").toString()
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
         replaceFragment4(parentFragmentManager, FoodDetailFragment(), bundle)
      }

      binding.tvBtn2.setOnClickListener {
         replaceFragment4(parentFragmentManager, FoodRecord2Fragment(), bundle)
      }

      binding.tvBtn3.setOnClickListener {
         replaceFragment2(parentFragmentManager, FoodInputFragment(), bundle)
      }

      lifecycleScope.launch {
         val getFoods = powerSync.getFoods()
         for(i in getFoods.indices) powerSync.deleteDuplicate(FOODS, "name", getFoods[i].name, getFoods[i].id)
         val itemList = powerSync.getFoodUsages() as ArrayList<Food>

         if(itemList.isNotEmpty()) {
            binding.tvEmpty.visibility = View.GONE
            binding.rv1.visibility = View.VISIBLE

            val recordAdapter = FoodRecordAdapter(requireActivity(), parentFragmentManager, itemList, type)
            binding.rv1.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

            recordAdapter.setOnItemClickListener(object : FoodRecordAdapter.OnItemClickListener {
               override fun onItemClick(pos: Int) {
                  bundle.putParcelable(FOODS, itemList[pos])
                  replaceFragment2(parentFragmentManager, FoodAddFragment(), bundle)
               }
            })

            binding.rv1.adapter = recordAdapter
            binding.rv1.requestLayout()
         }

         val searchAdapter = SearchAdapter(requireActivity(), parentFragmentManager, type)
         binding.rv2.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
         binding.rv2.adapter = searchAdapter

         binding.etSearch.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
               binding.rv1.visibility = View.GONE
               binding.rv2.visibility = View.VISIBLE
               searchList.clear()
               if(binding.etSearch.text.toString() == "") {
                  binding.rv1.visibility = View.VISIBLE
                  binding.rv2.visibility = View.GONE
                  searchAdapter.clearItems()
               }else {
                  for(i in itemList.indices) { // 검색 단어를 포함하는지 확인
                     if(itemList[i].name.lowercase().contains(binding.etSearch.text.toString().lowercase())) {
                        searchList.add(Item(string1 = itemList[i].id, string2 = itemList[i].name, string3 = itemList[i].registerType))
                     }
                     searchAdapter.setItems(searchList)
                  }
               }
            }
         })

         searchAdapter.setItemClickListener(object: SearchAdapter.OnItemClickListener{
            override fun onClick(v: View, pos: Int) {
               bundle.putParcelable(FOODS, itemList[pos])
               replaceFragment2(parentFragmentManager, FoodAddFragment(), bundle)
            }
         })

         binding.rv2.adapter = searchAdapter
      }

      return binding.root
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}