package kr.bodywell.android.view.home.drug

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.runBlocking
import kr.bodywell.android.adapter.DrugAdapter2
import kr.bodywell.android.databinding.FragmentDrugRecordBinding
import kr.bodywell.android.model.Medicine
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.util.CustomUtil.replaceFragment3
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.view.home.DetailFragment
import java.util.ArrayList

class DrugRecordFragment : Fragment() {
   private var _binding: FragmentDrugRecordBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
//   private lateinit var dataManager: DataManager
   private var adapter: DrugAdapter2? = null

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment3(requireActivity(), DetailFragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentDrugRecordBinding.inflate(layoutInflater)

      setStatusBar(requireActivity(), binding.mainLayout)

//      dataManager = DataManager(activity)
//      dataManager.open()

      binding.clBack.setOnClickListener {
         replaceFragment3(requireActivity(), DetailFragment())
      }

      binding.tvAdd.setOnClickListener {
         replaceFragment1(requireActivity(), DrugAddFragment())
      }

      runBlocking {
         val getMedicine = powerSync.getMedicine(selectedDate.toString()) as ArrayList<Medicine>

         adapter = DrugAdapter2(requireActivity(), getMedicine)
         binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
         binding.recyclerView.requestLayout()
         binding.recyclerView.adapter = adapter
      }

      return binding.root
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}