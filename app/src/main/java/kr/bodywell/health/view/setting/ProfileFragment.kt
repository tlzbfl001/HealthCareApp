package kr.bodywell.health.view.setting

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
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import kr.bodywell.health.R
import kr.bodywell.health.databinding.FragmentProfileBinding
import kr.bodywell.health.model.Constant.FEMALE
import kr.bodywell.health.model.Constant.MALE
import kr.bodywell.health.model.FileItem
import kr.bodywell.health.model.Profile
import kr.bodywell.health.util.CustomUtil.filterText
import kr.bodywell.health.util.CustomUtil.getRotatedBitmap
import kr.bodywell.health.util.CustomUtil.getUUID
import kr.bodywell.health.util.CustomUtil.hideKeyboard
import kr.bodywell.health.util.CustomUtil.replaceFragment3
import kr.bodywell.health.util.CustomUtil.saveFile
import kr.bodywell.health.util.CustomUtil.setStatusBar
import kr.bodywell.health.util.MyApp.Companion.powerSync
import kr.bodywell.health.util.PermissionUtil.MEDIA_PERMISSION_1
import kr.bodywell.health.util.PermissionUtil.MEDIA_PERMISSION_2
import kr.bodywell.health.util.PermissionUtil.checkMediaPermission
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

class ProfileFragment : Fragment() {
	private var _binding: FragmentProfileBinding? = null
	private val binding get() = _binding!!

	private lateinit var callback: OnBackPressedCallback
	private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
	private lateinit var cLauncher: ActivityResultLauncher<Intent>
	private var getProfile = Profile()
	private var getFile = FileItem()
	private var fileAbsolutePath: String? = null
	private var bitmap: Bitmap? = null
	private var pictureFlag = 0
	private var dialog: BottomSheetDialog? = null
	private var gender = FEMALE

