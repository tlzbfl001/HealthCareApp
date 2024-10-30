package kr.bodywell.android.view.home.food

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kr.bodywell.android.adapter.FoodRecordAdapter
import kr.bodywell.android.adapter.SearchAdapter
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentFoodRecord1Binding
import kr.bodywell.android.model.Constant
import kr.bodywell.android.model.Item
import kr.bodywell.android.util.CustomUtil.hideKeyboard
import kr.bodywell.android.util.CustomUtil.replaceFragment2
import kr.bodywell.android.util.CustomUtil.replaceFragment4
import kr.bodywell.android.util.CustomUtil.setStatusBar

class FoodRecord1Fragment : Fragment() {
   private var _binding: FragmentFoodRecord1Binding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private var bundle = Bundle()
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
      _binding = FragmentFoodRecord1Binding.inflate(layoutInflater)

      setStatusBar(requireActivity(), binding.constraint)

      dataManager = DataManager(activity)
      dataManager.open()

      type = arguments?.getString("type").toString()
      bundle.putString("type", type)
      bundle.putString("back", "1")

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

      binding.tvBtn2.setOnClickListener {
         replaceFragment4(requireActivity(), FoodRecord2Fragment(), bundle)
      }

      binding.tvBtn3.setOnClickListener {
         replaceFragment2(requireActivity(), FoodInputFragment(), bundle)
      }

      val itemList = dataManager.getSearchFood("useCount")

      if(itemList.size > 0) {
         binding.tvEmpty.visibility = View.GONE
         binding.rv1.visibility = View.VISIBLE

         val adapter = FoodRecordAdapter(requireActivity(), itemList, "1", type)
         binding.rv1.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

         adapter.setOnItemClickListener(object : FoodRecordAdapter.OnItemClickListener {
            override fun onItemClick(pos: Int) {
               bundle.putString("id", itemList[pos].id.toString())
               replaceFragment2(requireActivity(), FoodAddFragment(), bundle)
            }
         })

         binding.rv1.adapter = adapter
         binding.rv1.requestLayout()
      }

      val adapter = SearchAdapter(requireActivity(), "1", type)

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
               adapter.clearItems()
            }else {
               for(i in 0 until itemList.size) { // 검색 단어를 포함하는지 확인
                  if(itemList[i].name.lowercase().contains(binding.etSearch.text.toString().lowercase())) {
                     searchList.add(Item(int1 = itemList[i].id, string1 = itemList[i].registerType, string2 = itemList[i].uid!!, string3 = itemList[i].name))
                  }
                  adapter.setItems(searchList)
               }
            }
         }
      })

      binding.rv2.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
      binding.rv2.adapter = adapter

      adapter.setItemClickListener(object: SearchAdapter.OnItemClickListener{
         override fun onClick(v: View, pos: Int) {
            bundle.putString("id", searchList[pos].int1.toString())
            replaceFragment2(requireActivity(), FoodAddFragment(), bundle)
         }
      })

      binding.rv2.adapter = adapter

      return binding.root
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}