package kr.bodywell.health.view.home.medicine

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import kr.bodywell.health.adapter.MedicineAdapter2
import kr.bodywell.health.databinding.FragmentMedicineRecordBinding
import kr.bodywell.health.model.Medicine
import kr.bodywell.health.util.CalendarUtil.selectedDate
import kr.bodywell.health.util.CustomUtil.replaceFragment1
import kr.bodywell.health.util.CustomUtil.replaceFragment3
import kr.bodywell.health.util.CustomUtil.setStatusBar
import kr.bodywell.health.util.MyApp.Companion.powerSync
import kr.bodywell.health.view.home.DetailFragment
import java.util.ArrayList

class MedicineRecordFragment : Fragment() {
   private var _binding: FragmentMedicineRecordBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private var adapter: MedicineAdapter2? = null

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment3(requireActivity().supportFragmentManager, DetailFragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentMedicineRecordBinding.inflate(layoutInflater)

      setStatusBar(requireActivity(), binding.mainLayout)

      binding.clBack.setOnClickListener {
         replaceFragment3(requireActivity().supportFragmentManager, DetailFragment())
      }

      binding.tvAdd.setOnClickListener {
         replaceFragment1(requireActivity().supportFragmentManager, MedicineAddFragment())
      }

      lifecycleScope.launch {
         val getMedicine = powerSync.getMedicines(selectedDate.toString()) as ArrayList<Medicine>
         if(getMedicine.isNotEmpty()) {
            adapter = MedicineAdapter2(requireActivity().supportFragmentManager, getMedicine)
            binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            binding.recyclerView.requestLayout()
            binding.recyclerView.adapter = adapter
         }
      }

      return binding.root
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}