	override fun onAttach(context: Context) {
		super.onAttach(context)
		callback = object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				replaceFragment3(requireActivity().supportFragmentManager, SettingFragment())
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

		lifecycleScope.launch {
			getProfile = powerSync.getProfile()
			getFile = powerSync.getFile(getProfile.id)
		}

		if(getFile.name != "") {
			val imgPath = requireActivity().filesDir.toString() + "/" + getFile.name // 내부저장소에 저장되어있는 이미지 경로
			val file = File(imgPath)

			if(file.exists()){
				val bm = BitmapFactory.decodeFile(imgPath)
				binding.ivUser.setImageBitmap(bm)
			}
		}

		if(getProfile.name != "") binding.etName.setText(getProfile.name)
		if(getProfile.birth != "") binding.tvBirthday.text = getProfile.birth

		when(getProfile.gender) {
			MALE -> unit2()
			else -> unit1()
		}

		binding.mainLayout.setOnTouchListener { _, _ ->
			hideKeyboard(requireActivity())
			true
		}

		binding.clBack.setOnClickListener {
			replaceFragment3(requireActivity().supportFragmentManager, SettingFragment())
		}

		binding.ivUser.setOnClickListener {
			if(checkMediaPermission(requireActivity())) {
				dialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme)
				val bottomSheetView = layoutInflater.inflate(R.layout.dialog_photo, null)

				val btnCamera = bottomSheetView.findViewById<ConstraintLayout>(R.id.btnCamera)
				val btnGallery = bottomSheetView.findViewById<ConstraintLayout>(R.id.btnGallery)

				btnCamera.setOnClickListener {
					val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) // 카메라앱 호출을 위한 Intent생성

					val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)	// 이미지 경로 지정
					val file = File.createTempFile(
						SimpleDateFormat("yyMMddhhmmSSS").format(Date()),
						".jpg",
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

				btnGallery.setOnClickListener {
					val intent = Intent(Intent.ACTION_PICK) // 갤러리에서 이미지를 선택하는 Intent 생성
					intent.type = "image/*"
					cLauncher.launch(intent)
					pictureFlag = 2
				}

				dialog!!.setContentView(bottomSheetView)
				dialog!!.show()
			}else {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
					pLauncher.launch(MEDIA_PERMISSION_2)
				}else {
					pLauncher.launch(MEDIA_PERMISSION_1)
				}
			}
		}

		val contract1=ActivityResultContracts.StartActivityForResult()
		cLauncher = registerForActivityResult(contract1){
			if(pictureFlag == 1) { // 카메라
				val file = File(fileAbsolutePath!!)
				val decode = ImageDecoder.createSource(requireActivity().contentResolver,Uri.fromFile(file.absoluteFile)) // 카메라에서 찍은 사진을 디코딩
				bitmap = ImageDecoder.decodeBitmap(decode) // 디코딩한 사진을 비트맵으로 변환
				binding.ivUser.setImageBitmap(bitmap)
				file.delete()
			}else { // 갤러리
				val uri = it.data?.data // 선택한 이미지의 주소
				if(uri != null) { // 이미지 파일 읽어와서 설정하기
					bitmap = getRotatedBitmap(requireActivity(), uri) // 이미지 회전하기
					binding.ivUser.setImageBitmap(bitmap)
				}
			}
			dialog!!.dismiss()
		}

		binding.tvWoman.setOnClickListener { unit1() }

		binding.tvMan.setOnClickListener { unit2() }

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
			lifecycleScope.launch {
				val getProfile = powerSync.getProfile()
				val name = if(binding.etName.text.trim().toString() == "") getProfile.name else binding.etName.text.trim().toString()
				val birthday = if(binding.tvBirthday.text.toString() == "") LocalDate.now().toString() else binding.tvBirthday.text.toString()

				if(binding.etName.text.trim().isEmpty()) {
					Toast.makeText(context, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
				}else if(!filterText(binding.etName.text.toString())) {
					Toast.makeText(context, "특수문자는 입력 불가합니다.", Toast.LENGTH_SHORT).show()
				}else {
					// 프로필 수정
					powerSync.updateProfile(Profile(name = name, birth = birthday, height = getProfile.height, weight = getProfile.weight, gender = gender))

					if(bitmap != null) {
						val result = saveFile(requireActivity(), bitmap!!)
						if(result != "") {
							val file = File(requireActivity().filesDir, result)
							if(file.length() < 1048576) {
								powerSync.insertProfileFile(FileItem(id = getUUID(), name = result, profileId = getProfile.id))
								Toast.makeText(context, "수정되었습니다.", Toast.LENGTH_SHORT).show()
								replaceFragment3(requireActivity().supportFragmentManager, SettingFragment())
							}else {
								Toast.makeText(context, "파일 크기가 허용되는 한도를 초과하여 파일을 저장할 수 없습니다.", Toast.LENGTH_SHORT).show()
								File(requireActivity().filesDir, result).delete()
							}
						}
					}else {
						Toast.makeText(context, "수정되었습니다.", Toast.LENGTH_SHORT).show()
						replaceFragment3(requireActivity().supportFragmentManager, SettingFragment())
					}
				}
			}
		}

		return binding.root
	}

	private fun unit1() {
		binding.tvWoman.setBackgroundResource(R.drawable.rec_25_gray)
		binding.tvWoman.setTextColor(resources.getColor(R.color.button_text))
		binding.tvMan.setBackgroundResource(R.drawable.rec_25_border_gray)
		binding.tvMan.setTextColor(Color.parseColor("#90212121"))
		gender = FEMALE
	}

	private fun unit2() {
		binding.tvWoman.setBackgroundResource(R.drawable.rec_25_border_gray)
		binding.tvWoman.setTextColor(Color.parseColor("#90212121"))
		binding.tvMan.setBackgroundResource(R.drawable.rec_25_gray)
		binding.tvMan.setTextColor(resources.getColor(R.color.button_text))
		gender = MALE
	}

	override fun onDetach() {
		super.onDetach()
		callback.remove()
	}
}