package com.makebodywell.bodywell.view.home.exercise

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.makebodywell.bodywell.adapter.ExerciseListAdapter
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentExerciseListBinding
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import com.makebodywell.bodywell.util.MyApp
import java.time.LocalDate

class ExerciseListFragment : Fragment() {
   private var _binding: FragmentExerciseListBinding? = null
   private val binding get() = _binding!!

   private var bundle = Bundle()
   private var dataManager: DataManager? = null
   private var calendarDate = ""

   @SuppressLint("DiscouragedApi", "InternalInsetResource")
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
      dataManager!!.open()

      calendarDate = arguments?.getString("calendarDate").toString()
      bundle.putString("calendarDate", calendarDate)

      binding.clBack.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseFragment())
      }

      binding.tvInput.setOnClickListener {
         replaceFragment2(requireActivity(), ExerciseRecord1Fragment(), bundle)
      }

      val getExercise = dataManager!!.getExercise(calendarDate)

      val adapter = ExerciseListAdapter(requireActivity(), getExercise)
      binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
      binding.recyclerView.adapter = adapter

      return binding.root
   }
}