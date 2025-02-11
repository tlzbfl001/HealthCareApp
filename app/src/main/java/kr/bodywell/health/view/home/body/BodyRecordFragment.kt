package kr.bodywell.health.view.home.body

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kr.bodywell.health.R
import kr.bodywell.health.databinding.FragmentBodyRecordBinding
import kr.bodywell.health.model.Body
import kr.bodywell.health.model.Constant.BODY_MEASUREMENTS
import kr.bodywell.health.model.Constant.MALE
import kr.bodywell.health.model.Profile
import kr.bodywell.health.util.CalendarUtil.selectedDate
import kr.bodywell.health.util.CustomUtil.dateTimeToIso
import kr.bodywell.health.util.CustomUtil.getUUID
import kr.bodywell.health.util.CustomUtil.hideKeyboard
import kr.bodywell.health.util.CustomUtil.replaceFragment3
import kr.bodywell.health.util.CustomUtil.replaceFragment4
import kr.bodywell.health.util.CustomUtil.setStatusBar
import kr.bodywell.health.util.MyApp.Companion.powerSync
import kr.bodywell.health.view.home.DetailFragment
import kr.bodywell.health.view.setting.ProfileFragment
import java.time.LocalDate
import java.util.Calendar

class BodyRecordFragment : Fragment() {
   private var _binding: FragmentBodyRecordBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private var bundle = Bundle()
   private var getBody = Body()
   private var intensity = 1

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
      _binding = FragmentBodyRecordBinding.inflate(layoutInflater)

      setStatusBar(requireActivity(), binding.mainLayout)

      bundle.putString("type", BODY_MEASUREMENTS)

      lifecycleScope.launch {
         getBody = powerSync.getBody(selectedDate.toString())
      }

      // 데이터 초기화
      if (getBody.bodyMassIndex != null) binding.etBmi.setText(getBody.bodyMassIndex.toString())
      if(getBody.bodyFatPercentage != null) binding.etFat.setText(getBody.bodyFatPercentage.toString())
      if(getBody.skeletalMuscleMass != null) binding.etMuscle.setText(getBody.skeletalMuscleMass.toString())
      if(getBody.height != null) binding.etHeight.setText(getBody.height.toString())
      if(getBody.weight != null) binding.etWeight.setText(getBody.weight.toString())

      when(getBody.workoutIntensity) {
         1 -> {
            binding.radioBtn1.isChecked = true
            intensity = 1
         }
         2 -> {
            binding.radioBtn2.isChecked = true
            intensity = 2
         }
         3 -> {
            binding.radioBtn3.isChecked = true
            intensity = 3
         }
         4 -> {
            binding.radioBtn4.isChecked = true
            intensity = 4
         }
         5 -> {
            binding.radioBtn5.isChecked = true
            intensity = 5
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
         replaceFragment3(requireActivity().supportFragmentManager, DetailFragment())
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
            R.id.radioBtn1 -> intensity = 1
            R.id.radioBtn2 -> intensity = 2
            R.id.radioBtn3 -> intensity = 3
            R.id.radioBtn4 -> intensity = 4
            R.id.radioBtn5 -> intensity = 5
         }
      }

