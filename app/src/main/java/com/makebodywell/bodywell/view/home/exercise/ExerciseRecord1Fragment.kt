package com.makebodywell.bodywell.view.home.exercise

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
import com.makebodywell.bodywell.databinding.FragmentExerciseRecord1Binding
import com.makebodywell.bodywell.model.Item
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2

class ExerciseRecord1Fragment : Fragment() {
   private var _binding: FragmentExerciseRecord1Binding? = null
   private val binding get() = _binding!!

   private var bundle = Bundle()
   private var dataManager: DataManager? = null
   private val itemList = ArrayList<Item>()
   private val searchList = ArrayList<Item>()
   private val originalList = ArrayList<Item>()

   private var calendarDate = ""

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentExerciseRecord1Binding.inflate(layoutInflater)

      requireActivity().window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK

         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.cl1.setPadding(0, statusBarHeight, 0, 0)
      }

      dataManager = DataManager(activity)
      dataManager!!.open()

      calendarDate = arguments?.getString("calendarDate")!!
      bundle.putString("calendarDate", calendarDate)

      binding.clBack.setOnClickListener {
         replaceFragment2(requireActivity(), ExerciseListFragment(), bundle)
      }

      binding.tvBtn2.setOnClickListener {
         replaceFragment2(requireActivity(), ExerciseRecord2Fragment(), bundle)
      }

      binding.tvBtn3.setOnClickListener {
         replaceFragment2(requireActivity(), ExerciseInputFragment(), bundle)
      }

      listView()
      searchView()

      return binding.root
   }

   private fun listView() {
      val dataList = dataManager!!.getExercise(calendarDate)

      if(dataList.size > 0) {
         binding.tvEmpty.visibility = View.GONE
         binding.rv1.visibility = View.VISIBLE

         for(i in 0 until dataList.size) {
            itemList.add(Item(string1 = dataList[i].name, int1 = dataList[i].id))
         }

         val adapter = FoodRecordAdapter(itemList)
         binding.rv1.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

         adapter.setOnItemClickListener(object : FoodRecordAdapter.OnItemClickListener {
            override fun onItemClick(pos: Int) {
               bundle.putString("id", dataList[pos].id.toString())
               replaceFragment2(requireActivity(), ExerciseEditFragment(), bundle)
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
                  if(originalList[i].string1.lowercase().contains(binding.etSearch.text.toString().lowercase())) {
                     searchList.add(originalList[i])
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
            bundle.putString("id", searchList[pos].int1.toString())
            replaceFragment2(requireActivity(), ExerciseEditFragment(), bundle)
         }
      })
   }
}