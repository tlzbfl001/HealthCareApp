package kr.bodywell.health.view.home.exercise

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
import kr.bodywell.health.adapter.ExerciseListAdapter
import kr.bodywell.health.databinding.FragmentExerciseListBinding
import kr.bodywell.health.model.Workout
import kr.bodywell.health.util.CalendarUtil.selectedDate
import kr.bodywell.health.util.CustomUtil.replaceFragment1
import kr.bodywell.health.util.CustomUtil.replaceFragment3
import kr.bodywell.health.util.CustomUtil.setStatusBar
import kr.bodywell.health.util.MyApp.Companion.powerSync
import kr.bodywell.health.view.home.DetailFragment

class ExerciseListFragment : Fragment() {
   private var _binding: FragmentExerciseListBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment3(parentFragmentManager, DetailFragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentExerciseListBinding.inflate(layoutInflater)

      setStatusBar(requireActivity(), binding.mainLayout)

      binding.clBack.setOnClickListener {
         replaceFragment3(parentFragmentManager, DetailFragment())
      }

      binding.tvInput.setOnClickListener {
         replaceFragment1(parentFragmentManager, ExerciseRecord1Fragment())
      }

      lifecycleScope.launch {
         val getAllWorkout = powerSync.getWorkouts(selectedDate.toString()) as ArrayList<Workout>
         val adapter = ExerciseListAdapter(getAllWorkout)
         binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
         binding.recyclerView.adapter = adapter
      }

      return binding.root
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}