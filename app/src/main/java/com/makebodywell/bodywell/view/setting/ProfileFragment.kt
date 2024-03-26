package com.makebodywell.bodywell.view.setting

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DBHelper
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_USER
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentProfileBinding
import com.makebodywell.bodywell.databinding.FragmentSettingBinding
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.util.CustomUtil.Companion.hideKeyboard
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.PermissionUtil
import com.makebodywell.bodywell.util.PermissionUtil.Companion.CAMERA_REQUEST_CODE
import com.makebodywell.bodywell.util.PermissionUtil.Companion.STORAGE_REQUEST_CODE
import com.makebodywell.bodywell.util.PermissionUtil.Companion.saveFile
import com.makebodywell.bodywell.view.home.MainFragment
import com.makebodywell.bodywell.view.home.food.FoodRecord1Fragment
import java.util.regex.Pattern

class ProfileFragment : Fragment() {
	private var _binding: FragmentProfileBinding? = null
	private val binding get() = _binding!!

	private lateinit var callback: OnBackPressedCallback
	private lateinit var dataManager: DataManager
	private var dialog: BottomSheetDialog? = null
	private var image = ""

	override fun onAttach(context: Context) {
		super.onAttach(context)
		callback = object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				replaceFragment1(requireActivity(), SettingFragment())
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

		if(getUser.profileImage != "") binding.ivProfile.setImageURI(Uri.parse(getUser.profileImage))

		if(getUser.height != "0") {
			val hSplit = getUser.height!!.split(".")
			val height = if(hSplit[1] == "0") hSplit[0] else getUser.height!!
			binding.etHeight.setText(height)
		}

		if(getUser.weight != "0") {
			val wSplit = getUser.weight!!.split(".")
			val weight = if(wSplit[1] == "0") wSplit[0] else getUser.weight!!
			binding.etWeight.setText(weight)
		}

		binding.mainLayout.setOnTouchListener { _, _ ->
			hideKeyboard(requireActivity())
			true
		}

		binding.clBack.setOnClickListener {
			replaceFragment1(requireActivity(), SettingFragment())
		}

		binding.ivProfile.setOnClickListener {
			if(PermissionUtil.cameraRequest(requireActivity())) {
				dialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme)
				val bottomSheetView = layoutInflater.inflate(R.layout.dialog_camera, null)

				val clCamera = bottomSheetView.findViewById<ConstraintLayout>(R.id.clCamera)
				val clPhoto = bottomSheetView.findViewById<ConstraintLayout>(R.id.clPhoto)

				clCamera.setOnClickListener {
					val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
					startActivityForResult(intent, PermissionUtil.CAMERA_REQUEST_CODE)
				}

				clPhoto.setOnClickListener {
					val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
					intent.type = "image/*"
					startActivityForResult(intent, PermissionUtil.STORAGE_REQUEST_CODE)
				}

				dialog!!.setContentView(bottomSheetView)
				dialog!!.show()
			}
		}

		binding.etName.filters = arrayOf(filterAlphaNumSpace)

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

		binding.cvEdit.setOnClickListener {
			if(binding.etName.text.length < 2) {

			}
			val name = if(binding.etName.text.toString() == "") "" else binding.etName.text.toString()
			val height = if(binding.etHeight.text.toString() == "") 0.0 else binding.etHeight.text.toString().toDouble()
			val weight = if(binding.etWeight.text.toString() == "") 0.0 else binding.etWeight.text.toString().toDouble()

			dataManager.updateUserStr(TABLE_USER, "name", name)
			dataManager.updateUserDouble(TABLE_USER, "height", height)
			dataManager.updateUserDouble(TABLE_USER, "weight", weight)

			if(image != "") dataManager.updateUserStr(TABLE_USER, "profileImage", image)

			Toast.makeText(context, "수정되었습니다.", Toast.LENGTH_SHORT).show()
			replaceFragment1(requireActivity(), SettingFragment())
		}

		return binding.root
	}

	private var filterAlphaNumSpace = InputFilter { source, start, end, dest, dstart, dend ->
		val ps = Pattern.compile("^[ㄱ-ㅣ가-힣a-zA-Z0-9]+$")
		if (!ps.matcher(source).matches()) "" else source
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

	override fun onDetach() {
		super.onDetach()
		callback.remove()
	}
}