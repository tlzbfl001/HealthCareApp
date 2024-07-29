package kr.bodywell.android.view.home.food

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import kr.bodywell.android.adapter.FoodRecordAdapter
import kr.bodywell.android.adapter.SearchAdapter
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentFoodRecord2Binding
import kr.bodywell.android.model.Item
import kr.bodywell.android.util.CustomUtil.Companion.hideKeyboard
import kr.bodywell.android.util.CustomUtil.Companion.replaceFragment2
import kr.bodywell.android.util.CustomUtil.Companion.replaceFragment4

class FoodRecord2Fragment : Fragment() {
   private var _binding: FragmentFoodRecord2Binding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private var bundle = Bundle()
   private val searchList = ArrayList<Item>()
   private var type = "BREAKFAST"

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

      requireActivity().window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK

         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.constraint.setPadding(0, statusBarHeight, 0, 0)
      }

      dataManager = DataManager(activity)
      dataManager.open()

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

      val itemList = dataManager.getSearchFood("useDate")

      if(itemList.size > 0) {
         binding.tvEmpty.visibility = View.GONE
         binding.rv1.visibility = View.VISIBLE

         val adapter = FoodRecordAdapter(requireActivity(), itemList, "2", type)
         binding.rv1.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

         adapter.setOnItemClickListener(object : FoodRecordAdapter.OnItemClickListener {
            override fun onItemClick(pos: Int) {
               bundle.putString("id", itemList[pos].id.toString())
               replaceFragment2(requireActivity(), FoodAddFragment(), bundle)
            }
         })

         binding.rv1.adapter = adapter
      }

      val adapter = SearchAdapter(requireActivity(), "2", type)

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
                  if(itemList[i].name.lowercase().contains(binding.etSearch.text.toString().lowercase())) {
                     searchList.add(Item(int1 = itemList[i].id, int2 = itemList[i].admin, string1 = itemList[i].uid, string2 = itemList[i].name))
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

      return binding.root
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}