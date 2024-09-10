package kr.bodywell.test.view.home.exercise

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kr.bodywell.test.adapter.ExerciseListAdapter
import kr.bodywell.test.database.DBHelper.Companion.CREATED_AT
import kr.bodywell.test.database.DataManager
import kr.bodywell.test.databinding.FragmentExerciseListBinding
import kr.bodywell.test.util.CalendarUtil.selectedDate
import kr.bodywell.test.util.CustomUtil.replaceFragment1
import kr.bodywell.test.util.CustomUtil.replaceFragment3
import kr.bodywell.test.util.CustomUtil.setStatusBar
import kr.bodywell.test.view.home.DetailFragment

class ExerciseListFragment : Fragment() {
   private var _binding: FragmentExerciseListBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager

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
      _binding = FragmentExerciseListBinding.inflate(layoutInflater)

      setStatusBar(requireActivity(), binding.mainLayout)

      dataManager = DataManager(activity)
      dataManager.open()

      binding.clBack.setOnClickListener {
         replaceFragment3(requireActivity(), DetailFragment())
      }

      binding.cvInput.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseRecord1Fragment())
      }

      val getDailyExercise = dataManager.getDailyExercise(CREATED_AT, selectedDate.toString())

      val adapter = ExerciseListAdapter(requireActivity(), getDailyExercise)
      binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
      binding.recyclerView.adapter = adapter

      return binding.root
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}