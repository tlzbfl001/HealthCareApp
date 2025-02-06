package kr.bodywell.health.view.home.food

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import kr.bodywell.health.adapter.FoodRecordAdapter
import kr.bodywell.health.adapter.SearchAdapter
import kr.bodywell.health.databinding.FragmentFoodRecord1Binding
import kr.bodywell.health.model.Constant.BREAKFAST
import kr.bodywell.health.model.Constant.FOODS
import kr.bodywell.health.model.Food
import kr.bodywell.health.model.Item
import kr.bodywell.health.util.CustomUtil.hideKeyboard
import kr.bodywell.health.util.CustomUtil.replaceFragment2
import kr.bodywell.health.util.CustomUtil.replaceFragment4
import kr.bodywell.health.util.CustomUtil.setStatusBar
import kr.bodywell.health.util.MyApp.Companion.powerSync

class FoodRecord1Fragment : Fragment() {
   private var _binding: FragmentFoodRecord1Binding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private var bundle = Bundle()
   private var itemList = ArrayList<Food>()
   private val searchList = ArrayList<Item>()
   private var type = BREAKFAST

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
         replaceFragment4(parentFragmentManager, FoodDetailFragment(), bundle)
      }

      binding.tvBtn2.setOnClickListener {
         replaceFragment4(parentFragmentManager, FoodRecord2Fragment(), bundle)
      }

      binding.tvBtn3.setOnClickListener {
         replaceFragment2(parentFragmentManager, FoodInputFragment(), bundle)
      }

      listView()
      searchView()

      return binding.root
   }

   private fun listView() {
      lifecycleScope.launch {
         val getFoods = powerSync.getFoods() as ArrayList<Food>
         for(i in getFoods.indices) powerSync.deleteDuplicate(FOODS, "name", getFoods[i].name, getFoods[i].id)

         itemList = powerSync.getFoodUsages1() as ArrayList<Food>
         val getUsages = powerSync.getFoodUsages1() as ArrayList<Food>

         for(i in 0 until getFoods.size) {
            var check = false
            for(j in 0 until getUsages.size) {
               if(getFoods[i].name == getUsages[j].name) {
                  check = true
                  break
               }
            }
            if(!check) itemList.add(getFoods[i])
         }

         if(itemList.isNotEmpty()) {
            binding.tvEmpty.visibility = View.GONE
            binding.rv1.visibility = View.VISIBLE

            val recordAdapter = FoodRecordAdapter(requireActivity(), parentFragmentManager, itemList, type)
            binding.rv1.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

            recordAdapter.setOnItemClickListener(object : FoodRecordAdapter.OnItemClickListener {
               override fun onItemClick(pos: Int) {
                  bundle.putString("foodId", itemList[pos].id)
                  replaceFragment2(parentFragmentManager, FoodAddFragment(), bundle)
               }
            })

            binding.rv1.adapter = recordAdapter
            binding.rv1.requestLayout()
         }
      }
   }

   private fun searchView() {
      val searchAdapter = SearchAdapter(requireActivity(), parentFragmentManager, type)
      binding.rv2.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

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
            bundle.putString("foodId", searchList[pos].string1)
            replaceFragment2(parentFragmentManager, FoodAddFragment(), bundle)
         }
      })

      binding.rv2.adapter = searchAdapter
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}