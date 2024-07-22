package kr.bodywell.android.view.home.body

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import kr.bodywell.android.R
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentBodyRecordBinding
import kr.bodywell.android.model.Body
import kr.bodywell.android.util.CalendarUtil.Companion.selectedDate
import kr.bodywell.android.util.CustomUtil.Companion.hideKeyboard
import kr.bodywell.android.util.CustomUtil.Companion.replaceFragment3
import kr.bodywell.android.util.CustomUtil.Companion.replaceFragment4
import kr.bodywell.android.view.home.DetailFragment
import java.util.Calendar

class BodyRecordFragment : Fragment() {
   private var _binding: FragmentBodyRecordBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private var bundle = Bundle()
   private var level = 1

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
      _binding = FragmentBodyRecordBinding.inflate(layoutInflater)

      requireActivity().window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK

         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.mainLayout.setPadding(0, statusBarHeight, 0, 0)
      }

      bundle.putString("type", "body")

      dataManager = DataManager(activity)
      dataManager.open()

      val getBody = dataManager.getBody(selectedDate.toString())

      // 데이터가 존재하는 경우 데이터 가져와서 수정
      if (getBody.createdAt != "") {
         if(getBody.bmi > 0.0) binding.etBmi.setText(getBody.bmi.toString())
         if(getBody.fat > 0.0) binding.etFat.setText(getBody.fat.toString())
         if(getBody.muscle > 0.0) binding.etMuscle.setText(getBody.muscle.toString())
         if(getBody.height > 0.0) binding.etHeight.setText(getBody.height.toString())
         if(getBody.weight > 0.0) binding.etWeight.setText(getBody.weight.toString())

         when(getBody.intensity) {
            1 -> {
               binding.radioBtn1.isChecked = true
               level = 1
            }
            2 -> {
               binding.radioBtn2.isChecked = true
               level = 2
            }
            3 -> {
               binding.radioBtn3.isChecked = true
               level = 3
            }
            4 -> {
               binding.radioBtn4.isChecked = true
               level = 4
            }
            5 -> {
               binding.radioBtn5.isChecked = true
               level = 5
            }
         }
      }

      binding.mainLayout.setOnTouchListener { _, _ ->
         hideKeyboard(requireActivity())
         true
      }

      binding.constraint.setOnTouchListener { _, _ ->
         hideKeyboard(requireActivity())
         true
      }

      binding.clBack.setOnClickListener {
         replaceFragment3(requireActivity(), DetailFragment())
      }

      binding.etBmi.addTextChangedListener(object : TextWatcher {
         override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val text = s.toString().replace(".","")

            if(s.length == 1 && s[0].toString() == ".") binding.etBmi.setText("")

            if(text.length == 2) {
               val format = text[0].toString() + "." + text[1].toString()
               binding.etBmi.removeTextChangedListener(this)
               binding.etBmi.setText(format)
               binding.etBmi.setSelection(format.length)
               binding.etBmi.addTextChangedListener(this)
            }

            if(text.length == 3) {
               val format = text[0].toString() + text[1].toString() + "." + text[2].toString()
               binding.etBmi.removeTextChangedListener(this)
               binding.etBmi.setText(format)
               binding.etBmi.setSelection(format.length)
               binding.etBmi.addTextChangedListener(this)
            }
         }

         override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
         override fun afterTextChanged(p0: Editable?) {}
      })

      binding.etFat.addTextChangedListener(object : TextWatcher {
         override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val text = s.toString().replace(".","")

            if(s.length == 1 && s[0].toString() == ".") binding.etFat.setText("")

            if(text.length == 2) {
               val format = text[0].toString() + "." + text[1].toString()
               binding.etFat.removeTextChangedListener(this)
               binding.etFat.setText(format)
               binding.etFat.setSelection(format.length)
               binding.etFat.addTextChangedListener(this)
            }

            if(text.length == 3) {
               val format = text[0].toString() + text[1].toString() + "." + text[2].toString()
               binding.etFat.removeTextChangedListener(this)
               binding.etFat.setText(format)
               binding.etFat.setSelection(format.length)
               binding.etFat.addTextChangedListener(this)
            }
         }

         override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
         override fun afterTextChanged(p0: Editable?) {}
      })

      binding.etMuscle.addTextChangedListener(object : TextWatcher {
         override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val text = s.toString().replace(".","")

            if(s.length == 1 && s[0].toString() == ".") binding.etMuscle.setText("")

            if(text.length == 2) {
               val format = text[0].toString() + "." + text[1].toString()
               binding.etMuscle.removeTextChangedListener(this)
               binding.etMuscle.setText(format)
               binding.etMuscle.setSelection(format.length)
               binding.etMuscle.addTextChangedListener(this)
            }

            if(text.length == 3) {
               val format = text[0].toString() + text[1].toString() + "." + text[2].toString()
               binding.etMuscle.removeTextChangedListener(this)
               binding.etMuscle.setText(format)
               binding.etMuscle.setSelection(format.length)
               binding.etMuscle.addTextChangedListener(this)
            }
         }

         override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
         override fun afterTextChanged(p0: Editable?) {}
      })

      binding.etHeight.addTextChangedListener(object : TextWatcher {
         override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if(s.toString() != "") {
               val text = s.toString().replace(".","")

               if(s.length == 1 && s[0].toString() == ".") binding.etHeight.setText("")

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

               if(s.length == 1 && s[0].toString() == ".") binding.etWeight.setText("")

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

      binding.radioGroup.setOnCheckedChangeListener{ _, checkedId ->
         when(checkedId) {
            R.id.radioBtn1 -> level = 1
            R.id.radioBtn2 -> level = 2
            R.id.radioBtn3 -> level = 3
            R.id.radioBtn4 -> level = 4
            R.id.radioBtn5 -> level = 5
         }
      }

      // BMR 구하기
      binding.cvResult.setOnClickListener {
         val getUser = dataManager.getUser()
         val current = Calendar.getInstance()
         val currentYear = current.get(Calendar.YEAR)
         val age = currentYear - getUser.birthday!!.substring(0 until 4).toInt()

         if(binding.etHeight.text.toString().trim() == "" || binding.etWeight.text.toString().trim() == "") {
            Toast.makeText(requireActivity(), "신체 정보를 전부 입력해야합니다.", Toast.LENGTH_SHORT).show()
         }else {
            var step = 0.0

            when(level) {
               1 -> step = 1.2
               2 -> step = 1.375
               3 -> step = 1.55
               4 -> step = 1.725
               5 -> step = 1.9
            }

            val bmr = if(getUser.gender == "MALE") {
               val num = ((10*binding.etWeight.text.toString().toDouble())+(6.25*binding.etHeight.text.toString().toDouble())-(5*age)+5)*step
               String.format("%.1f", num)
            }else {
               val num = ((10*binding.etWeight.text.toString().toDouble())+(6.25*binding.etHeight.text.toString().toDouble())-(5*age)-161)*step
               String.format("%.1f", num)
            }

            binding.tvBmr.text = bmr
         }
      }

      binding.cvSave.setOnClickListener {
         val height = if(binding.etHeight.text.toString() == "") 0.0 else binding.etHeight.text.toString().toDouble()
         val weight = if(binding.etWeight.text.toString() == "") 0.0 else binding.etWeight.text.toString().toDouble()
         val fat = if(binding.etFat.text.toString() == "") 0.0 else binding.etFat.text.toString().toDouble()
         val muscle = if(binding.etMuscle.text.toString() == "") 0.0 else binding.etMuscle.text.toString().toDouble()
         val bmi = if(binding.etBmi.text.toString() == "") 0.0 else binding.etBmi.text.toString().toDouble()
         val bmr = if(binding.tvBmr.text.toString() == "") 0.0 else binding.tvBmr.text.toString().toDouble()

         if(fat > 100) {
            Toast.makeText(requireActivity(), "체지방율은 100을 넘을 수 없습니다.", Toast.LENGTH_SHORT).show()
         }else {
            if(getBody.createdAt == "") {
               dataManager.insertBody(Body(height = height, weight = weight, intensity = level, fat = fat, muscle = muscle, bmi = bmi, bmr = bmr, createdAt = selectedDate.toString()))
               Toast.makeText(requireActivity(), "저장되었습니다.", Toast.LENGTH_SHORT).show()
            }else {
               dataManager.updateBody(Body(id = getBody.id, height = height, weight = weight, intensity = level, fat = fat, muscle = muscle, bmi = bmi, bmr = bmr))
               Toast.makeText(requireActivity(), "수정되었습니다.", Toast.LENGTH_SHORT).show()
            }

            replaceFragment4(requireActivity(), DetailFragment(), bundle)
         }
      }

      return binding.root
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}