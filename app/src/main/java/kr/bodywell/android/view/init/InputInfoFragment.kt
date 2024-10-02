package kr.bodywell.android.view.init

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import kr.bodywell.android.R
import kr.bodywell.android.database.DBHelper.Companion.USER
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentInputInfoBinding
import kr.bodywell.android.util.CustomUtil
import kr.bodywell.android.util.CustomUtil.filterText
import kr.bodywell.android.util.CustomUtil.hideKeyboard
import kr.bodywell.android.util.CustomUtil.saveImage
import kr.bodywell.android.util.PermissionUtil
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import kotlin.system.exitProcess

class InputInfoFragment : Fragment() {
   private var _binding: FragmentInputInfoBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
   private lateinit var cLauncher: ActivityResultLauncher<Intent>
   private var fileAbsolutePath: String? = null
   private var pictureFlag = 0
   private var dialog: Dialog? = null
   private var pressedTime: Long = 0
   private var bitmap: Bitmap? = null

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            pressedTime = if(pressedTime == 0L) {
               Toast.makeText(requireActivity(), "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
               System.currentTimeMillis()
            }else {
               val seconds = (System.currentTimeMillis() - pressedTime).toInt()
               if(seconds > 2000) {
                  Toast.makeText(requireActivity(), "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                  0
               }else {
                  requireActivity().finishAffinity()
                  System.runFinalization()
                  exitProcess(0)
               }
            }
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentInputInfoBinding.inflate(layoutInflater)

      pLauncher = registerForActivityResult(
         ActivityResultContracts.RequestMultiplePermissions()
      ){

      }

      dataManager = DataManager(activity)
      dataManager.open()

      binding.mainLayout.setOnTouchListener { _, _ ->
         hideKeyboard(requireActivity())
         true
      }

      binding.ivProfile.setOnClickListener {
         if(PermissionUtil.checkCameraPermission(requireActivity())) {
            dialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme)
            val bottomSheetView = layoutInflater.inflate(R.layout.dialog_camera, null)

            val clCamera = bottomSheetView.findViewById<ConstraintLayout>(R.id.clCamera)
            val clGallery = bottomSheetView.findViewById<ConstraintLayout>(R.id.clGallery)

            clCamera.setOnClickListener {
               val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) // 카메라앱 호출을위한 Intent생성

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
               pLauncher.launch(PermissionUtil.CAMERA_PERMISSION_2)
            }else {
               pLauncher.launch(PermissionUtil.CAMERA_PERMISSION_1)
            }
         }
      }

      val contract1=ActivityResultContracts.StartActivityForResult()
      cLauncher = registerForActivityResult(contract1){
         if(pictureFlag == 1) { // 카메라
            val file = File(fileAbsolutePath)
            val decode = ImageDecoder.createSource(requireActivity().contentResolver, Uri.fromFile(file.absoluteFile)) // 카메라에서 찍은 사진을 디코딩
            bitmap = ImageDecoder.decodeBitmap(decode) // 디코딩한 사진을 비트맵으로 변환
            binding.ivProfile.setImageBitmap(bitmap)
            file.delete()
         }else if(pictureFlag == 2) { // 갤러리
            val uri = it.data?.data // 선택한 이미지의 주소
            if(uri != null) { // 이미지파일 읽어와서 설정하기
               bitmap = CustomUtil.getRotatedBitmap(requireActivity(), it.data?.data!!) // 이미지 회전하기
               binding.ivProfile.setImageBitmap(bitmap)
            }
         }
         dialog!!.dismiss()
      }

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

      binding.cvContinue.setOnClickListener {
         val name = if(binding.etName.text.toString() == "") "바디웰" else binding.etName.text.toString()
         val birthday = if(binding.tvBirthday.text.toString() == "") LocalDate.now().toString() else binding.tvBirthday.text.toString()

         if(binding.etName.text.length in 1..1 && !filterText(binding.etName.text.toString())) {
            Toast.makeText(context, "특수문자는 입력 불가합니다.", Toast.LENGTH_SHORT).show()
         }else {
            dataManager.updateUserStr(USER, "name", name, "id")
            dataManager.updateUserStr(USER, "birthday", birthday, "id")

            if(bitmap != null) {
               val data = dataManager.getUser().profileImage
               File(requireActivity().filesDir, data).delete()
               val result = saveImage(requireActivity(), bitmap!!) // 선택한 이미지를 저장하는 메서드 호출
               if(result != "") dataManager.updateUserStr(USER, "profileImage", result, "id")
            }

            requireActivity().supportFragmentManager.beginTransaction().apply {
               replace(R.id.inputFrame, InputBodyFragment())
               commit()
            }
         }
      }

      return binding.root
   }
}