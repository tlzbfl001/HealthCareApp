package kr.bodywell.health.view.home.exercise

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
import kr.bodywell.health.adapter.ExerciseRecordAdapter
import kr.bodywell.health.adapter.SearchAdapter
import kr.bodywell.health.databinding.FragmentExerciseRecord1Binding
import kr.bodywell.health.model.ActivityData
import kr.bodywell.health.model.Constant.ACTIVITIES
import kr.bodywell.health.model.Item
import kr.bodywell.health.util.CustomUtil.hideKeyboard
import kr.bodywell.health.util.CustomUtil.replaceFragment2
import kr.bodywell.health.util.CustomUtil.replaceFragment3
import kr.bodywell.health.util.CustomUtil.setStatusBar
import kr.bodywell.health.util.MyApp.Companion.powerSync

class ExerciseRecord1Fragment : Fragment() {
   private var _binding: FragmentExerciseRecord1Binding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private val bundle = Bundle()
   private var itemList = ArrayList<ActivityData>()
   private val searchList = ArrayList<Item>()

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment3(requireActivity().supportFragmentManager, ExerciseListFragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentExerciseRecord1Binding.inflate(layoutInflater)

      setStatusBar(requireActivity(), binding.mainLayout)

      binding.mainLayout.setOnTouchListener { _, _ ->
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
         replaceFragment3(requireActivity().supportFragmentManager, ExerciseListFragment())
      }

      binding.tvBtn2.setOnClickListener {
         replaceFragment3(requireActivity().supportFragmentManager, ExerciseRecord2Fragment())
      }

      binding.tvBtn3.setOnClickListener {
         replaceFragment3(requireActivity().supportFragmentManager, ExerciseInputFragment())
      }

      listView()
      searchView()

      return binding.root
   }

   private fun listView() {
      lifecycleScope.launch {
         val getActivities = powerSync.getActivities() as ArrayList<ActivityData>
         for(i in getActivities.indices) powerSync.deleteDuplicate(ACTIVITIES, "name", getActivities[i].name, getActivities[i].id)
         itemList = powerSync.getActivityUsages1() as ArrayList<ActivityData>
         val getUsages = powerSync.getActivityUsages1() as ArrayList<ActivityData>

         for(i in 0 until getActivities.size) {
            var check = false
            for(j in 0 until getUsages.size) {
               if(getActivities[i].name == getUsages[j].name) {
                  check = true
                  break
               }
            }
            if(!check) itemList.add(getActivities[i])
         }

         if(itemList.isNotEmpty()) {
            binding.tvEmpty.visibility = View.GONE
            binding.rv1.visibility = View.VISIBLE
            val adapter = ExerciseRecordAdapter(requireActivity(), itemList)
            binding.rv1.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

            adapter.setOnItemClickListener(object : ExerciseRecordAdapter.OnItemClickListener {
               override fun onItemClick(pos: Int) {
                  bundle.putString("id", itemList[pos].id)
                  replaceFragment2(requireActivity().supportFragmentManager, ExerciseAddFragment(), bundle)
               }
            })

            binding.rv1.adapter = adapter
         }
      }
   }

   private fun searchView() {
      val adapter = SearchAdapter(requireActivity(), childFragmentManager, "")

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
                     searchList.add(Item(string1 = itemList[i].id, string2 = itemList[i].name, string3 = itemList[i].registerType))
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
            bundle.putString("id", searchList[pos].string1)
            replaceFragment2(requireActivity().supportFragmentManager, ExerciseAddFragment(), bundle)
         }
      })
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}