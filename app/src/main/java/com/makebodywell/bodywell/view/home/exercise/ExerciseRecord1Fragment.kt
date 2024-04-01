package com.makebodywell.bodywell.view.home.exercise

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
import com.makebodywell.bodywell.adapter.ExerciseRecordAdapter
import com.makebodywell.bodywell.adapter.SearchAdapter
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentExerciseRecord1Binding
import com.makebodywell.bodywell.model.Exercise
import com.makebodywell.bodywell.model.Item
import com.makebodywell.bodywell.util.CustomUtil.Companion.hideKeyboard
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2

class ExerciseRecord1Fragment : Fragment() {
   private var _binding: FragmentExerciseRecord1Binding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private var bundle = Bundle()
   private var itemList = ArrayList<Exercise>()
   private val searchList = ArrayList<Item>()

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment1(requireActivity(), ExerciseListFragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

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
         binding.constraint.setPadding(0, statusBarHeight, 0, 0)
      }

      dataManager = DataManager(activity)
      dataManager.open()

      bundle.putString("back", "1")

      binding.constraint.setOnTouchListener { _, _ ->
         hideKeyboard(requireActivity())
         true
      }

      binding.linear.setOnTouchListener { _, _ ->
         hideKeyboard(requireActivity())
         true
      }

      binding.rv1.setOnTouchListener { _, _ ->
         hideKeyboard(requireActivity())
         true
      }

      binding.rv2.setOnTouchListener { _, _ ->
         hideKeyboard(requireActivity())
         true
      }

      binding.clBack.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseListFragment())
      }

      binding.tvBtn2.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseRecord2Fragment())
      }

      binding.tvBtn3.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseInputFragment())
      }

      listView()
      searchView()

      return binding.root
   }

   private fun listView() {
      itemList = dataManager.getSearchExercise("useCount")

      if(itemList.size > 0) {
         binding.tvEmpty.visibility = View.GONE
         binding.rv1.visibility = View.VISIBLE

         val adapter = ExerciseRecordAdapter(requireActivity(), itemList, "1")
         binding.rv1.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

         adapter.setOnItemClickListener(object : ExerciseRecordAdapter.OnItemClickListener {
            override fun onItemClick(pos: Int) {
               bundle.putString("id", itemList[pos].id.toString())
               replaceFragment2(requireActivity(), ExerciseAddFragment(), bundle)
            }
         })

         binding.rv1.adapter = adapter
      }
   }

   private fun searchView() {
      val adapter = SearchAdapter(requireActivity(), "1", "")

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
               for(i in 0 until itemList.size) { // 검색 단어를 포함하는지 확인
                  if(itemList[i].name.lowercase().contains(binding.etSearch.text.toString().lowercase())) {
                     searchList.add(Item(int1 = itemList[i].id, string1 = itemList[i].name))
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
            replaceFragment2(requireActivity(), ExerciseAddFragment(), bundle)
         }
      })
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}