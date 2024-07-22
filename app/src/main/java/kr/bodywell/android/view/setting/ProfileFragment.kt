package kr.bodywell.android.view.setting

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import kr.bodywell.android.R
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentProfileBinding
import kr.bodywell.android.model.User
import kr.bodywell.android.util.CustomUtil.Companion.filterText
import kr.bodywell.android.util.CustomUtil.Companion.hideKeyboard
import kr.bodywell.android.util.CustomUtil.Companion.isoFormat2
import kr.bodywell.android.util.CustomUtil.Companion.replaceFragment3
import kr.bodywell.android.util.PermissionUtil.Companion.CAMERA_REQUEST_CODE
import kr.bodywell.android.util.PermissionUtil.Companion.STORAGE_REQUEST_CODE
import kr.bodywell.android.util.PermissionUtil.Companion.saveFile
import java.time.LocalDate

class ProfileFragment : Fragment() {
	private var _binding: FragmentProfileBinding? = null
	private val binding get() = _binding!!

	private lateinit var callback: OnBackPressedCallback
	private lateinit var dataManager: DataManager
	private var dialog: BottomSheetDialog? = null
	private var gender = "Female"
	private var image = ""

	override fun onAttach(context: Context) {
		super.onAttach(context)
		callback = object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				replaceFragment3(requireActivity(), SettingFragment())
			}
		}

		requireActivity().onBackPressedDispatcher.addCallback(this, callback)
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentProfileBinding.inflate(layoutInflater)

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

		val getUser = dataManager.getUser()

		if(getUser.name != "") binding.etName.setText(getUser.name)

		when(getUser.gender) {
			"Male" -> unit2()
			else -> unit1()
		}

		if(getUser.image != "") binding.ivProfile.setImageURI(Uri.parse(getUser.image))

		if(getUser.height!! > 0) {
			val hSplit = getUser.height.toString().split(".")
			val height = if(hSplit[1] == "0") hSplit[0] else getUser.height
			binding.etHeight.setText(height.toString())
		}

		if(getUser.weight!! > 0) {
			val wSplit = getUser.weight.toString().split(".")
			val weight = if(wSplit[1] == "0") wSplit[0] else getUser.weight
			binding.etWeight.setText(weight.toString())
		}

		if(getUser.birthday != "") binding.tvBirthday.text = getUser.birthday

		binding.cl.setOnTouchListener { _, _ ->
			hideKeyboard(requireActivity())
			true
		}

		binding.clBack.setOnClickListener {
			replaceFragment3(requireActivity(), SettingFragment())
		}

//		binding.ivProfile.setOnClickListener {
//			if(cameraRequest(requireActivity())) {
//				dialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme)
//				val bottomSheetView = layoutInflater.inflate(R.layout.dialog_camera, null)
//
//				val clCamera = bottomSheetView.findViewById<ConstraintLayout>(R.id.clCamera)
//				val clPhoto = bottomSheetView.findViewById<ConstraintLayout>(R.id.clPhoto)
//
//				clCamera.setOnClickListener {
//					val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//					startActivityForResult(intent, CAMERA_REQUEST_CODE)
//				}
//
//				clPhoto.setOnClickListener {
//					val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
//					intent.type = "image/*"
//					startActivityForResult(intent, STORAGE_REQUEST_CODE)
//				}
//
//				dialog!!.setContentView(bottomSheetView)
//				dialog!!.show()
//			}
//		}

		binding.tvWoman.setOnClickListener { unit1() }

		binding.tvMan.setOnClickListener { unit2() }

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

		binding.tvBirthday.setOnClickListener {
			val dialog = Dialog(requireActivity())
			dialog.setContentView(R.layout.dialog_date_picker)
			dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

			val datePicker = dialog.findViewById<DatePicker>(R.id.datePicker)
			val tvConfirm = dialog.findViewById<TextView>(R.id.tvConfirm)
			val tvCancel = dialog.findViewById<TextView>(R.id.tvCancel)

			tvConfirm.setOnClickListener {
				binding.tvBirthday.text = "${datePicker.year}-${String.format("%02d", datePicker.month + 1)}-${String.format("%02d", datePicker.dayOfMonth)}"
				dialog.dismiss()
			}

			tvCancel.setOnClickListener {
				dialog.dismiss()
			}

			datePicker.maxDate = System.currentTimeMillis() - 1000

			dialog.show()
		}

		binding.cvEdit.setOnClickListener {
			val name = if(binding.etName.text.trim().toString() == "") "" else binding.etName.text.trim().toString()
			val height = if(binding.etHeight.text.trim().toString() == "") 0.0 else binding.etHeight.text.trim().toString().toDouble()
			val weight = if(binding.etWeight.text.trim().toString() == "") 0.0 else binding.etWeight.text.trim().toString().toDouble()
			val birthday = if(binding.tvBirthday.text.toString() == "") LocalDate.now().toString() else binding.tvBirthday.text.toString()

			if(binding.etName.text.trim().isEmpty()) {
				Toast.makeText(context, "이름은 최소 1자 ~ 최대 15자 이내로 입력하여야합니다.", Toast.LENGTH_SHORT).show()
			}else if(!filterText(binding.etName.text.trim().toString())) {
				Toast.makeText(context, "특수문자는 입력 불가합니다.", Toast.LENGTH_SHORT).show()
			}else {
				val date = isoFormat2()
				dataManager.updateProfile(User(name=name, gender=gender, birthday=birthday, height=height, weight=weight, updatedAt=date, isUpdated=1))

				if(image != "") dataManager.updateUserStr("image", image)

				Toast.makeText(context, "수정되었습니다.", Toast.LENGTH_SHORT).show()
				replaceFragment3(requireActivity(), SettingFragment())
			}
		}

		return binding.root
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)

		if(resultCode == Activity.RESULT_OK){
			when(requestCode){
				CAMERA_REQUEST_CODE -> {
					if(data!!.extras?.get("data") != null){
						val img = data.extras?.get("data") as Bitmap
						val uri = saveFile(requireActivity(), "image/jpeg", img)
						image = uri.toString()

						binding.ivProfile.setImageURI(Uri.parse(image))

						dialog!!.dismiss()
					}
				}
				STORAGE_REQUEST_CODE -> {
					val uri = data!!.data
					val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
					requireActivity().contentResolver.takePersistableUriPermission(uri!!, takeFlags)
					image = uri.toString()

					binding.ivProfile.setImageURI(Uri.parse(image))

					dialog!!.dismiss()
				}
			}
		}
	}

	private fun unit1() {
		binding.tvWoman.setBackgroundResource(R.drawable.rec_25_gray)
		binding.tvWoman.setTextColor(Color.WHITE)
		binding.tvMan.setBackgroundResource(R.drawable.rec_25_border_gray)
		binding.tvMan.setTextColor(Color.BLACK)
		gender = "Female"
	}

	private fun unit2() {
		binding.tvWoman.setBackgroundResource(R.drawable.rec_25_border_gray)
		binding.tvWoman.setTextColor(Color.BLACK)
		binding.tvMan.setBackgroundResource(R.drawable.rec_25_gray)
		binding.tvMan.setTextColor(Color.WHITE)
		gender = "Male"
	}

	override fun onDetach() {
		super.onDetach()
		callback.remove()
	}
}