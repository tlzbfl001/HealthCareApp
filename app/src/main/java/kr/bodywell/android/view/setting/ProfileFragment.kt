package kr.bodywell.android.view.setting

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import kr.bodywell.android.R
import kr.bodywell.android.database.DBHelper.Companion.USER
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentProfileBinding
import kr.bodywell.android.model.Constant
import kr.bodywell.android.model.User
import kr.bodywell.android.util.CustomUtil.filterText
import kr.bodywell.android.util.CustomUtil.getRotatedBitmap
import kr.bodywell.android.util.CustomUtil.hideKeyboard
import kr.bodywell.android.util.CustomUtil.replaceFragment3
import kr.bodywell.android.util.CustomUtil.saveImage
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.util.PermissionUtil.CAMERA_PERMISSION_1
import kr.bodywell.android.util.PermissionUtil.CAMERA_PERMISSION_2
import kr.bodywell.android.util.PermissionUtil.checkCameraPermission
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

class ProfileFragment : Fragment() {
	private var _binding: FragmentProfileBinding? = null
	private val binding get() = _binding!!

	private lateinit var callback: OnBackPressedCallback
	private lateinit var dataManager: DataManager
	private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
	private lateinit var cLauncher: ActivityResultLauncher<Intent>
	private var fileAbsolutePath: String? = null
	private var bitmap: Bitmap? = null
	private var pictureFlag = 0
	private var dialog: BottomSheetDialog? = null
	private var gender = Constant.Female.name

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

		setStatusBar(requireActivity(), binding.mainLayout)

		pLauncher = registerForActivityResult(
			ActivityResultContracts.RequestMultiplePermissions()
		){}

		dataManager = DataManager(activity)
		dataManager.open()

		val getUser = dataManager.getUser()

		if(getUser.name != "") binding.etName.setText(getUser.name)

		if(getUser.profileImage != "") {
			val imgPath = requireActivity().filesDir.toString() + "/" + getUser.profileImage // 내부저장소에 저장되어있는 이미지 경로
			val bm = BitmapFactory.decodeFile(imgPath)
			binding.ivProfile.setImageBitmap(bm)
		}

		when(getUser.gender) {
			Constant.Male.name -> unit2()
			else -> unit1()
		}

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

		binding.ivProfile.setOnClickListener {
			if(checkCameraPermission(requireActivity())) {
				dialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme)
				val bottomSheetView = layoutInflater.inflate(R.layout.dialog_camera, null)

				val clCamera = bottomSheetView.findViewById<ConstraintLayout>(R.id.clCamera)
				val clGallery = bottomSheetView.findViewById<ConstraintLayout>(R.id.clGallery)

				clCamera.setOnClickListener {
					val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) // 카메라앱 호출을 위한 Intent생성

					val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)	// 이미지 경로 지정
					val file = File.createTempFile(
						SimpleDateFormat("yyMMddhhmmSSS").format(Date()),
						".png",
						storageDir
					).apply {
						fileAbsolutePath = absolutePath // 절대경로 변수에 저장
					}

					// 사진이 저장될 경로를 관리할 Uri 객체를 생성한다.
					val contentUri = FileProvider.getUriForFile(requireActivity(), requireActivity().applicationContext.packageName + ".file_provider", file)

					intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
					cLauncher.launch(intent)
					pictureFlag = 1
				}

				clGallery.setOnClickListener {
					val intent = Intent(Intent.ACTION_PICK) // 갤러리에서 이미지를 선택하는 Intent 생성
					intent.type = "image/*"
					cLauncher.launch(intent)
					pictureFlag = 2
				}

				dialog!!.setContentView(bottomSheetView)
				dialog!!.show()
			}else {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
					pLauncher.launch(CAMERA_PERMISSION_2)
				}else {
					pLauncher.launch(CAMERA_PERMISSION_1)
				}
			}
		}

		val contract1=ActivityResultContracts.StartActivityForResult()
		cLauncher = registerForActivityResult(contract1){
			if(pictureFlag == 1) { // 카메라
				val file = File(fileAbsolutePath)
				val decode = ImageDecoder.createSource(requireActivity().contentResolver,Uri.fromFile(file.absoluteFile)) // 카메라에서 찍은 사진을 디코딩
				bitmap = ImageDecoder.decodeBitmap(decode) // 디코딩한 사진을 비트맵으로 변환
				binding.ivProfile.setImageBitmap(bitmap)
				file.delete()
			}else if(pictureFlag == 2) { // 갤러리
				val uri = it.data?.data // 선택한 이미지의 주소
				if(uri != null) { // 이미지 파일 읽어와서 설정하기
					bitmap = getRotatedBitmap(requireActivity(), it.data?.data!!) // 이미지 회전하기
					binding.ivProfile.setImageBitmap(bitmap)
				}
			}
			dialog!!.dismiss()
		}

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
			dialog.window!!.statusBarColor = Color.BLACK

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

			if(height < 1 || weight < 1) {
				Toast.makeText(context, "키, 몸무게는 1이상 입력하여야합니다.", Toast.LENGTH_SHORT).show()
			}else if(binding.etName.text.trim().isEmpty()) {
				Toast.makeText(context, "이름은 최소 1자 ~ 최대 15자 이내로 입력하여야합니다.", Toast.LENGTH_SHORT).show()
			}else if(!filterText(binding.etName.text.toString())) {
				Toast.makeText(context, "특수문자는 입력 불가합니다.", Toast.LENGTH_SHORT).show()
			}else {
				dataManager.updateProfile(User(name = name, gender = gender, birthday = birthday, height = height, weight = weight, isUpdated = 1))

				if(bitmap != null) {
					val data = dataManager.getUser().profileImage
					File(requireActivity().filesDir, data).delete()
					val result = saveImage(requireActivity(), bitmap!!) // 선택한 이미지를 저장하는 메서드 호출
					if(result != "") dataManager.updateUserStr(USER, "profileImage", result, "id")
				}

				Toast.makeText(context, "수정되었습니다.", Toast.LENGTH_SHORT).show()
				replaceFragment3(requireActivity(), SettingFragment())
			}
		}

		return binding.root
	}

	private fun unit1() {
		binding.tvWoman.setBackgroundResource(R.drawable.rec_25_gray)
		binding.tvWoman.setTextColor(Color.WHITE)
		binding.tvMan.setBackgroundResource(R.drawable.rec_25_border_gray)
		binding.tvMan.setTextColor(Color.BLACK)
		gender = Constant.Female.name
	}

	private fun unit2() {
		binding.tvWoman.setBackgroundResource(R.drawable.rec_25_border_gray)
		binding.tvWoman.setTextColor(Color.BLACK)
		binding.tvMan.setBackgroundResource(R.drawable.rec_25_gray)
		binding.tvMan.setTextColor(Color.WHITE)
		gender = Constant.Male.name
	}

	override fun onDetach() {
		super.onDetach()
		callback.remove()
	}
}