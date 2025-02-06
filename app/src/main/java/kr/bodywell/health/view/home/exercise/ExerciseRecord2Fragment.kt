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
import kr.bodywell.health.databinding.FragmentExerciseRecord2Binding
import kr.bodywell.health.model.ActivityData
import kr.bodywell.health.model.Item
import kr.bodywell.health.util.CustomUtil.hideKeyboard
import kr.bodywell.health.util.CustomUtil.replaceFragment2
import kr.bodywell.health.util.CustomUtil.replaceFragment3
import kr.bodywell.health.util.CustomUtil.setStatusBar
import kr.bodywell.health.util.MyApp.Companion.powerSync

class ExerciseRecord2Fragment : Fragment() {
   private var _binding: FragmentExerciseRecord2Binding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private val bundle = Bundle()
   private var adapter1: ExerciseRecordAdapter? = null
   private var itemList = ArrayList<ActivityData>()
   private val searchList = ArrayList<Item>()

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment3(parentFragmentManager, ExerciseListFragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentExerciseRecord2Binding.inflate(layoutInflater)

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
         replaceFragment3(parentFragmentManager, ExerciseListFragment())
      }

      binding.tvBtn1.setOnClickListener {
         replaceFragment3(parentFragmentManager, ExerciseRecord1Fragment())
      }

      binding.tvBtn3.setOnClickListener {
         replaceFragment3(parentFragmentManager, ExerciseInputFragment())
      }

      lifecycleScope.launch {
         val getActivities = powerSync.getActivities() as ArrayList<ActivityData>
         itemList = powerSync.getActivityUsages2() as ArrayList<ActivityData>
         val getUsages = powerSync.getActivityUsages2() as ArrayList<ActivityData>

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

         if(itemList.size > 0) {
            binding.tvEmpty.visibility = View.GONE
            binding.rv1.visibility = View.VISIBLE

            adapter1 = ExerciseRecordAdapter(requireActivity(), itemList)
            binding.rv1.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

            adapter1!!.setOnItemClickListener(object : ExerciseRecordAdapter.OnItemClickListener {
               override fun onItemClick(pos: Int) {
                  bundle.putString("id", itemList[pos].id)
                  replaceFragment2(parentFragmentManager, ExerciseAddFragment(), bundle)
               }
            })

            binding.rv1.adapter = adapter1
         }
      }

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
               for(i in 0 until itemList.size) {
                  if(itemList[i].name.lowercase().contains(binding.etSearch.text.toString().lowercase())) {
                     searchList.add(Item(string1 = itemList[i].id, string2 = itemList[i].name, string3 = itemList[i].registerType))
                  }
                  adapter.setItems(searchList)
               }
            }
         }
      })

      binding.rv2.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

      adapter.setItemClickListener(object: SearchAdapter.OnItemClickListener{
         override fun onClick(v: View, pos: Int) {
            bundle.putString("id", searchList[pos].string1)
            replaceFragment2(parentFragmentManager, ExerciseAddFragment(), bundle)
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