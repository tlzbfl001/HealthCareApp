package com.makebodywell.bodywell.view.home.food

<<<<<<< HEAD
=======
import android.content.Context
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
<<<<<<< HEAD
=======
import androidx.activity.OnBackPressedCallback
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
import com.makebodywell.bodywell.databinding.FragmentFoodRecordListBinding
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1

class FoodRecordListFragment : Fragment() {
   private var _binding: FragmentFoodRecordListBinding? = null
   private val binding get() = _binding!!

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentFoodRecordListBinding.inflate(layoutInflater)

      binding.ivBack.setOnClickListener {
         replaceFragment1(requireActivity(), FoodBreakfastFragment())
      }

      return binding.root
   }
}