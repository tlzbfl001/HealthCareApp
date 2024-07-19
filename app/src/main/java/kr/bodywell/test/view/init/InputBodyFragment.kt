package kr.bodywell.test.view.init

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import kr.bodywell.test.R
import kr.bodywell.test.database.DataManager
import kr.bodywell.test.databinding.FragmentInputBodyBinding
import kr.bodywell.test.util.CustomUtil.Companion.hideKeyboard

class InputBodyFragment : Fragment() {
   private var _binding: FragmentInputBodyBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private var height = 163
   private var weight = 58
   private var gender = "Female"

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            requireActivity().supportFragmentManager.beginTransaction().apply {
               replace(R.id.inputFrame, InputInfoFragment())
               commit()
            }
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentInputBodyBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager.open()

      binding.mainLayout.setOnTouchListener { _, _ ->
         hideKeyboard(requireActivity())
         true
      }

      binding.ivBack.setOnClickListener {
         requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.inputFrame, InputInfoFragment())
            commit()
         }
      }

      binding.tvSkip.setOnClickListener {
         requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.inputFrame, InputGoalFragment())
            commit()
         }
      }

      binding.cvWoman.setOnClickListener {
         height = 163
         weight = 58
         gender = "Female"
         binding.cvMan.setCardBackgroundColor(Color.parseColor("#EEEEEE"))
         binding.ivMan.imageTintList = ColorStateList.valueOf(Color.parseColor("#aaaaaa"))
         binding.tvMan.setTextColor(Color.BLACK)
         binding.cvWoman.setCardBackgroundColor(Color.parseColor("#9F98FF"))
         binding.ivWoman.imageTintList = ColorStateList.valueOf(Color.WHITE)
         binding.tvWoman.setTextColor(Color.WHITE)
         binding.etHeight.hint = height.toString()
         binding.etWeight.hint = weight.toString()
      }

      binding.cvMan.setOnClickListener {
         height = 173
         weight = 68
         gender = "Male"
         binding.cvMan.setCardBackgroundColor(Color.parseColor("#9F98FF"))
         binding.ivMan.imageTintList = ColorStateList.valueOf(Color.WHITE)
         binding.tvMan.setTextColor(Color.WHITE)
         binding.cvWoman.setCardBackgroundColor(Color.parseColor("#EEEEEE"))
         binding.ivWoman.imageTintList = ColorStateList.valueOf(Color.parseColor("#aaaaaa"))
         binding.tvWoman.setTextColor(Color.BLACK)
         binding.etHeight.hint = height.toString()
         binding.etWeight.hint = weight.toString()
      }

      binding.etHeight.addTextChangedListener(object : TextWatcher {
         override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if(s.toString() != "") {
               val text = s.toString().replace(".","")

               if(s.length == 1 && s[0].toString() == ".") {
                  binding.etHeight.setText("")
               }

               if(text.length == 2) {
                  val format = text[0].toString() + "." + text[1].toString()
                  binding.etHeight.removeTextChangedListener(this)
                  binding.etHeight.setText(format)
                  binding.etHeight.setSelection(format.length)
                  binding.etHeight.addTextChangedListener(this)
               }

               if(text.length == 3) {
                  val format = text[0].toString() + text[1].toString() + "." + text[2].toString()
                  binding.etHeight.removeTextChangedListener(this)
                  binding.etHeight.setText(format)
                  binding.etHeight.setSelection(format.length)
                  binding.etHeight.addTextChangedListener(this)
               }

               if(text.length == 4) {
                  val format = text[0].toString() + text[1].toString() + text[2].toString() + "." + text[3].toString()
                  binding.etHeight.removeTextChangedListener(this)
                  binding.etHeight.setText(format)
                  binding.etHeight.setSelection(format.length)
                  binding.etHeight.addTextChangedListener(this)
               }
            }
         }

         override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
         override fun afterTextChanged(p0: Editable?) {}
      })

      binding.etWeight.addTextChangedListener(object : TextWatcher {
         override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if(s.toString() != "") {
               val text = s.toString().replace(".","")

               if(s.length == 1 && s[0].toString() == ".") {
                  binding.etWeight.setText("")
               }

               if(text.length == 2) {
                  val format = text[0].toString() + "." + text[1].toString()
                  binding.etWeight.removeTextChangedListener(this)
                  binding.etWeight.setText(format)
                  binding.etWeight.setSelection(format.length)
                  binding.etWeight.addTextChangedListener(this)
               }

               if(text.length == 3) {
                  val format = text[0].toString() + text[1].toString() + "." + text[2].toString()
                  binding.etWeight.removeTextChangedListener(this)
                  binding.etWeight.setText(format)
                  binding.etWeight.setSelection(format.length)
                  binding.etWeight.addTextChangedListener(this)
               }

               if(text.length == 4) {
                  val format = text[0].toString() + text[1].toString() + text[2].toString() + "." + text[3].toString()
                  binding.etWeight.removeTextChangedListener(this)
                  binding.etWeight.setText(format)
                  binding.etWeight.setSelection(format.length)
                  binding.etWeight.addTextChangedListener(this)
               }
            }
         }

         override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
         override fun afterTextChanged(p0: Editable?) {}
      })

      binding.cvContinue.setOnClickListener {
         val height = if(binding.etHeight.text.toString() == "") height.toDouble() else binding.etHeight.text.toString().toDouble()
         val weight = if(binding.etWeight.text.toString() == "") weight.toDouble() else binding.etWeight.text.toString().toDouble()

         dataManager.updateUserStr("gender", gender)
         dataManager.updateUserDouble("height", height)
         dataManager.updateUserDouble("weight", weight)

         requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.inputFrame, InputGoalFragment())
            commit()
         }
      }

      return binding.root
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}