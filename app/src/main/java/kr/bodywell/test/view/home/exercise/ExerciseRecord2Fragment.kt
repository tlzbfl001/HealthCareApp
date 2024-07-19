package kr.bodywell.test.view.home.exercise

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
import kr.bodywell.test.adapter.ExerciseRecordAdapter
import kr.bodywell.test.adapter.SearchAdapter
import kr.bodywell.test.database.DataManager
import kr.bodywell.test.databinding.FragmentExerciseRecord2Binding
import kr.bodywell.test.model.Exercise
import kr.bodywell.test.model.Item
import kr.bodywell.test.util.CustomUtil.Companion.hideKeyboard
import kr.bodywell.test.util.CustomUtil.Companion.replaceFragment2
import kr.bodywell.test.util.CustomUtil.Companion.replaceFragment3

class ExerciseRecord2Fragment : Fragment() {
   private var _binding: FragmentExerciseRecord2Binding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private var bundle = Bundle()
   private var adapter1: ExerciseRecordAdapter? = null
   private var itemList = ArrayList<Exercise>()
   private val searchList = ArrayList<Item>()

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment3(requireActivity(), ExerciseListFragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentExerciseRecord2Binding.inflate(layoutInflater)

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

      bundle.putString("back", "2")

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
         replaceFragment3(requireActivity(), ExerciseListFragment())
      }

      binding.tvBtn1.setOnClickListener {
         replaceFragment3(requireActivity(), ExerciseRecord1Fragment())
      }

      binding.tvBtn3.setOnClickListener {
         replaceFragment3(requireActivity(), ExerciseInputFragment())
      }

      itemList = dataManager.getSearchExercise("useDate")

      if(itemList.size > 0) {
         binding.tvEmpty.visibility = View.GONE
         binding.rv1.visibility = View.VISIBLE

         adapter1 = ExerciseRecordAdapter(requireActivity(), itemList, "2")
         binding.rv1.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

         adapter1!!.setOnItemClickListener(object : ExerciseRecordAdapter.OnItemClickListener {
            override fun onItemClick(pos: Int) {
               bundle.putString("id", itemList[pos].id.toString())
               replaceFragment2(requireActivity(), ExerciseAddFragment(), bundle)
            }
         })

         binding.rv1.adapter = adapter1
      }

      val adapter = SearchAdapter(requireActivity(), "2", "")

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
               for(i in 0 until itemList.size) {
                  if(itemList[i].name.lowercase().contains(binding.etSearch.text.toString().lowercase())) {
                     searchList.add(Item(int1 = itemList[i].id, int2 = itemList[i].basic, string1 = itemList[i].uid, string2 = itemList[i].name))
                  }
                  adapter.setItems(searchList)
               }
            }
         }
      })

      binding.rv2.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

      adapter.setItemClickListener(object: SearchAdapter.OnItemClickListener{
         override fun onClick(v: View, pos: Int) {
            bundle.putString("id", searchList[pos].int1.toString())
            replaceFragment2(requireActivity(), ExerciseAddFragment(), bundle)
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