      // BMR 구하기
      binding.cvResult.setOnClickListener {
         lifecycleScope.launch {
            val getProfile = powerSync.getProfile()

            if(getProfile.gender == null || getProfile.gender == "" || getProfile.birth == null || getProfile.birth == "") {
               val dialog = Dialog(requireActivity())
               dialog.setContentView(R.layout.dialog_setting_profile)
               dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
               val cvConfirm = dialog.findViewById<CardView>(R.id.cvConfirm)

               cvConfirm.setOnClickListener {
                  replaceFragment3(requireActivity().supportFragmentManager, ProfileFragment())
                  dialog.dismiss()
               }

               dialog.show()
            }else if(binding.etHeight.text.toString().trim() == "") {
               Toast.makeText(requireActivity(), "키를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else if(binding.etWeight.text.toString().trim() == "") {
               Toast.makeText(requireActivity(), "몸무게를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else {
               val age = LocalDate.now().year - getProfile.birth!!.substring(0 until 4).toInt()
               var step = 0.0

               when(intensity) {
                  1 -> step = 1.2
                  2 -> step = 1.375
                  3 -> step = 1.55
                  4 -> step = 1.725
                  5 -> step = 1.9
               }

               val bmr = if(getProfile.gender == MALE) {
                  88.362 + (13.397 * binding.etWeight.text.toString().toDouble()) + (4.799 * binding.etHeight.text.toString().toDouble()) - (5.677 * age)
               }else {
                  66 + (13.7 * binding.etWeight.text.toString().toDouble()) + (5 * binding.etHeight.text.toString().toDouble()) - (6.8 * age) * step
               }

               binding.clResult.visibility = View.VISIBLE
               binding.tvBmr.text = if(bmr < 0) "0" else String.format("%.2f", bmr)
               binding.tvTotal.text = if(bmr < 0) "0" else String.format("%.2f", bmr * step)
            }
         }
      }

      binding.cvSave.setOnClickListener {
         val height = if(binding.etHeight.text.toString() == "") null else binding.etHeight.text.toString().toDouble()
         val weight = if(binding.etWeight.text.toString() == "") null else binding.etWeight.text.toString().toDouble()
         val fat = if(binding.etFat.text.toString() == "") null else binding.etFat.text.toString().toDouble()
         val muscle = if(binding.etMuscle.text.toString() == "") null else binding.etMuscle.text.toString().toDouble()
         val bmi = if(binding.etBmi.text.toString() == "") null else binding.etBmi.text.toString().toDouble()
         val bmr = if(binding.tvBmr.text.toString() == "") null else binding.tvBmr.text.toString().toDouble()

         if((height != null && height < 1) || (weight != null && weight < 1) || (fat != null && fat < 1) || (muscle != null && muscle < 1) || (bmi != null && bmi < 1) || (bmr != null && bmr < 1)) {
            Toast.makeText(requireActivity(), "데이터는 1이상 입력해야합니다.", Toast.LENGTH_SHORT).show()
         }else if((height != null && height > 999) || (weight != null && weight > 999)) {
            Toast.makeText(requireActivity(), "키, 몸무게는 999이하여야합니다.", Toast.LENGTH_SHORT).show()
         }else if((fat != null && fat > 99) || (muscle != null && muscle > 99) || (bmi != null && bmi > 99)) {
            Toast.makeText(requireActivity(), "BMI, 체지방율, 골격근량은 99이하여야합니다.", Toast.LENGTH_SHORT).show()
         }else {
            lifecycleScope.launch {
               val getProfile = powerSync.getProfile()
               powerSync.updateProfile(Profile(name = getProfile.name, birth = getProfile.birth, height = height, weight = weight, gender = getProfile.gender))

               if(getBody.id == "") {
                  powerSync.insertBody(Body(id = getUUID(), height = height, weight = weight, bodyMassIndex = bmi, bodyFatPercentage = fat,
                     skeletalMuscleMass = muscle, basalMetabolicRate = bmr, workoutIntensity = intensity, time = selectedDate.toString(),
                     createdAt = dateTimeToIso(Calendar.getInstance()), updatedAt = dateTimeToIso(Calendar.getInstance())))
                  Toast.makeText(requireActivity(), "저장되었습니다.", Toast.LENGTH_SHORT).show()
               }else {
                  powerSync.updateBody(Body(id = getBody.id, height = height, weight = weight, bodyMassIndex = bmi, bodyFatPercentage = fat,
                     skeletalMuscleMass = muscle, basalMetabolicRate = bmr, workoutIntensity = intensity))
                  Toast.makeText(requireActivity(), "수정되었습니다.", Toast.LENGTH_SHORT).show()
               }
            }

            replaceFragment4(requireActivity().supportFragmentManager, DetailFragment(), bundle)
         }
      }

      return binding.root
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}