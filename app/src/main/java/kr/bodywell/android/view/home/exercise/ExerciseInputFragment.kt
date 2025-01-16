package kr.bodywell.android.view.home.exercise

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kr.bodywell.android.databinding.FragmentExerciseInputBinding
import kr.bodywell.android.model.ActivityData
import kr.bodywell.android.model.Constant.ACTIVITIES
import kr.bodywell.android.util.CustomUtil.dateTimeToIso1
import kr.bodywell.android.util.CustomUtil.filterText
import kr.bodywell.android.util.CustomUtil.getUUID
import kr.bodywell.android.util.CustomUtil.hideKeyboard
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment3
import kr.bodywell.android.util.CustomUtil.setStatusBar
import java.util.Calendar

class ExerciseInputFragment : Fragment() {
   private var _binding: FragmentExerciseInputBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment3(parentFragmentManager, ExerciseRecord1Fragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentExerciseInputBinding.inflate(layoutInflater)

      setStatusBar(requireActivity(), binding.mainLayout)

      binding.mainLayout.setOnTouchListener { _, _ ->
         hideKeyboard(requireActivity())
         true
      }

      binding.clX.setOnClickListener {
         replaceFragment3(parentFragmentManager, ExerciseRecord1Fragment())
      }

      binding.cvSave.setOnClickListener {
         lifecycleScope.launch {
            val getData = powerSync.getData(ACTIVITIES, "name", "name", binding.etName.text.trim().toString())

            if(binding.etName.text.toString().trim().isEmpty()) {
               Toast.makeText(context, "운동이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else if(!filterText(binding.etName.text.toString().trim())) {
               Toast.makeText(context, "특수문자는 입력 불가합니다.", Toast.LENGTH_SHORT).show()
            }else if(getData != "") {
               Toast.makeText(context, "운동이름이 중복됩니다.", Toast.LENGTH_SHORT).show()
            }else {
               powerSync.insertActivity(ActivityData(id = getUUID(), name = binding.etName.text.toString().trim(),
                  createdAt = dateTimeToIso1(Calendar.getInstance()), updatedAt = dateTimeToIso1(Calendar.getInstance())))
               Toast.makeText(requireActivity(), "저장되었습니다.", Toast.LENGTH_SHORT).show()
               replaceFragment3(parentFragmentManager, ExerciseRecord1Fragment())
            }
         }
      }

      return binding.root
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}