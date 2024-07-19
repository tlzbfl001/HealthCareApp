package kr.bodywell.test.view.home.exercise

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kr.bodywell.test.adapter.ExerciseListAdapter
import kr.bodywell.test.database.DataManager
import kr.bodywell.test.databinding.FragmentExerciseListBinding
import kr.bodywell.test.util.CalendarUtil.Companion.selectedDate
import kr.bodywell.test.util.CustomUtil.Companion.replaceFragment1
import kr.bodywell.test.util.CustomUtil.Companion.replaceFragment3
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

      requireActivity().window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK

         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.mainLayout.setPadding(0, statusBarHeight, 0, 0)
      }

      dataManager = DataManager(activity)
      dataManager.open()

      binding.clBack.setOnClickListener {
         replaceFragment3(requireActivity(), DetailFragment())
      }

      binding.cvInput.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseRecord1Fragment())
      }

      val getDailyExercise = dataManager.getDailyExercise("created", selectedDate.toString())

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