package kr.bodywell.android.view.home.food

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import kr.bodywell.android.adapter.FoodRecordAdapter
import kr.bodywell.android.adapter.SearchAdapter
import kr.bodywell.android.databinding.FragmentFoodRecord2Binding
import kr.bodywell.android.model.Constant
import kr.bodywell.android.model.Food
import kr.bodywell.android.model.Item
import kr.bodywell.android.util.CustomUtil.hideKeyboard
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment2
import kr.bodywell.android.util.CustomUtil.replaceFragment4
import kr.bodywell.android.util.CustomUtil.setStatusBar

class FoodRecord2Fragment : Fragment() {
   private var _binding: FragmentFoodRecord2Binding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private var bundle = Bundle()
   private var itemList = ArrayList<Food>()
   private val searchList = ArrayList<Item>()
   private var type = Constant.BREAKFAST.name

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment4(requireActivity(), FoodDetailFragment(), bundle)
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentFoodRecord2Binding.inflate(layoutInflater)

      setStatusBar(requireActivity(), binding.constraint)

      type = arguments?.getString("type").toString()
      bundle.putString("type", type)
      bundle.putString("back", "2")

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
         replaceFragment4(requireActivity(), FoodDetailFragment(), bundle)
      }

      binding.tvBtn1.setOnClickListener {
         replaceFragment4(requireActivity(), FoodRecord1Fragment(), bundle)
      }

      binding.tvBtn3.setOnClickListener {
         replaceFragment2(requireActivity(), FoodInputFragment(), bundle)
      }

      lifecycleScope.launch {
         val itemList = powerSync.getAllFoodOrder() as ArrayList<Food>

         for(i in 0 until itemList.size) powerSync.deleteDuplicates("foods", "name", itemList[i].name, itemList[i].id)

         if(itemList.size > 0) {
            binding.tvEmpty.visibility = View.GONE
            binding.rv1.visibility = View.VISIBLE

            val recordAdapter = FoodRecordAdapter(requireActivity(), itemList, "2", type)
            binding.rv1.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

            recordAdapter.setOnItemClickListener(object : FoodRecordAdapter.OnItemClickListener {
               override fun onItemClick(pos: Int) {
                  bundle.putParcelable("food", itemList[pos])
                  replaceFragment2(requireActivity(), FoodAddFragment(), bundle)
               }
            })

            binding.rv1.adapter = recordAdapter
         }

         val searchAdapter = SearchAdapter(requireActivity(), "2", type)

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
                  searchAdapter.clearItems()
               }else {
                  // 검색 단어를 포함하는지 확인
                  for(i in 0 until itemList.size) {
                     if(itemList[i].name.lowercase().contains(binding.etSearch.text.toString().lowercase())) {
                        searchList.add(Item(string1 = itemList[i].id, string2 = itemList[i].name, string3 = itemList[i].registerType))
                     }
                     searchAdapter.setItems(searchList)
                  }
               }
            }
         })

         binding.rv2.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
         binding.rv2.adapter = searchAdapter

         searchAdapter.setItemClickListener(object: SearchAdapter.OnItemClickListener{
            override fun onClick(v: View, pos: Int) {
               bundle.putString("id", searchList[pos].int1.toString())
               replaceFragment2(requireActivity(), FoodAddFragment(), bundle)
            }
         })
      }

      return binding.root
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}